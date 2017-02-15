import {connect} from 'react-redux';

import Radium from 'radium';
import React, {Component, PropTypes} from 'react';
import {push} from 'react-router-redux';
import DataForm from './DataForm';
import {Instant, ZonedDateTime, DateTimeFormatter, ZoneId} from 'js-joda';
import DataTable from '../DataTable';
import IconButton from '../IconButton';
import GlobalStyles from '../GlobalStyles';
import {newFrameRequest, finishCalibrationRequest, deleteFrameRequest} from '../../../api/api';

let styles = {
  base: {
    padding: GlobalStyles.pagePadding,
    display: 'flex',
    flexDirection: 'column'
  },
  titleContainer: {
    display: 'flex',
    flexDirection: 'dataRow'
  },
  title: {
    flexGrow: 1
  }
};

@Radium
class NewCalibrationView extends Component {
  constructor(props) {
    super(props);

    this.state = {
      // Start as editing info because you are creating a new session
      // TODO(doug) - Check if session info is default values, set false if not
      editingInfo: false
    };
  }

  handleEditInfoClick = () => {
    console.log("Edit info");
    this.setState({
      editingInfo: true
    });
  };

  onSaveInfo = (info) => {
    // TODO(doug) - send info to server
    console.log("Edit info save");
    this.setState({
      editingInfo: false
    });
  };

  onCancelEditInfo = (info) => {
    console.log("Edit info cancelled");
    this.setState({
      editingInfo: false
    });
  };

  handleNewFrameClick = () => {
    // TODO(doug) - implement
    console.log("New Frame clicked");
    newFrameRequest();
  };

  handleDeleteFrame = (id) => {
    console.log("Delete frame: " + id);
    // TODO(doug) - implement
    deleteFrameRequest(id);
  };

  handleFinishClicked = () => {
    // TODO(doug) - implement
    console.log("Finished clicked");
    finishCalibrationRequest();
  };

  render() {
    const controllerState = this.props.controllerState;

    if (controllerState) {
      const calibration = controllerState.newCalibration;
      if (this.state.editingInfo) {

        return (<div style={styles.base}>
          <DataForm
              initialValues={calibration}
              fields={[{
                key: "name",
                title: "Name",
                type: "text"
              }]}
              onSaveClick={this.onSaveInfo}
              onCancelClick={this.onCancelEditInfo}/>
        </div>);

      } else {
        // Create frames table
        const header = ["ID", "Error", "Date Created", ""];
        const tableKeys = ['id', 'error', 'dateCreated'];
        const recordingsTable = (<DataTable
            header={header}
            idKey="id"
            content={calibration.recordings}
            contentKeys={tableKeys}
            rightIcon="delete"
            onRightIconClick={this.handleDeleteFrame}
            emptyMessage="No frames"
        />);

        return (<div style={[styles.base]}>
          <div style={[styles.titleContainer]}>
            <div style={[styles.title]}>
              <h1>{calibration.name}
              <IconButton
                  icon="edit"
                  onClick={this.handleEditInfoClick}/>
              </h1>
              <p>Error: {calibration.error}</p>
              <p>TODO(doug)</p>
            </div>
            <IconButton
                icon="add"
                onClick={this.handleNewFrameClick}/>
            <IconButton
                icon="done"
                onClick={this.handleFinishClicked}/>
          </div>
          <h2>Recordings</h2>
          {recordingsTable}
        </div>);
      }
    }
    else {
      return (<div>Loading...</div>);
    }
  }
}

NewCalibrationView.contextTypes = {
  router: PropTypes.object
};

NewCalibrationView.propTypes = {
  controllerState: PropTypes.object.isRequired
};

export default NewCalibrationView;

