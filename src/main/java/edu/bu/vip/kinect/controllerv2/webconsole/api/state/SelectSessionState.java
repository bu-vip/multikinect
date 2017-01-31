package edu.bu.vip.kinect.controllerv2.webconsole.api.state;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.bu.vip.kinect.controllerv2.webconsole.api.Calibration;
import edu.bu.vip.kinect.controllerv2.webconsole.api.Session;
import java.util.List;

public class SelectSessionState extends ControllerState {
  private Calibration currentCalibration;
  private List<Session> sessions;

  public SelectSessionState(Calibration currentCalibration,
      List<Session> sessions) {
    super(State.SELECT_SESSION);
    this.currentCalibration = currentCalibration;
    this.sessions = sessions;
  }

  @JsonProperty
  public Calibration getCurrentCalibration() {
    return currentCalibration;
  }

  @JsonProperty
  public List<Session> getSessions() {
    return sessions;
  }
}
