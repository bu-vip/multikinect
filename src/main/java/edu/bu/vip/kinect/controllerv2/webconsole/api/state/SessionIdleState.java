package edu.bu.vip.kinect.controllerv2.webconsole.api.state;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.bu.vip.kinect.controllerv2.Controller.State;
import edu.bu.vip.kinect.controllerv2.webconsole.api.CalibrationRep;
import edu.bu.vip.kinect.controllerv2.webconsole.api.SessionRep;

public class SessionIdleState extends ControllerState {
  private CalibrationRep calibrationRep;
  private SessionRep sessionRep;

  public SessionIdleState(
      CalibrationRep calibrationRep,
      SessionRep sessionRep) {
    super(State.SESSION_IDLE);
    this.calibrationRep = calibrationRep;
    this.sessionRep = sessionRep;
  }

  @JsonProperty
  public CalibrationRep getCalibrationRep() {
    return calibrationRep;
  }

  @JsonProperty
  public SessionRep getSessionRep() {
    return sessionRep;
  }
}
