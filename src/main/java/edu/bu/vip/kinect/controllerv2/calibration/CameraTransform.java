package edu.bu.vip.kinect.controllerv2.calibration;

import com.google.common.collect.ImmutableList;
import com.google.common.primitives.Doubles;
import com.roeper.bu.kinect.Protos.Frame;
import edu.bu.vip.kinect.controller.calibration.Protos.CameraPairCalibration;
import edu.bu.vip.kinect.controller.calibration.Protos.GroupOfFrames;
import edu.bu.vip.kinect.sync.CoordinateTransform;
import edu.bu.vip.kinect.sync.CoordinateTransform.Transform;
import edu.bu.vip.kinect.sync.FrameUtils;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import org.ejml.data.DenseMatrix64F;


public class CameraTransform implements Callable<CameraPairCalibration> {

  private final String cameraA;
  private final String cameraB;
  private final CalibrationDataDB calibrationDataDB;
  private final List<GroupOfFrames> frames = new ArrayList<>();

  public CameraTransform(String cameraA, String cameraB, CalibrationDataDB calibrationDataDB) {
    this.cameraA = cameraA;
    this.cameraB = cameraB;
    this.calibrationDataDB = calibrationDataDB;
  }

  private static double[] concatList(List<double[]> list) {
    double[][] dataXArray = new double[list.size()][];
    list.toArray(dataXArray);
    double[] allDataX = Doubles.concat(dataXArray);
    return allDataX;
  }

  public String getCameraA() {
    return cameraA;
  }

  public String getCameraB() {
    return cameraB;
  }

  public void addFrame(GroupOfFrames frame) {
    frames.add(frame);
  }

  @Override
  public CameraPairCalibration call() throws Exception {

    List<double[]> dataX = new ArrayList<>();
    List<double[]> dataY = new ArrayList<>();

    // Iterate over all frames
    frames.forEach((frame) -> {

      // TODO(doug) - handle shifts in time
      int offset = 0;

      // Get the frame data
      ImmutableList<Frame> framesA = calibrationDataDB
          .getAllFramesInInterval(frame.getCameraA(), frame.getStartTimeA(), frame.getEndTimeA());
      ImmutableList<Frame> framesB = calibrationDataDB
          .getAllFramesInInterval(frame.getCameraB(), frame.getStartTimeB(), frame.getEndTimeB());

      Iterator<Frame> itA = framesA.iterator();
      Iterator<Frame> itB = framesB.iterator();

      int globalFrameIndex = 0;
      while (globalFrameIndex + offset < 0) {
        itA.next();
        globalFrameIndex++;
      }

      while (itA.hasNext() && itB.hasNext()) {
        Frame frameA = itA.next();
        Frame frameB = itB.next();

        if (frameA.getSkeletonsCount() != frameB.getSkeletonsCount()
            || frameA.getSkeletonsCount() == 0) {
          continue;
        }

        boolean[] jointMask =
            FrameUtils.joinJointMasks(FrameUtils.jointMasks(frameA), FrameUtils.jointMasks(frameB));

        double[] dataA = FrameUtils.jointMatrix(frameA, jointMask);
        dataX.add(dataA);
        double[] dataB = FrameUtils.jointMatrix(frameB, jointMask);
        dataY.add(dataB);
      }

    });

    // Concatenate all data point sets into a single array
    double[] allDataX = concatList(dataX);
    double[] allDataY = concatList(dataY);

    // Create matrices
    DenseMatrix64F matX = new DenseMatrix64F(allDataX.length / 3, 3, true, allDataX);
    DenseMatrix64F matY = new DenseMatrix64F(allDataY.length / 3, 3, true, allDataY);

    // Calculate transform
    Transform transform = CoordinateTransform.calculateTransform(matX, matY);

    // Build the result
    CameraPairCalibration.Builder builder = CameraPairCalibration.newBuilder();
    builder.setCameraA(cameraA);
    builder.setCameraB(cameraB);
    builder.addAllTransform(Doubles.asList(transform.getTransform().data));
    builder.setError(transform.getError());
    return builder.build();
  }
}
