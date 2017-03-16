package edu.bu.vip.multikinect.controller.realtime;

import edu.bu.vip.kinect.controller.realtime.Protos.CombinedSkeleton;
import edu.bu.vip.kinect.controller.realtime.Protos.FrameDataPoint;
import edu.bu.vip.kinect.controller.realtime.Protos.SkeletonDataPoint;
import edu.bu.vip.kinect.controller.realtime.Protos.SyncedFrame;
import edu.bu.vip.multikinect.Protos.Frame;
import edu.bu.vip.multikinect.Protos.Position;
import edu.bu.vip.multikinect.Protos.Skeleton;
import edu.bu.vip.multikinect.sync.PositionUtils;
import edu.bu.vip.multikinect.sync.SkeletonUtils;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FrameCombiner {

  private static final float MAX_COMBINE_DISTANCE = 0.1f;

  public static SyncedFrame combineFrames(Map<String, Frame> frames) {
    // Group skeletons together
    List<SkeletonGroup> groups = new ArrayList<>();
    frames.forEach((cameraId, frame) -> {
      // Check if we have any skeleton sets
      if (groups.size() == 0) {
        // If not, create new sets for each skeleton in the frame
        frame.getSkeletonsList().forEach(skeleton -> {
          SkeletonGroup skeletonGroup = new SkeletonGroup(groups.size());
          skeletonGroup.addSkeleton(cameraId, skeleton);
          groups.add(skeletonGroup);
        });
      } else {
        // If we have skeleton sets, try and map the skeletons to existing sets

        // Create possible mappings between each skeleton and each set
        List<SkeletonGroupMapping> mappings = new ArrayList<>();
        frame.getSkeletonsList().forEach(skeleton -> {
          groups.forEach(skeletonGroup -> {
            Position center = SkeletonUtils.calculateCenter(skeleton, false);
            float distance = PositionUtils.distanceXZ(center, skeletonGroup.getCenter());
            SkeletonGroupMapping mapping = new SkeletonGroupMapping(cameraId, skeleton,
                skeletonGroup, center, distance);
            mappings.add(mapping);
          });
        });

        // Sort the mappings
        mappings.sort((o1, o2) -> Float.compare(o1.getDistance(), o2.getDistance()));

        // Add skeletons to existing mappings
        Set<Long> groupsMapped = new HashSet<>();
        Set<Long> skeletonsMapped = new HashSet<>();
        Iterator<SkeletonGroupMapping> mappingIt = mappings.iterator();
        while (mappingIt.hasNext() && skeletonsMapped.size() < frame.getSkeletonsCount()) {
          SkeletonGroupMapping mapping = mappingIt.next();
          if (mapping.getDistance() > MAX_COMBINE_DISTANCE) {
            // If the mapping is too long, finish
            break;
          } else if (skeletonsMapped.contains(mapping.getSkeleton().getId()) ||
              groupsMapped.contains(mapping.getGroup().getId())) {
            // If skeleton or group has already been mapped, skip it
            continue;
          } else {
            // Add skeleton to group
            mapping.getGroup().addSkeleton(mapping.getCameraId(), mapping.getSkeleton());
            // Store that skeleton has been added to a group
            skeletonsMapped.add(mapping.getSkeleton().getId());
            // Store that the group has been mapped
            groupsMapped.add(mapping.getGroup().getId());
          }
        }

        // Create new groups for all skeletons that haven't been mapped
        frame.getSkeletonsList().forEach(skeleton -> {
          if (!skeletonsMapped.contains(skeleton.getId())) {
            SkeletonGroup skeletonGroup = new SkeletonGroup(groups.size());
            skeletonGroup.addSkeleton(cameraId, skeleton);
            groups.add(skeletonGroup);
          }
        });
      }
    });

    // Create combined skeletons from each group
    final SyncedFrame.Builder builder = SyncedFrame.newBuilder();
    groups.forEach(group -> {
      builder.addSkeletons(group.toCombinedSkeleton());
    });

    // Set the frame data points used
    frames.forEach((cameraId, frame) -> {
      builder.addFramePoints(FrameDataPoint.newBuilder()
          .setCameraId(cameraId)
          .setFrameId(frame.getTime())
          .build());
    });

    return builder.build();
  }

  private static class SkeletonGroup {

    private final long id;
    private final List<String> cameraIds = new ArrayList<>();
    private final List<Skeleton> skeletons = new ArrayList<>();
    private final List<Position> centers = new ArrayList<>();

    public SkeletonGroup(long id) {
      this.id = id;
    }

    public void addSkeleton(String cameraId, Skeleton skeleton) {
      cameraIds.add(cameraId);
      skeletons.add(skeleton);
      centers.add(SkeletonUtils.calculateCenter(skeleton, false));
    }

    public Position getCenter() {
      return PositionUtils.average(centers);
    }

    public long getId() {
      return id;
    }

    public CombinedSkeleton toCombinedSkeleton() {
      CombinedSkeleton.Builder builder = CombinedSkeleton.newBuilder();

      // TODO(doug) - Unique id...

      // Add the skeleton data points
      for (int i = 0; i < cameraIds.size(); i++) {
        String cameraId = cameraIds.get(i);
        Skeleton skeleton = skeletons.get(i);
        builder.addDataPoints(SkeletonDataPoint.newBuilder()
            .setCameraId(cameraId)
            .setSkeletonId(skeleton.getId())
            .build());
      }

      Skeleton combined = SkeletonUtils.combine(skeletons);
      builder.setSkeleton(combined);
      return builder.build();
    }
  }

  private static class SkeletonGroupMapping {

    private final String cameraId;
    private final Skeleton skeleton;
    private final SkeletonGroup group;
    private final Position skeletonCenter;
    private final float distance;

    public SkeletonGroupMapping(String cameraId, Skeleton skeleton, SkeletonGroup group,
        Position skeletonCenter, float distance) {
      this.cameraId = cameraId;
      this.skeleton = skeleton;
      this.group = group;
      this.skeletonCenter = skeletonCenter;
      this.distance = distance;
    }

    public String getCameraId() {
      return cameraId;
    }

    public Skeleton getSkeleton() {
      return skeleton;
    }

    public SkeletonGroup getGroup() {
      return group;
    }

    public Position getSkeletonCenter() {
      return skeletonCenter;
    }

    public float getDistance() {
      return distance;
    }
  }
}
