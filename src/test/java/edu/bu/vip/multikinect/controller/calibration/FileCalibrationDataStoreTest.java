package edu.bu.vip.multikinect.controller.calibration;

public class FileCalibrationDataStoreTest extends CalibrationDataStoreTest{

  @Override
  public CalibrationDataStore getDataStore() {
    return new FileCalibrationDataStore("/tmp/multikinect/test/calibration");
  }
}
