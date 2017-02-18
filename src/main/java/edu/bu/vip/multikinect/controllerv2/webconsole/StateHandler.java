package edu.bu.vip.multikinect.controllerv2.webconsole;

import static ratpack.jackson.Jackson.json;

import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import edu.bu.vip.kinect.controller.calibration.Protos;
import edu.bu.vip.multikinect.controllerv2.Controller;
import edu.bu.vip.multikinect.controllerv2.calibration.CalibrationLoader;
import edu.bu.vip.multikinect.controllerv2.calibration.CalibrationStore;
import edu.bu.vip.multikinect.controllerv2.webconsole.api.CalibrationRep;
import edu.bu.vip.multikinect.controllerv2.webconsole.api.state.ControllerState;
import edu.bu.vip.multikinect.controllerv2.webconsole.api.state.NewCalibrationFrameState;
import edu.bu.vip.multikinect.controllerv2.webconsole.api.state.NewCalibrationState;
import edu.bu.vip.multikinect.controllerv2.webconsole.api.state.RecordingDataState;
import edu.bu.vip.multikinect.controllerv2.webconsole.api.state.SelectCalibrationState;
import edu.bu.vip.multikinect.controllerv2.webconsole.api.state.SelectSessionState;
import edu.bu.vip.multikinect.controllerv2.webconsole.api.state.SessionIdleState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ratpack.handling.Context;
import ratpack.handling.Handler;

@Singleton
public class StateHandler implements Handler {

  public static final String URL_PATH = "_/state";

  private final Logger logger = LoggerFactory.getLogger(getClass());
  private Controller controller;
  private CalibrationStore calibrationStore;

  @Inject
  protected StateHandler(Controller controller, CalibrationStore calibrationStore) {
    this.controller = controller;
    this.calibrationStore = calibrationStore;
  }

  @Override
  public void handle(Context ctx) throws Exception {
    ControllerState state = null;
    switch (controller.getState()) {
      case SELECT_CALIBRATION: {
        // NOTE(doug) - Could do some caching here, if needed
        ImmutableList.Builder<CalibrationRep> builder = ImmutableList.builder();
        for (Protos.Calibration calibration : calibrationStore.getCalibrations()) {
          builder.add(new CalibrationRep(calibration));
        }
        state = new SelectCalibrationState(builder.build());
        break;
      }

      case NEW_CALIBRATION: {
        // TODO(doug) - implement
        state = new NewCalibrationState(new CalibrationRep(controller.getCurrentCalibration()));
        break;
      }

      case NEW_CALIBRATION_FRAME: {
        // TODO(doug) - implement
        state = new NewCalibrationFrameState(new CalibrationRep(controller.getCurrentCalibration()));
        break;
      }

      case SELECT_SESSION: {
        // TODO(doug) - implement
        state = new SelectSessionState(new CalibrationRep(controller.getCurrentCalibration()),
            controller.getSessions());
        break;
      }

      case SESSION_IDLE: {
        // TODO(doug) - implement
        state = new SessionIdleState(new CalibrationRep(controller.getCurrentCalibration()),
            controller.getCurrentSession());
        break;
      }

      case RECORDING_DATA: {
        // TODO(doug) - implement
        state = new RecordingDataState(new CalibrationRep(controller.getCurrentCalibration()),
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
