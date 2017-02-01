package edu.bu.vip.kinect.controllerv2.webconsole;

import static ratpack.jackson.Jackson.json;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.protobuf.Timestamp;
import edu.bu.vip.kinect.controllerv2.Controllerv2;
import edu.bu.vip.kinect.controllerv2.Controllerv2.State;
import edu.bu.vip.kinect.controllerv2.webconsole.api.Calibration;
import edu.bu.vip.kinect.controllerv2.webconsole.api.CalibrationFrame;
import edu.bu.vip.kinect.controllerv2.webconsole.api.Recording;
import edu.bu.vip.kinect.controllerv2.webconsole.api.Session;
import edu.bu.vip.kinect.controllerv2.webconsole.api.state.ControllerState;
import edu.bu.vip.kinect.controllerv2.webconsole.api.state.NewCalibrationFrameState;
import edu.bu.vip.kinect.controllerv2.webconsole.api.state.NewCalibrationState;
import edu.bu.vip.kinect.controllerv2.webconsole.api.state.RecordingDataState;
import edu.bu.vip.kinect.controllerv2.webconsole.api.state.SelectCalibrationState;
import edu.bu.vip.kinect.controllerv2.webconsole.api.state.SelectSessionState;
import edu.bu.vip.kinect.controllerv2.webconsole.api.state.SessionIdleState;
import edu.bu.vip.kinect.util.TimestampUtils;
import java.time.Instant;
import java.util.LinkedList;
import java.util.List;
import ratpack.handling.Context;
import ratpack.handling.Handler;

@Singleton
public class StateHandler implements Handler {

  public static final String URL_PATH = "_/state";

  private Controllerv2 controller;

  @Inject
  protected StateHandler(Controllerv2 controller) {
    this.controller = controller;
  }

  @Override
  public void handle(Context ctx) throws Exception {
    ControllerState state = null;
    switch (controller.getState()) {
      case SELECT_CALIBRATION: {
        // TODO(doug) - implement
        List<Calibration> calibrations = new LinkedList<>();
        calibrations.add(new Calibration(1, "Calibration 1", Instant.now(), null, 0.1));
        state = new SelectCalibrationState(calibrations);
        break;
      }

      case NEW_CALIBRATION: {
        // TODO(doug) - implement
        List<CalibrationFrame> frames = new LinkedList<>();
        frames.add(new CalibrationFrame(1, 0.1, Instant.now()));
        frames.add(new CalibrationFrame(2, 0.3, Instant.now()));
        Calibration calibration = new Calibration(1, "Calibration 1", Instant.now(), null, 0.2);
        state = new NewCalibrationState(calibration);
        break;
      }

      case NEW_CALIBRATION_FRAME: {
        // TODO(doug) - implement
        Calibration calibration = new Calibration(1, "Calibration 1", Instant.now(), null, 0.3);
        state = new NewCalibrationFrameState(calibration);
        break;
      }

      case SELECT_SESSION: {
        // TODO(doug) - implement
        List<Session> sessions = new LinkedList<>();
        List<Recording> recordings = new LinkedList<>();
        recordings.add(new Recording(1, 2, "myRecording", Instant.now()));
        recordings.add(new Recording(1, 3, "recording2", Instant.now()));
        sessions.add(new Session(1, "Session 1", Instant.now(), recordings));
        state = new SelectSessionState(null, sessions);
        break;
      }

      case SESSION_IDLE: {
        // TODO(doug) - implement
        List<Recording> recordings = new LinkedList<>();
        recordings.add(new Recording(1, 2, "myRecording", Instant.now()));
        recordings.add(new Recording(1, 3, "recording2", Instant.now()));
        Session session = new Session(1, "Session 1", Instant.now(), recordings);
        state = new SessionIdleState(null, session);
        break;
      }

      case RECORDING_DATA: {
        // TODO(doug) - implement
        List<Recording> recordings = new LinkedList<>();
        recordings.add(new Recording(1, 2, "myRecording", Instant.now()));
        recordings.add(new Recording(1, 3, "recording2", Instant.now()));
        Session session = new Session(1, "Session 1", Instant.now(), recordings);
        state = new RecordingDataState(null, session,
            new Recording(1, 4, "newRecording", Instant.now()));
        break;
      }

      default:
        // TODO(doug) - handle
        break;
    }

    ctx.render(json(state));
  }
}
