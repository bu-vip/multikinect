package edu.bu.vip.kinect.controller.webconsole.state;

import static ratpack.jackson.Jackson.json;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import edu.bu.vip.kinect.controller.Controller;
import edu.bu.vip.kinect.controller.calibration.CalibrationManager;
import ratpack.handling.Context;
import ratpack.handling.Handler;

@Singleton
public class StateHandler implements Handler {
  public static final String URL_PATH = "_/state";

  private final Controller controller;
  private final CalibrationManager calibration;

  @Inject
  protected StateHandler(Controller controller, CalibrationManager calibrationManager) {
    this.controller = controller;
    this.calibration = calibrationManager;
  }

  @Override
  public void handle(Context ctx) throws Exception {
    CalibrationState calibrationState =
        new CalibrationState(calibration.isSession(), calibration.getSessionId(),
            calibration.getSessionDataLocation(), calibration.getSessionFrameCount(),
            calibration.isRecording(), calibration.getSessionAccuracy());
    ControllerState controllerState = new ControllerState(controller.getMode(), calibrationState);

    ctx.render(json(controllerState));
  }
}
