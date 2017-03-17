package edu.bu.vip.multikinect.controller;

import com.google.common.collect.ImmutableList;
import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import edu.bu.vip.kinect.controller.calibration.Protos.Calibration;
import edu.bu.vip.kinect.controller.data.Protos.Recording;
import edu.bu.vip.kinect.controller.data.Protos.Session;
import edu.bu.vip.multikinect.controller.calibration.CalibrationDataStore;
import edu.bu.vip.multikinect.controller.calibration.CalibrationManager;
import edu.bu.vip.multikinect.controller.calibration.CalibrationModule;
import edu.bu.vip.multikinect.controller.calibration.FileCalibrationDataStore;
import edu.bu.vip.multikinect.controller.camera.CameraManager;
import edu.bu.vip.multikinect.controller.camera.CameraModule;
import edu.bu.vip.multikinect.controller.data.FileSessionDataStore;
import edu.bu.vip.multikinect.controller.data.SessionDataStore;
import edu.bu.vip.multikinect.controller.plugin.Plugin;
import edu.bu.vip.multikinect.controller.realtime.RealTimeManager;
import edu.bu.vip.multikinect.controller.realtime.RealtimeModule;
import edu.bu.vip.multikinect.controller.webconsole.ApiHandler;
import edu.bu.vip.multikinect.controller.webconsole.DevRedirectHandler;
import edu.bu.vip.multikinect.controller.webconsole.IPHandler;
import edu.bu.vip.multikinect.controller.webconsole.StateHandler;
import edu.bu.vip.multikinect.controller.webconsole.TransformedFeedHandler;
import edu.bu.vip.multikinect.util.TimestampUtils;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ratpack.guice.Guice;
import ratpack.server.RatpackServer;
import smartthings.ratpack.protobuf.CacheConfig;
import smartthings.ratpack.protobuf.ProtobufModule;
import smartthings.ratpack.protobuf.ProtobufModule.Config;

@Singleton
public class Controller {

  public enum State {
    SELECT_CALIBRATION,
    NEW_CALIBRATION,
    NEW_CALIBRATION_FRAME,
    SELECT_SESSION,
    SESSION_IDLE,
    RECORDING_DATA
  }

  public static final int GRPC_PORT = 45555;

  private final Logger logger = LoggerFactory.getLogger(getClass());

  private State state = State.SELECT_CALIBRATION;
  private long currentCalibration = -1;
  private long currentSession = -1;
  private Recording newRecording;

  private Server grpcServer;
  private boolean started = false;
  private CameraManager cameraManager;
  private CalibrationManager calibrationManager;
  private CalibrationDataStore calibrationStore;
  private RealTimeManager realTimeManager;
  private SessionDataStore sessionDataStore;
  private Map<String, Plugin> plugins = new HashMap<>();

  public static void main(String[] args) throws Exception {
    RatpackServer server = RatpackServer.start(s -> {
      s.serverConfig(config -> {
        config.port(8080);
      });
      s.registry(Guice.registry(b -> {
        b.module(CameraModule.class);
        b.module(CalibrationModule.class);
        b.module(RealtimeModule.class);

        Config protoConfig = new Config();
        protoConfig.setCache(new CacheConfig());
        b.moduleConfig(ProtobufModule.class, protoConfig);

        b.module(new AbstractModule() {
          @Override
          protected void configure() {
            bind(ControllerService.class);
            bind(ApiHandler.class);
            bind(StateHandler.class);
            bind(TransformedFeedHandler.class);
            bind(IPHandler.class);
            bind(CalibrationModule.class);
            bind(DevRedirectHandler.class);
            // TODO(doug) - Make a command line arg
            bind(CalibrationDataStore.class).toInstance(new FileCalibrationDataStore(
                "/home/doug/Desktop/multikinect/calibration"));
            bind(SessionDataStore.class).toInstance(new FileSessionDataStore(
                "/home/doug/Desktop/multikinect/sessions"));
          }
        });
      }));
      s.handlers(chain -> {
        chain.insert(ApiHandler.class);
        chain.get(StateHandler.URL_PATH, StateHandler.class);
        chain.get(IPHandler.URL_PATH, IPHandler.class);
        chain.get(TransformedFeedHandler.URL_PATH, TransformedFeedHandler.class);
        chain.get("::.*", DevRedirectHandler.class);
      });
    });

    System.out.println("Press enter to stop");
    Scanner scanner = new Scanner(System.in);
    scanner.nextLine();
    scanner.close();

    server.stop();
  }

  @Inject
  public Controller(CameraManager cameraManager, CalibrationManager calibrationManager,
      CalibrationDataStore calibrationStore, RealTimeManager realTimeManager,
      SessionDataStore sessionDataStore) {
    this.cameraManager = cameraManager;
    this.calibrationManager = calibrationManager;
    this.calibrationStore = calibrationStore;
    this.realTimeManager = realTimeManager;
    this.sessionDataStore = sessionDataStore;
  }

  public void start() throws Exception {
    if (!started) {
      grpcServer = ServerBuilder.forPort(GRPC_PORT).addService(cameraManager).build();
      grpcServer.start();

      // Start all the plugins
      plugins.forEach((pluginId, plugin) -> {
        try {
          plugin.start();
        } catch (Exception e) {
          logger.warn("Plugin threw exception during start()", e);
        }
      });

      started = true;
    }
  }

  public void stop() throws Exception {
    if (started) {
      // Stop all plugins
      plugins.forEach((pluginId, plugin) -> {
        try {
          plugin.stop();
        } catch (Exception e) {
          logger.warn("Plugin threw exception during start()", e);
        }
      });

      grpcServer.shutdown();
      started = false;
    }
  }

  public State getState() {
    return this.state;
  }

  public ImmutableList<Calibration> getCalibrations() {
    return calibrationStore.getCalibrations();
  }

  public Calibration getCurrentCalibration() {
    if (currentCalibration == -1) {
      return calibrationManager.getCalibration();
    } else {
      Optional<Calibration> optCal = calibrationStore.getCalibration(currentCalibration);
      if (!optCal.isPresent()) {
        logger.error("Could not retrieve current calibration");
        throw new RuntimeException("Could not retrieve current calibration");
      }

      return optCal.get();
    }
  }

  public ImmutableList<Session> getSessions() {
    return sessionDataStore.getSessions();
  }

  public Session getCurrentSession() {
    Optional<Session> optSes = sessionDataStore.getSession(currentSession);
    if (!optSes.isPresent()) {
      logger.error("Could not retrieve current session");
      throw new RuntimeException("Could not retrieve current session");
    }

    return optSes.get();
  }

  public Recording getCurrentRecording() {
    return newRecording;
  }

  public void newCalibration(String name) {
    // TODO(doug) - Check current state
    state = State.NEW_CALIBRATION;

    // TODO(doug) - notes
    calibrationManager.start(name, "");
  }

  public void selectCalibration(long calibrationId) {
    logger.info("Selecting calibration: {}", calibrationId);
    // TODO(doug) - Check current state
    // TODO(doug) - Handle calibration not found
    state = State.SELECT_SESSION;
    currentCalibration = calibrationId;
  }

  public void deleteCalibration(long calibrationId) {
    logger.info("Deleting calibration: {}", calibrationId);
    // TODO(doug) - Check current state
    calibrationStore.deleteCalibration(calibrationId);
  }

  public void newCalibrationFrame() {
    // TODO(doug) - Check current state
    state = State.NEW_CALIBRATION_FRAME;

    calibrationManager.startRecording();
  }

  public void finishNewCalibration() {
    // TODO(doug) - Check current state
    state = State.SELECT_CALIBRATION;

    calibrationManager.finish();
  }

  public void finishNewCalibrationFrame() {
    // TODO(doug) - Check current state
    state = State.NEW_CALIBRATION;

    calibrationManager.stopRecording();
  }

  public void deleteCalibrationRecording(long id) {
    // TODO(doug) - check state

    calibrationManager.deleteRecording(id);
  }

  public void createSession(String name) {
    logger.info("Creating session: {}", name);
    // TODO(doug) - Check current state
    // TODO(doug) - create new session
    Session.Builder builder = Session.newBuilder();
    builder.setId(System.currentTimeMillis());
    builder.setDateCreated(TimestampUtils.now());
    builder.setName(name);
    sessionDataStore.createSession(builder.build());
  }

  public void selectSession(long sessionId) {
    logger.info("Selecting session: {}", sessionId);
    // TODO(doug) - Check current state
    // TODO(doug) - Handle session not found

    Optional<Calibration> optCal = calibrationStore.getCalibration(currentCalibration);
    if (optCal.isPresent()) {
      state = State.SESSION_IDLE;
      currentSession = sessionId;
      realTimeManager.start(optCal.get());
    } else {
      logger.warn("Couldn't get current calibration from store, id: {}", currentCalibration);
      state = State.SELECT_CALIBRATION;
    }
  }

  public void deleteSession(long sessionId) {
    logger.info("Deleting session: {}", sessionId);
    // TODO(doug) - Check current state
    sessionDataStore.deleteSession(sessionId);
  }

  public void finishSelectSession() {
    logger.info("Canceling session selection");
    // TODO(doug) - Check current state
    // TODO(doug) - implement
    state = State.SELECT_CALIBRATION;
    currentCalibration = -1;
  }

  public void newRecording(String name) {
    logger.info("Creating new recording");
    // TODO(doug) - Check current state
    state = State.RECORDING_DATA;
    newRecording = realTimeManager.startRecording(currentSession, name, "");

    final long sessionId = currentSession;
    final long recordingId = newRecording.getId();
    plugins.forEach((pluginId, plugin) -> {
      plugin.recordingStarted(data -> {
        sessionDataStore.storePluginData(sessionId, recordingId, pluginId, data);
      });
    });
  }

  public void deleteRecording(long recordingId) {
    logger.info("Deleting recording: {}", recordingId);
    // TODO(doug) - implement

    Optional<Session> optSes = sessionDataStore.getSession(currentSession);
    if (optSes.isPresent()) {
      Session session = optSes.get();
      for (int i = 0; i < session.getRecordingsCount(); i++) {
        if (session.getRecordings(i).getId() == recordingId) {
          Session.Builder builder = session.toBuilder();
          builder.removeRecordings(i);
          sessionDataStore.updateSession(builder.build());
          break;
        }
      }
    }
  }

  public void finishSession() {
    logger.info("Finishing session: {}", currentSession);
    // TODO(doug) - Check current state
    // TODO(doug) - implement
    state = State.SELECT_SESSION;
    currentSession = -1;
  }

  public void stopRecording() {
    logger.info("Stopping recording");
    // TODO(doug) - Check current state
    // TODO(doug) - implement
    state = State.SESSION_IDLE;
    realTimeManager.stopRecording();
    plugins.forEach((pluginId, plugin) -> {
      plugin.recordingStopped();
    });
  }

  /**
   * Registers a plugin with the controller. Cannot be called after starting the controller.
   */
  public void registerPlugin(String pluginId, Plugin plugin) {
    if (started) {
      logger.error("Tried to register a plugin after starting controller");
      throw new RuntimeException("Can't register plugins after starting controller");
    }

    plugins.put(pluginId, plugin);
  }
}
