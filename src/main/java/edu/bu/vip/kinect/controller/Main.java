package edu.bu.vip.kinect.controller;

import edu.bu.vip.kinect.controller.camera.CameraModule;
import java.util.Scanner;

import edu.bu.vip.kinect.controller.webconsole.DevRedirectHandler;
import edu.bu.vip.kinect.controller.webconsole.ModeHandler;
import edu.bu.vip.kinect.controller.webconsole.WebConsoleModule;
import edu.bu.vip.kinect.controller.webconsole.calibration.CalibrationHandler;
import edu.bu.vip.kinect.controller.webconsole.skeletonfeed.SkeletonFeedServlet;
import edu.bu.vip.kinect.controller.webconsole.state.StateHandler;
import ratpack.guice.Guice;
import ratpack.server.RatpackServer;

public class Main {

  public static void main(String[] args) throws Exception {
    RatpackServer server = RatpackServer.start(s -> {
      s.serverConfig(config -> {
        config.port(8080);
      });
      s.registry(Guice.registry(b -> {
        b.module(WebConsoleModule.class);
        b.module(CameraModule.class);
      }));
      s.handlers(chain -> {
        chain.get(StateHandler.URL_PATH, StateHandler.class);
        chain.post(ModeHandler.URL_PATH, ModeHandler.class);
        chain.post(CalibrationHandler.URL_PATH, CalibrationHandler.class);
        chain.get(SkeletonFeedServlet.URL_PATH, SkeletonFeedServlet.class);
        chain.get("::.*", DevRedirectHandler.class);
      });
    });

    System.out.println("Press enter to stop");
    Scanner scanner = new Scanner(System.in);
    scanner.nextLine();
    scanner.close();

    server.stop();
  }
}
