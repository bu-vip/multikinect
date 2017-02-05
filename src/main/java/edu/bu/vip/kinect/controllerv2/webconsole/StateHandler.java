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
        state = new SelectCalibrationState(controller.getCalibrations());
        break;
      }

      case NEW_CALIBRATION: {
        // TODO(doug) - implement
        state = new NewCalibrationState(controller.getCurrentCalibration());
        break;
      }

      case NEW_CALIBRATION_FRAME: {
        // TODO(doug) - implement
        state = new NewCalibrationFrameState(controller.getCurrentCalibration());
        break;
      }

      case SELECT_SESSION: {
        // TODO(doug) - implement
        state = new SelectSessionState(controller.getCurrentCalibration(),
            controller.getSessions());
        break;
      }

      case SESSION_IDLE: {
        // TODO(doug) - implement
        state = new SessionIdleState(controller.getCurrentCalibration(),
            controller.getCurrentSession());
        break;
      }

      case RECORDING_DATA: {
        // TODO(doug) - implement
        state = new RecordingDataState(controller.getCurrentCalibration(),
            controller.getCurrentSession(), controller.getCurrentRecording());
        break;
      }

      default:
        // TODO(doug) - handle
        break;
    }

    ctx.render(json(state));
  }
}
