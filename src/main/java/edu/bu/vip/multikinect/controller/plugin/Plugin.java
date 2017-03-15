package edu.bu.vip.multikinect.controller.plugin;

import com.google.protobuf.GeneratedMessageV3;
import java.util.function.Consumer;

public interface Plugin {

  /**
   * Starts the plugin. Called during startup.
   */
  void start() throws Exception;

  /**
   * Called when recording starts. The plugin should send all readings to the consumer.
   *
   * @param readingConsumer - Consumes readings from the plugin
   */
  void recordingStarted(Consumer<GeneratedMessageV3> readingConsumer);

  /**
   * Called when recording stops.
   */
  void recordingStopped();

  /**
   * Stops the plugin. Called during shutdown. Clean up any resources created during {@link #start}
   */
  void stop() throws Exception;

}
