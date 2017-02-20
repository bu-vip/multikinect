package edu.bu.vip.multikinect.controller.calibration;

import com.google.common.collect.ImmutableList;
import com.google.common.math.Stats;
import com.google.common.primitives.Doubles;
import edu.bu.vip.kinect.controller.calibration.Protos.CameraPairCalibration;
import edu.bu.vip.kinect.controller.calibration.Protos.ErrorStats;
import edu.bu.vip.kinect.controller.calibration.Protos.GroupOfFrames;
import edu.bu.vip.multikinect.Protos.Frame;
import edu.bu.vip.multikinect.sync.CoordinateTransform;
import edu.bu.vip.multikinect.sync.CoordinateTransform.Transform;
import edu.bu.vip.multikinect.sync.FrameUtils;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.ejml.data.DenseMatrix64F;

public class BruteForceCalibrationAlgorithm implements CalibrationAlgorithm {

  private ImmutableList<Frame> cameraAFrames;
  private ImmutableList<Frame> cameraBFrames;

  @Override
  public void init(ImmutableList<Frame> cameraAFrames, ImmutableList<Frame> cameraBFrames) {
    this.cameraAFrames = cameraAFrames;
    this.cameraBFrames = cameraBFrames;
  }

  @Override
  public ImmutableList<GroupOfFrames> call() throws Exception {

    // TODO(doug) - implement
    GroupOfFrames best = null;
    double bestError = Double.POSITIVE_INFINITY;

    for (int indexA = 0; indexA < cameraAFrames.size(); indexA++) {
      Frame frameA = cameraAFrames.get(indexA);

      // Check if the frame has a person in it, skip if not
      if (frameA.getSkeletonsCount() > 0) {
        for (int indexB = 0; indexB < cameraBFrames.size(); indexB++) {
          Frame frameB = cameraBFrames.get(indexB);

          // Check if the frame has a person in it, skip if not
          if (frameB.getSkeletonsCount() > 0) {
            // Both frames have people in them

            // Find when the first one stops
            int length = 0;
            Frame lastFrameA = frameA;
            Frame lastFrameB = frameB;
            while (indexA + length < cameraAFrames.size() && indexB + length < cameraBFrames.size()
                && length < 100) {
              lastFrameA = cameraAFrames.get(indexA + length);
              lastFrameB = cameraBFrames.get(indexB + length);
              if (lastFrameA.getSkeletonsCount() > 0 && lastFrameB.getSkeletonsCount() > 0) {
                length++;
              } else {
                break;
              }
            }

            // Evaluate gof performance
            long startA = frameA.getTime();
            long startB = frameB.getTime();
            long endA = lastFrameA.getTime();
            long endB = lastFrameB.getTime();
            GroupOfFrames.Builder builder = GroupOfFrames.newBuilder();
            builder.setStartTimeA(startA);
            builder.setStartTimeB(startB);
            builder.setEndTimeA(endA);
            builder.setEndTimeB(endB);
            GroupOfFrames gof = builder.build();
            double error = getErrorOfFrameMappings(ImmutableList.of(gof));
            if (error < bestError) {
              best = gof;
              bestError = error;
            }
          }
        }
      }
    }

    return (best == null ?ImmutableList.of() : ImmutableList.of(best));
  }

  private double getErrorOfFrameMappings(ImmutableList<GroupOfFrames> groupOfFrames) {
    List<double[]> dataX = new ArrayList<>();
    List<double[]> dataY = new ArrayList<>();

    groupOfFrames.forEach((frame) -> {
      ImmutableList<Frame> framesA = getFramesInInterval(cameraAFrames, frame.getStartTimeA(),
          frame.getEndTimeA());
      ImmutableList<Frame> framesB = getFramesInInterval(cameraBFrames, frame.getStartTimeB(),
          frame.getEndTimeB());

      Iterator<Frame> itA = framesA.iterator();
      Iterator<Frame> itB = framesB.iterator();
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
    return transform.getError();
  }

  private static double[] concatList(List<double[]> list) {
    double[][] dataXArray = new double[list.size()][];
    list.toArray(dataXArray);
    double[] allDataX = Doubles.concat(dataXArray);
    return allDataX;
  }

  private ImmutableList<Frame> getFramesInInterval(ImmutableList<Frame> allFrames, long startTime,
      long endTime) {
    Iterator<Frame> itA = allFrames.iterator();

    int currentIndex = 0;
    int startIndex = 0;
    int endIndex = allFrames.size();
    while (itA.hasNext()) {
      Frame frame = itA.next();
      if (frame.getTime() >= startTime) {
        startIndex = currentIndex;
      } else if (frame.getTime() > endTime) {
        endIndex = currentIndex;
        break;
      }
      currentIndex++;
    }

    return allFrames.subList(startIndex, endIndex);
  }
}
