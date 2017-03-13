package edu.bu.vip.multikinect.controller.data;

import com.google.common.collect.ImmutableList;
import com.google.protobuf.GeneratedMessageV3;
import edu.bu.vip.kinect.controller.data.Protos.Session;
import edu.bu.vip.kinect.controller.realtime.Protos.SyncedFrame;
import edu.bu.vip.multikinect.Protos.Frame;
import java.util.Optional;

public interface SessionDataStore {

  /**
   * Creates a new session. The id field of the session must be set by the implementation and it
   * must be unique.
   */
  Session createSession(Session session);

  /**
   * Updates an existing session.
   */
  Session updateSession(Session session);

  /**
   * Get the session if it exists.
   */
  Optional<Session> getSession(long id);

  /**
   * Gets all stored sessions.
   */
  ImmutableList<Session> getSessions();

  /**
   * Deletes the session if it exists.
   */
  void deleteSession(long id);

  /**
   * Stores raw frames from a given camera.
   */
  void storeRawFrame(long sessionId, long recordingId, String cameraId, Frame frame);

  /**
   * Store a synced frame.
   * TODO(doug) - Synced frame object
   */
  void storeSyncFrame(long sessionId, long recordingId, SyncedFrame syncedFrame);

  /**
   * Store data from a plugin.
   */
  void storePluginData(long sessionId, long recordingId, String pluginId, GeneratedMessageV3 data);
}
