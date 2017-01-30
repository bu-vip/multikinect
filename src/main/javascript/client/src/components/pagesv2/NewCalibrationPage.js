import {connect} from 'react-redux';

import Radium from 'radium';
import React, {Component, PropTypes} from 'react';
import {push} from 'react-router-redux';
import EditCalibrationInfoView from './EditCalibrationInfoView';

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
class NewCalibrationView extends Component {
  constructor(props) {
    super(props);

    this.state = {
      // Start as editing info because you are creating a new session
      // TODO(doug) - Check if session info is default values, set false if not
      editingInfo: true
    };
  }

  onEditInfo = () => {
    this.setState({
      editingInfo: true
    });
  };

  onSaveInfo = (info) => {
    // TODO(doug) - send info to server
    this.setState({
      editingInfo: false
    });
  };

  onCancelEditInfo = (info) => {
    this.setState({
      editingInfo: false
    });
  };

  onNewFrame = () => {
    // TODO(doug) - implement
  };

  onDeleteFrame = (id) => {
    console.log("Delete frame: " + id);
    // TODO(doug) - implement
  };

  onFinishClick = () => {
    // TODO(doug) - implement
  };

  render() {

    if (this.state.editingInfo) {

      return (<div style={styles.base}>
        NewCalibration Page
          <EditCalibrationInfoView
              initialInfo={this.props.calibrationSessionInfo}
              onSaveClick={this.onSaveInfo}
              onCancelClick={this.onCancelEditInfo}/>
      </div>);

    } else {

      console.log(this.props);

      const listItems = this.props.calibrationSessionInfo.frames.map((frame) => {
        return (<li key={frame.id}>
          Id: {frame.id}
          Error: {frame.error}
          <button onClick={() => {this.onDeleteFrame(frame.id)}}>Delete</button>
        </li>)
      });


      return (<div style={styles.base}>
        <h2>New Calibration Page</h2>
        <button onClick={this.onEditInfo}>Edit</button>
        <button onClick={this.onNewFrame}>New Frame</button>
        <ul>{listItems}</ul>
        <button onClick={this.onFinishClick}>Finish</button>
      </div>);
    }
  }
}

NewCalibrationView.contextTypes = {
  router : PropTypes.object
};

NewCalibrationView.propTypes = {
  controllerState : PropTypes.object
};

const mapStateToProps = (state, ownProps) => {
  return {
    calibrationSessionInfo: {
      name: "Session XXX",
      frames: [
        {
          id: 123,
          error: 0.4
        },
        {
          id: 456,
          error: 0.6
        }
      ]
    }
  };
};

const mapDispatchToProps = (dispatch) => {
  return {
    handleClick: (event) => {
      dispatch(push(ConsolePage.url()));
    },
  };
};

const NewCalibrationPage = connect(mapStateToProps, mapDispatchToProps)(NewCalibrationView);

const baseUrl = '/newCalibration';
NewCalibrationPage.route = baseUrl;
NewCalibrationPage.url = () => baseUrl;

export default NewCalibrationPage;
