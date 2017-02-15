package edu.bu.vip.kinect.controllerv2.webconsole.api.state;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.bu.vip.kinect.controllerv2.Controller.State;
import edu.bu.vip.kinect.controllerv2.webconsole.api.CalibrationRep;

public class NewCalibrationState extends ControllerState {
  private CalibrationRep newCalibration;

  public NewCalibrationState(CalibrationRep newCalibration) {
    super(State.NEW_CALIBRATION);
    this.newCalibration = newCalibration;
  }

  @JsonProperty
  public CalibrationRep getNewCalibration() {
    return newCalibration;
  }
}
