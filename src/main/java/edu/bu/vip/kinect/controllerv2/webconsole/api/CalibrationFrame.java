package edu.bu.vip.kinect.controllerv2.webconsole.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;

public class CalibrationFrame {
  private long id;
  private double error;
  private Instant dateCreated;

  protected CalibrationFrame() {
    // Jackson
  }

  public CalibrationFrame(long id, double error, Instant dateCreated) {
    this.id = id;
    this.error = error;
    this.dateCreated = dateCreated;
  }

  @JsonProperty
  public long getId() {
    return id;
  }

  @JsonProperty
  public double getError() {
    return error;
  }

  @JsonProperty
  public Instant getDateCreated() {
    return dateCreated;
  }
}
