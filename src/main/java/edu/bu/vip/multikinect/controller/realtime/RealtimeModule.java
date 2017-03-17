package edu.bu.vip.multikinect.controller.realtime;

import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;
import com.google.inject.AbstractModule;
import edu.bu.vip.multikinect.controller.data.SessionDataStore;
import java.util.concurrent.Executors;

public class RealtimeModule extends AbstractModule {

  @Override
  protected void configure() {
    bind(EventBus.class).annotatedWith(SyncedFrameBus.class)
        .toInstance(new AsyncEventBus("SyncedFrameBus", Executors.newCachedThreadPool()));
  }

}
