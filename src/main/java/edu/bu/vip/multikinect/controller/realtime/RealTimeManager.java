package edu.bu.vip.multikinect.controller.realtime;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import edu.bu.vip.kinect.controller.calibration.Protos.Calibration;
import edu.bu.vip.kinect.controller.data.Protos.Recording;
import edu.bu.vip.kinect.controller.realtime.Protos.SyncedFrame;
import edu.bu.vip.multikinect.Protos.Frame;
import edu.bu.vip.multikinect.Protos.Skeleton;
import edu.bu.vip.multikinect.controller.camera.FrameBus;
import edu.bu.vip.multikinect.controller.camera.FrameReceivedEvent;
import edu.bu.vip.multikinect.controller.data.SessionDataStore;
import edu.bu.vip.multikinect.sync.CoordinateTransform;
import java.util.HashMap;
import java.util.Map;
import org.ejml.data.DenseMatrix64F;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class RealTimeManager {

  private final Logger logger = LoggerFactory.getLogger(getClass());
  private final EventBus frameBus;
  private final EventBus syncedFrameBus;
  private final SessionDataStore sessionDataStore;
  private final Object recordingLock = new Object();
  // TODO(doug) - Implement
  private String centralCameraId = "camera1";
  private Calibration calibration;
  private CameraGraph cameraGraph;
  private Map<String, Frame> lastFrames = new HashMap<>();
  private boolean recording = false;
  private long sessionId;
  private long recordingId;
  private FrameCombiner frameCombiner;

  @Inject
  protected RealTimeManager(@FrameBus EventBus frameBus, @SyncedFrameBus EventBus syncedFrameBus,
      SessionDataStore sessionDataStore) {
    this.frameBus = frameBus;
    this.syncedFrameBus = syncedFrameBus;
    this.sessionDataStore = sessionDataStore;
  }

  public void start(Calibration calibration) {
    this.calibration = calibration;

    // Initialize the camera graph with the calibration
    cameraGraph = new CameraGraph(this.calibration.getCameraCalibrationsList());

    frameCombiner = new FrameCombiner();

    // Subscribe to frame events
    frameBus.register(this);
  }

  public void stop() {
    calibration = null;
    cameraGraph = null;
    frameCombiner = null;
    frameBus.unregister(this);
  }

  @Subscribe
  public void onFrameReceivedEvent(FrameReceivedEvent event) {
    // TODO(doug) - Time synchronization

    // Transform frame into central camera's coordinate system
    String cameraId = event.getProps().getId();
    DenseMatrix64F transform = cameraGraph.calculateTransform(cameraId, centralCameraId);
    Frame transformedFrame = CoordinateTransform.transformFrame(event.getFrame(), transform);
    lastFrames.put(cameraId, transformedFrame);

    // Combine skeletons that are close together
    SyncedFrame syncedFrame = frameCombiner.combineFrames(lastFrames);

    // Send out an new synced frame event
    syncedFrameBus.post(syncedFrame);

    synchronized (recordingLock) {
      if (recording) {
        sessionDataStore.storeRawFrame(sessionId, recordingId, cameraId, event.getFrame());
      }
    }
  }

  public Recording startRecording(long sessionId, String name, String notes) {
    synchronized (recordingLock) {
      recording = true;

      Recording.Builder builder = Recording.newBuilder();

      // TODO(doug)
      long recordingId = 0;


      this.sessionId = sessionId;
      this.recordingId = recordingId;

      return builder.build();
    }
  }

  public void stopRecording() {
    synchronized (recordingLock) {
      recording = false;
    }
  }
}
