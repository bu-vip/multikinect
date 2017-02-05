package edu.bu.vip.kinect.controllerv2.webconsole;

import static ratpack.jackson.Jackson.json;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import edu.bu.vip.kinect.controllerv2.Controllerv2;
import edu.bu.vip.kinect.controllerv2.webconsole.api.Calibration;
import edu.bu.vip.kinect.controllerv2.webconsole.api.Recording;
import edu.bu.vip.kinect.controllerv2.webconsole.api.Session;
import ratpack.func.Action;
import ratpack.handling.Chain;
import ratpack.http.Status;

@Singleton
public class SelectCalibrationHandler implements Action<Chain> {
  // TODO(doug) - Consistent CRUD naming style
  private static final String BASE_URL = "_";
  private static final String NEW_CALIBRATION_URL = BASE_URL + "/newCalibration";
  private static final String SELECT_CALIBRATION_URL = BASE_URL + "/selectCalibration/:id";
  private static final String DELETE_CALIBRATION_URL = BASE_URL + "/deleteCalibration/:id";
  private static final String NEW_FRAME_URL = BASE_URL + "/newFrame";
  private static final String DELETE_FRAME_URL = BASE_URL + "/deleteFrame/:id";
  private static final String FINISH_CALIBRATION_URL = BASE_URL + "/finishCalibration";
  private static final String FINISH_FRAME_URL = BASE_URL + "/finishFrame";
  private static final String CREATE_SESSION_URL = BASE_URL + "/createSession";
  private static final String SELECT_SESSION_URL = BASE_URL + "/selectSession/:id";
  private static final String DELETE_SESSION_URL = BASE_URL + "/deleteSession/:id";
  private static final String CANCEL_SELECT_SESSION_URL = BASE_URL + "/cancelSelectSession";
  private static final String NEW_RECORDING_URL = BASE_URL + "/newRecording";
  private static final String DELETE_RECORDING_URL = BASE_URL + "/deleteRecording/:id";
  private static final String FINISH_SESSION_URL = BASE_URL + "/finishSession";
  private static final String STOP_RECORDING_URL = BASE_URL + "/stopRecording";

  private Controllerv2 controllerv2;

  @Inject
  protected SelectCalibrationHandler(Controllerv2 controllerv2) {
    this.controllerv2 = controllerv2;
  }

  @Override
  public void execute(Chain chain) throws Exception {
    chain.post(NEW_CALIBRATION_URL, (context) -> {
      // TODO(doug) - implement
      context.parse(Calibration.class).then(calibration -> {
        controllerv2.newCalibration(calibration.getName());
        context.getResponse().status(Status.OK).send();
      });
    });

    chain.get(SELECT_CALIBRATION_URL, (context) -> {
      // TODO(doug) - implement
      long id = Long.parseLong(context.getPathTokens().get("id"));
      controllerv2.selectCalibration(id);
      context.getResponse().status(Status.OK).send();
    });

    chain.get(DELETE_CALIBRATION_URL, (context) -> {
      // TODO(doug) - implement
      long id = Long.parseLong(context.getPathTokens().get("id"));
      controllerv2.deleteCalibration(id);
      context.getResponse().status(Status.OK).send();
    });

    chain.post(NEW_FRAME_URL, context -> {
      // TODO(doug) - implement

      controllerv2.newCalibrationFrame();
      context.getResponse().status(Status.OK).send();
    });

    chain.get(DELETE_FRAME_URL, context -> {
      // TODO(doug) - implement
      long id = Long.parseLong(context.getPathTokens().get("id"));
      // TODO(doug) - implement
      context.getResponse().status(Status.OK).send();
    });

    chain.post(FINISH_CALIBRATION_URL, context -> {
      // TODO(doug) - implement

      controllerv2.finishNewCalibration();
      context.getResponse().status(Status.OK).send();
    });

    chain.post(FINISH_FRAME_URL, context -> {
      // TODO(doug) - implement
      controllerv2.finishNewCalibrationFrame();
      context.getResponse().status(Status.OK).send();
    });

    chain.post(CREATE_SESSION_URL, context -> {
      // TODO(doug) - implement
      context.parse(Session.class).then(session -> {
        controllerv2.createSession(session.getName());
        context.getResponse().status(Status.OK).send();
      });
    });

    chain.get(SELECT_SESSION_URL, context -> {
      // TODO(doug) - implement
      long id = Long.parseLong(context.getPathTokens().get("id"));
      controllerv2.selectSession(id);
      context.getResponse().status(Status.OK).send();
    });

    chain.get(DELETE_SESSION_URL, context -> {
      // TODO(doug) - implement
      long id = Long.parseLong(context.getPathTokens().get("id"));
      controllerv2.deleteSession(id);
      context.getResponse().status(Status.OK).send();
    });

    chain.post(CANCEL_SELECT_SESSION_URL, context -> {
      // TODO(doug) - implement
      controllerv2.finishSelectSession();
    });

    chain.post(NEW_RECORDING_URL, context -> {
      // TODO(doug) - implement
      context.parse(Recording.class).then(recording -> {
        controllerv2.newRecording(recording.getName());
        context.getResponse().status(Status.OK).send();
      });
    });

    chain.get(DELETE_RECORDING_URL, context -> {
      // TODO(doug) - implement
      long id = Long.parseLong(context.getPathTokens().get("id"));
      controllerv2.deleteRecording(id);
      context.getResponse().status(Status.OK).send();
    });

    chain.post(FINISH_SESSION_URL, context -> {
      // TODO(doug) - implement
      controllerv2.finishSession();
      context.getResponse().status(Status.OK).send();
    });

    chain.post(STOP_RECORDING_URL, context -> {
      // TODO(doug) - implement
      controllerv2.stopRecording();
      context.getResponse().status(Status.OK).send();
    });
  }
}
