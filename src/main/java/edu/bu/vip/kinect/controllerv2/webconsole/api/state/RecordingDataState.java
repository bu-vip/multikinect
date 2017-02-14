package edu.bu.vip.kinect.controllerv2.webconsole.api.state;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.bu.vip.kinect.controllerv2.Controller.State;
import edu.bu.vip.kinect.controllerv2.webconsole.api.CalibrationRep;
import edu.bu.vip.kinect.controllerv2.webconsole.api.RecordingRep;
import edu.bu.vip.kinect.controllerv2.webconsole.api.SessionRep;

public class RecordingDataState extends ControllerState {
  private CalibrationRep calibrationRep;
  private SessionRep sessionRep;
  private RecordingRep recordingData;

  public RecordingDataState(
      CalibrationRep calibrationRep,
      SessionRep sessionRep, RecordingRep recordingData) {
    super(State.RECORDING_DATA);
    this.calibrationRep = calibrationRep;
    this.sessionRep = sessionRep;
    this.recordingData = recordingData;
  }

  @JsonProperty
  public CalibrationRep getCalibrationRep() {
    return calibrationRep;
  }

  @JsonProperty
  public SessionRep getSessionRep() {
    return sessionRep;
  }

  @JsonProperty
  public RecordingRep getRecordingData() {
    return recordingData;
  }
}
