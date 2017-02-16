package edu.bu.vip.multikinect.controller.realtime;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import edu.bu.vip.multikinect.sync.Calibration;

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
