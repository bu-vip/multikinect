package edu.bu.vip.kinect.controllerv2.calibration;

import com.google.inject.AbstractModule;

public class CalibrationModule extends AbstractModule{

  @Override
  protected void configure() {
    bind(CalibrationDataDB.class).toInstance(new InMemoryCalibrationDataDB());
  }
}
