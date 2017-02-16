package edu.bu.vip.kinect.sync;

import edu.bu.vip.multikinect.Protos.Frame;

public interface FrameDataSource {
  public boolean hasNext();
  public Frame getNext();
}
