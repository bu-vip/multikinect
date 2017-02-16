package edu.bu.vip.multikinect.controller;

import com.google.common.base.Optional;
importedu.bu.vip.multikinect.camera.Grpc.RecordOptions;

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
