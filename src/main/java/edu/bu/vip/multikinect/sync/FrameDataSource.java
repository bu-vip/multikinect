package edu.bu.vip.multikinect.sync;

import edu.bu.vip.multikinect.Protos.Frame;

public interface FrameDataSource {
  public boolean hasNext();
  public Frame getNext();
}
