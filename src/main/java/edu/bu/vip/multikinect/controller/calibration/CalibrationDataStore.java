package edu.bu.vip.multikinect.controller.calibration;

import com.google.common.collect.ImmutableList;
import edu.bu.vip.kinect.controller.calibration.Protos.Calibration;
import edu.bu.vip.multikinect.Protos.Frame;
import java.util.List;
import java.util.Optional;

/**
 * Stores calibrations and the data used for calculating them.
 */
public interface CalibrationDataStore {

  /**
   * Creates a new calibration. The id field of the calibration should be set by the implementation.
   * Ids should be unique.
   */
  Calibration createCalibration(Calibration calibration);

  /**
   * Updates an existing calibration.
   */
  Calibration updateCalibration(Calibration calibration);

  /**
   * Gets the calibration with the specified ID. Optional will be empty if not found.
   */
  Optional<Calibration> getCalibration(long id);

  /**
   * Gets all calibrations in the store.
   */
  ImmutableList<Calibration> getCalibrations();

  /**
   * Deletes the calibration from the store, if it exists.
   */
  void deleteCalibration(long id);

  /**
   * Stores a frame for the specified calibration+recording.
   */
  void storeFrame(long calibrationId, long recordingId, String cameraId, Frame frame);

  /**
   * Store many frames, equivalent to calling {storeFrame} for each frame.
   */
  void storeFrames(long calibrationId, long recordingId, String cameraId, List<Frame> frames);

  /**
   * Gets all the frames for a given camera in a recording for the specified calibration.
   */
  ImmutableList<Frame> getAllFrames(long calibrationId, long recordingId, String cameraId);

  /**
   * Returns all frames for a given camera within the specified time interval.
   */
  ImmutableList<Frame> getAllFramesInInterval(long calibrationId, long recordingId, String cameraId,
      long startTime, long endTime);
}
