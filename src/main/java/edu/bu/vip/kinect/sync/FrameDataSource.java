package edu.bu.vip.kinect.sync;

import com.roeper.bu.kinect.Protos.Frame;

public interface FrameDataSource {
  public boolean hasNext();
  public Frame getNext();
}
