package edu.bu.vip.kinect.controllerv2.webconsole.api.state;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.bu.vip.kinect.controllerv2.Controllerv2.State;
import edu.bu.vip.kinect.controllerv2.webconsole.api.Calibration;
import edu.bu.vip.kinect.controllerv2.webconsole.api.Session;
import java.util.List;

public class SelectSessionState extends ControllerState {
  private Calibration calibration;
  private List<Session> sessions;

  public SelectSessionState(Calibration calibration,
      List<Session> sessions) {
    super(State.SELECT_SESSION);
    this.calibration = calibration;
    this.sessions = sessions;
  }

  @JsonProperty
  public Calibration getCalibration() {
    return calibration;
  }

  @JsonProperty
  public List<Session> getSessions() {
    return sessions;
  }
}
