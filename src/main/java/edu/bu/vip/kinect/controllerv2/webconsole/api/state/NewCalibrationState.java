package edu.bu.vip.kinect.controllerv2.webconsole.api.state;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.bu.vip.kinect.controllerv2.Controller.State;
import edu.bu.vip.kinect.controllerv2.webconsole.api.CalibrationRep;

public class NewCalibrationState extends ControllerState {
  private CalibrationRep newCalibrationRep;

  public NewCalibrationState(CalibrationRep newCalibrationRep) {
    super(State.NEW_CALIBRATION);
    this.newCalibrationRep = newCalibrationRep;
  }

  @JsonProperty
  public CalibrationRep getNewCalibrationRep() {
    return newCalibrationRep;
  }
}
