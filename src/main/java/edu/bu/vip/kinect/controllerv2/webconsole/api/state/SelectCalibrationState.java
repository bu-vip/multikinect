package edu.bu.vip.kinect.controllerv2.webconsole.api.state;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.bu.vip.kinect.controllerv2.Controller.State;
import edu.bu.vip.kinect.controllerv2.webconsole.api.CalibrationRep;
import java.util.List;

public class SelectCalibrationState extends ControllerState {
  private List<CalibrationRep> calibrationReps;

  public SelectCalibrationState(List<CalibrationRep> calibrationReps) {
    super(State.SELECT_CALIBRATION);
    this.calibrationReps = calibrationReps;
  }

  @JsonProperty
  public List<CalibrationRep> getCalibrationReps() {
    return calibrationReps;
  }
}
