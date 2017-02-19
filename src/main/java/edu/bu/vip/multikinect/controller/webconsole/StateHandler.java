package edu.bu.vip.multikinect.controller.webconsole;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.protobuf.Message;
import edu.bu.vip.kinect.controller.web.Protos.NewCalibrationFrameState;
import edu.bu.vip.kinect.controller.web.Protos.NewCalibrationState;
import edu.bu.vip.kinect.controller.web.Protos.RecordingDataState;
import edu.bu.vip.kinect.controller.web.Protos.SelectCalibrationState;
import edu.bu.vip.kinect.controller.web.Protos.SelectSessionState;
import edu.bu.vip.kinect.controller.web.Protos.SessionIdleState;
import edu.bu.vip.kinect.controller.web.Protos.State;
import edu.bu.vip.multikinect.controller.Controller;
import edu.bu.vip.multikinect.controller.calibration.CalibrationStore;
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
    Message state = null;
    switch (controller.getState()) {
      case SELECT_CALIBRATION: {
        // NOTE(doug) - Could do some caching here, if needed
        SelectCalibrationState.Builder builder = SelectCalibrationState.newBuilder();
        builder.addAllCalibrations(calibrationStore.getCalibrations());
        builder.setState(State.SELECT_CALIBRATION);
        state = builder.build();

        break;
      }

      case NEW_CALIBRATION: {
        NewCalibrationState.Builder builder = NewCalibrationState.newBuilder();
        builder.setCalibration(controller.getCurrentCalibration());
        builder.setState(State.NEW_CALIBRATION);
        state = builder.build();
        break;
      }

      case NEW_CALIBRATION_FRAME: {
        NewCalibrationFrameState.Builder builder = NewCalibrationFrameState.newBuilder();
        builder.setCalibration(controller.getCurrentCalibration());
        builder.setState(State.NEW_CALIBRATION_FRAME);
        state = builder.build();
        break;
      }

      case SELECT_SESSION: {
        SelectSessionState.Builder builder = SelectSessionState.newBuilder();
        builder.setCalibration(controller.getCurrentCalibration());
        builder.addAllSessions(controller.getSessions());
        builder.setState(State.SELECT_SESSION);
        state = builder.build();
        break;
      }

      case SESSION_IDLE: {
        SessionIdleState.Builder builder = SessionIdleState.newBuilder();
        builder.setCalibration(controller.getCurrentCalibration());
        builder.setSession(controller.getCurrentSession());
        builder.setState(State.SESSION_IDLE);
        state = builder.build();
        break;
      }

      case RECORDING_DATA: {
        RecordingDataState.Builder builder = RecordingDataState.newBuilder();
        builder.setCalibration(controller.getCurrentCalibration());
        builder.setSession(controller.getCurrentSession());
        builder.setRecording(controller.getCurrentRecording());
        builder.setState(State.RECORDING_DATA);
        state = builder.build();
        break;
      }

      default:
        // TODO(doug) - handle
        break;
    }

    ctx.render(state);
  }
}
