package edu.bu.vip.multikinect.controller.calibration;

import com.google.common.collect.ImmutableList;
import com.google.common.io.PatternFilenameFilter;
import com.google.inject.Inject;
import edu.bu.vip.kinect.controller.calibration.Protos.Calibration;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CalibrationLoader {

  private static final String CALIBRATION_FILE_EXT = ".calib";

  private final Logger logger = LoggerFactory.getLogger(getClass());
  private final String dataDirPath;
  private boolean loggedDataNonExistent = false;

  @Inject
  public CalibrationLoader(@CalibrationDataLocation String dataDirPath) {
    this.dataDirPath = dataDirPath;
  }

  /**
   * Loads all calibrations in the directory specified during instantiation. Any files that do not
   * have the correct extension are ignored.
   *
   * @return An immutable list of calibrations
   */
  public ImmutableList<Calibration> loadCalibrations() {
    // Get the data directory
    File dataRoot = new File(dataDirPath);
    // Builder to hold loaded calibrations
    ImmutableList.Builder<Calibration> builder = ImmutableList.builder();
    // Check that the data directory exists
    if (dataRoot.exists()) {
      // Only get files with the correct extension
      File[] files = dataRoot.listFiles(new PatternFilenameFilter("$" + CALIBRATION_FILE_EXT));
      for (File calibrationFile : files) {
        try (FileInputStream fileStream = new FileInputStream(calibrationFile)) {
          // Parse data and add to list
          Calibration newCalibration = Calibration.parseFrom(fileStream);
          builder.add(newCalibration);
        } catch (IOException e) {
          logger.error("Error loading file: " + calibrationFile.getAbsolutePath(), e);
        }
      }
    } else {
      if (!loggedDataNonExistent) {
        logger.warn("Calibration data directory does not exist: {}", dataDirPath);
        loggedDataNonExistent = true;
      }
    }

    return builder.build();
  }
}
