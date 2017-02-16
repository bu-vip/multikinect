package edu.bu.vip.multikinect.controller;

import edu.bu.vip.multikinect.controllerv2.camera.CameraManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;
import com.google.inject.Inject;
import com.google.inject.Singleton;
importedu.bu.vip.multikinect.camera.Grpc.RecordOptions;

import edu.bu.vip.kinect.controller.calibration.CalibrationManager;
import io.grpc.Server;
import io.grpc.ServerBuilder;

@Singleton
// TODO(doug) - evaluate thread safety
public class Controller {
  public static final int GRPC_PORT = 45555;

  public enum Mode {
    IDLE, CALIBRATION, REALTIME
  }

  private static final long MILLISECONDS_PER_SECOND = 1000;
  private static final long UPDATE_THREAD_TIMEOUT = MILLISECONDS_PER_SECOND * 1;

  private final Logger logger = LoggerFactory.getLogger(getClass());
  private final RecordingManager recordingManager;
  private final CameraManager cameraManager;
  private final CalibrationManager calibrationManager;
  private Mode currentMode = Mode.IDLE;
  private Server grpcServer;
  private Thread updateThread;

  @Inject
  // TODO(doug) - recording manager
  protected Controller( CameraManager cameraManager, CalibrationManager calibrationManager) {
    this.cameraManager = cameraManager;
    this.recordingManager = null;
    this.calibrationManager = calibrationManager;
  }

  public void start() throws Exception {
    grpcServer = ServerBuilder.forPort(GRPC_PORT).addService(cameraManager).build();
    grpcServer.start();
  }

  public void stop() throws Exception {
    grpcServer.shutdown();
  }

  public void setMode(Mode nextMode) {
    if (nextMode != currentMode) {
      logger.info("Transitioning from mode {} to {}", currentMode, nextMode);
      
      switch (currentMode) {
        case CALIBRATION:
          this.calibrationManager.finishCalibrationSession();
          break;

        default:
          break;
      }

      currentMode = nextMode;

      switch (currentMode) {
        case CALIBRATION:
          this.calibrationManager.beginCalibrationSession();
          break;

        default:
          break;
      }
    }
  }
  
  public Mode getMode() {
    return currentMode;
  }

  public CameraManager getCameraManager() {
    return this.cameraManager;
  }

  private void stopUpdateThread() {
    updateThread.interrupt();
    try {
      updateThread.join(UPDATE_THREAD_TIMEOUT);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  private class CameraUpdater implements Runnable {
    @Override
    public void run() {
      while (!Thread.currentThread().isInterrupted()) {
        // Update the recording manager
        Optional<RecordOptions> optOptions = recordingManager.update();
        // Update camera options if needed
        if (optOptions.isPresent()) {
          cameraManager.updateCameraOptions(optOptions.get());
        }
      }
    }
  }
}
