package edu.bu.vip.kinect.controller.webconsole.calibration;

import static ratpack.jackson.Jackson.jsonNode;

import com.google.inject.Inject;

import edu.bu.vip.kinect.controller.calibration.CalibrationManager;
import ratpack.handling.Context;
import ratpack.handling.Handler;

public class CalibrationHandler implements Handler {
  public static final String URL_PATH = "_/calibration";
  
  private static final String KEY_RECORDING = "recording";
  
  private final CalibrationManager calibrationManager;
  
  @Inject
  protected CalibrationHandler(CalibrationManager manager) {
    this.calibrationManager = manager;
  }

  @Override
  public void handle(Context ctx) throws Exception {
    ctx.render(ctx.parse(jsonNode()).map((node) -> {
      // Check if the JSON has the key
      if (node.has(KEY_RECORDING)) {
        boolean value = node.get(KEY_RECORDING).asBoolean();
        if (value) {
          this.calibrationManager.startCalibration();
        } else {
          this.calibrationManager.stopCalibration();
        }
      } else {
        ctx.clientError(400);
      }
      
      return "{}";
    }));
  }

}
