import {connect} from 'react-redux';

import Radium from 'radium';
import React, {Component, PropTypes} from 'react';
import {Instant, ZonedDateTime, DateTimeFormatter, ZoneId} from 'js-joda';
import DataTable from '../DataTable';
import IconButton from '../IconButton';
import GlobalStyles from '../GlobalStyles';
import DataForm from './DataForm';

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
  },
  bodyDiv: {
    display: 'flex',
    flexDirection: 'row'
  },
  previewDiv: {
    display: 'flex',
    flexDirection: 'row',
    flex: 6,
    alignItems: 'stretch',
    justifyContent: 'center',
    paddingRight: GlobalStyles.pagePadding
  },
  previewView: {
    flexGrow: 1,
    background: 'red'
  },
  recordingsDiv: {
    flex: 4
  }
};

@Radium
class RecordingHomeView extends Component {
  constructor(props) {
    super(props);

    this.state = {
      // Start as editing info because you are creating a new session
      // TODO(doug) - Check if session info is default values, set false if not
      editingInfo: true,
      newRecording: false
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

  onNewRecording = (info) => {
    console.log("new recording");
    // todo(doug) - send
  };

  onCancelNewRecording = (info) => {
    console.log("cancel new recording");
    this.setState({
      newRecording: false
    });
  };

  handleNewRecording = () => {
    // TODO(doug) - implement
    console.log("New recording clicked");
    this.setState({
      newRecording: true
    });
  };

  handleDeleteRecording = (id) => {
    console.log("Delete recording: " + id);
    // TODO(doug) - implement
  };

  render() {
    const controllerState = this.props.controllerState;

    if (controllerState) {
      const session = controllerState.session;
      if (this.state.editingInfo) {
        return (<div style={styles.base}>
          <DataForm
              title="Edit Session"
              initialValues={session}
              fields={[{
                key: "name",
                title: "Name",
                type: "text"
              }]}
              onSaveClick={this.onSaveInfo}
              onCancelClick={this.onCancelEditInfo}/>
        </div>);
      } else if (this.state.newRecording) {
        return (<div style={styles.base}>
          <DataForm
              title="New Recording"
              fields={[
                {
                  key: "name",
                  title: "Name",
                  type: "text"
                }
              ]
              }
              onSaveClick={this.onNewRecording}
              onCancelClick={this.onCancelNewRecording}/>
        </div>);
      } else {
        // Create frames table
        const header = ["Name", "Id", "Date Created", ""];
        const tableKeys = ['name', 'id', 'dateCreated'];
        const recordingsTable = (<DataTable
            header={header}
            idKey="id"
            content={session.recordings}
            contentKeys={tableKeys}
            rightIcon="delete"
            onRightIconClick={this.handleDeleteRecording}
            emptyMessage="No recordings"
        />);

        return (<div style={[styles.base]}>
          <div style={[styles.titleContainer]}>
            <div style={[styles.title]}>
              <h1>{session.name}
                <IconButton
                    icon="edit"
                    onClick={this.handleEditInfoClick}/>
              </h1>
              <p>TODO(doug)</p>
            </div>
            <IconButton
                icon="add"
                onClick={this.handleNewRecording}/>
          </div>
          <div style={[styles.bodyDiv]}>

            <div style={[styles.previewDiv]}>
              <div style={[styles.previewView]}>
                3D View
              </div>
            </div>

            <div style={[styles.recordingsDiv]}>
              <h2>Recordings</h2>
              {recordingsTable}
            </div>
          </div>
        </div>);
      }
    }
    else {
      return (<div>Loading...</div>);
    }
  }
}

RecordingHomeView.contextTypes = {
  router: PropTypes.object
};

RecordingHomeView.propTypes = {
  controllerState: PropTypes.object.isRequired
};

export default RecordingHomeView;
