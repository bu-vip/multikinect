package edu.bu.vip.multikinect.controller.camera;

import java.util.concurrent.Executors;

import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;
import com.google.inject.AbstractModule;

public class CameraModule extends AbstractModule {

  @Override
  protected void configure() {
    bind(EventBus.class).annotatedWith(FrameBus.class)
        .toInstance(new AsyncEventBus("FrameRecievedEventBus", Executors.newCachedThreadPool()));
    bind(EventBus.class).annotatedWith(CameraBus.class)
        .toInstance(new AsyncEventBus("CameraChangeEventBus", Executors.newCachedThreadPool()));
  }

}
