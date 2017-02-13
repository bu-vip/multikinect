package edu.bu.vip.kinect.controllerv2.calibration;

import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import com.roeper.bu.kinect.Protos.Frame;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class InMemoryCalibrationDataDB implements CalibrationDataDB {

  private Map<String, List<Frame>> frames = new HashMap<>();

  @Inject
  protected InMemoryCalibrationDataDB() {

  }

  @Override
  public void storeFrame(String cameraId, Frame frame) {
    if (!frames.containsKey(cameraId)) {
      frames.put(cameraId, new ArrayList<>());
    }
    frames.get(cameraId).add(frame);
  }

  @Override
  public ImmutableList<Frame> getAllFrames(String cameraId) {
    if (!frames.containsKey(cameraId)) {
      // TODO(doug) - evaluate
      throw new RuntimeException("No frames for camera: " + cameraId);
    }

    return ImmutableList.copyOf(frames.get(cameraId));
  }

  @Override
  public ImmutableList<Frame> getAllFramesInInterval(String cameraId, long startTime, long endTime) {
    if (!frames.containsKey(cameraId)) {
      // TODO(doug) - evaluate
      throw new RuntimeException("No frames for camera: " + cameraId);
    }

    // Get frames for camera
    List<Frame> cameraFrames = frames.get(cameraId);
    Iterator<Frame> cameraFrameIt = cameraFrames.iterator();

    ImmutableList.Builder<Frame> builder = ImmutableList.builder();
    // Seek to first frame
    while (cameraFrameIt.hasNext()) {
      Frame next = cameraFrameIt.next();
      if (next.getTime() >= startTime) {
        builder.add(next);
        break;
      }
    }

    // Read until end time is reached
    while (cameraFrameIt.hasNext()) {
      Frame next = cameraFrameIt.next();
      if (next.getTime() <= endTime) {
        builder.add(next);
      } else {
        break;
      }
    }

    return builder.build();
  }
}
