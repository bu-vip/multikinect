package edu.bu.vip.kinect.controllerv2.webconsole.api.state;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.bu.vip.kinect.controllerv2.Controller.State;
import edu.bu.vip.kinect.controllerv2.webconsole.api.Calibration;

public class NewCalibrationState extends ControllerState {
  private Calibration newCalibration;

  public NewCalibrationState(Calibration newCalibration) {
    super(State.NEW_CALIBRATION);
    this.newCalibration = newCalibration;
  }

  @JsonProperty
  public Calibration getNewCalibration() {
    return newCalibration;
  }
}
