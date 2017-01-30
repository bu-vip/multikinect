package edu.bu.vip.kinect.controllerv2;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ControllerState {

  public enum State {
    IDLE,
    NEW_CALIBRATION_SESSION,
    RECORDING_CALIBRATION_FRAME,
    REALTIME_IDLE,
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
