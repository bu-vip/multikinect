package edu.bu.vip.kinect.controller.calibration;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableSet;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.roeper.bu.kinect.Protos.Frame;
import com.roeper.bu.kinect.master.camera.Grpc.CameraProps;

import edu.bu.vip.kinect.controller.calibration.Protos.CalibrationFrame;
import edu.bu.vip.kinect.controller.camera.FrameBus;
import edu.bu.vip.kinect.controller.camera.FrameReceivedEvent;

public class Fragmenter {
  private final Logger logger = LoggerFactory.getLogger(getClass());

  // Counter for assigning fragment IDs
  private final AtomicLong fragmentCount = new AtomicLong(0);
  // Maps camera IDs to fragment IDs. Cameras can go through multiple fragment IDs over time.
  private final Map<String, Long> cameraFragmentIds = new HashMap<>();
  // Holds the time of the last seen frame of a fragment.
  private final Map<Long, Long> fragmentLastTime = new HashMap<>();
  // Maps a pair of fragment IDs to a calibration frame.
  private final Map<ImmutableSet<Long>, CalibrationFrame> calibrationFrames = new HashMap<>();
  private EventBus calibrationFrameBus;
  private final EventBus frameBus;

  @Inject
  protected Fragmenter(@FrameBus EventBus frameBus, @CalibrationFrameBus EventBus calibrationFrameBus) {
    this.frameBus = frameBus;
    this.calibrationFrameBus = calibrationFrameBus;
  }

  public void start() {
    // Reset
    this.cameraFragmentIds.clear();
    this.fragmentLastTime.clear();
    this.calibrationFrames.clear();
    
    // Subscribe to frame received events
    this.frameBus.register(this);
  }

  public void stop() {
    this.frameBus.unregister(this);
  }

  @Subscribe
  public void onFrameReceivedEvent(FrameReceivedEvent event) {
    final CameraProps props = event.getProps();
    final Frame frame = event.getFrame();

    // Check if the frame has at least 1 skeleton
    if (frame.getSkeletonsCount() > 0) {
      // Check if the incoming frame is part of an existing fragment
      if (cameraFragmentIds.containsKey(props.getId())) {
        // Write frame to fragment file
        final long fragmentId = cameraFragmentIds.get(props.getId());
        fragmentLastTime.put(fragmentId, frame.getTime());
      } else {
        // Create a new fragment for the camera
        createNewFragment(props, event.getFrame());
      }
    } else {
      // Check if calibration frames need to be finished
      if (cameraFragmentIds.containsKey(props.getId())) {
        logger.info("Closing all fragments connected to camera {}", props.getId());

        // Close all calibration frames containing this camera
        final long fragmentId = cameraFragmentIds.get(props.getId());
        closeFragment(fragmentId);

        // Remove the fragment id
        cameraFragmentIds.remove(props.getId());
      }
    }
  }

  private void createNewFragment(CameraProps props, Frame frame) {
    final long newFragmentId = fragmentCount.getAndIncrement();

    logger.info("Creating new fragment {} for camera {}", newFragmentId, props.getId());

    // Create new calibration frames between each pair of cameras
    cameraFragmentIds.forEach((cameraId, fragmentId) -> {
      CalibrationFrame.Builder builder = CalibrationFrame.newBuilder();
      builder.setCameraA(cameraId);
      builder.setFragmentA(fragmentId);
      builder.setStartTimeA(fragmentLastTime.get(fragmentId));

      builder.setCameraB(props.getId());
      builder.setFragmentB(newFragmentId);
      builder.setStartTimeB(frame.getTime());

      calibrationFrames.put(ImmutableSet.of(fragmentId, newFragmentId), builder.build());
    });

    // Add the new fragment
    cameraFragmentIds.put(props.getId(), newFragmentId);
  }

  private void closeFragment(final long fragmentId) {
    // Iterate over all calibration frame
    Iterator<ImmutableSet<Long>> it = calibrationFrames.keySet().iterator();
    while (it.hasNext()) {
      ImmutableSet<Long> fragmentSet = it.next();
      // Check if this camera's fragment is part of the frame
      if (fragmentSet.contains(fragmentId)) {
        // Get the frame with start times
        CalibrationFrame started = calibrationFrames.get(fragmentSet);
        CalibrationFrame.Builder builder = CalibrationFrame.newBuilder(started);

        // Update the end times
        builder.setEndTimeA(fragmentLastTime.get(started.getFragmentA()));
        builder.setEndTimeB(fragmentLastTime.get(started.getFragmentB()));

        // Post the new frame
        calibrationFrameBus.post(builder.build());
        it.remove();
      }
    }
  }
}
