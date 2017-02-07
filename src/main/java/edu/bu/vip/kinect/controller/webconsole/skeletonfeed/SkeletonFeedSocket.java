package edu.bu.vip.kinect.controller.webconsole.skeletonfeed;

import edu.bu.vip.kinect.controllerv2.camera.CameraBus;
import edu.bu.vip.kinect.controllerv2.camera.CameraChangeEvent;
import edu.bu.vip.kinect.controllerv2.camera.FrameBus;
import edu.bu.vip.kinect.controllerv2.camera.FrameReceivedEvent;
import java.nio.ByteBuffer;
import java.util.Base64;

import com.google.common.eventbus.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.Subscribe;

import edu.bu.vip.kinect.controller.webconsole.Protos.FeedMessage;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import ratpack.websocket.WebSocket;
import ratpack.websocket.WebSocketClose;
import ratpack.websocket.WebSocketHandler;
import ratpack.websocket.WebSocketMessage;

public class SkeletonFeedSocket implements WebSocketHandler<ByteBuf> {

  private final Logger logger = LoggerFactory.getLogger(getClass());
  private EventBus frameBus;
  private EventBus cameraBus;
  private Object sessionLock = new Object();
  private boolean subscribed = false;
  private WebSocket session;

  protected SkeletonFeedSocket(@FrameBus EventBus frameBus, @CameraBus EventBus cameraBus) {
    this.frameBus = frameBus;
    this.cameraBus = cameraBus;
  }

  @Subscribe
  public void onCameraChangeEvent(CameraChangeEvent event) {
    System.out.println("Camera updated");
  }

  @Subscribe
  public void onFrameReceivedEvent(FrameReceivedEvent event) {
    synchronized (sessionLock) {
      FeedMessage.Builder builder = FeedMessage.newBuilder();
      builder.setCameraId(event.getProps().getId());
      builder.setFrame(event.getFrame());
      FeedMessage feed = builder.build();
      // TODO(doug) - encoding to base64 is less than ideal...
      ByteBuffer base64V = Base64.getEncoder().encode(feed.toByteString().asReadOnlyByteBuffer());
      session.send(Unpooled.wrappedBuffer(base64V));
    }
  }

  private void unsubscribe() {
    synchronized (sessionLock) {
      if (subscribed) {
        this.cameraBus.unregister(this);
        this.frameBus.unregister(this);
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
      this.cameraBus.register(this);
      this.frameBus.register(this);
      subscribed = true;
    }
    return null;
  }

  @Override
  public void onMessage(WebSocketMessage<ByteBuf> frame) throws Exception {
    logger.info("Recieved message: {}", frame);
  }

  @Override
  public void onClose(WebSocketClose<ByteBuf> close) throws Exception {
    logger.info("Socket closed with {}", close);
    synchronized (sessionLock) {
      unsubscribe();
    }
  }
}
