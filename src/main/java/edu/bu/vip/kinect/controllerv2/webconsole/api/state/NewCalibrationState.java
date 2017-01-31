package edu.bu.vip.kinect.controllerv2.webconsole.api.state;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.protobuf.Timestamp;

public class NewCalibrationState extends ControllerState {
  private String name;
  private Timestamp dateCreated;

  public NewCalibrationState(String name, Timestamp dateCreated) {
    super(State.NEW_CALIBRATION);
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
