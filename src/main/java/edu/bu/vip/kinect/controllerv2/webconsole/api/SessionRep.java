package edu.bu.vip.kinect.controllerv2.webconsole.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;
import java.util.List;

public class SessionRep {
  private long id;
  private String name;
  private Instant dateCreated;
  private List<RecordingRep> recordingReps;

  protected SessionRep() {
    // Jackson
  }

  public SessionRep(long id, String name, Instant dateCreated, List<RecordingRep> recordingReps) {
    this.id = id;
    this.name = name;
    this.dateCreated = dateCreated;
    this.recordingReps = recordingReps;
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
  public List<RecordingRep> getRecordingReps() {
    return recordingReps;
  }
}
