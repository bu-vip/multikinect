import Radium from 'radium';
import React, {Component, PropTypes} from 'react';

import {sendGetStateRequest, updateModeRequest, MODES, updateCalibrationRequest} from '../api/api';

let styles = {
  base : {
    display : 'flex',
    flexDirection : 'column'
  },
  button: {
    width: 120,
    height: 20
  }
};

@Radium
class CalibrationView extends Component {
  constructor(props) {
    super(props);

    this.state = {state : null};
  }

  componentDidMount() {}

  componentWillUnmount() {}

  startRecording = () => {
    updateCalibrationRequest({
        recording: true
    });
  };

  stopRecording = () => {
    updateCalibrationRequest({
        recording: false
    });
  };

  finishSession = () => {
      updateModeRequest({mode: MODES.IDLE});
  };

  render() {
    if (this.props.calibrationState.recording) {
        return (<div style={[styles.base]}>
          <button
            style={[styles.button]}
            onClick={() => this.stopRecording()}>Stop Recording</button>
        </div>);
    } else {
        return (<div style={[styles.base]}>
          <button
            style={[styles.button]}
            onClick={() => this.startRecording()}>Start Recording</button>
          <button
            style={[styles.button]}
            onClick={() => this.finishSession()}>Finish Calibration</button>
        </div>);
    }
  }
}

CalibrationView.contextTypes = {
  router : PropTypes.object
};

CalibrationView.propTypes = {
  calibrationState: PropTypes.object
};

export default CalibrationView;
