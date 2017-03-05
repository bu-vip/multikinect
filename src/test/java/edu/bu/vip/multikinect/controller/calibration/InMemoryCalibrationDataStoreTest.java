package edu.bu.vip.multikinect.controller.calibration;

public class InMemoryCalibrationDataStoreTest extends CalibrationDataStoreTest {

  @Override
  public CalibrationDataStore getDataStore() {
    return new InMemoryCalibrationDataStore();
  }
}
