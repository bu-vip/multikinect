package edu.bu.vip.kinect.controllerv2;

import static ratpack.jackson.Jackson.json;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import edu.bu.vip.kinect.controllerv2.ControllerState.State;
import ratpack.handling.Context;
import ratpack.handling.Handler;

@Singleton
public class StateHandler implements Handler {
  public static final String URL_PATH = "_/state";

  @Inject
  protected StateHandler() {
  }

  @Override
  public void handle(Context ctx) throws Exception {
    ControllerState controllerState = new ControllerState(State.IDLE);

    ctx.render(json(controllerState));
  }
}
