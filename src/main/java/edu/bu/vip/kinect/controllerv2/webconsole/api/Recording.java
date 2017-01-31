package edu.bu.vip.kinect.controllerv2.webconsole.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.protobuf.Timestamp;

public class Recording {
  private long sessionId;
  private long id;
  private String name;
  private Timestamp dateCreated;

  public Recording(long sessionId, long id, String name, Timestamp dateCreated) {
    this.sessionId = sessionId;
    this.id = id;
    this.name = name;
    this.dateCreated = dateCreated;
  }

  @JsonProperty
  public long getSessionId() {
    return sessionId;
  }

  @JsonProperty
  public long getId() {
    return id;
  }

  @JsonProperty
  public String getName() {
    return name;
  }

  @JsonProperty
  public Timestamp getDateCreated() {
    return dateCreated;
  }
}
