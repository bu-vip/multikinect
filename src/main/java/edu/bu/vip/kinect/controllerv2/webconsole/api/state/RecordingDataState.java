package edu.bu.vip.kinect.controllerv2.webconsole.api.state;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.bu.vip.kinect.controllerv2.Controller.State;
import edu.bu.vip.kinect.controllerv2.webconsole.api.Calibration;
import edu.bu.vip.kinect.controllerv2.webconsole.api.Recording;
import edu.bu.vip.kinect.controllerv2.webconsole.api.Session;

public class RecordingDataState extends ControllerState {
  private Calibration calibration;
  private Session session;
  private Recording recordingData;

  public RecordingDataState(
      Calibration calibration,
      Session session, Recording recordingData) {
    super(State.RECORDING_DATA);
    this.calibration = calibration;
    this.session = session;
    this.recordingData = recordingData;
  }

  @JsonProperty
  public Calibration getCalibration() {
    return calibration;
  }

  @JsonProperty
  public Session getSession() {
    return session;
  }

  @JsonProperty
  public Recording getRecordingData() {
    return recordingData;
  }
}
