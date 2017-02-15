package edu.bu.vip.kinect.controllerv2.webconsole.api.state;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.bu.vip.kinect.controllerv2.Controller.State;
import edu.bu.vip.kinect.controllerv2.webconsole.api.CalibrationRep;

public class NewCalibrationFrameState extends ControllerState {
  private CalibrationRep calibration;

  public NewCalibrationFrameState(
      CalibrationRep calibration) {
    super(State.NEW_CALIBRATION_FRAME);
    this.calibration = calibration;
  }

  @JsonProperty
  public CalibrationRep getCalibration() {
    return calibration;
  }
}
