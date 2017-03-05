package edu.bu.vip.multikinect.controller.calibration;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Table;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import edu.bu.vip.kinect.controller.calibration.Protos.Calibration;
import edu.bu.vip.multikinect.Protos.Frame;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A {@link CalibrationDataStore} that stores all data in memory. Useful for testing.
 */
@Singleton
public class InMemoryCalibrationDataStore implements CalibrationDataStore {

  private final Logger logger = LoggerFactory.getLogger(getClass());
  private long idCounter = (new Random()).nextInt();
  private Map<Long, Calibration> calibrations = new HashMap<>();
  private Table<Long, Long, Map<String, List<Frame>>> dataStore = HashBasedTable.create();

  @Inject
  protected InMemoryCalibrationDataStore() {

  }

  @Override
  public Calibration createCalibration(Calibration calibration) {
    Calibration.Builder builder = calibration.toBuilder();
    builder.setId(idCounter);
    idCounter++;

    Calibration finalCal = builder.build();
    calibrations.put(finalCal.getId(), finalCal);
    return finalCal;
  }

  @Override
  public Calibration updateCalibration(Calibration calibration) {
    // Check that calibration exists
    if (!calibrations.containsKey(calibration.getId())) {
      throw new RuntimeException("Couldn't find calibration to update");
    }

    calibrations.put(calibration.getId(), calibration);

    return calibration;
  }

  @Override
  public Optional<Calibration> getCalibration(long id) {
    return Optional.ofNullable(calibrations.get(id));
  }

  @Override
  public ImmutableList<Calibration> getCalibrations() {
    return ImmutableList.copyOf(calibrations.values());
  }

  @Override
  public void deleteCalibration(long id) {
    if (calibrations.containsKey(id)) {
      calibrations.remove(id);
    }
  }

  @Override
  public void storeFrame(long calibrationId, long recordingId, String cameraId, Frame frame) {
    // Check if the data store has a camera map for this recording
    if (!dataStore.contains(calibrationId, recordingId)) {
      dataStore.put(calibrationId, recordingId, new HashMap<>());
    }
    Map<String, List<Frame>> cameraFrames = dataStore.get(calibrationId, recordingId);

    // Check if the map has an list for this camera
    if (!cameraFrames.containsKey(cameraId)) {
      cameraFrames.put(cameraId, new ArrayList<>());
    }
    cameraFrames.get(cameraId).add(frame);
  }

  @Override
  public void storeFrames(long calibrationId, long recordingId, String cameraId,
      List<Frame> frames) {
    // Check if the data store has a camera map for this recording
    if (!dataStore.contains(calibrationId, recordingId)) {
      dataStore.put(calibrationId, recordingId, new HashMap<>());
    }
    Map<String, List<Frame>> cameraFrames = dataStore.get(calibrationId, recordingId);

    // Check if the map has an list for this camera
    if (!cameraFrames.containsKey(cameraId)) {
      cameraFrames.put(cameraId, new ArrayList<>());
    }
    cameraFrames.get(cameraId).addAll(frames);
  }

  @Override
  public ImmutableList<Frame> getAllFrames(long calibrationId, long recordingId, String cameraId) {
    // Check if the data store has a camera map for this recording
    if (!dataStore.contains(calibrationId, recordingId)) {
      // TODO(doug) - evaluate exception, could return an empty list instead
      throw new RuntimeException("No frames for recording");
    }

    Map<String, List<Frame>> cameraFrames = dataStore.get(calibrationId, recordingId);
    if (!cameraFrames.containsKey(cameraId)) {
      // TODO(doug) - evaluate exception, could return an empty list instead
      throw new RuntimeException("No cameraFrames for camera: " + cameraId);
    }

    return ImmutableList.copyOf(cameraFrames.get(cameraId));
  }

  @Override
  public ImmutableList<Frame> getAllFramesInInterval(long calibrationId, long recordingId,
      String cameraId, long startTime, long endTime) {
    // Check if the data store has a camera map for this recording
    if (!dataStore.contains(calibrationId, recordingId)) {
      // TODO(doug) - evaluate exception, could return an empty list instead
      throw new RuntimeException("No frames for recording");
    }

    Map<String, List<Frame>> cameraFrames = dataStore.get(calibrationId, recordingId);
    if (!cameraFrames.containsKey(cameraId)) {
      // TODO(doug) - evaluate exception, could return an empty list instead
      throw new RuntimeException("No cameraFrames for camera: " + cameraId);
    }

    // Get cameraFrames for camera
    List<Frame> frameList = cameraFrames.get(cameraId);
    Iterator<Frame> cameraFrameIt = frameList.iterator();

    ImmutableList.Builder<Frame> builder = ImmutableList.builder();
    // Seek to first frame
    while (cameraFrameIt.hasNext()) {
      Frame next = cameraFrameIt.next();
      if (next.getTime() >= startTime) {
        builder.add(next);
        break;
      }
    }

    // Read until end time is reached
    while (cameraFrameIt.hasNext()) {
      Frame next = cameraFrameIt.next();
      if (next.getTime() <= endTime) {
        builder.add(next);
      } else {
        break;
      }
    }

    return builder.build();
  }
}
