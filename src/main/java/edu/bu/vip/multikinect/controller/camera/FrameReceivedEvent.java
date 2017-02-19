package edu.bu.vip.multikinect.controller.camera;

import edu.bu.vip.multikinect.Protos.Frame;
import edu.bu.vip.multikinect.master.camera.Grpc.CameraProps;

public class FrameReceivedEvent {
  private CameraProps props;
  private Frame frame;

  public FrameReceivedEvent(CameraProps props, Frame frame) {
    this.props = props;
    this.frame = frame;
  }

  public CameraProps getProps() {
    return props;
  }

  public Frame getFrame() {
    return frame;
  }
}
