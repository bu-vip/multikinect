package edu.bu.vip.kinect.controllerv2;

import com.google.inject.AbstractModule;
import edu.bu.vip.kinect.controller.webconsole.DevRedirectHandler;
import edu.bu.vip.kinect.controllerv2.webconsole.api.state.ControllerState;
import edu.bu.vip.kinect.controllerv2.webconsole.StateHandler;
import java.util.Scanner;
import ratpack.guice.Guice;
import ratpack.server.RatpackServer;

public class Controllerv2 {

  public static void main(String[] args) throws Exception {
    RatpackServer server = RatpackServer.start(s -> {
      s.serverConfig(config -> {
        config.port(8080);
      });
      s.registry(Guice.registry(b -> {
        b.module(new AbstractModule() {
          @Override
          protected void configure() {
            bind(StateHandler.class);
            bind(DevRedirectHandler.class);
          }
        });
      }));
      s.handlers(chain -> {
        chain.get(StateHandler.URL_PATH, StateHandler.class);
        chain.get("::.*", DevRedirectHandler.class);
      });
    });

    System.out.println("Press enter to stop");
    Scanner scanner = new Scanner(System.in);
    scanner.nextLine();
    scanner.close();

    server.stop();
  }

  public ControllerState getState() {
    return null;
  }
}
