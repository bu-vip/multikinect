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
import edu.bu.vip.multikinect.sync.CoordinateTransform;
import java.util.HashMap;
import java.util.Map;
import org.ejml.data.DenseMatrix64F;

public class RealTimeManager {

  // TODO(doug) - Implement
  private String centralCameraId = "camera1";
  private Calibration calibration;
  private CameraGraph cameraGraph;
  private EventBus frameBus;
  private EventBus syncedFrameBus;
  private Map<String, Frame> lastFrames = new HashMap<>();

  @Inject
  protected RealTimeManager(@FrameBus EventBus frameBus, @SyncedFrameBus EventBus syncedFrameBus) {
    this.frameBus = frameBus;
    this.syncedFrameBus = syncedFrameBus;
  }

  public void start(Calibration calibration) {
    this.calibration = calibration;

    // Initialize the camera graph with the calibration
    cameraGraph = new CameraGraph(this.calibration.getCameraCalibrationsList());

    // Subscribe to frame events
    frameBus.register(this);
  }

  @Subscribe
  public void onFrameReceivedEvent(FrameReceivedEvent event) {
    // TODO(doug) - Time synchronization

    // Transform frame into central camera's coordinate system
    String cameraId = event.getProps().getId();
    DenseMatrix64F transform = cameraGraph.calculateTransform(cameraId, centralCameraId);
    Frame transformedFrame = CoordinateTransform.transformFrame(event.getFrame(), transform);
    lastFrames.put(cameraId, transformedFrame);

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
