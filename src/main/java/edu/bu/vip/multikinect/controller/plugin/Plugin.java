package edu.bu.vip.multikinect.controller.plugin;

import java.io.OutputStream;

public interface Plugin {

  void start() throws Exception;

  void recordingStarted(OutputStream outputStream);

  void recordingStopped();

  void stop() throws Exception;

}
