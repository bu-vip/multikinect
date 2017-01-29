package edu.bu.vip.kinect.sync;

import java.util.Arrays;

import com.google.common.primitives.Booleans;
import com.google.common.primitives.Doubles;
import com.roeper.bu.kinect.Protos.Frame;
import com.roeper.bu.kinect.Protos.Joint.JointType;

public class FrameUtils {

  public static boolean[] jointMasks(Frame frame) {
    boolean[][] masks = new boolean[frame.getSkeletonsCount()][];
    for (int i = 0; i < masks.length; i++) {
      masks[i] = SkeletonUtils.jointMask(frame.getSkeletons(i));
    }
    return Booleans.concat(masks);
  }

  public static boolean[] joinJointMasks(boolean[]... masks) {
    return SkeletonUtils.joinJointMasks(masks);
  }

  public static double[] jointMatrix(Frame frame) {
    double[][] skeletons = new double[frame.getSkeletonsCount()][];
    for (int i = 0; i < frame.getSkeletonsCount(); i++) {
      skeletons[i] = SkeletonUtils.jointMatrix(frame.getSkeletons(i));
    }
    return Doubles.concat(skeletons);
  }

  public static double[] jointMatrix(Frame frame, boolean[] jointMask) {
    double[][] skeletons = new double[frame.getSkeletonsCount()][];
    for (int i = 0; i < frame.getSkeletonsCount(); i++) {
      boolean[] skelMask = Arrays.copyOfRange(jointMask, i * JointType.values().length,
          (i + 1) * JointType.values().length);
      skeletons[i] = SkeletonUtils.jointMatrix(frame.getSkeletons(i), skelMask);
    }
    return Doubles.concat(skeletons);
  }

  public static long[] idMatrix(Frame frame) {
    long[] ids = new long[frame.getSkeletonsCount()];
    for (int i = 0; i < frame.getSkeletonsCount(); i++) {
      ids[i] = frame.getSkeletons(i).getId();
    }
    return ids;
  }
}
