package edu.bu.vip.kinect.controller.realtime;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import edu.bu.vip.kinect.sync.Calibration;

@Singleton
public class RealtimeManager {

  private Calibration calibration;
  private EventBus realtimeBus = new EventBus("realtime");
  
  @Inject
  protected RealtimeManager() {
  }
  
  public void loadCalibration(String calibrationFile) {
    // TODO(doug) - load calibration data from file
  }

  public EventBus getRealtimeBus() {
    return realtimeBus;
  }
}
