package edu.bu.vip.multikinect.controller.realtime;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import edu.bu.vip.kinect.controller.calibration.Protos.Calibration;
import edu.bu.vip.kinect.controller.realtime.Protos.SyncedFrame;
import edu.bu.vip.multikinect.Protos.Frame;
import edu.bu.vip.multikinect.Protos.Skeleton;
import edu.bu.vip.multikinect.controller.camera.CameraManager;
import edu.bu.vip.multikinect.controller.camera.FrameBus;
import edu.bu.vip.multikinect.controller.camera.FrameReceivedEvent;
import java.util.HashMap;
import java.util.Map;

public class RealtimeManager {

  private Calibration calibration;
  private CameraManager cameraManager;
  private EventBus frameBus;
  private EventBus syncedFrameBus;
  private Map<String, Frame> lastFrames = new HashMap<>();

  @Inject
  protected RealtimeManager(CameraManager cameraManager, @FrameBus EventBus frameBus, @SyncedFrameBus EventBus syncedFrameBus) {
    this.cameraManager = cameraManager;
    this.frameBus = frameBus;
    this.syncedFrameBus = syncedFrameBus;
  }

  @Subscribe
  public void onFrameReceivedEvent(FrameReceivedEvent event) {
    // TODO(doug) - transform frames
    lastFrames.put(event.getProps().getId(), event.getFrame());

    // TODO(doug) - combine skeletons that are close together

    // Send out an new synced frame event
    SyncedFrame.Builder builder = SyncedFrame.newBuilder();
    for (Frame frame : lastFrames.values()) {
      for (Skeleton skeleton : frame.getSkeletonsList()) {
        builder.addSkeletons(skeleton);
      }
    }
    syncedFrameBus.post(builder.build());
  }

}
