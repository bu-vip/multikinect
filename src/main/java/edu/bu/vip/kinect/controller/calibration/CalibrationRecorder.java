package edu.bu.vip.kinect.controller.calibration;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableSet;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.roeper.bu.kinect.Protos.Frame;
import com.roeper.bu.kinect.master.camera.Grpc.CameraProps;

import edu.bu.vip.kinect.controller.calibration.Protos.CalibrationFrame;
import edu.bu.vip.kinect.controller.camera.FrameReceivedEvent;
import edu.bu.vip.kinect.controller.camera.MessageWriter;

public class CalibrationRecorder {

  private final Logger logger = LoggerFactory.getLogger(getClass());
  private final MessageWriter writer;
  private final String calibrationFileLocation;
  // Counter for assigning fragment IDs
  private final AtomicLong fragmentCount = new AtomicLong(0);
  // Maps camera IDs to fragment IDs. Cameras can go through multiple fragment IDs over time.
  private final Map<String, Long> cameraFragmentIds = new HashMap<>();
  // Holds the time of the last seen frame of a fragment.
  private final Map<Long, Long> fragmentLastTime = new HashMap<>();
  // Maps a pair of fragment IDs to a calibration frame.
  private final Map<ImmutableSet<Long>, CalibrationFrame> calibrationFrames = new HashMap<>();
  // Holds the finished calibration frame
  private final List<CalibrationFrame> finishedFrames = new ArrayList<>();

  private EventBus frameBus;

  public CalibrationRecorder(String fileLocation, MessageWriter writer) {
    this.calibrationFileLocation = fileLocation;
    this.writer = writer;
  }

  public void start(EventBus frameBus) {
    // Subscribe to frame received events
    this.frameBus = frameBus;
    this.frameBus.register(this);
  }

  public List<CalibrationFrame> stop() {
    this.frameBus.unregister(this);
    return finishedFrames;
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
        writer.writeFrame(Long.toString(fragmentId), frame);
        fragmentLastTime.put(fragmentId, frame.getTime());
      } else {
        // Create a new fragment for the camera
        try {
          createNewFragment(props, event.getFrame());
        } catch (IOException e) {
          // TODO(doug) - Handle
          logger.error("Error opening file: {}", e);
        }
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

  private void createNewFragment(CameraProps props, Frame frame) throws IOException {
    final long newFragmentId = fragmentCount.getAndIncrement();

    logger.info("Creating new fragment {} for camera {}", newFragmentId, props.getId());

    // Add a file stream to the writer for writing frames of the fragment
    FileOutputStream fragmentStream = new FileOutputStream(getFragmentFilePath(newFragmentId));
    writer.addOutputStream(Long.toString(newFragmentId), fragmentStream);

    // Create new calibration frames between each pair of cameras
    cameraFragmentIds.forEach((cameraId, fragmentId) -> {
      CalibrationFrame.Builder builder = CalibrationFrame.newBuilder();
      builder.setCameraA(cameraId);
      builder.setFragmentA(fragmentId);
      builder.setStartTimeA(fragmentLastTime.get(fragmentId));
      builder.setFileAPath(getFragmentFilePath(fragmentId));

      builder.setCameraB(props.getId());
      builder.setFragmentB(newFragmentId);
      builder.setStartTimeB(frame.getTime());
      builder.setFileBPath(getFragmentFilePath(newFragmentId));

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

        // Store the finished frame and remove the old frame from the map
        finishedFrames.add(builder.build());
        it.remove();

      }
    }

    // Tell writer to close the stream
    writer.closeOutputStreamAsync(Long.toString(fragmentId));
  }

  private String getFragmentFilePath(long fragmentId) {
    String filename = calibrationFileLocation;
    if (!filename.isEmpty() && !filename.endsWith(File.separator)) {
      filename += File.separator;
    }
    filename += fragmentId + ".dat";
    return filename;
  }
}
