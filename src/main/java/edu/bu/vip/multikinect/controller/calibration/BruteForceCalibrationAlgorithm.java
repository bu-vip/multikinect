package edu.bu.vip.multikinect.controller.calibration;

import com.google.common.collect.ImmutableList;
import edu.bu.vip.kinect.controller.calibration.Protos.GroupOfFrames;
import edu.bu.vip.multikinect.Protos.Frame;
import edu.bu.vip.multikinect.sync.CoordinateTransform.Transform;
import edu.bu.vip.multikinect.util.TimestampUtils;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
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

    private static final int MIN_LENGTH = 50;
    private static final int MAX_LENGTH = 200;
    private static final long MAX_NTP_TIME_DELTA = 1000 * 1;

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final ImmutableList<Frame> cameraAFrames;
    private final ImmutableList<Frame> cameraBFrames;

    public Job(ImmutableList<Frame> cameraAFrames, ImmutableList<Frame> cameraBFrames) {
      this.cameraAFrames = cameraAFrames;
      this.cameraBFrames = cameraBFrames;
    }

    @Override
    public ImmutableList<GroupOfFrames> call() throws Exception {

      logger.info("Starting calculation on {} and {} frames", cameraAFrames.size(), cameraBFrames.size());

      GroupOfFrames best = null;
      Transform bestTransform = null;
      double bestError = Double.POSITIVE_INFINITY;
      for (int indexA = 0; indexA < cameraAFrames.size(); indexA++) {
        Frame frameA = cameraAFrames.get(indexA);
        Instant ntpA = TimestampUtils.from(frameA.getNtpCaptureTime());


        // Check if the frame has a person in it, skip if not
        if (frameA.getSkeletonsCount() > 0) {
          for (int indexB = 0; indexB < cameraBFrames.size(); indexB++) {
            Frame frameB = cameraBFrames.get(indexB);
            Instant ntpB = TimestampUtils.from(frameB.getNtpCaptureTime());

            // Check NTP time to limit search distance
            long ntpDelta = ntpA.until(ntpB, ChronoUnit.MILLIS);
            if (ntpDelta > MAX_NTP_TIME_DELTA) {
              break;
            }

            // Check if the frame has a person in it, skip if not
            if (frameB.getSkeletonsCount() > 0 && Math.abs(ntpDelta) < MAX_NTP_TIME_DELTA) {
              // Both frames have people in them

              // Find when the first one stops
              int length = 0;
              Frame lastFrameA = frameA;
              Frame lastFrameB = frameB;
              while (indexA + length < cameraAFrames.size() && indexB + length < cameraBFrames
                  .size()
                  && length < MAX_LENGTH) {
                lastFrameA = cameraAFrames.get(indexA + length);
                lastFrameB = cameraBFrames.get(indexB + length);
                if (lastFrameA.getSkeletonsCount() > 0 && lastFrameB.getSkeletonsCount() > 0) {
                  length++;
                } else {
                  break;
                }
              }

              // Window must also be longer than minimum
              if (length > MIN_LENGTH) {
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
                Transform transform = CalibrationUtils
                    .tranformOfMappings(cameraAFrames, cameraBFrames, ImmutableList.of(gof));
                if (transform.getError() < bestError) {
                  best = gof;
                  bestError = transform.getError();
                  bestTransform = transform;
                }
              }
            }
          }
        }
      }

      if (best != null) {
        logger.info("Got error: {} on points {}", bestError, bestTransform.getErrors().numRows);
      }

      return (best == null ? ImmutableList.of() : ImmutableList.of(best));
    }
  }
}
