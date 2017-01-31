package edu.bu.vip.kinect.controllerv2.webconsole.api.state;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ControllerState {

  public enum State {
    SELECT_CALIBRATION,
    NEW_CALIBRATION,
    SELECT_SESSION,
    SESSION_IDLE,
    RECORDING_DATA
  }

  private State state;

  protected ControllerState() {
    // Jackson
  }

  public ControllerState(State state) {
    this.state = state;
  }

  @JsonProperty
  public State getState() {
    return state;
  }
}
