package edu.bu.vip.multikinect.io;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import com.google.protobuf.GeneratedMessageV3;
import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * MessageWriter simplifies writing multiple streams of messages to different files concurrently. A
 * key object is used to uniquely identify each stream.
 *
 * @param <K> - The key object. Must implement hashCode() and equals() <b>properly</b>.
 */
public class MessageWriter<K> implements Closeable {

  private final Logger logger = LoggerFactory.getLogger(getClass());

  private Object closeLock = new Object();
  private boolean closed = false;

  private Function<K, OutputStream> streamProducer;
  private LoadingCache<K, OutputStream> streamCache = CacheBuilder.newBuilder()
      .maximumSize(10)
      .expireAfterAccess(10, TimeUnit.MINUTES)
      .removalListener(
          new RemovalListener<K, OutputStream>() {
            @Override
            public void onRemoval(RemovalNotification<K, OutputStream> removalNotification) {
              try {
                OutputStream stream = removalNotification.getValue();
                synchronized (stream) {
                  stream.close();
                }
              } catch (IOException e) {
                logger.warn("Error closing cached stream", e);
              }
            }
          }).build(new CacheLoader<K, OutputStream>() {
        @Override
        public OutputStream load(K k1) throws Exception {
          return streamProducer.apply(k1);
        }
      });

  public MessageWriter(Function<K, OutputStream> streamProducer) {
    this.streamProducer = streamProducer;
  }

  /**
   * Writes a single message.
   */
  public <T extends GeneratedMessageV3> void write(K key, T message) {
    checkClosed();

    try {
      OutputStream outputStream = streamCache.get(key);
      synchronized (outputStream) {
        message.writeDelimitedTo(outputStream);
      }
    } catch (ExecutionException | IOException e) {
      logger.error("Error writing message", e);
    }
  }

  /**
   * Writes a list of messages.
   */
  public <T extends GeneratedMessageV3> void writeAll(K key, List<T> messages) {
    checkClosed();

    try {
      OutputStream outputStream = streamCache.get(key);
      synchronized (outputStream) {
        for (T message : messages) {
          message.writeDelimitedTo(outputStream);
        }
      }
    } catch (ExecutionException | IOException e) {
      logger.error("Error writing message", e);
    }
  }

  private void checkClosed() {
    synchronized (closeLock) {
      if (closed) {
        throw new RuntimeException("MessageWriter is closed");
      }
    }
  }

  /**
   * Closes all open streams. Once closed, the writer can not be used again for writing.
   */
  @Override
  public void close() throws IOException {
    synchronized (closeLock) {
      if (!closed) {
        closed = true;
        // Remove everything from the cache, closing all streams
        streamCache.invalidateAll();
      }
    }
  }
}
