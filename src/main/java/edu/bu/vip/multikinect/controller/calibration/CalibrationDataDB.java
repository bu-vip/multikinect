package edu.bu.vip.multikinect.controller.calibration;

import com.google.common.collect.ImmutableList;
import edu.bu.vip.multikinect.Protos.Frame;
import java.util.List;

public interface CalibrationDataDB {

  void storeFrame(long calibrationId, long recordingId, String cameraId, Frame frame);

  void storeFrames(long calibrationId, long recordingId, String cameraId, List<Frame> frames);

  ImmutableList<Frame> getAllFrames(long calibrationId, long recordingId, String cameraId);

  ImmutableList<Frame> getAllFramesInInterval(long calibrationId, long recordingId, String cameraId, long startTime, long endTime);
}
