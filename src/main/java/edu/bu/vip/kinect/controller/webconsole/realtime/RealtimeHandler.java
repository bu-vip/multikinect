package edu.bu.vip.kinect.controller.webconsole.realtime;

import com.google.inject.Inject;

import edu.bu.vip.kinect.controller.realtime.RealtimeManager;
import ratpack.handling.Context;
import ratpack.handling.Handler;
import ratpack.websocket.WebSockets;

public class RealtimeHandler implements Handler {
  public static final String URL_PATH = "_/realtime/feed";

  @Inject
  protected RealtimeHandler() {
  }

  @Override
  public void handle(Context ctx) throws Exception {
    WebSockets.websocket(ctx, new RealtimeSocket(ctx.get(RealtimeManager.class)));
  }

}
