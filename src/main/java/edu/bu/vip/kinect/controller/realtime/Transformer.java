package edu.bu.vip.kinect.controller.realtime;

import java.util.List;
import java.util.Map;

import edu.bu.vip.multikinect.Protos.Frame;
import edu.bu.vip.multikinect.Protos.Skeleton;

import edu.bu.vip.kinect.sync.Calibration;

public class Transformer {
  private Calibration calibration;
  
  public Transformer() {
    
  }
  
  public List<Skeleton> transform(Map<String, Frame> frames) {
    return null;
  }
}
