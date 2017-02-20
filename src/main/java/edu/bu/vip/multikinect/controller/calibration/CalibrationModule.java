package edu.bu.vip.multikinect.controller.calibration;

import com.google.inject.AbstractModule;

public class CalibrationModule extends AbstractModule{

  @Override
  protected void configure() {
    bind(CalibrationDataDB.class).to(FileCalibrationDataStore.class);
    bind(CalibrationStore.class).to(InMemoryCalibrationStore.class);
  }
}
