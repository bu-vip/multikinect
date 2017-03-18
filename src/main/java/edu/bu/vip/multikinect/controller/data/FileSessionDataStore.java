package edu.bu.vip.multikinect.controller.data;

import com.google.common.collect.ImmutableList;
import com.google.common.io.Files;
import com.google.common.io.PatternFilenameFilter;
import com.google.inject.Inject;
import com.google.protobuf.GeneratedMessageV3;
import edu.bu.vip.kinect.controller.data.Protos.Session;
import edu.bu.vip.kinect.controller.realtime.Protos.SyncedFrame;
import edu.bu.vip.multikinect.Protos.Frame;
import edu.bu.vip.multikinect.controller.data.FileSessionDataStore.Key.Type;
import edu.bu.vip.multikinect.io.MessageWriter;
import edu.bu.vip.multikinect.util.TimestampUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileSessionDataStore implements SessionDataStore {

  private static final String SESSION_FILE_EXT = ".session";

  private static final String DATA_EXT = ".pbdat";
  private static final String RAW_FOLDER = "cameras";
  private static final String PLUGIN_FOLDER = "plugins";
  private static final String SYNC_FILE = "synced";


  private final Logger logger = LoggerFactory.getLogger(getClass());
  private final File rootDir;
  private final MessageWriter<Key> dataWriter;

  public FileSessionDataStore(String rootDirPath) {
    this(new File(rootDirPath));
  }

  public FileSessionDataStore(File rootDir) {
    this.rootDir = rootDir;

    // Check that the data directory exists
    if (!rootDir.exists()) {
      // Try to create parent directories
      try {
        Files.createParentDirs(rootDir);
      } catch (IOException e) {
        throw new RuntimeException("Couldn't create data root directory");
      }
    }

    this.dataWriter = new MessageWriter<>((key) -> {
      OutputStream outputStream;
      try {
        File file;
        switch (key.getType()) {
          case CAMERA:
            file = getRawCameraFile(key);
            break;
          case SYNC:
            file = getSyncedFile(key);
            break;
          case PLUGIN:
            file = getPluginFile(key);
            break;
          default:
            throw new RuntimeException("Unknown key type");
        }
        Files.createParentDirs(file);
        outputStream = new FileOutputStream(file);
      } catch (IOException e) {
        // TODO(doug) - Might be able to recover from this error, but as it's implemented not it will cause the program to crash
        logger.error("Error opening file to write", e);
        throw new RuntimeException("Couldn't open file: " + e.getMessage());
      }

      return outputStream;
    });
  }

  @Override
  public Session createSession(Session session) {
    // Generate a new ID
    Random rand = new Random();
    long newId = Math.abs(rand.nextInt());
    while (sessionExists(newId)) {
      newId = Math.abs(rand.nextInt());
    }

    // Update fields
    Session.Builder builder = session.toBuilder();
    builder.setId(newId);
    builder.setDateCreated(TimestampUtils.now());

    // Save to file
    Session result = builder.build();
    saveSession(result);

    return result;
  }

  @Override
  public Session updateSession(Session session) {
    if (!sessionExists(session.getId())) {
      logger.error("Couldn't find session {} to update", session.getId());
      throw new RuntimeException("Session does not exist: " + session.getId());
    }

    saveSession(session);

    return session;
  }

  @Override
  public Optional<Session> getSession(long id) {
    // Check that the session exists
    if (!sessionExists(id)) {
      return Optional.empty();
    }
    // Load and return
    return Optional.of(loadSession(id));
  }

  @Override
  public ImmutableList<Session> getSessions() {
    ImmutableList.Builder<Session> resultBuilder = ImmutableList.builder();

    // Search for session files
    try {
      Set<FileVisitOption> options = new HashSet<>();
      java.nio.file.Files.walkFileTree(rootDir.toPath(), options, 2, new FileVisitor<Path>() {
        @Override
        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
            throws IOException {
          return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
          // Check that the file is a session
          PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:**" + SESSION_FILE_EXT);
          if (matcher.matches(file)) {
            try (FileInputStream fileStream = new FileInputStream(file.toFile())) {
              // Parse data and add to list
              Session newSession = Session.parseFrom(fileStream);
              resultBuilder.add(newSession);
            } catch (IOException e) {
              logger.error("Error loading file: " + file.toAbsolutePath(), e);
            }
          }

          return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
          return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
          return FileVisitResult.CONTINUE;
        }
      });
    } catch (IOException e) {
      logger.error("Error", e);
    }

    return resultBuilder.build();
  }

  @Override
  public void deleteSession(long id) {
    // Removing the session file effectively removes the session
    if (sessionExists(id)) {
      // TODO(doug) - Delete all data
      File sessionFile = getSessionFile(id);
      if (!sessionFile.delete()) {
        logger.warn("Error deleting file: {}", sessionFile.getAbsolutePath());
      }
    }
  }

  @Override
  public void storeRawFrame(long sessionId, long recordingId, String cameraId, Frame frame) {
    Key key = new Key(sessionId, recordingId, Type.CAMERA, cameraId);
    dataWriter.write(key, frame);
  }

  @Override
  public void storeSyncFrame(long sessionId, long recordingId, SyncedFrame syncedFrame) {
    Key key = new Key(sessionId, recordingId, Type.SYNC, null);
    dataWriter.write(key, syncedFrame);
  }

  @Override
  public void storePluginData(long sessionId, long recordingId, String pluginId,
      GeneratedMessageV3 data) {
    Key key = new Key(sessionId, recordingId, Type.PLUGIN, pluginId);
    dataWriter.write(key, data);
  }

  private File getSessionFolder(long id) {
    return new File(rootDir, Long.toString(id));
  }

  private File getSessionFile(long id) {
    return new File(getSessionFolder(id), id + SESSION_FILE_EXT);
  }

  private File getRecordingFolder(long sessionId, long recordingId) {
    return new File(getSessionFolder(sessionId), Long.toString(recordingId));
  }

  private File getRawCameraFile(Key key) {
    return getRawCameraFile(key.getSessionId(), key.getRecordingId(), key.getTypeKey());
  }

  private File getRawCameraFile(long sessionId, long recordingId, String cameraId) {
    // TODO(doug) - Sanitize for file name
    String sanitizedId = cameraId + DATA_EXT;
    return new File(getRecordingFolder(sessionId, recordingId), sanitizedId);
  }

  private File getPluginFolder(long sessionId, long recordingId) {
    return new File(getRecordingFolder(sessionId, recordingId), PLUGIN_FOLDER);
  }

  private File getPluginFile(Key key) {
    // TODO(doug) - sanitize file name
    String sanitizedId = key.getTypeKey() + DATA_EXT;
    File pluginFolder = getPluginFolder(key.getSessionId(), key.getRecordingId());
    return new File(pluginFolder, sanitizedId);
  }

  private File getSyncedFile(Key key) {
    File recordingFolder = getRecordingFolder(key.getSessionId(), key.getRecordingId());
    return new File(recordingFolder, SYNC_FILE + DATA_EXT);
  }

  private boolean sessionExists(long id) {
    return getSessionFile(id).exists();
  }

  private void saveSession(Session session) {
    File sessionFile = getSessionFile(session.getId());

    // Create parent dirs
    try {
      Files.createParentDirs(sessionFile);
    } catch (IOException e) {
      logger.error("Error saving session", e);
      throw new RuntimeException("Error saving session: " + e.getMessage());
    }

    // Write data to file
    try (FileOutputStream outputStream = new FileOutputStream(sessionFile)) {
      session.writeTo(outputStream);
    } catch (IOException e) {
      logger.error("Error saving session", e);
      throw new RuntimeException("Error saving session: " + e.getMessage());
    }
  }

  private Session loadSession(long id) {
    Session result = null;
    try (FileInputStream inputStream = new FileInputStream(getSessionFile(id))) {
      result = Session.parseFrom(inputStream);
    } catch (IOException e) {
      logger.error("Error loading session", e);
      throw new RuntimeException("Error loading session: " + e.getMessage());
    }

    return result;
  }

  static class Key {

    private final long sessionId;
    private final long recordingId;
    private final Type type;
    private final String typeKey;
    public Key(long sessionId, long recordingId, Type type, String typeKey) {
      this.sessionId = sessionId;
      this.recordingId = recordingId;
      this.type = type;
      this.typeKey = typeKey;
    }

    public long getSessionId() {
      return sessionId;
    }

    public long getRecordingId() {
      return recordingId;
    }

    public Type getType() {
      return type;
    }

    public String getTypeKey() {
      return typeKey;
    }

    @Override
    public int hashCode() {
      return Objects.hash(this.sessionId, this.recordingId, this.type, this.typeKey);
    }

    @Override
    public boolean equals(Object obj) {
      if (obj instanceof Key) {
        Key other = (Key) obj;
        return Objects.equals(other.sessionId, this.sessionId) &&
            Objects.equals(other.recordingId, this.recordingId) &&
            Objects.equals(other.type, this.type) &&
            Objects.equals(other.typeKey, this.typeKey);
      }

      return false;
    }

    public enum Type {
      CAMERA,
      SYNC,
      PLUGIN
    }
  }
}
