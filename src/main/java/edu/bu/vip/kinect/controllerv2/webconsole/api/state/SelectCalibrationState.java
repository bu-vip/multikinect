package edu.bu.vip.kinect.controllerv2.webconsole.api.state;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.bu.vip.kinect.controllerv2.Controllerv2.State;
import edu.bu.vip.kinect.controllerv2.webconsole.api.Calibration;
import java.util.List;

public class SelectCalibrationState extends ControllerState {
  private List<Calibration> calibrations;

  public SelectCalibrationState(List<Calibration> calibrations) {
    super(State.SELECT_CALIBRATION);
    this.calibrations = calibrations;
  }

  @JsonProperty
  public List<Calibration> getCalibrations() {
    return calibrations;
  }
}
