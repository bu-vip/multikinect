package edu.bu.vip.multikinect.controller.calibration;

import com.google.common.collect.ImmutableList;
import edu.bu.vip.kinect.controller.calibration.Protos.GroupOfFrames;
import edu.bu.vip.multikinect.Protos.Frame;
import java.util.concurrent.Callable;

public interface CalibrationAlgorithm {
  Callable<ImmutableList<GroupOfFrames>> createJob(ImmutableList<Frame> cameraAFrames, ImmutableList<Frame> cameraBFrames);
}
