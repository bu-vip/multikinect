package edu.bu.vip.kinect.controllerv2.webconsole.api.state;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.bu.vip.kinect.controllerv2.Controller.State;
import edu.bu.vip.kinect.controllerv2.webconsole.api.Calibration;

public class NewCalibrationFrameState extends ControllerState {
  private Calibration calibration;

  public NewCalibrationFrameState(
      Calibration calibration) {
    super(State.NEW_CALIBRATION_FRAME);
    this.calibration = calibration;
  }

  @JsonProperty
  public Calibration getCalibration() {
    return calibration;
  }
}
