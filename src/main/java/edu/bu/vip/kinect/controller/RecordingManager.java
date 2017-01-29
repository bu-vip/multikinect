package edu.bu.vip.kinect.controller;

import com.google.common.base.Optional;
import com.roeper.bu.kinect.camera.Grpc.RecordOptions;

public interface RecordingManager {
  public RecordOptions init();
  public Optional<RecordOptions> update();
}
