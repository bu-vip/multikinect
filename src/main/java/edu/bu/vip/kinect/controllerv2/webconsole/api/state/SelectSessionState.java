package edu.bu.vip.kinect.controllerv2.webconsole.api.state;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.bu.vip.kinect.controllerv2.Controller.State;
import edu.bu.vip.kinect.controllerv2.webconsole.api.CalibrationRep;
import edu.bu.vip.kinect.controllerv2.webconsole.api.SessionRep;
import java.util.List;

public class SelectSessionState extends ControllerState {
  private CalibrationRep calibrationRep;
  private List<SessionRep> sessionReps;

  public SelectSessionState(CalibrationRep calibrationRep,
      List<SessionRep> sessionReps) {
    super(State.SELECT_SESSION);
    this.calibrationRep = calibrationRep;
    this.sessionReps = sessionReps;
  }

  @JsonProperty
  public CalibrationRep getCalibrationRep() {
    return calibrationRep;
  }

  @JsonProperty
  public List<SessionRep> getSessionReps() {
    return sessionReps;
  }
}
