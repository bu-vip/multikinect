package edu.bu.vip.multikinect.controllerv2.webconsole.api.state;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.bu.vip.multikinect.controllerv2.Controller.State;

public class ControllerState {
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
