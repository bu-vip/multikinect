package edu.bu.vip.kinect.controller.calibration;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.ejml.data.DenseMatrix64F;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.roeper.bu.kinect.Protos.Frame;

import edu.bu.vip.kinect.controller.ConfigManager;
import edu.bu.vip.kinect.controller.calibration.Protos.CalibrationFrame;
import edu.bu.vip.kinect.controller.camera.CameraManager;
import edu.bu.vip.kinect.controller.camera.MessageWriter;
import edu.bu.vip.kinect.sync.CoordinateTransform.Transform;
import edu.bu.vip.kinect.sync.FrameReader;

@Singleton
// TODO(doug) - thread safety
public class CalibrationManager {

  private static final int TRANSFORM_TIMEOUT = 1000000;
  
  private final Logger logger = LoggerFactory.getLogger(getClass());
  private final CameraManager cameraManager;
  private final ConfigManager configManager;
  
  private boolean session = false;
  private String sessionId;
  private String sessionDataLocation;
  // TODO(doug) - make immutable list so there isn't any threading issues
  private List<CalibrationFrame> sessionFrames = new LinkedList<>();
  private CalibrationRecorder recorder;
  private boolean recording = false;
  private double sessionAccuracy = -1;
  private DenseMatrix64F lastCalibration;

  @Inject
  protected CalibrationManager(ConfigManager configManager, CameraManager cameraManager) {
    this.configManager = configManager;
    this.cameraManager = cameraManager;
  }
  
  public void beginCalibrationSession() {
    if (session) {
      // TODO(doug) - throw exception?
    }
    
    Date currentDate = new Date();
    SimpleDateFormat formtter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
    sessionId = "session-" + formtter.format(currentDate);
    sessionDataLocation = configManager.getCalibrationDataFolder() + File.separator + sessionId;
    // Make the directory if doesn't exists
    (new File(sessionDataLocation)).mkdirs();
    sessionFrames = new ArrayList<>();
    recorder = new CalibrationRecorder(sessionDataLocation, new MessageWriter());
    
    logger.info("Beginning new calibration session {}", sessionId);
    
    session = true;
  }
  
  public void startCalibration() {
    if (!session) {
      // TODO(doug) 
    }

    if (recording) {
      // TODO(doug) 
    }
    
    logger.info("Starting calibration recording for session {}", sessionId);

    // TODO(doug) - recorder.start(cameraManager.getFrameBus());
    recording = true;
  }

  public void stopCalibration() {
    if (!session) {
      // TODO(doug) 
    }
    
    if (!recording) {
      // TODO(doug) 
    }
    
    List<CalibrationFrame> frames = recorder.stop();
    
    logger.info("Finished recording {} frames for session {}", frames.size(), sessionId);
    
    sessionFrames.addAll(frames);
    calculateTransform();

    recording = false;
  }

  public void finishCalibrationSession() {
    if (!session) {
      // TODO(doug) 
    }
    
    if (recording) {
      // TODO(doug) 
    }
    
    calculateTransform();
    
    session = false;
  }
  
  private void calculateTransform() {
    // Separate frames by pair of cameras
    HashMap<ImmutableSet<String>, CameraTransform> transforms = new HashMap<>();
    sessionFrames.forEach((frame) -> {
      ImmutableSet<String> key = ImmutableSet.of(frame.getCameraA(), frame.getCameraB());
      if (!transforms.containsKey(key)) {
        transforms.put(key, new CameraTransform());
      }

      try {
        ImmutableList<Frame> framesA = FrameReader.readFramesInTimeInterval(frame.getFileAPath(),
            frame.getStartTimeA(), frame.getEndTimeA());
        ImmutableList<Frame> framesB = FrameReader.readFramesInTimeInterval(frame.getFileBPath(),
            frame.getStartTimeB(), frame.getEndTimeB());

        transforms.get(key).addFramePair(framesA, framesB);
      } catch (IOException e) {
        // TODO(doug) - handle
        throw new RuntimeException(e);
      }
    });

    List<Future<Transform>> tasks = new ArrayList<>();
    ExecutorService executorService = Executors.newFixedThreadPool(4);
    transforms.forEach((key, value) -> {
      Future<Transform> future = executorService.submit(value);
      tasks.add(future);
    });

    // Wait for tasks to finish
    try {
      for (Future<Transform> task : tasks) {
        Transform result = task.get(TRANSFORM_TIMEOUT, TimeUnit.MILLISECONDS);
        sessionAccuracy = result.getError();
        lastCalibration = result.getTransform();
        logger.info("Matrix: {}", result.getTransform().toString());
      }
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
 
  public boolean isSession() {
    return session;
  }

  public String getSessionId() {
    return sessionId;
  }

  public String getSessionDataLocation() {
    return sessionDataLocation;
  }

  public int getSessionFrameCount() {
    return sessionFrames.size();
  }

  public boolean isRecording() {
    return recording;
  }

  public double getSessionAccuracy() {
    return sessionAccuracy;
  }

  public DenseMatrix64F getLastCalbration() {
    return lastCalibration;
  }
}
