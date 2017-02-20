package edu.bu.vip.multikinect.controller.calibration;

import com.google.inject.AbstractModule;

public class CalibrationModule extends AbstractModule{

  @Override
  protected void configure() {
    bind(CalibrationDataStore.class).to(FileCalibrationDataStore.class);
    bind(CalibrationStore.class).to(InMemoryCalibrationStore.class);
    bind(CalibrationAlgorithm.class).to(BruteForceCalibrationAlgorithm.class);
  }
}
