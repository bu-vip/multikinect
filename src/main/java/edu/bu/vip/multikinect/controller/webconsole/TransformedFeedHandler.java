package edu.bu.vip.multikinect.controller.webconsole;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import edu.bu.vip.kinect.controller.realtime.Protos.SyncedFrame;
import edu.bu.vip.multikinect.Protos.Skeleton;
import edu.bu.vip.multikinect.controller.camera.CameraChangeEvent;
import edu.bu.vip.multikinect.controller.realtime.SyncedFrameBus;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.nio.ByteBuffer;
import java.util.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ratpack.handling.Context;
import ratpack.handling.Handler;
import ratpack.websocket.WebSocket;
import ratpack.websocket.WebSocketClose;
import ratpack.websocket.WebSocketHandler;
import ratpack.websocket.WebSocketMessage;
import ratpack.websocket.WebSockets;

@Singleton
public class TransformedFeedHandler implements Handler {
  public static final String URL_PATH = "_/syncedFeed";

  private final EventBus syncedFrameBus;

  @Inject
  protected TransformedFeedHandler(@SyncedFrameBus EventBus syncedFrameBus) {
    this.syncedFrameBus = syncedFrameBus;
  }

  @Override
  public void handle(Context ctx) throws Exception {
    WebSockets.websocket(ctx, new TransformedFeedSocket(syncedFrameBus));
  }

  private static class TransformedFeedSocket implements WebSocketHandler<ByteBuf> {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private EventBus syncedFrameBus;
    private Object sessionLock = new Object();
    private boolean subscribed = false;
    private WebSocket session;

    protected TransformedFeedSocket(@SyncedFrameBus EventBus syncedFrameBus) {
      this.syncedFrameBus = syncedFrameBus;
    }

    @Subscribe
    public void onSyncedFrameReceived(SyncedFrame syncedFrame) {
      synchronized (sessionLock) {
        // TODO(doug) - encoding to base64 is less than ideal...
        ByteBuffer base64V = Base64.getEncoder()
            .encode(syncedFrame.toByteString().asReadOnlyByteBuffer());
        session.send(Unpooled.wrappedBuffer(base64V));
      }
    }

    private void unsubscribe() {
      synchronized (sessionLock) {
        if (subscribed) {
          this.syncedFrameBus.unregister(this);
          subscribed = false;
          session = null;
        }
      }
    }

    @Override
    public ByteBuf onOpen(WebSocket webSocket) throws Exception {
      logger.info("Connected: {}", webSocket);
      synchronized (sessionLock) {
        session = webSocket;
        this.syncedFrameBus.register(this);
        subscribed = true;
      }
      return null;
    }

    @Override
    public void onMessage(WebSocketMessage<ByteBuf> frame) throws Exception {
      logger.info("Received message: {}", frame);
    }

    @Override
    public void onClose(WebSocketClose<ByteBuf> close) throws Exception {
      logger.info("Socket closed with {}", close);
      synchronized (sessionLock) {
        unsubscribe();
      }
    }
  }

}
