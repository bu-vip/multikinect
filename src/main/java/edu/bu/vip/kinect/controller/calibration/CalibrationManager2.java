package edu.bu.vip.kinect.controller.calibration;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableList;
import com.google.common.eventbus.Subscribe;

import edu.bu.vip.kinect.controller.calibration.Protos.CalibrationFrame;

public class CalibrationManager2 {
  
  /**
   * Calibration from user's perspective:
   *    - Start new calibration
   *    - Start new "calibration session"
   *        - 1 person walks around area, creating frames
   *        - End session
   *    - Repeat until accuracy is good
   *    - Can delete sessions if something bad happens
   *    - Save calibration
   *        - Add name, description, other metadata
   */
  
  private boolean creatingCalibration = false;
  private int sessionCount;
  private Map<Integer, ImmutableList<CalibrationFrame>> sessionFrames;
  private double sessionError;
  private Fragmenter fragmenter;

  private boolean recordingSession;
  private List<CalibrationFrame> currentSessionFrames;
  
  
  public void startNewCalibration() {
    if (creatingCalibration) {
      throw new RuntimeException("Already creating a calibration");
    }
    
    sessionCount = 0;
    sessionFrames = new Hashtable<>();
    sessionError = Double.POSITIVE_INFINITY;
    recordingSession = false;
    creatingCalibration = true;
  }
  
  public void startNewSession() {
    if (!creatingCalibration) {
      throw new RuntimeException("Not creating a calibration");
    }
    
    if (recordingSession) {
      throw new RuntimeException("Already recording");
    }
    
    currentSessionFrames = new ArrayList<>();
    fragmenter.start();
    recordingSession = true;
  }
  
  public void stopSession() {
    if (!creatingCalibration) {
      throw new RuntimeException("Not creating a calibration");
    }
    
    if (!recordingSession) {
      throw new RuntimeException("Not recording");
    }
   
    fragmenter.stop();
    sessionFrames.put(sessionCount, ImmutableList.copyOf(currentSessionFrames));
    currentSessionFrames.clear();
    sessionCount++;
    
    // TODO(doug) - recalculate error
  }
  
  public void deleteSession(int sessionId) {
    sessionFrames.remove(sessionId);
  }
  

  @Subscribe
  public void onCalibrationFrame(CalibrationFrame frame) {
    
  }
}
