package edu.bu.vip.kinect.controller.calibration;

import com.google.common.eventbus.EventBus;
import com.google.inject.AbstractModule;
import edu.bu.vip.kinect.controllerv2.calibration.CalibrationFrameBus;

public class CalibrationModule extends AbstractModule {

  @Override
  protected void configure() {
    bind(EventBus.class).annotatedWith(CalibrationFrameBus.class)
        .toInstance(new EventBus("calibrationFrameBus"));
  }

}
