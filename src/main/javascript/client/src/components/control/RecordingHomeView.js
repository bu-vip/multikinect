import React, {Component, PropTypes} from "react";
import DataTable from "../DataTable";
import IconButton from "../IconButton";
import {
  createRecordingRequest,
  deleteRecordingRequest,
  finishSessionRequest
} from "../../api/api";
import RealTimeView from "../realtimeview/RealTimeView";
import ToggleDisplay from "react-toggle-display";
import EditSessionDialog from "./EditSessionDialog";
import EditRecordingDialog from "./EditRecordingDialog";
import {Button, ButtonToolbar, Col, Grid, Row} from "react-bootstrap";

class RecordingHomeView extends Component {
  constructor(props) {
    super(props);

    this.state = {
      editingInfo: false,
      newRecording: false
    };
  }

  handleEditInfoClick = () => {
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

  onNewRecording = (info) => {
    createRecordingRequest(info);
  };

  onCancelNewRecording = (info) => {
    this.setState({
      newRecording: false
    });
  };

  handleNewRecording = () => {
    this.setState({
      newRecording: true
    });
  };

  handleDeleteRecording = (id) => {
    deleteRecordingRequest(id);
  };

  handleBackNavigation = () => {
    finishSessionRequest();
  };

  render() {
    const controllerState = this.props.controllerState;
    const session = controllerState.session;
    // Create frames table
    const header = ["Name", "Id", "Date Created", ""];
    const tableKeys = ['name', 'id', 'dateCreated'];

    return (
        <div>
          <Grid>
            <Row>
              <Col xs={12} md={8}>
                <h1>
                  {session.name}
                  {/** Editing session is not implemented on server
                   <IconButton
                   icon="edit"
                   onClick={this.handleEditInfoClick}/>
                   */}
                </h1>
              </Col>
              <Col xs={12} md={4}>
                <ButtonToolbar>
                  <Button bsStyle="primary"
                          onClick={this.handleNewRecording}>
                    New Recording
                  </Button>
                  <Button bsStyle="warning"
                          onClick={this.handleBackNavigation}>
                    Close Session
                  </Button>
                </ButtonToolbar>
              </Col>
            </Row>
            <Row>
              <RealTimeView />
            </Row>
            <Row>
              <h2>Recordings</h2>
              <DataTable
                  header={header}
                  idKey="id"
                  content={session.recordings}
                  contentKeys={tableKeys}
                  rightIcon="delete"
                  onRightIconClick={this.handleDeleteRecording}
                  emptyMessage="No recordings"
              />
            </Row>
          </Grid>
          <ToggleDisplay show={this.state.editingInfo}>
            <div>
              <EditSessionDialog
                  editing={true}
                  initialValues={session}
                  onSaveClick={this.onSaveInfo}
                  onCancelClick={this.onCancelEditInfo}
              />
            </div>
          </ToggleDisplay>
          <ToggleDisplay show={this.state.newRecording}>
            <div>
              <EditRecordingDialog
                  editing={false}
                  onSaveClick={this.onNewRecording}
                  onCancelClick={this.onCancelNewRecording}
              />
            </div>
          </ToggleDisplay>
        </div>
    );
  }
}

RecordingHomeView.propTypes = {
  controllerState: PropTypes.object.isRequired
};

export default RecordingHomeView;
