package edu.bu.vip.kinect.controllerv2.webconsole.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.protobuf.Timestamp;
import edu.bu.vip.kinect.controller.calibration.Protos;
import edu.bu.vip.kinect.util.TimestampUtils;
import java.time.Instant;
import java.util.List;

public class Calibration {
  private long id;
  private String name;
  private Instant dateCreated;
  private List<CalibrationFrame> frames;
  private double error;

  protected Calibration() {
    // Jackson
  }

  public Calibration(Protos.Calibration calibration) {
    this.id = calibration.getId();
    this.name = calibration.getName();
    this.dateCreated = TimestampUtils.from(calibration.getDateCreated());
    // TODO(doug)
  }

  public Calibration(long id, String name, Instant dateCreated, List<CalibrationFrame> frames, double error) {
    this.id = id;
    this.name = name;
    this.dateCreated = dateCreated;
    this.frames = frames;
    this.error = error;
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
  public double getError() {
    return error;
  }

  @JsonProperty
  public List<CalibrationFrame> getFrames() {
    return frames;
  }
}
