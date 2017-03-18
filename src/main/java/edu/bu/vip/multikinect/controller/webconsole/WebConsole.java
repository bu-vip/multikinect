package edu.bu.vip.multikinect.controller.webconsole;

import edu.bu.vip.multikinect.controller.Controller;
import ratpack.guice.Guice;
import ratpack.server.RatpackServer;
import smartthings.ratpack.protobuf.CacheConfig;
import smartthings.ratpack.protobuf.ProtobufModule;
import smartthings.ratpack.protobuf.ProtobufModule.Config;

public class WebConsole {

  private final Controller controller;
  private RatpackServer server;

  public WebConsole(Controller controller) {
    this.controller = controller;
  }

  public void start() throws Exception {

    if (!controller.isStarted()) {
      throw new RuntimeException("Controller not started");
    }

    server = RatpackServer.start(s -> {
      s.serverConfig(config -> {
        config.port(8080);
      });
      s.registry(Guice.registry(b -> {
        Config protoConfig = new Config();
        protoConfig.setCache(new CacheConfig());
        b.moduleConfig(ProtobufModule.class, protoConfig);
      }));
      s.handlers(chain -> {
        chain.insert(new ApiHandler(controller));
        chain.get(StateHandler.URL_PATH,
            new StateHandler(controller, controller.getCalibrationStore()));
        chain.get(IPHandler.URL_PATH, new IPHandler());
        chain.get(TransformedFeedHandler.URL_PATH,
            new TransformedFeedHandler(controller.getRealTimeManager().getSyncedFrameBus()));
        chain.get("::.*", new DevRedirectHandler());
      });
    });
  }

  public void stop() throws Exception {
    if (server != null) {
      server.stop();
      server = null;
    }
  }
}
