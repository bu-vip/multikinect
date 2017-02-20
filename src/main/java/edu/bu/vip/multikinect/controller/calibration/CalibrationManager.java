package edu.bu.vip.multikinect.controller.calibration;

import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import edu.bu.vip.kinect.controller.calibration.Protos.Calibration;
import edu.bu.vip.kinect.controller.calibration.Protos.CameraPairCalibration;
import edu.bu.vip.kinect.controller.calibration.Protos.GroupOfFrames;
import edu.bu.vip.kinect.controller.calibration.Protos.Recording;
import edu.bu.vip.multikinect.Protos.Frame;
import edu.bu.vip.multikinect.controller.camera.CameraManager;
import edu.bu.vip.multikinect.controller.camera.FrameBus;
import edu.bu.vip.multikinect.controller.camera.FrameReceivedEvent;
import edu.bu.vip.multikinect.util.TimestampUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class CalibrationManager {

  private static final int TRANSFORM_TIMEOUT = 1000000;

  /**
   * Calibration from user's perspective:
   * - Start new calibration
   * - Start new "calibration session"
   * - 1 person walks around area, creating frames
   * - End session
   * - Repeat until accuracy is good
   * - Can delete sessions if something bad happens
   * - Save calibration
   * - Add name, description, other metadata
   */

  private final Logger logger = LoggerFactory.getLogger(getClass());

  private boolean active = false;
  private boolean recording;
  private Recording currentRecording;
  private Calibration calibration;
  private Object calibrationLock = new Object();
  private Object recordingLock = new Object();

  private CalibrationDataStore calibrationDataStore;
  private EventBus frameBus;
  private final CameraManager cameraManager;
  private final CalibrationAlgorithm algorithm;
  private final ExecutorService algorithmExecutor = Executors.newCachedThreadPool();

  @Inject
  public CalibrationManager(CalibrationDataStore calibrationDataStore, @FrameBus EventBus frameBus,
      CameraManager cameraManager, CalibrationAlgorithm algorithm) {
    this.calibrationDataStore = calibrationDataStore;
    this.frameBus = frameBus;
    this.cameraManager = cameraManager;
    this.algorithm = algorithm;
  }

  public void start(String name, String notes) {
    synchronized (calibrationLock) {
      if (!active) {
        recording = false;
        active = true;
        Calibration.Builder builder = Calibration.newBuilder();
        builder.setName(name);
        builder.setNotes(notes);
        builder.setDateCreated(TimestampUtils.now());
        calibration = builder.build();
      } else {
        logger.warn("Already creating a calibration");
      }
    }
  }

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
        logger.warn("Already recording a frame");
      }
    }
  }

  @Subscribe
  public void onFrameEventReceived(FrameReceivedEvent event) {
    synchronized (calibrationLock) {
      synchronized (recordingLock) {
        calibrationDataStore
            .storeFrame(calibration.getId(), currentRecording.getId(), event.getProps().getId(),
                event.getFrame());
      }
    }
  }

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
                .getAllFrames(calibration.getId(), currentRecording.getId(), cameraA);
            ImmutableList<Frame> framesB = calibrationDataStore
                .getAllFrames(calibration.getId(), currentRecording.getId(), cameraB);
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
            Calibration.Builder calBuilder = calibration.toBuilder();
            calBuilder.addRecordings(builder.build());
            calibration = calBuilder.build();
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

  public void deleteRecording(long sessionId) {
    checkActive();

    synchronized (calibrationLock) {
      Calibration.Builder builder = calibration.toBuilder();
      List<Recording> recordings = builder.getRecordingsList();
      for (int i = 0; i < recordings.size(); i++) {
        if (recordings.get(i).getId() == sessionId) {
          builder.removeRecordings(i);
          break;
        }
      }
      calibration = builder.build();
    }
  }

  public Calibration getCalibration() {
    checkActive();
    synchronized (calibrationLock) {
      return calibration;
    }
  }

  public Calibration finish() {
    checkActive();

    if (recording) {
      logger.warn("finish() was called while recording, discarding active recording");

      this.frameBus.unregister(this);
      currentRecording = null;
      recording = false;
    }

    logger.info("Waiting for jobs to finish...");
    algorithmExecutor.shutdown();
    try {
      algorithmExecutor.awaitTermination(TRANSFORM_TIMEOUT, TimeUnit.MILLISECONDS);
    } catch (InterruptedException e) {
      logger.error("Calculations timed out", e);
    }

    logger.info("Finished calibration");
    active = false;

    synchronized (calibrationLock) {
      return calibration;
    }
  }

  private void checkActive() {
    if (!active) {
      throw new RuntimeException("Not creating a calibration");
    }
  }

  private void calculateTransform() {
    synchronized (calibrationLock) {
      List<ImmutableList<String>> pairs = getCameraPairs();
      List<Future<CameraPairCalibration>> tasks = new ArrayList<>();
      // Create camera transforms for each pair of cameras
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
        for (Future<CameraPairCalibration> task : tasks) {
          CameraPairCalibration result = task.get(TRANSFORM_TIMEOUT, TimeUnit.MILLISECONDS);
          logger.info("Error: {}", result.getError());
          builder.addCameraCalibrations(result);
        }
        calibration = builder.build();
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
}
