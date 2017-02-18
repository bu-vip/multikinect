package edu.bu.vip.multikinect.controllerv2.webconsole.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.bu.vip.kinect.controller.calibration.Protos.Calibration;
import edu.bu.vip.kinect.controller.calibration.Protos.Recording;
import edu.bu.vip.multikinect.util.TimestampUtils;
import java.time.Instant;

public class CalibrationRecordingRep {
  private long id;
  private double error;
  private Instant dateCreated;

  protected CalibrationRecordingRep() {
    // Jackson
  }

  public CalibrationRecordingRep(Recording recording) {
    this.id = recording.getId();
    // TODO(doug) - error
    this.dateCreated = TimestampUtils.from(recording.getDateCreated());
  }

  public CalibrationRecordingRep(long id, double error, Instant dateCreated) {
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
