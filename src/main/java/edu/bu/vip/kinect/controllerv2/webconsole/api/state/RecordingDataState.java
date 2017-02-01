package edu.bu.vip.kinect.controllerv2.webconsole.api.state;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.bu.vip.kinect.controllerv2.Controllerv2.State;
import edu.bu.vip.kinect.controllerv2.webconsole.api.Calibration;
import edu.bu.vip.kinect.controllerv2.webconsole.api.Recording;
import edu.bu.vip.kinect.controllerv2.webconsole.api.Session;

public class RecordingDataState extends ControllerState {
  private Calibration currentCalibration;
  private Session currentSession;
  private Recording recordingData;

  public RecordingDataState(
      Calibration currentCalibration,
      Session currentSession, Recording recordingData) {
    super(State.RECORDING_DATA);
    this.currentCalibration = currentCalibration;
    this.currentSession = currentSession;
    this.recordingData = recordingData;
  }

  @JsonProperty
  public Calibration getCurrentCalibration() {
    return currentCalibration;
  }

  @JsonProperty
  public Session getCurrentSession() {
    return currentSession;
  }

  @JsonProperty
  public Recording getRecordingData() {
    return recordingData;
  }
}
