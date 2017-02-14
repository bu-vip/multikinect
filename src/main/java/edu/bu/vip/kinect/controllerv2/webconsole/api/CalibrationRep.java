package edu.bu.vip.kinect.controllerv2.webconsole.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.bu.vip.kinect.controller.calibration.Protos;
import edu.bu.vip.kinect.util.TimestampUtils;
import java.time.Instant;
import java.util.List;

public class CalibrationRep {
  private long id;
  private String name;
  private Instant dateCreated;
  private List<CalibrationRecordingRep> recordings;
  private double error;

  protected CalibrationRep() {
    // Jackson
  }

  public CalibrationRep(Protos.Calibration calibration) {
    this.id = calibration.getId();
    this.name = calibration.getName();
    this.dateCreated = TimestampUtils.from(calibration.getDateCreated());
    // TODO(doug)
  }

  public CalibrationRep(long id, String name, Instant dateCreated, List<CalibrationRecordingRep> recordings, double error) {
    this.id = id;
    this.name = name;
    this.dateCreated = dateCreated;
    this.recordings = recordings;
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
  public List<CalibrationRecordingRep> getRecordings() {
    return recordings;
  }
}