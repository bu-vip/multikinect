package edu.bu.vip.multikinect.controller.calibration;

import com.google.common.collect.ImmutableList;
import edu.bu.vip.kinect.controller.calibration.Protos.Calibration;
import java.util.Optional;

// TODO(doug) - I think CalibrationStore and CalibrationDataStore can be combined into a single module
public interface CalibrationStore {
  Calibration createCalibration(Calibration calibration);
  Calibration updateCalibration(Calibration calibration);
  Optional<Calibration> getCalibration(long id);
  ImmutableList<Calibration> getCalibrations();
  void deleteCalibration(long id);
}
