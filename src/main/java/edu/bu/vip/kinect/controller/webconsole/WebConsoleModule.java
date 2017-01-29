package edu.bu.vip.kinect.controller.webconsole;

import com.google.inject.AbstractModule;

import edu.bu.vip.kinect.controller.ControllerService;
import edu.bu.vip.kinect.controller.webconsole.calibration.CalibrationHandler;
import edu.bu.vip.kinect.controller.webconsole.skeletonfeed.SkeletonFeedServlet;
import edu.bu.vip.kinect.controller.webconsole.state.StateHandler;

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