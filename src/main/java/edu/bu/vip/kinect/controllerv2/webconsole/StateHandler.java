package edu.bu.vip.kinect.controllerv2.webconsole;

import static ratpack.jackson.Jackson.json;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import edu.bu.vip.kinect.controllerv2.Controllerv2;
import ratpack.handling.Context;
import ratpack.handling.Handler;

@Singleton
public class StateHandler implements Handler {
  public static final String URL_PATH = "_/state";

  private Controllerv2 controller;

  @Inject
  protected StateHandler(Controllerv2 controller) {
    this.controller = controller;
  }

  @Override
  public void handle(Context ctx) throws Exception {
    ctx.render(json(controller.getState()));
  }
}
