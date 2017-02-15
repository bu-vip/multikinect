package edu.bu.vip.kinect.controllerv2.webconsole.api.state;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.bu.vip.kinect.controllerv2.Controller.State;
import edu.bu.vip.kinect.controllerv2.webconsole.api.CalibrationRep;
import edu.bu.vip.kinect.controllerv2.webconsole.api.RecordingRep;
import edu.bu.vip.kinect.controllerv2.webconsole.api.SessionRep;

public class RecordingDataState extends ControllerState {
  private CalibrationRep calibration;
  private SessionRep session;
  private RecordingRep recordingData;

  public RecordingDataState(
      CalibrationRep calibration,
      SessionRep session, RecordingRep recordingData) {
    super(State.RECORDING_DATA);
    this.calibration = calibration;
    this.session = session;
    this.recordingData = recordingData;
  }

  @JsonProperty
  public CalibrationRep getCalibration() {
    return calibration;
  }

  @JsonProperty
  public SessionRep getSession() {
    return session;
  }

  @JsonProperty
  public RecordingRep getRecordingData() {
    return recordingData;
  }
}
