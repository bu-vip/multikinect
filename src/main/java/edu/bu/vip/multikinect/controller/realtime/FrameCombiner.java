package edu.bu.vip.multikinect.controller.realtime;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import edu.bu.vip.kinect.controller.realtime.Protos.SyncedFrame;
import edu.bu.vip.multikinect.Protos.Frame;
import edu.bu.vip.multikinect.Protos.Position;
import edu.bu.vip.multikinect.sync.SkeletonUtils;
import java.util.Map;

public class FrameCombiner {

  public static SyncedFrame combineFrames(Map<String, Frame> frames) {
    // Find the centers of all the skeletons
    Table<String, Long, Position> centers = HashBasedTable.create();
    frames.forEach((cameraId, frame) -> {
      frame.getSkeletonsList().forEach((skeleton) -> {
        Position center = SkeletonUtils.calculateCenter(skeleton, false);
        centers.put(cameraId, skeleton.getId(), center);
      });
    });

    // Find the closest center
    // TODO(doug)

    SyncedFrame.Builder builder = SyncedFrame.newBuilder();

    return builder.build();
  }

}
