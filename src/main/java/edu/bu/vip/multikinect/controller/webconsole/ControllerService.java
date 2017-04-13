package edu.bu.vip.multikinect.controller.webconsole;

import com.google.inject.Inject;
import edu.bu.vip.multikinect.controller.Controller;
import ratpack.service.Service;
import ratpack.service.StartEvent;
import ratpack.service.StopEvent;

public class ControllerService implements Service {
  private final Controller controller;

  @Inject
  protected ControllerService(Controller controller) {
    this.controller = controller;
  }

  public void onStart(StartEvent event) {
    try {
      this.controller.start();
    } catch (Exception e) {
      // TODO(doug)
      e.printStackTrace();
    }
  }

  public void onStop(StopEvent event) {
    try {
      this.controller.stop();
    } catch (Exception e) {
      // TODO(doug)
      e.printStackTrace();
    }
  }
}
