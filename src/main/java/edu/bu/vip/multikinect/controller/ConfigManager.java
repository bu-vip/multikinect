package edu.bu.vip.multikinect.controller;

import java.io.File;

import com.google.inject.Singleton;

@Singleton
public class ConfigManager {
  
  private static final String CALIBRATION_FOLDER = "calibration";
  
  private String dataRoot = "/home/doug/Desktop/kinect";
  
  public String getRootFolder() {
    return dataRoot;
  }
  
  public String getCalibrationDataFolder() {
    return dataRoot + File.separator + CALIBRATION_FOLDER;
  }
}
