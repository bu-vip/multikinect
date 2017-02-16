package edu.bu.vip.multikinect.controllerv2;

import com.google.common.collect.ImmutableList;
import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import edu.bu.vip.kinect.controller.calibration.Protos.Calibration;
import edu.bu.vip.multikinect.controllerv2.camera.CameraModule;
import edu.bu.vip.multikinect.controllerv2.webconsole.DevRedirectHandler;
import edu.bu.vip.multikinect.controllerv2.calibration.CalibrationDataLocation;
import edu.bu.vip.multikinect.controllerv2.calibration.CalibrationManager;
import edu.bu.vip.multikinect.controllerv2.calibration.CalibrationModule;
import edu.bu.vip.multikinect.controllerv2.webconsole.ApiHandler;
import edu.bu.vip.multikinect.controllerv2.webconsole.IPHandler;
import edu.bu.vip.multikinect.controllerv2.webconsole.StateHandler;
import edu.bu.vip.multikinect.controllerv2.webconsole.api.RecordingRep;
import edu.bu.vip.multikinect.controllerv2.webconsole.api.SessionRep;
import java.time.Instant;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ratpack.guice.Guice;
import ratpack.server.RatpackServer;

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

  private final Logger logger = LoggerFactory.getLogger(getClass());

  private State state = State.SELECT_CALIBRATION;
  private Map<Long, Calibration> calibrations = new ConcurrentHashMap<>();
  private Map<Long, SessionRep> sessions = new ConcurrentHashMap<>();
  private RecordingRep newRecordingRep;
  private long currentCalibration = -1;
  private long currentSession = -1;

  private CalibrationManager calibrationManager;

  public static void main(String[] args) throws Exception {
    RatpackServer server = RatpackServer.start(s -> {
      s.serverConfig(config -> {
        config.port(8080);
      });
      s.registry(Guice.registry(b -> {
        b.module(CameraModule.class);
        b.module(CalibrationModule.class);
        b.module(new AbstractModule() {
          @Override
          protected void configure() {
            bind(ApiHandler.class);
            bind(StateHandler.class);
            bind(IPHandler.class);
            bind(CalibrationModule.class);
            bind(DevRedirectHandler.class);
            // TODO(doug) - Make a command line arg
            bindConstant().annotatedWith(CalibrationDataLocation.class)
                .to("/home/doug/Desktop/multikinect/calibration");
          }
        });
      }));
      s.handlers(chain -> {
        chain.insert(ApiHandler.class);
        chain.get(StateHandler.URL_PATH, StateHandler.class);
        chain.get(IPHandler.URL_PATH, IPHandler.class);
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
  public Controller(CalibrationManager calibrationManager) {
    this.calibrationManager = calibrationManager;
  }

  public State getState() {
    return this.state;
  }

  public ImmutableList<Calibration> getCalibrations() {
    return ImmutableList.copyOf(calibrations.values());
  }

  public Calibration getCurrentCalibration() {
    if (currentCalibration == -1) {
      return calibrationManager.getCalibration();
    } else {
      return calibrations.get(currentCalibration);
    }
  }

  public ImmutableList<SessionRep> getSessions() {
    return ImmutableList.copyOf(sessions.values());
  }

  public SessionRep getCurrentSession() {
    return sessions.get(currentSession);
  }

  public RecordingRep getCurrentRecording() {
    return newRecordingRep;
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
    // TODO(doug) - Handle calibration not found
    calibrations.remove(calibrationId);
  }

  public void newCalibrationFrame() {
    // TODO(doug) - Check current state
    state = State.NEW_CALIBRATION_FRAME;

    calibrationManager.startRecording();
  }

  public void finishNewCalibration() {
    // TODO(doug) - Check current state
    state = State.SELECT_CALIBRATION;

    Calibration newCalibration = calibrationManager.finish();
    calibrations.put(newCalibration.getId(), newCalibration);
  }

  public void finishNewCalibrationFrame() {
    // TODO(doug) - Check current state
    state = State.NEW_CALIBRATION;

    calibrationManager.stopRecording();
  }

  public void createSession(String name) {
    logger.info("Creating session: {}", name);
    // TODO(doug) - Check current state
    // TODO(doug) - create new session
    SessionRep newSessionRep = new SessionRep(System.currentTimeMillis(), name, Instant.now(),
        new LinkedList<>());
    sessions.put(newSessionRep.getId(), newSessionRep);
  }

  public void selectSession(long sessionId) {
    logger.info("Selecting session: {}", sessionId);
    // TODO(doug) - Check current state
    // TODO(doug) - Handle session not found
    state = State.SESSION_IDLE;
    currentSession = sessionId;
  }

  public void deleteSession(long sessionId) {
    logger.info("Deleting session: {}", sessionId);
    // TODO(doug) - Check current state
    // TODO(doug) - Handle session not found
    sessions.remove(sessionId);
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
    newRecordingRep = new RecordingRep(currentSession, System.currentTimeMillis(), name, Instant.now());
  }

  public void deleteRecording(long recordingId) {
    logger.info("Deleting recording: {}", recordingId);
    // TODO(doug) - implement
    SessionRep sessionRep = sessions.get(currentSession);
    Iterator<RecordingRep> recordings = sessionRep.getRecordingReps().iterator();
    while (recordings.hasNext()) {
      if (recordings.next().getId() == recordingId) {
        recordings.remove();
        break;
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
    sessions.get(currentSession).getRecordingReps().add(newRecordingRep);
    newRecordingRep = null;
  }
}
