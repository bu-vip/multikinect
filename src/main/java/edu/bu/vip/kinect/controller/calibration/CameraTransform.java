package edu.bu.vip.kinect.controller.calibration;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;

import org.ejml.data.DenseMatrix64F;

import com.google.common.collect.ImmutableList;
import com.google.common.primitives.Doubles;
import com.roeper.bu.kinect.Protos.Frame;

import edu.bu.vip.kinect.sync.CoordinateTransform;
import edu.bu.vip.kinect.sync.FrameUtils;
import edu.bu.vip.kinect.sync.CoordinateTransform.Transform;

public class CameraTransform implements Callable<Transform> {

  public List<ImmutableList<List<Frame>>> framePairs = new ArrayList<>();

  public CameraTransform() {

  }

  public void addFramePair(List<Frame> framesA, List<Frame> framesB) {
    framePairs.add(ImmutableList.of(framesA, framesB));
  }

  @Override
  public Transform call() throws Exception {

    List<double[]> dataX = new ArrayList<>();
    List<double[]> dataY = new ArrayList<>();

    // Iterate over all frames
    framePairs.forEach((pair) -> {

      // TODO(doug) - handle shifts in time
      int offset = 0;

      Iterator<Frame> itA = pair.get(0).iterator();
      Iterator<Frame> itB = pair.get(1).iterator();

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
    System.out.println("all err: " + transform.getError());
    return transform;
  }

  private static double[] concatList(List<double[]> list) {
    double[][] dataXArray = new double[list.size()][];
    list.toArray(dataXArray);
    double[] allDataX = Doubles.concat(dataXArray);
    return allDataX;
  }
}
