package edu.bu.vip.multikinect.controllerv2.camera;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.MessageLite;

/**
 * Class for writing messages from many different sources to different files concurrently.
 */
public class MessageWriter {
  private static final int STOP_TIMEOUT = 10000;

  private final Logger logger = LoggerFactory.getLogger(getClass());
  private ConcurrentHashMap<String, ExecutorService> fileExecutors = new ConcurrentHashMap<>();
  private ConcurrentHashMap<String, OutputStream> streams = new ConcurrentHashMap<>();

  public MessageWriter() {

  }

  public MessageWriter(Map<String, OutputStream> streams) {
    streams.forEach((id, stream) -> {
      addOutputStream(id, stream);
    });
  }

  public void addOutputStream(String id, OutputStream stream) {
    streams.put(id, stream);
    fileExecutors.put(id, Executors.newSingleThreadExecutor());
  }

  public void closeOutputStream(String id) {
    try {
      ExecutorService service = fileExecutors.remove(id);
      service.shutdown();
      service.awaitTermination(STOP_TIMEOUT, TimeUnit.MILLISECONDS);
    } catch (InterruptedException e) {
      // TODO(doug) - Handle
      logger.error("Error stopping executor id: {} error: {}", id, e);
    }

    try {
      OutputStream stream = streams.remove(id);
      stream.close();
    } catch (IOException e) {
      // TODO(doug) - Handle
      logger.error("Error closing stream id: {} error: {}", id, e);
    }
  }
  
  public void closeOutputStreamAsync(String id) {
    new Thread(new Runnable() {
      @Override
      public void run() {
        closeOutputStream(id);
      }
    }).start();
  }

  public void stop() {
    // Shutdown all file writing threads
    fileExecutors.forEach((id, executor) -> {
      executor.shutdown();
    });

    // Wait for all threads to finish
    fileExecutors.forEach((id, executor) -> {
      try {
        executor.awaitTermination(STOP_TIMEOUT, TimeUnit.MILLISECONDS);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    });

    // Close all file streams
    streams.forEach((id, stream) -> {
      try {
        stream.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    });
  }

  public void writeFrame(String id, MessageLite message) {
    fileExecutors.get(id).execute(new Worker(id, message));
  }

  /**
   * Worker for writing message to file. Assumes that only 1 {@link Worker} per file runs at a time.
   */
  private class Worker implements Runnable {
    private final String id;
    private final MessageLite message;

    public Worker(String id, MessageLite message) {
      this.id = id;
      this.message = message;
    }

    @Override
    public void run() {
      if (!streams.containsKey(id)) {
        throw new RuntimeException("No stream for id: " + id);
      }

      OutputStream stream = streams.get(id);
      try {
        message.writeDelimitedTo(stream);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
  }
}
