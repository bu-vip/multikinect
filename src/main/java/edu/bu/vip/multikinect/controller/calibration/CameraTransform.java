package edu.bu.vip.multikinect.controller.calibration;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.math.Stats;
import com.google.common.primitives.Doubles;
import edu.bu.vip.kinect.controller.calibration.Protos.Calibration;
import edu.bu.vip.kinect.controller.calibration.Protos.CameraPairCalibration;
import edu.bu.vip.kinect.controller.calibration.Protos.ErrorStats;
import edu.bu.vip.kinect.controller.calibration.Protos.GroupOfFrames;
import edu.bu.vip.kinect.controller.calibration.Protos.Recording;
import edu.bu.vip.multikinect.Protos.Frame;
import edu.bu.vip.multikinect.sync.CoordinateTransform.Transform;
import java.util.List;
import java.util.concurrent.Callable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class CameraTransform implements Callable<CameraPairCalibration> {

  private final Logger logger = LoggerFactory.getLogger(getClass());
  private final Calibration calibration;
  private final String cameraA;
  private final String cameraB;
  private final CalibrationDataStore calibrationDataStore;

  public CameraTransform(Calibration calibration,
      String cameraA,
      String cameraB,
      CalibrationDataStore calibrationDataStore) {
    this.calibration = calibration;
    this.cameraA = cameraA;
    this.cameraB = cameraB;
    this.calibrationDataStore = calibrationDataStore;
  }

  public String getCameraA() {
    return cameraA;
  }

  public String getCameraB() {
    return cameraB;
  }

  @Override
  public CameraPairCalibration call() throws Exception {
    // Build a mapping of frames
    ImmutableList.Builder<Frame> allFramesA = ImmutableList.builder();
    ImmutableList.Builder<Frame> allFramesB = ImmutableList.builder();
    for (Recording recording : calibration.getRecordingsList()) {
      for (GroupOfFrames gof : recording.getGofsList()) {
        // Check that the GOF is for this pair of cameras
        if (ImmutableSet.of(gof.getCameraA(), gof.getCameraB())
            .equals(ImmutableSet.of(cameraA, cameraB))) {
          allFramesA.addAll(calibrationDataStore.getAllFramesInInterval(calibration.getId(),
              recording.getId(),
              cameraA,
              gof.getStartTimeA(),
              gof.getEndTimeA()));
          allFramesB.addAll(calibrationDataStore.getAllFramesInInterval(calibration.getId(),
              recording.getId(),
              cameraB,
              gof.getStartTimeB(),
              gof.getEndTimeB()));
        }
      }
    }

    // Calculate transform
    Transform transform = CalibrationUtils
        .transformFromFramePairs(allFramesA.build(), allFramesB.build());

    CameraPairCalibration.Builder builder = CameraPairCalibration.newBuilder();
    builder.setCameraA(cameraA);
    builder.setCameraB(cameraB);
    builder.addAllTransform(Doubles.asList(transform.getTransform().data));
    builder.setError(transform.getError());

    ErrorStats.Builder errorBuilder = ErrorStats.newBuilder();
    List<Double> errorsList = Doubles.asList(transform.getErrors().data);
    Stats stats = Stats.of(errorsList);
    errorBuilder.setMax(stats.max());
    errorBuilder.setMin(stats.min());
    errorBuilder.setMean(stats.mean());
    errorBuilder.setStddev(stats.populationStandardDeviation());
    errorBuilder.addAllErrors(errorsList);
    builder.setErrorStats(errorBuilder);

    return builder.build();
  }
}
