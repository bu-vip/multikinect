package edu.bu.vip.kinect.controllerv2.webconsole.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.protobuf.Timestamp;

public class Calibration {
  private long id;
  private String name;
  private Timestamp dateCreated;

  public Calibration(long id, String name, Timestamp dateCreated) {
    this.id = id;
    this.name = name;
    this.dateCreated = dateCreated;
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
