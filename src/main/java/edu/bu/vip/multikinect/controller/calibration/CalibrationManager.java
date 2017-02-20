package edu.bu.vip.multikinect.controller.calibration;

import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import edu.bu.vip.kinect.controller.calibration.Protos.Calibration;
import edu.bu.vip.kinect.controller.calibration.Protos.CameraPairCalibration;
import edu.bu.vip.kinect.controller.calibration.Protos.GroupOfFrames;
import edu.bu.vip.kinect.controller.calibration.Protos.Recording;
import edu.bu.vip.multikinect.Protos.Frame;
import edu.bu.vip.multikinect.controller.camera.FrameBus;
import edu.bu.vip.multikinect.controller.camera.FrameReceivedEvent;
import edu.bu.vip.multikinect.util.TimestampUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
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
   * CalibrationRep from user's perspective:
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
  private Recording.Builder currentRecording;
  private Calibration.Builder calibration;

  private CalibrationDataStore calibrationDataStore;
  private EventBus frameBus;

  @Inject
  public CalibrationManager(CalibrationDataStore calibrationDataStore, @FrameBus EventBus frameBus) {
    this.calibrationDataStore = calibrationDataStore;
    this.frameBus =frameBus;
  }

  public void start(String name, String notes) {
    if (!active) {
      recording = false;
      active = true;
      calibration = Calibration.newBuilder();
      calibration.setName(name);
      calibration.setNotes(notes);
      calibration.setDateCreated(TimestampUtils.now());
    } else {
      logger.warn("Already creating a calibration");
    }
  }

  public void startRecording() {
    checkActive();

    if (!recording) {
      // Start recording
      currentRecording = Recording.newBuilder();
      currentRecording.setId(System.currentTimeMillis());
      currentRecording.setDateCreated(TimestampUtils.now());

      this.frameBus.register(this);
      recording = true;
    } else {
      logger.warn("Already recording a frame");
    }
  }

  @Subscribe
  public void onFrameEventReceived(FrameReceivedEvent event) {
    calibrationDataStore
        .storeFrame(calibration.getId(), currentRecording.getId(), event.getProps().getId(), event.getFrame());
  }

  public void stopRecording() {
    checkActive();

    if (recording) {
      this.frameBus.unregister(this);

      /*
      ImmutableList<Frame> framesA = calibrationDataStore.getAllFrames(calibration.getId(), currentRecording.getId(), cameraA);
      ImmutableList<Frame> framesB = calibrationDataStore.getAllFrames(calibration.getId(), currentRecording.getId(), cameraA);
      Callable<ImmutableList<GroupOfFrames>> job = algorithm.createJob(framesA, framesB);
      Future<ImmutableList<GroupOfFrames>> future = jobExecutor.submit(job);
      */

      calibration.addRecordings(currentRecording);
      currentRecording = null;

      calculateTransform();
      recording = false;
    } else {
      logger.warn("Not recording a frame");
    }
  }

  public void deleteRecording(long sessionId) {
    checkActive();
    List<Recording> recordings = calibration.getRecordingsList();
    for (int i = 0; i < recordings.size(); i++) {
      if (recordings.get(i).getId() == sessionId) {
        calibration.removeRecordings(i);
        break;
      }
    }
  }

  public Calibration getCalibration() {
    checkActive();
    return calibration.build();
  }

  public Calibration finish() {
    checkActive();

    if (recording) {
      logger.warn("finish() was called while recording, discarding active recording");

      this.frameBus.unregister(this);
      currentRecording = null;
      recording = false;
    }

    logger.info("Finished calibration");
    active = false;

    return calibration.build();
  }

  private void checkActive() {
    if (!active) {
      throw new RuntimeException("Not creating a calibration");
    }
  }

  private void calculateTransform() {
    // TODO(doug) - get cameras
    List<String> cameraIds = ImmutableList.of("camera1", "camera2");
    Collection<List<String>> pairs = Collections2.permutations(cameraIds);

    List<Future<CameraPairCalibration>> tasks = new ArrayList<>();
    ExecutorService executorService = Executors.newFixedThreadPool(4);
    // Create camera transforms for each pair of cameras
    for (List<String> pair : pairs) {
      Callable<CameraPairCalibration> job = new CameraTransform(calibration.build(),
          pair.get(0),
          pair.get(1),
          calibrationDataStore);
      Future<CameraPairCalibration> future = executorService.submit(job);
      tasks.add(future);
    }

    // Wait for tasks to finish
    try {
      calibration.clearCameraCalibrations();
      for (Future<CameraPairCalibration> task : tasks) {
        CameraPairCalibration result = task.get(TRANSFORM_TIMEOUT, TimeUnit.MILLISECONDS);
        logger.info("Error: {}", result.getError());
        calibration.addCameraCalibrations(result);
      }
    } catch (TimeoutException | InterruptedException | ExecutionException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    // Shutdown executor. On normal execution, all tasks should be done.
    executorService.shutdownNow();
  }
}
