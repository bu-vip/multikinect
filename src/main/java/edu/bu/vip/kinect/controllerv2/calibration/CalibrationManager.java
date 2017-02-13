package edu.bu.vip.kinect.controllerv2.calibration;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.roeper.bu.kinect.Protos.Frame;
import edu.bu.vip.kinect.controller.calibration.Protos.Calibration;
import edu.bu.vip.kinect.controller.calibration.Protos.CalibrationFrame;
import edu.bu.vip.kinect.controller.calibration.Protos.CameraPairCalibration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
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

  private boolean creatingCalibration = false;
  private int sessionCount;
  private Map<Integer, ImmutableList<CalibrationFrame>> sessionFrames;
  private double sessionError;
  private Fragmenter fragmenter;

  private boolean recordingSession;
  private List<CalibrationFrame> currentSessionFrames;
  private Calibration calibration;
  private CalibrationDataDB calibrationDataDB;

  @Inject
  public CalibrationManager(CalibrationDataDB calibrationDataDB) {
    this.calibrationDataDB = calibrationDataDB;
  }

  public void start(Calibration calibration) {
    if (!creatingCalibration) {
      sessionCount = 0;
      sessionFrames = new Hashtable<>();
      sessionError = Double.POSITIVE_INFINITY;
      recordingSession = false;
      creatingCalibration = true;
    } else {
      logger.warn("Already creating a calibration");
    }
  }

  public void startRecording() {
    if (!creatingCalibration) {
      throw new RuntimeException("Not creating a calibration");
    }

    if (!recordingSession) {
      currentSessionFrames = new ArrayList<>();
      fragmenter.start((frame) -> {
        currentSessionFrames.add(frame);
      });
      recordingSession = true;
    } else {
      logger.warn("Already recording a frame");
    }
  }

  public void stopRecording() {
    if (!creatingCalibration) {
      throw new RuntimeException("Not creating a calibration");
    }

    if (recordingSession) {
      fragmenter.stop();
      sessionFrames.put(sessionCount, ImmutableList.copyOf(currentSessionFrames));
      currentSessionFrames.clear();
      sessionCount++;
      calculateTransform();
      recordingSession = false;
    } else {
      logger.warn("Not recording a frame");
    }
  }

  private void calculateTransform() {
    // Concatenate all recording frame lists together
    ImmutableList.Builder<CalibrationFrame> allFramesBuilder = ImmutableList.builder();
    sessionFrames.forEach((sessionId, framesList) -> {
      allFramesBuilder.addAll(framesList);
    });
    ImmutableList<CalibrationFrame> allFrames = allFramesBuilder.build();

    // Separate the frames by camera pair
    HashMap<ImmutableSet<String>, CameraTransform> transforms = new HashMap<>();
    allFrames.forEach((frame) -> {
      // Use a set as the key
      ImmutableSet<String> key = ImmutableSet.of(frame.getCameraA(), frame.getCameraB());
      // Check if this is a new camera pair, if so add a transform object
      if (!transforms.containsKey(key)) {
        transforms.put(key,
            new CameraTransform(frame.getCameraA(), frame.getCameraB(), calibrationDataDB));
      }

      // Add the frame to the transform
      transforms.get(key).addFrame(frame);
    });

    List<Future<CameraPairCalibration>> tasks = new ArrayList<>();
    ExecutorService executorService = Executors.newFixedThreadPool(4);
    transforms.forEach((key, value) -> {
      Future<CameraPairCalibration> future = executorService.submit(value);
      tasks.add(future);
    });

    // Wait for tasks to finish
    try {
      Calibration.Builder builder = calibration.toBuilder();
      builder.clearCameraCalibrations();
      for (Future<CameraPairCalibration> task : tasks) {
        CameraPairCalibration result = task.get(TRANSFORM_TIMEOUT, TimeUnit.MILLISECONDS);
        builder.addCameraCalibrations(result);
      }
      calibration = builder.build();
    } catch (TimeoutException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (ExecutionException ex) {
      ex.printStackTrace();
    } catch (InterruptedException ie) {
      ie.printStackTrace();
    }

    // Shutdown executor. On normal execution, all tasks should be done.
    executorService.shutdownNow();
  }

  public void deleteRecording(int sessionId) {
    sessionFrames.remove(sessionId);
  }

  public Calibration getCalibration() {
    return null;
  }

  public Calibration finish() {
    return null;
  }
}
