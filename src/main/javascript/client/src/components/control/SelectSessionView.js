import React, {Component, PropTypes} from "react";
import DateTable from "../DataTable";
import {
  createSessionRequest,
  deleteSessionRequest,
  newCalibration,
  selectSessionRequest
} from "../../api/api";
import EditSessionDialog from "./EditSessionDialog";
import {Button, ButtonToolbar, Col, Grid, Row, Panel} from "react-bootstrap";
import ToggleDisplay from "react-toggle-display";
import EditCalibrationDialog from "./EditCalibrationDialog";

class SelectSessionView extends Component {
  constructor(props) {
    super(props);

    this.state = {
      newSession: (this.props.newSessionForm ? true : false),
      newCalibration: false,
      newSessionFormState: this.props.newSessionForm
    };
  }

  handleNewSessionClick = (event) => {
    this.setState({
      newSession: true
    });
  };

  handleSaveNewSession = (info) => {
    createSessionRequest(info);
    this.setState({
      newSession: false
    });
  };

  handleCancelNewSession = (info) => {
    this.props.saveNewSessionState(null);
    this.setState({
      newSession: false
    });
  };

  handleSelectSession = (id) => {
    selectSessionRequest(id);
  };

  handleDeleteSession = (id) => {
    deleteSessionRequest(id);
    // Stop propagation so select is not called
    event.stopPropagation();
  };

  handleCreateCalibration = (info) => {
    this.setState({
      newCalibration: true,
      newSessionFormState: info
    });
  };

  handleSaveNewCalibration = (info) => {
    this.props.saveNewSessionState(this.state.newSessionFormState);
    newCalibration(info);
  };

  handleCancelNewCalibration = (info) => {
    this.setState({
      newCalibration: false
    });
  };

  render() {
    const header = ["Name", "ID", "Date Created", ""];
    const tableKeys = ['name', 'id', 'dateCreated'];

    return (
        <div>
          <Grid>
            <Row>
              <Panel header="Cameras" bsStyle="warning">
                Have you connected the cameras? (Note: This message always appears, even if the cameras are connected.)
              </Panel>
            </Row>
            <Row>
              <Col xs={12} md={8}>
                <h1>Select Session</h1>
              </Col>
              <Col xs={12} md={4}>
                <ButtonToolbar>
                  <Button bsStyle="primary"
                          onClick={this.handleNewSessionClick}>
                    New Session
                  </Button>
                </ButtonToolbar>
              </Col>
            </Row>
            <Row>
              <Col xs={12}>
                <p>
                  Select a session from the list below or create a new one by
                  clicking on the '+' button on the right:
                </p>
              </Col>
            </Row>
            <Row>
              <Col xs={12}>
                <DateTable
                    header={header}
                    idKey="id"
                    content={this.props.controllerState.sessions}
                    contentKeys={tableKeys}
                    onRowClick={this.handleSelectSession}
                    rightIcon="delete"
                    onRightIconClick={this.handleDeleteSession}
                    emptyMessage="No sessions"
                />
              </Col>
            </Row>
          </Grid>
          <ToggleDisplay show={this.state.newSession}>
            <div>
              <EditSessionDialog
                  initialValues={this.props.newSessionForm}
                  editing={false}
                  onSaveClick={this.handleSaveNewSession}
                  onCancelClick={this.handleCancelNewSession}
                  onCreateCalibration={this.handleCreateCalibration}
                  calibrations={this.props.controllerState.calibrations}
              />
            </div>
          </ToggleDisplay>
          <ToggleDisplay show={this.state.newCalibration}>
            <div>
              <EditCalibrationDialog
                  editing={false}
                  onSaveClick={this.handleSaveNewCalibration}
                  onCancelClick={this.handleCancelNewCalibration}
              />
            </div>
          </ToggleDisplay>
        </div>
    );
  }
}

SelectSessionView.propTypes = {
  controllerState: PropTypes.object.isRequired,
  newSessionForm: PropTypes.object,
  saveNewSessionState: PropTypes.func.isRequired
};

export default SelectSessionView;