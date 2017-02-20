package edu.bu.vip.multikinect.controller.calibration;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import com.google.common.collect.ImmutableList;
import com.google.common.io.Files;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import edu.bu.vip.multikinect.Protos.Frame;
import edu.bu.vip.multikinect.controller.camera.FrameReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class FileCalibrationDataStore implements CalibrationDataStore {

  private static final String FILE_EXT = ".pbdat";

  private final Logger logger = LoggerFactory.getLogger(getClass());
  private final String rootDir;
  private LoadingCache<Key, OutputStream> streamCache = CacheBuilder.newBuilder()
      .maximumSize(10)
      .expireAfterAccess(10, TimeUnit.MINUTES)
      .removalListener(
          new RemovalListener<Key, OutputStream>() {
            @Override
            public void onRemoval(RemovalNotification<Key, OutputStream> removalNotification) {
              try {
                OutputStream stream = removalNotification.getValue();
                synchronized (stream) {
                  stream.close();
                }
              } catch (IOException e) {
                logger.warn("Error closing cached stream", e);
              }
            }
          }).build(new CacheLoader<Key, OutputStream>() {
        @Override
        public OutputStream load(Key k1) throws Exception {
          File file = new File(getFilePath(k1));
          Files.createParentDirs(file);
          return new FileOutputStream(file);
        }
      });

  @Inject
  public FileCalibrationDataStore(@CalibrationDataLocation String rootDir) {
    this.rootDir = rootDir;
  }

  @Override
  public void storeFrame(long calibrationId, long recordingId, String cameraId, Frame frame) {
    try {
      OutputStream outputStream = streamCache.get(new Key(calibrationId, recordingId, cameraId));
      synchronized (outputStream) {
        frame.writeDelimitedTo(outputStream);
      }
    } catch (ExecutionException | IOException e) {
      logger.error("Error writing frame", e);
    }
  }

  @Override
  public void storeFrames(long calibrationId, long recordingId, String cameraId,
      List<Frame> frames) {

    try {
      OutputStream outputStream = streamCache.get(new Key(calibrationId, recordingId, cameraId));
      synchronized (outputStream) {
        for (Frame frame : frames) {
          frame.writeDelimitedTo(outputStream);
        }
      }
    } catch (ExecutionException | IOException e) {
      logger.error("Error writing frame", e);
    }
  }

  @Override
  public ImmutableList<Frame> getAllFrames(long calibrationId, long recordingId, String cameraId) {
    Key key = new Key(calibrationId, recordingId, cameraId);
    ImmutableList<Frame> frames = null;
    FileInputStream stream = null;
    try {
      stream = new FileInputStream(getFilePath(key));
      frames = FrameReader.readAllFrames(stream);
    } catch (IOException e) {
      logger.error("Error reading frames", e);
    } finally {
      if (stream != null) {
        try {
          stream.close();
        } catch (IOException e) {
          logger.warn("Error closing stream", e);
        }
      }
    }

    return frames;
  }

  @Override
  public ImmutableList<Frame> getAllFramesInInterval(long calibrationId, long recordingId,
      String cameraId, long startTime, long endTime) {
    Key key = new Key(calibrationId, recordingId, cameraId);
    ImmutableList<Frame> frames = null;
    FileInputStream stream = null;
    try {
      stream = new FileInputStream(getFilePath(key));
      frames = FrameReader.readFramesInTimeInterval(stream, startTime, endTime);
    } catch (IOException e) {
      logger.error("Error reading frames", e);
    } finally {
      if (stream != null) {
        try {
          stream.close();
        } catch (IOException e) {
          logger.warn("Error closing stream", e);
        }
      }
    }

    return frames;
  }

  private String getFilePath(Key key) {
    String path = this.rootDir;
    path += File.separator + key.getCalibrationId();
    path += File.separator + key.getRecordingId();
    // TODO(doug) - Careful, camera ids could have bad characters for file names
    path += File.separator + key.getCameraId();
    path += FILE_EXT;
    return path;
  }

  private static class Key {

    private final long calibrationId;
    private final long recordingId;
    private final String cameraId;

    public Key(long calibrationId, long recordingId, String cameraId) {
      this.calibrationId = calibrationId;
      this.recordingId = recordingId;
      this.cameraId = cameraId;
    }

    public long getCalibrationId() {
      return calibrationId;
    }

    public long getRecordingId() {
      return recordingId;
    }

    public String getCameraId() {
      return cameraId;
    }

    @Override
    public int hashCode() {
      return Objects.hash(this.calibrationId, this.recordingId, this.cameraId);
    }

    @Override
    public boolean equals(Object obj) {
      if (obj instanceof Key) {
        Key other = (Key) obj;
        return Objects.equals(other.calibrationId, this.calibrationId) &&
            Objects.equals(other.recordingId, this.recordingId) &&
            Objects.equals(other.cameraId, this.cameraId);
      }

      return false;
    }
  }
}
