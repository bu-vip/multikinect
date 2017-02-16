package edu.bu.vip.multikinect.controllerv2.webconsole.api.state;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.bu.vip.multikinect.controllerv2.Controller.State;
import edu.bu.vip.multikinect.controllerv2.webconsole.api.CalibrationRep;
import java.util.List;

public class SelectCalibrationState extends ControllerState {
  private List<CalibrationRep> calibrations;

  public SelectCalibrationState(List<CalibrationRep> calibrations) {
    super(State.SELECT_CALIBRATION);
    this.calibrations = calibrations;
  }

  @JsonProperty
  public List<CalibrationRep> getCalibrations() {
    return calibrations;
  }
}
