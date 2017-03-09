package edu.bu.vip.multikinect.controller.calibration;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import com.google.common.collect.ImmutableList;
import com.google.common.io.Files;
import com.google.common.io.PatternFilenameFilter;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import edu.bu.vip.kinect.controller.calibration.Protos.Calibration;
import edu.bu.vip.multikinect.Protos.Frame;
import edu.bu.vip.multikinect.controller.camera.FrameReader;
import edu.bu.vip.multikinect.util.TimestampUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A {@link CalibrationDataStore} that stores all data in files.
 *
 * The file structure is:
 * <pre>
 *   rootDir/
 *     # Calibration data folders
 *     calibration-id/
 *       # Calibration data file
 *       calibration-id.calib
 *       # Recording data folders
 *       recording-id/
 *         # Frame data for each camera
 *         camera-id.pbdat
 *         ...
 *       ...
 *     ...
 * </pre>
 */
@Singleton
public class FileCalibrationDataStore implements CalibrationDataStore {

  private static final String CALIBRATION_FILE_EXT = ".calib";
  private static final String FILE_EXT = ".pbdat";

  private final Logger logger = LoggerFactory.getLogger(getClass());
  private final File rootDir;
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
          File file = getFrameFile(k1);
          Files.createParentDirs(file);
          return new FileOutputStream(file);
        }
      });

  @Inject
  public FileCalibrationDataStore(String rootDirPath) {
    // Check that the data directory exists
    this.rootDir = new File(rootDirPath);
    if (!rootDir.exists()) {
      // Try to create parent directories
      try {
        Files.createParentDirs(rootDir);
      } catch (IOException e) {
        throw new RuntimeException("Couldn't create data root directory");
      }
    }
  }

  @Override
  public Calibration createCalibration(Calibration calibration) {
    // Generate a new ID for the calibration
    Random rand = new Random();
    long newId = Math.abs(rand.nextInt());
    while (calibrationExists(newId)) {
      newId = Math.abs(rand.nextInt());
    }

    // Update fields
    Calibration.Builder builder = calibration.toBuilder();
    builder.setId(newId);
    builder.setDateCreated(TimestampUtils.now());

    // Save to file
    Calibration result = builder.build();
    saveCalibration(result);

    return result;
  }


  @Override
  public Calibration updateCalibration(Calibration calibration) {
    // Check that the calibration exists
    if (!calibrationExists(calibration.getId())) {
      logger.error("Couldn't find calibration {} to update", calibration.getId());
      throw new RuntimeException("Calibration does not exist: " + calibration.getId());
    }

    // Save the calibration
    saveCalibration(calibration);

    return calibration;
  }

  @Override
  public Optional<Calibration> getCalibration(long id) {
    // Check that the calibration exists
    if (!calibrationExists(id)) {
      return Optional.empty();
    }
    // Load and return
    return Optional.of(loadCalibration(id));
  }

  @Override
  public ImmutableList<Calibration> getCalibrations() {
    ImmutableList.Builder<Calibration> resultBuilder = ImmutableList.builder();

    // Search for calibration files
    File[] files = rootDir
        .listFiles(new PatternFilenameFilter("*" + File.separator + "*" + CALIBRATION_FILE_EXT));
    if (files != null) {
      for (File calibrationFile : files) {
        try (FileInputStream fileStream = new FileInputStream(calibrationFile)) {
          // Parse data and add to list
          Calibration newCalibration = Calibration.parseFrom(fileStream);
          resultBuilder.add(newCalibration);
        } catch (IOException e) {
          logger.error("Error loading file: " + calibrationFile.getAbsolutePath(), e);
        }
      }
    }

    return resultBuilder.build();
  }

  @Override
  public void deleteCalibration(long id) {
    // Removing the calibration file effectively removes the calibration
    if (calibrationExists(id)) {
      // TODO(doug) - Delete all recordings
      File calibrationFile = getCalibrationFile(id);
      if (!calibrationFile.delete()) {
        logger.warn("Error deleting file: {}", calibrationFile.getAbsolutePath());
      }
    }
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
      stream = new FileInputStream(getFrameFile(key));
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
      stream = new FileInputStream(getFrameFile(key));
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

  private File getCalibrationFile(long id) {
    return new File(getCalibrationFolder(id), id + CALIBRATION_FILE_EXT);
  }

  private File getCalibrationFolder(long id) {
    return new File(rootDir, Long.toString(id));
  }

  private File getRecordingFolder(long calibrationId, long recordingId) {
    return new File(getCalibrationFolder(calibrationId), Long.toString(recordingId));
  }

  private File getFrameFile(Key key) {
    return getFrameFile(key.getCalibrationId(), key.getRecordingId(), key.getCameraId());
  }

  private File getFrameFile(long calibrationId, long recordingId, String cameraId) {
    // TODO(doug) - Sanitize for file name
    String sanitizedId = cameraId;
    return new File(getRecordingFolder(calibrationId, recordingId), sanitizedId);
  }

  private boolean calibrationExists(long id) {
    return getCalibrationFile(id).exists();
  }

  private void saveCalibration(Calibration calibration) {
    File calibrationFile = getCalibrationFile(calibration.getId());

    // Create parent dirs
    try {
      Files.createParentDirs(calibrationFile);
    } catch (IOException e) {
      logger.error("Error saving calibration", e);
      throw new RuntimeException("Error saving calibration: " + e.getMessage());
    }

    // Write data to file
    try (FileOutputStream outputStream = new FileOutputStream(calibrationFile)) {
      calibration.writeTo(outputStream);
    } catch (IOException e) {
      logger.error("Error saving calibration", e);
      throw new RuntimeException("Error saving calibration: " + e.getMessage());
    }
  }

  private Calibration loadCalibration(long id) {
    Calibration result = null;
    try (FileInputStream inputStream = new FileInputStream(getCalibrationFile(id))) {
      result = Calibration.parseFrom(inputStream);
    } catch (IOException e) {
      logger.error("Error loading calibration", e);
      throw new RuntimeException("Error loading calibration: " + e.getMessage());
    }

    return result;
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
