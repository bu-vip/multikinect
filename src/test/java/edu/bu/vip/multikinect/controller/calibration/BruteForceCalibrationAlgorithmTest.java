package edu.bu.vip.multikinect.controller.calibration;

public class BruteForceCalibrationAlgorithmTest extends CalibrationAlgorithmTest {

  @Override
  public CalibrationAlgorithm getAlgorithm() {
    return new BruteForceCalibrationAlgorithm();
  }
}
