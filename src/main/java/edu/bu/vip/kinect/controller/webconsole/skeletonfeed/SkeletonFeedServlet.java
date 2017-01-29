package edu.bu.vip.kinect.controller.webconsole.skeletonfeed;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import edu.bu.vip.kinect.controller.Controller;
import edu.bu.vip.kinect.controller.camera.CameraBus;
import edu.bu.vip.kinect.controller.camera.FrameBus;
import ratpack.handling.Context;
import ratpack.handling.Handler;
import ratpack.websocket.WebSockets;

@Singleton
public class SkeletonFeedServlet implements Handler {
  public static final String URL_PATH = "_/feed";

  private final EventBus frameBus;
  private final EventBus cameraBus;

  @Inject
  protected SkeletonFeedServlet(@FrameBus EventBus frameBus, @CameraBus EventBus cameraBus) {
      this.frameBus = frameBus;
      this.cameraBus = cameraBus;
  }

  @Override
  public void handle(Context ctx) throws Exception {
    WebSockets.websocket(ctx, new SkeletonFeedSocket(frameBus, cameraBus));
  }

}
