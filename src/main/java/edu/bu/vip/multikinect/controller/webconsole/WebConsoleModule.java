package edu.bu.vip.multikinect.controller.webconsole;

import com.google.inject.AbstractModule;

import edu.bu.vip.multikinect.controller.ControllerService;
import edu.bu.vip.multikinect.controller.webconsole.calibration.CalibrationHandler;
import edu.bu.vip.multikinect.controller.webconsole.skeletonfeed.SkeletonFeedServlet;
import edu.bu.vip.multikinect.controller.webconsole.state.StateHandler;
import edu.bu.vip.multikinect.controllerv2.webconsole.DevRedirectHandler;

public class WebConsoleModule extends AbstractModule {
  @Override
  protected void configure() {
    bind(ControllerService.class);
    bind(ModeHandler.class);
    bind(StateHandler.class);
    bind(CalibrationHandler.class);
    bind(SkeletonFeedServlet.class);
    // TODO(doug) -redirect for dev only
    bind(DevRedirectHandler.class);
  }
}