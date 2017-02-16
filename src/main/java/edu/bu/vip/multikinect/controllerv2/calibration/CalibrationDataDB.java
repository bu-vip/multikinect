package edu.bu.vip.multikinect.controllerv2.calibration;

import com.google.common.collect.ImmutableList;
import edu.bu.vip.multikinect.Protos.Frame;
import java.util.List;

public interface CalibrationDataDB {

  void storeFrame(String cameraId, Frame frame);

  void storeFrames(String cameraId, List<Frame> frames);

  ImmutableList<Frame> getAllFrames(String cameraId);

  ImmutableList<Frame> getAllFramesInInterval(String cameraId, long startTime, long endTime);
}
