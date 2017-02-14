package edu.bu.vip.kinect.controllerv2.calibration;

import com.google.common.collect.ImmutableList;
import com.roeper.bu.kinect.Protos.Frame;
import java.util.List;

public interface CalibrationDataDB {

  void storeFrame(String cameraId, Frame frame);

  void storeFrames(String cameraId, List<Frame> frames);

  ImmutableList<Frame> getAllFrames(String cameraId);

  ImmutableList<Frame> getAllFramesInInterval(String cameraId, long startTime, long endTime);
}
