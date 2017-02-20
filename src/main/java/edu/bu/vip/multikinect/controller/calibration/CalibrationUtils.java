package edu.bu.vip.multikinect.controller.calibration;

import com.google.common.collect.ImmutableList;
import com.google.common.primitives.Doubles;
import edu.bu.vip.kinect.controller.calibration.Protos.GroupOfFrames;
import edu.bu.vip.multikinect.Protos.Frame;
import edu.bu.vip.multikinect.sync.CoordinateTransform;
import edu.bu.vip.multikinect.sync.CoordinateTransform.Transform;
import edu.bu.vip.multikinect.sync.FrameUtils;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.ejml.data.DenseMatrix64F;

public class CalibrationUtils {

  public static Transform tranformOfMappings(ImmutableList<Frame> cameraAFrames,
      ImmutableList<Frame> cameraBFrames, ImmutableList<GroupOfFrames> groupOfFrames)
      throws Exception {

    ImmutableList.Builder<Frame> framesA = ImmutableList.builder();
    ImmutableList.Builder<Frame> framesB = ImmutableList.builder();
    for (GroupOfFrames frame : groupOfFrames) {
      ImmutableList<Frame> gofA = getFramesInInterval(cameraAFrames, frame.getStartTimeA(),
          frame.getEndTimeA());
      ImmutableList<Frame> gofB = getFramesInInterval(cameraBFrames, frame.getStartTimeB(),
          frame.getEndTimeB());

      framesA.addAll(gofA);
      framesB.addAll(gofB);
    }

    return transformFromFramePairs(framesA.build(), framesB.build());
  }

  public static Transform transformFromFramePairs(ImmutableList<Frame> framesA,
      ImmutableList<Frame> framesB) {
    List<double[]> dataX = new ArrayList<>();
    List<double[]> dataY = new ArrayList<>();

    Iterator<Frame> itA = framesA.iterator();
    Iterator<Frame> itB = framesB.iterator();
    while (itA.hasNext() && itB.hasNext()) {
      Frame frameA = itA.next();
      Frame frameB = itB.next();

      if (frameA.getSkeletonsCount() != frameB.getSkeletonsCount()
          || frameA.getSkeletonsCount() == 0) {
        //throw new RuntimeException("Invalid pairing");
        continue;
      }

      boolean[] jointMask =
          FrameUtils
              .joinJointMasks(FrameUtils.jointMasks(frameA),
                  FrameUtils.jointMasks(frameB));

      double[] dataA = FrameUtils.jointMatrix(frameA, jointMask);
      double[] dataB = FrameUtils.jointMatrix(frameB, jointMask);
      dataX.add(dataA);
      dataY.add(dataB);
    }

    // Concatenate all data point sets into a single array
    double[] allDataX = concatList(dataX);
    double[] allDataY = concatList(dataY);

    // Create matrices
    DenseMatrix64F matX = new DenseMatrix64F(allDataX.length / 3, 3, true, allDataX);
    DenseMatrix64F matY = new DenseMatrix64F(allDataY.length / 3, 3, true, allDataY);

    // Calculate transform
    Transform transform = CoordinateTransform.calculateTransform(matX, matY);
    return transform;
  }

  private static double[] concatList(List<double[]> list) {
    double[][] dataXArray = new double[list.size()][];
    list.toArray(dataXArray);
    double[] allDataX = Doubles.concat(dataXArray);
    return allDataX;
  }

  private static ImmutableList<Frame> getFramesInInterval(ImmutableList<Frame> allFrames,
      long startTime,
      long endTime) {
    Iterator<Frame> itA = allFrames.iterator();

    int currentIndex = 0;
    int startIndex = -1;
    int endIndex = allFrames.size();
    while (itA.hasNext()) {
      Frame frame = itA.next();
      if (frame.getTime() >= startTime && startIndex == -1) {
        startIndex = currentIndex;
      } else if (frame.getTime() > endTime) {
        endIndex = currentIndex;
        break;
      }
      currentIndex++;
    }

    if (startIndex == -1) {
      return ImmutableList.of();
    }

    return allFrames.subList(startIndex, endIndex);
  }
}
