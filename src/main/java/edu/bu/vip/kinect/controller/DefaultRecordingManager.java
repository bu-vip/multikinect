package edu.bu.vip.kinect.controller;

import com.google.common.base.Optional;
import com.roeper.bu.kinect.camera.Grpc.RecordOptions;

public class DefaultRecordingManager implements RecordingManager {
  
  @Override
  public RecordOptions init() {
    RecordOptions.Builder builder = RecordOptions.newBuilder();
    
    return builder.build();
  }

  @Override
  public Optional<RecordOptions> update() {
    return Optional.absent();
  }
}
