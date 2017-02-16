package edu.bu.vip.multikinect.controllerv2.webconsole.api.state;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.bu.vip.multikinect.controllerv2.Controller.State;
import edu.bu.vip.multikinect.controllerv2.webconsole.api.CalibrationRep;
import edu.bu.vip.multikinect.controllerv2.webconsole.api.SessionRep;
import java.util.List;

public class SelectSessionState extends ControllerState {
  private CalibrationRep calibration;
  private List<SessionRep> sessions;

  public SelectSessionState(CalibrationRep calibration,
      List<SessionRep> sessions) {
    super(State.SELECT_SESSION);
    this.calibration = calibration;
    this.sessions = sessions;
  }

  @JsonProperty
  public CalibrationRep getCalibration() {
    return calibration;
  }

  @JsonProperty
  public List<SessionRep> getSessions() {
    return sessions;
  }
}
