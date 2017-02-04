import {connect} from 'react-redux';

import Radium from 'radium';
import React, {Component, PropTypes} from 'react';

import SelectCalibrationView from './SelectCalibrationView';
import NewCalibrationView from './NewCalibrationView';
import SelectSessionView from './SelectSessionView';
import RecordingHomeView from './RecordingHomeView';

let styles = {
  base: {}
};

@Radium
class ControlView extends Component {
  constructor(props) {
    super(props);
  }

  render() {
    if (this.props.controllerState) {

      let view;
      switch (this.props.controllerState.state) {
          // TODO(doug) - Define enums somewhere else
        case 'SELECT_CALIBRATION':
          view = (<SelectCalibrationView
              controllerState={this.props.controllerState}/>);
          break;
        case 'NEW_CALIBRATION':
          view = (<NewCalibrationView controllerState={this.props.controllerState}/>);
          break;
        case 'SELECT_SESSION':
          view = (<SelectSessionView controllerState={this.props.controllerState}/>);
          break;
        case 'SESSION_IDLE':
          view = (<RecordingHomeView controllerState={this.props.controllerState}/>);
          break;
        default:
          console.log("Unknown state: " + this.props.controllerState.state);
          view = (<div>Error</div>);
          break;
      }

      return (<div style={[styles.base]}>
        {view}
      </div>);
    }
    else {
      return (<div>Loading...</div>);
    }
  }
}

ControlView.contextTypes = {
  router: PropTypes.object
};

ControlView.propTypes = {
  controllerState: PropTypes.object.isRequired
};

const mapStateToProps = (state, ownProps) => {
  return {
    controllerState: state.controllerState
  };
};

const mapDispatchToProps = (dispatch) => {
  return {
    handleClick: (event) => {
      dispatch(push(ConsolePage.url()));
    },
  };
};

const ControlPage = connect(mapStateToProps, mapDispatchToProps)(
    ControlView);

const baseUrl = '/';
ControlPage.route = baseUrl;
ControlPage.url = () => baseUrl;

export default ControlPage;
