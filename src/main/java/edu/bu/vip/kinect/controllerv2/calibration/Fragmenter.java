package edu.bu.vip.kinect.controllerv2.calibration;

import edu.bu.vip.kinect.controller.calibration.Protos.GroupOfFrames;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableSet;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import edu.bu.vip.multikinect.Protos.Frame;
import edu.bu.vip.multikinect.master.camera.Grpc.CameraProps;

import edu.bu.vip.kinect.controllerv2.camera.FrameBus;
import edu.bu.vip.kinect.controllerv2.camera.FrameReceivedEvent;

/**
 * Subscribes to {@link FrameReceivedEvent}s and creates {@link GroupOfFrames} that link pairs of
 * camera data streams together.
 */
public class Fragmenter {

  /**
   * Internal Docs:
   *
   * "Fragments" are a section of a camera's data stream where at least one skeleton is visible. For
   * example, if a camera can see 0 skeletons from [t0, t1], 1 skeleton from [t1, t2], 0 from [t2,
   * t3], and 1 from [t3, t4], two fragments would be created for the intervals [t1, t2] and [t3,
   * t4].
   */

  private final Logger logger = LoggerFactory.getLogger(getClass());
  // Counter for assigning fragment IDs
  private final AtomicLong fragmentCount = new AtomicLong(0);
  // Maps camera IDs to fragment IDs. Cameras can go through multiple fragment IDs over time.
  private final Map<String, Long> cameraFragmentIds = new HashMap<>();
  // Holds the time of the last seen frame of a fragment.
  private final Map<Long, Long> fragmentLastTime = new HashMap<>();
  // Maps a pair of fragment IDs to a GOF. These GOFs have been started but are not finished.
  private final Map<ImmutableSet<Long>, GroupOfFrames> activeGOFs = new HashMap<>();

  private final EventBus frameBus;
  private final CalibrationDataDB calibrationDataDB;
  private Consumer<GroupOfFrames> groupOfFramesConsumer;

  @Inject
  protected Fragmenter(@FrameBus EventBus frameBus, CalibrationDataDB calibrationDataDB) {
    this.frameBus = frameBus;
    this.calibrationDataDB = calibrationDataDB;
  }

  public void start(Consumer<GroupOfFrames> groupOfFramesConsumer) {
    this.groupOfFramesConsumer = groupOfFramesConsumer;

    // Reset
    this.cameraFragmentIds.clear();
    this.fragmentLastTime.clear();
    this.activeGOFs.clear();

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

    // Save the camera frame
    calibrationDataDB.storeFrame(props.getId(), frame);

    // Check if the frame has at least 1 skeleton
    if (frame.getSkeletonsCount() > 0) {
      // Check if the incoming frame is part of an existing fragment
      if (cameraFragmentIds.containsKey(props.getId())) {
        // Update the time for this fragment
        final long fragmentId = cameraFragmentIds.get(props.getId());
        fragmentLastTime.put(fragmentId, frame.getTime());
      } else {
        // Create a new fragment for the camera
        createNewFragment(props, event.getFrame());
      }
    } else {
      // Check if GOFs need to be finished
      if (cameraFragmentIds.containsKey(props.getId())) {
        logger.info("Closing all fragments connected to camera {}", props.getId());

        // Close all GOFs containing this camera
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

    // Create new GOFs between each pair of cameras
    cameraFragmentIds.forEach((cameraId, fragmentId) -> {
      GroupOfFrames.Builder builder = GroupOfFrames.newBuilder();
      builder.setCameraA(cameraId);
      builder.setStartTimeA(fragmentLastTime.get(fragmentId));

      builder.setCameraB(props.getId());
      builder.setStartTimeB(frame.getTime());

      activeGOFs.put(ImmutableSet.of(fragmentId, newFragmentId), builder.build());
    });

    // Add the new fragment
    cameraFragmentIds.put(props.getId(), newFragmentId);
  }

  private void closeFragment(final long fragmentId) {
    // Iterate over all GOFs
    Iterator<ImmutableSet<Long>> it = activeGOFs.keySet().iterator();
    while (it.hasNext()) {
      ImmutableSet<Long> fragmentSet = it.next();
      // Check if this camera's fragment is part of the frame
      if (fragmentSet.contains(fragmentId)) {
        // Get the original GOF with start times
        GroupOfFrames started = activeGOFs.get(fragmentSet);
        GroupOfFrames.Builder builder = GroupOfFrames.newBuilder(started);

        // Update the end times
        final long fragmentAId = cameraFragmentIds.get(started.getCameraA());
        final long fragmentBId = cameraFragmentIds.get(started.getCameraB());
        builder.setEndTimeA(fragmentLastTime.get(fragmentAId));
        builder.setEndTimeB(fragmentLastTime.get(fragmentBId));

        // Send the new frame
        groupOfFramesConsumer.accept(builder.build());
        it.remove();
      }
    }
  }
}
