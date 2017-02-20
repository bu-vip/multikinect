package edu.bu.vip.multikinect.controller.calibration;

import com.google.common.collect.ImmutableList;
import edu.bu.vip.kinect.controller.calibration.Protos.GroupOfFrames;
import edu.bu.vip.multikinect.Protos.Frame;
import edu.bu.vip.multikinect.sync.CoordinateTransform.Transform;
import java.util.concurrent.Callable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BruteForceCalibrationAlgorithm implements CalibrationAlgorithm {

  @Override
  public Callable<ImmutableList<GroupOfFrames>> createJob(ImmutableList<Frame> cameraAFrames,
      ImmutableList<Frame> cameraBFrames) {
    return new Job(cameraAFrames, cameraBFrames);
  }

  private static class Job implements Callable<ImmutableList<GroupOfFrames>> {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final ImmutableList<Frame> cameraAFrames;
    private final ImmutableList<Frame> cameraBFrames;

    public Job(ImmutableList<Frame> cameraAFrames, ImmutableList<Frame> cameraBFrames) {
      this.cameraAFrames = cameraAFrames;
      this.cameraBFrames = cameraBFrames;
    }

    @Override
    public ImmutableList<GroupOfFrames> call() throws Exception {
      GroupOfFrames best = null;
      double bestError = Double.POSITIVE_INFINITY;
      for (int indexA = 0; indexA < cameraAFrames.size(); indexA++) {
        Frame frameA = cameraAFrames.get(indexA);

        // TODO(doug) - Checking NTP time to limit search distance would help

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
              while (indexA + length < cameraAFrames.size() && indexB + length < cameraBFrames
                  .size()
                  && length < 10) {
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
              Transform transform = CalibrationUtils.tranformOfMappings(cameraAFrames, cameraBFrames, ImmutableList.of(gof));
              if (transform.getError() < bestError) {
                best = gof;
                bestError = transform.getError();
              }
            }
          }
        }
      }

      if (best != null) {
        logger.info("Got error: {}", bestError);
      }

      return (best == null ? ImmutableList.of() : ImmutableList.of(best));
    }
  }
}
