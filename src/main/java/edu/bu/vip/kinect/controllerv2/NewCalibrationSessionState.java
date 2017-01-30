package edu.bu.vip.kinect.controllerv2;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.protobuf.Timestamp;

public class NewCalibrationSessionState extends ControllerState {
  private String name;
  private Timestamp dateCreated;

  public NewCalibrationSessionState(String name, Timestamp dateCreated) {
    super(State.NEW_CALIBRATION_SESSION);
    this.name = name;
    this.dateCreated = dateCreated;
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
