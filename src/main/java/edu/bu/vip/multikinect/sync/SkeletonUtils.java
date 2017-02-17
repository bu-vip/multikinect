package edu.bu.vip.multikinect.sync;

import edu.bu.vip.multikinect.Protos.Joint;
import edu.bu.vip.multikinect.Protos.Joint.TrackingState;
import edu.bu.vip.multikinect.Protos.Skeleton;

public class SkeletonUtils {
  
  public static boolean[] joinJointMasks(boolean[] ...masks) {
    boolean[] joints = new boolean[Joint.JointType.values().length];
    
    for (int i = 0; i < joints.length; i++) {
      boolean value = true;
      for (int j = 0; j < masks.length; j++) {
        if (!masks[j][i]) {
          value = false;
          break;
        }
      }
      joints[i] = value;
    }
    
    return joints;
  }

  public static boolean[] jointMask(Skeleton skel) {
    boolean[] joints = new boolean[Joint.JointType.values().length];
    for (Joint joint : skel.getJointsList()) {
      joints[joint.getTypeValue()] = joint.getTrackingState() == TrackingState.TRACKED
          || joint.getTrackingState() == TrackingState.INFERRED;
    }

    return joints;
  }

  public static double[] jointMatrix(Skeleton skel) {
    double[] joints = new double[Joint.JointType.values().length * 3];
    for (Joint joint : skel.getJointsList()) {
      joints[joint.getTypeValue() * 3 + 0] = joint.getPosition().getX();
      joints[joint.getTypeValue() * 3 + 1] = joint.getPosition().getY();
      joints[joint.getTypeValue() * 3 + 2] = joint.getPosition().getZ();
    }

    return joints;
  }

  public static double[] jointMatrix(Skeleton skel, boolean[] jointMask) {
    if (jointMask.length != Joint.JointType.values().length) {
      throw new RuntimeException("Joint mask not correct length");
    }

    int[] dataMap = new int[Joint.JointType.values().length];
    int dataIndex = 0;
    for (int i = 0; i < jointMask.length; i++) {
      if (jointMask[i]) {
        dataMap[i] = dataIndex;
        dataIndex++;
      } else {
        dataMap[i] = -1;
      }
    }


    double[] joints = new double[dataIndex * 3];
    for (Joint joint : skel.getJointsList()) {
      int index = dataMap[joint.getTypeValue()];
      if (index != -1) {
        joints[index * 3 + 0] = joint.getPosition().getX();
        joints[index * 3 + 1] = joint.getPosition().getY();
        joints[index * 3 + 2] = joint.getPosition().getZ();
      }
    }

    return joints;
  }
}