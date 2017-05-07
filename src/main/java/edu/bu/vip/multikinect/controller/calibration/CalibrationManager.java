package edu.bu.vip.multikinect.controller.calibration;

import com.google.common.collect.ImmutableList;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.common.primitives.Doubles;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import edu.bu.vip.kinect.controller.calibration.Protos.Calibration;
import edu.bu.vip.kinect.controller.calibration.Protos.CameraPairCalibration;
import edu.bu.vip.kinect.controller.calibration.Protos.ErrorStats;
import edu.bu.vip.kinect.controller.calibration.Protos.GroupOfFrames;
import edu.bu.vip.kinect.controller.calibration.Protos.Recording;
import edu.bu.vip.multikinect.Protos.Frame;
import edu.bu.vip.multikinect.controller.camera.CameraManager;
import edu.bu.vip.multikinect.controller.camera.FrameBus;
import edu.bu.vip.multikinect.controller.camera.FrameReceivedEvent;
import edu.bu.vip.multikinect.util.TimestampUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.ejml.data.DenseMatrix64F;
import org.ejml.simple.SimpleMatrix;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handles creating new calibrations.
 *
 * The process for creating a new calibration is:
 * <ol>
 * <li>Start a new calibration</li>
 * <li>Start recording data</li>
 * <li>Record data</li>
 * <li>Stop recording data</li>
 * <li>Repeat until accuracy is good</li>
 * <li>Delete bad recordings if necessary</li>
 * <li>Finish calibration</li>
 * </ol>
 */
@Singleton
public class CalibrationManager {

  private static final int TRANSFORM_TIMEOUT = 1000000;

  private final Logger logger = LoggerFactory.getLogger(getClass());

  private boolean creatingCalibration = false;
  private long calibrationId;
  private boolean recording;
  private Recording currentRecording;
  private Object calibrationLock = new Object();
  private Object recordingLock = new Object();

  private CalibrationDataStore calibrationDataStore;
  private EventBus frameBus;
  private final CameraManager cameraManager;
  private final CalibrationAlgorithm algorithm;
  private ExecutorService algorithmExecutor;

  @Inject
  public CalibrationManager(CalibrationDataStore calibrationDataStore, @FrameBus EventBus frameBus,
      CameraManager cameraManager, CalibrationAlgorithm algorithm) {
    this.calibrationDataStore = calibrationDataStore;
    this.frameBus = frameBus;
    this.cameraManager = cameraManager;
    this.algorithm = algorithm;
  }

  /**
   * Start creating a new calibration. Creates a new calibration in the data store.
   *
   * @param name - Name for the calibration
   * @param notes - Notes for the calibration
   *
   * TODO(doug) - This structure doesn't make editing the fields easy, should probably change...
   */
  public void start(String name, String notes) {
    synchronized (calibrationLock) {
      // Check if we're already creating a recording
      if (!creatingCalibration) {
        recording = false;
        creatingCalibration = true;
        algorithmExecutor = Executors.newCachedThreadPool();
        Calibration.Builder builder = Calibration.newBuilder();
        builder.setName(name);
        builder.setNotes(notes);
        builder.setDateCreated(TimestampUtils.now());
        // Create the calibration, store the ID so we can get it later
        calibrationId = calibrationDataStore.createCalibration(builder.build()).getId();
      } else {
        logger.warn("Already creating a calibration");
      }
    }
  }

  /**
   * Start recording new data to be used for calculating the calibration.
   */
  public void startRecording() {
    checkActive();

    synchronized (recordingLock) {
      if (!recording) {
        // Start recording
        Recording.Builder builder = Recording.newBuilder();
        builder.setId(System.currentTimeMillis());
        builder.setDateCreated(TimestampUtils.now());
        currentRecording = builder.build();

        this.frameBus.register(this);
        recording = true;
      } else {
        logger.warn("Already recording");
      }
    }
  }

  @Subscribe
  public void onFrameEventReceived(FrameReceivedEvent event) {
    synchronized (calibrationLock) {
      synchronized (recordingLock) {
        calibrationDataStore
            .storeFrame(calibrationId, currentRecording.getId(), event.getProps().getId(),
                event.getFrame());
      }
    }
  }

  /**
   * Stop recording data.
   */
  public void stopRecording() {
    checkActive();

    if (recording) {
      this.frameBus.unregister(this);
      final Recording.Builder builder;
      synchronized (recordingLock) {
        builder = currentRecording.toBuilder();
      }
      algorithmExecutor.submit(new Runnable() {
        @Override
        public void run() {
          List<GroupOfFrames> allFrames = new ArrayList<>();
          List<ImmutableList<String>> pairs = getCameraPairs();
          for (List<String> pair : pairs) {
            String cameraA = pair.get(0);
            String cameraB = pair.get(1);
            ImmutableList<Frame> framesA = calibrationDataStore
                .getAllFrames(calibrationId, currentRecording.getId(), cameraA);
            ImmutableList<Frame> framesB = calibrationDataStore
                .getAllFrames(calibrationId, currentRecording.getId(), cameraB);
            Callable<ImmutableList<GroupOfFrames>> job = algorithm.createJob(framesA, framesB);
            try {
              ImmutableList<GroupOfFrames> gofs = job.call();
              for (GroupOfFrames gof : gofs) {
                GroupOfFrames.Builder gofBuilder = gof.toBuilder();
                gofBuilder.setCameraA(cameraA);
                gofBuilder.setCameraB(cameraB);
                allFrames.add(gofBuilder.build());
              }
            } catch (Exception e) {
              // TODO(doug) - handle
              e.printStackTrace();
            }
          }

          builder.addAllGofs(allFrames);
          synchronized (calibrationLock) {
            Optional<Calibration> optCal = calibrationDataStore.getCalibration(calibrationId);
            if (!optCal.isPresent()) {
              throw new RuntimeException("Couldn't get calibration to update...");
            }
            Calibration.Builder calBuilder = optCal.get().toBuilder();
            calBuilder.addRecordings(builder.build());
            calibrationDataStore.updateCalibration(calBuilder.build());
          }
          currentRecording = null;

          calculateTransform();

          recording = false;
        }
      });

    } else {
      logger.warn("Not recording a frame");
    }
  }

  /**
   * Delete a data recording from the new calibration.
   */
  public void deleteRecording(long sessionId) {
    checkActive();

    synchronized (calibrationLock) {
      Optional<Calibration> optCal = calibrationDataStore.getCalibration(calibrationId);
      if (!optCal.isPresent()) {
        throw new RuntimeException("Couldn't get calibration to update...");
      }
      Calibration.Builder builder = optCal.get().toBuilder();
      List<Recording> recordings = builder.getRecordingsList();
      for (int i = 0; i < recordings.size(); i++) {
        if (recordings.get(i).getId() == sessionId) {
          builder.removeRecordings(i);
          break;
        }
      }
      calibrationDataStore.updateCalibration(builder.build());
    }
  }

  /**
   * Get the new calibration being created.
   */
  public Calibration getCalibration() {
    checkActive();
    synchronized (calibrationLock) {
      Optional<Calibration> optCal = calibrationDataStore.getCalibration(calibrationId);
      if (!optCal.isPresent()) {
        throw new RuntimeException("Couldn't get calibration to update...");
      }
      return optCal.get();
    }
  }

  /**
   * Finish creating the calibration.
   */
  public Calibration finish() {
    checkActive();

    if (recording) {
      logger.warn("finish() was called while recording, discarding creatingCalibration recording");

      this.frameBus.unregister(this);
      currentRecording = null;
      recording = false;
    }

    logger.info("Waiting for jobs to finish...");
    algorithmExecutor.shutdown();
    try {
      algorithmExecutor.awaitTermination(TRANSFORM_TIMEOUT, TimeUnit.MILLISECONDS);
      algorithmExecutor = null;
    } catch (InterruptedException e) {
      logger.error("Calculations timed out", e);
    }

    logger.info("Finished calibration");
    creatingCalibration = false;

    synchronized (calibrationLock) {
      Optional<Calibration> optCal = calibrationDataStore.getCalibration(calibrationId);
      if (!optCal.isPresent()) {
        throw new RuntimeException("Couldn't get calibration to update...");
      }
      return optCal.get();
    }
  }

  private void checkActive() {
    if (!creatingCalibration) {
      throw new RuntimeException("Not creating a calibration");
    }
  }

  private void calculateTransform() {
    synchronized (calibrationLock) {
      Optional<Calibration> optCal = calibrationDataStore.getCalibration(calibrationId);
      if (!optCal.isPresent()) {
        throw new RuntimeException("Couldn't get calibration to update...");
      }
      Calibration calibration = optCal.get();

      // Create camera transforms for each pair of cameras
      List<ImmutableList<String>> pairs = getCameraPairs();
      List<Future<CameraPairCalibration>> tasks = new ArrayList<>();
      for (List<String> pair : pairs) {
        Callable<CameraPairCalibration> job = new CameraTransform(calibration,
            pair.get(0),
            pair.get(1),
            calibrationDataStore);
        Future<CameraPairCalibration> future = algorithmExecutor.submit(job);
        tasks.add(future);
      }

      // Wait for tasks to finish
      try {
        Calibration.Builder builder = calibration.toBuilder();
        builder.clearCameraCalibrations();

        // Check if there is only 1 camera
        if (pairs.size() == 0) {
          // Create a pair of itself
          String cameraId = cameraManager.getConnectedCameras().asList().get(0);
          builder.addCameraCalibrations(createIdentityPair(cameraId));
        } else {
          for (Future<CameraPairCalibration> task : tasks) {
            CameraPairCalibration result = task.get(TRANSFORM_TIMEOUT, TimeUnit.MILLISECONDS);
            logger.info("Error: {}", result.getError());
            builder.addCameraCalibrations(result);
          }
        }

        calibrationDataStore.updateCalibration(builder.build());
      } catch (TimeoutException | InterruptedException | ExecutionException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
  }

  private List<ImmutableList<String>> getCameraPairs() {
    ImmutableList<String> cameraIds = ImmutableList.copyOf(cameraManager.getConnectedCameras());
    ImmutableList.Builder<ImmutableList<String>> pairs = ImmutableList.builder();
    for (int i = 0; i < cameraIds.size() - 1; i++) {
      for (int j = i + 1; j < cameraIds.size(); j++) {
        pairs.add(ImmutableList.of(cameraIds.get(i), cameraIds.get(j)));
      }
    }
    return pairs.build();
  }

  private CameraPairCalibration createIdentityPair(String cameraId) {
    DenseMatrix64F transform = SimpleMatrix.identity(4).getMatrix();

    CameraPairCalibration.Builder builder = CameraPairCalibration.newBuilder();
    builder.setCameraA(cameraId);
    builder.setCameraB(cameraId);
    builder.addAllTransform(Doubles.asList(transform.data));
    builder.setError(0);

    ErrorStats.Builder errorBuilder = ErrorStats.newBuilder();
    errorBuilder.addErrors(0);
    builder.setErrorStats(errorBuilder.build());

    return builder.build();
  }
}
