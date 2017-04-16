package edu.bu.vip.multikinect.controller.webconsole;


import edu.bu.vip.multikinect.controller.Controller;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.io.ZipInputStream;
import ratpack.guice.Guice;
import ratpack.server.RatpackServer;
import smartthings.ratpack.protobuf.CacheConfig;
import smartthings.ratpack.protobuf.ProtobufModule;
import smartthings.ratpack.protobuf.ProtobufModule.Config;

public class WebConsole {
  private static final String WEB_FILES_ZIP = "webfiles.zip";
  private static final String WEB_FILES_DIR = "webfiles";

  private final Controller controller;
  private boolean enableDevRedirect = false;
  private RatpackServer server;
  private final File dataDir;
  private final File webRoot;

  public WebConsole(Controller controller, String dataDir) {
    this.controller = controller;
    this.dataDir = new File(dataDir);
    this.webRoot = new File(dataDir, WEB_FILES_DIR);
  }

  public void setEnableDevRedirect(boolean enableDevRedirect) {
    this.enableDevRedirect = enableDevRedirect;
  }

  public void start() throws Exception {

    if (!controller.isStarted()) {
      throw new RuntimeException("Controller not started");
    }

    if (!enableDevRedirect) {
      // Unzip dist resource and put into data dir
      InputStream stream = getClass().getResourceAsStream("/main/javascript/client/dist.zip");
      File zipDest = new File(dataDir, WEB_FILES_ZIP);
      zipDest.delete();
      Files.copy(stream, zipDest.toPath());

      webRoot.delete();

      ZipFile file = new ZipFile(zipDest);
      file.extractAll(webRoot.getAbsolutePath());
    }

    server = RatpackServer.start(s -> {
      s.serverConfig(config -> {
        config.port(8080);
        config.baseDir(webRoot);
      });
      s.registry(Guice.registry(b -> {
        Config protoConfig = new Config();
        protoConfig.setCache(new CacheConfig());
        b.moduleConfig(ProtobufModule.class, protoConfig);
      }));
      s.handlers(chain -> {
        chain.all(handler -> {
          // TODO(doug) - This could be handled better
          handler.getResponse().getHeaders().set("Access-Control-Allow-Origin", "*");
          handler.getResponse().getHeaders()
              .set("Access-Control-Allow-Headers", "x-requested-with, origin, content-type, accept");
          handler.next();
        });
        chain.insert(new ApiHandler(controller));
        chain.get(StateHandler.URL_PATH,
            new StateHandler(controller, controller.getCalibrationManager(),
                controller.getCalibrationStore()));
        chain.get(IPHandler.URL_PATH, new IPHandler());
        chain.get(TransformedFeedHandler.URL_PATH,
            new TransformedFeedHandler(controller.getRealTimeManager().getSyncedFrameBus()));
        if (enableDevRedirect) {
          chain.get("::.*", new DevRedirectHandler());
        } else {
          chain.files(f -> f.dir("dist").indexFiles("index.html"));
        }
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
