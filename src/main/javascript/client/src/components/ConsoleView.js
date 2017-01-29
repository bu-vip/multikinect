import Radium from 'radium';
import React, {Component, PropTypes} from 'react';

import {updateModeRequest, MODES} from '../api/api';
import CalibrationPanel from './CalibrationPanel';

let styles = {
  base : {
    padding: 16,
    display: 'flex',
    flexDirection: 'column'
  },
  button: {
    width: 120,
    height: 20
  }
};

@Radium
class ConsoleView extends Component {
  constructor(props) { super(props); }

  sendState = (state) => { updateModeRequest({mode : state}); };

  render() {
    if (this.props.controllerState == null) {
        return (<div style={[styles.base]}>Loading...</div>);
    } else if (this.props.controllerState.mode == MODES.CALIBRATION) {
        return (<div style={[styles.base]}>
            <CalibrationPanel calibrationState={this.props.controllerState.calibration}/>
            <pre>
                {JSON.stringify(this.props.controllerState, null, 2)}
            </pre>
          </div>);
    }
    else {
        return (<div style={[styles.base]}>
              <button
                  style={[styles.button]}
                  onClick={() => (this.sendState('calibration'))}>Calibration</button>
              <button
                  style={[styles.button]}
                  onClick={() => (this.sendState('realtime'))}>Realtime</button>
              <pre>
              {JSON.stringify(this.props.controllerState, null, 2)}
              </pre>
        </div>);
    }
  }
}

ConsoleView.contextTypes = {
  router : PropTypes.object
};

ConsoleView.propTypes = {
  controllerState : PropTypes.object
};

export default ConsoleView;
