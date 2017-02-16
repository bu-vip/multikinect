package edu.bu.vip.multikinect.controller.realtime;

import edu.bu.vip.multikinect.sync.Calibration;
import java.util.List;
import java.util.Map;

import edu.bu.vip.multikinect.Protos.Frame;
import edu.bu.vip.multikinect.Protos.Skeleton;

public class Transformer {
  private Calibration calibration;
  
  public Transformer() {
    
  }
  
  public List<Skeleton> transform(Map<String, Frame> frames) {
    return null;
  }
}
