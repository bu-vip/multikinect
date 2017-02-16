package edu.bu.vip.kinect.controller;

import com.google.common.base.Optional;
import edu.bu.vip.multikinect.camera.Grpc.RecordOptions;

public interface RecordingManager {
  RecordOptions init();
  Optional<RecordOptions> update();
}
