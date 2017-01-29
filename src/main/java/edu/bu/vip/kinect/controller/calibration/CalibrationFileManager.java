package edu.bu.vip.kinect.controller.calibration;

import java.util.List;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import edu.bu.vip.kinect.controller.ConfigManager;
import edu.bu.vip.kinect.controller.calibration.Protos.Calibration;

@Singleton
public class CalibrationFileManager {
  
  private final ConfigManager configManager;
  
  @Inject
  protected CalibrationFileManager(ConfigManager configManager) {
    this.configManager = configManager;
  }

  public void saveCalibration(Calibration calibration) {
    // TODO(doug)
  }

  public List<Calibration> getAvailableCalibrations() {
    // TODO(doug)
    return null;
  }

  public Calibration loadCalibration(String filePath) {
    // TODO(doug)
    return null;
  }
}
