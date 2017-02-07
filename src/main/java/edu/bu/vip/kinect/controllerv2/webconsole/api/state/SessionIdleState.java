package edu.bu.vip.kinect.controllerv2.webconsole.api.state;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.bu.vip.kinect.controllerv2.Controller.State;
import edu.bu.vip.kinect.controllerv2.webconsole.api.Calibration;
import edu.bu.vip.kinect.controllerv2.webconsole.api.Session;

public class SessionIdleState extends ControllerState {
  private Calibration calibration;
  private Session session;

  public SessionIdleState(
      Calibration calibration,
      Session session) {
    super(State.SESSION_IDLE);
    this.calibration = calibration;
    this.session = session;
  }

  @JsonProperty
  public Calibration getCalibration() {
    return calibration;
  }

  @JsonProperty
  public Session getSession() {
    return session;
  }
}
