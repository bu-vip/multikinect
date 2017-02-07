package edu.bu.vip.kinect.controllerv2.camera;

import com.roeper.bu.kinect.Protos.Frame;
import com.roeper.bu.kinect.master.camera.Grpc.CameraProps;

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
