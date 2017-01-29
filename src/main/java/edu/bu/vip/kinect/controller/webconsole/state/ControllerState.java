package edu.bu.vip.kinect.controller.webconsole.state;

import com.fasterxml.jackson.annotation.JsonProperty;

import edu.bu.vip.kinect.controller.Controller.Mode;

class ControllerState {
  private Mode mode;
  private CalibrationState calibration;
  
  protected ControllerState() {
    // Jackson
  }
  
  public ControllerState(Mode mode, CalibrationState calibration) {
    super();
    this.mode = mode;
    this.calibration = calibration;
  }

  @JsonProperty
  public Mode getMode() {
    return mode;
  }
  
  @JsonProperty
  public CalibrationState getCalibration() {
    return calibration;
  }
}
