package edu.bu.vip.kinect.controllerv2.webconsole.api.state;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.bu.vip.kinect.controllerv2.webconsole.api.Calibration;
import edu.bu.vip.kinect.controllerv2.webconsole.api.Recording;
import edu.bu.vip.kinect.controllerv2.webconsole.api.Session;
import java.util.List;

public class SessionIdleState extends ControllerState {
  private Calibration currentCalibration;
  private Session currentSession;
  private List<Recording> recordings;

  public SessionIdleState(
      Calibration currentCalibration,
      Session currentSession,
      List<Recording> recordings) {
    super(State.SESSION_IDLE);
    this.currentCalibration = currentCalibration;
    this.currentSession = currentSession;
    this.recordings = recordings;
  }

  @JsonProperty
  public Calibration getCurrentCalibration() {
    return currentCalibration;
  }

  @JsonProperty
  public Session getCurrentSession() {
    return currentSession;
  }

  @JsonProperty
  public List<Recording> getRecordings() {
    return recordings;
  }
}
