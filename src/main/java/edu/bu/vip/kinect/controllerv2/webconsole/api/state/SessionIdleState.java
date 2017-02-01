package edu.bu.vip.kinect.controllerv2.webconsole.api.state;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.bu.vip.kinect.controllerv2.Controllerv2.State;
import edu.bu.vip.kinect.controllerv2.webconsole.api.Calibration;
import edu.bu.vip.kinect.controllerv2.webconsole.api.Recording;
import edu.bu.vip.kinect.controllerv2.webconsole.api.Session;
import java.util.List;

public class SessionIdleState extends ControllerState {
  private Calibration currentCalibration;
  private Session currentSession;

  public SessionIdleState(
      Calibration currentCalibration,
      Session currentSession) {
    super(State.SESSION_IDLE);
    this.currentCalibration = currentCalibration;
    this.currentSession = currentSession;
  }

  @JsonProperty
  public Calibration getCurrentCalibration() {
    return currentCalibration;
  }

  @JsonProperty
  public Session getCurrentSession() {
    return currentSession;
  }
}
