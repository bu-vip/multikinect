package edu.bu.vip.multikinect.controllerv2.webconsole.api.state;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.bu.vip.multikinect.controllerv2.Controller.State;
import edu.bu.vip.multikinect.controllerv2.webconsole.api.CalibrationRep;
import edu.bu.vip.multikinect.controllerv2.webconsole.api.SessionRep;

public class SessionIdleState extends ControllerState {
  private CalibrationRep calibration;
  private SessionRep sessionRep;

  public SessionIdleState(
      CalibrationRep calibration,
      SessionRep sessionRep) {
    super(State.SESSION_IDLE);
    this.calibration = calibration;
    this.sessionRep = sessionRep;
  }

  @JsonProperty
  public CalibrationRep getCalibration() {
    return calibration;
  }

  @JsonProperty
  public SessionRep getSessionRep() {
    return sessionRep;
  }
}
