package edu.bu.vip.kinect.controller.webconsole.state;

import com.fasterxml.jackson.annotation.JsonProperty;

import edu.bu.vip.kinect.controller.calibration.CalibrationManager;

class CalibrationState {
  private boolean session;
  private String sessionId;
  private String dataLocation;
  private int frameCount;
  private boolean recording;
  private double accuracy;
  
  protected CalibrationState() {
    // Jackson
  }
  
  public CalibrationState(boolean session, String sessionId, String dataLocation, int frameCount,
      boolean recording, double accuracy) {
    super();
    this.session = session;
    this.sessionId = sessionId;
    this.dataLocation = dataLocation;
    this.frameCount = frameCount;
    this.recording = recording;
    this.accuracy = accuracy;
  }
  
  @JsonProperty
  public boolean isSession() {
    return session;
  }
  
  @JsonProperty
  public String getSessionId() {
    return sessionId;
  }
  
  @JsonProperty
  public String getDataLocation() {
    return dataLocation;
  }

  @JsonProperty
  public int getFrameCount() {
    return frameCount;
  }
  
  @JsonProperty
  public boolean isRecording() {
    return recording;
  }
  
  @JsonProperty
  public double getAccuracy() {
    return accuracy;
  }
}
