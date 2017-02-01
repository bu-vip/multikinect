package edu.bu.vip.kinect.controllerv2.webconsole.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;
import java.util.List;

public class Session {
  private long id;
  private String name;
  private Instant dateCreated;
  private List<Recording> recordings;

  public Session(long id, String name, Instant dateCreated, List<Recording> recordings) {
    this.id = id;
    this.name = name;
    this.dateCreated = dateCreated;
    this.recordings = recordings;
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
  public Instant getDateCreated() {
    return dateCreated;
  }

  @JsonProperty
  public List<Recording> getRecordings() {
    return recordings;
  }
}
