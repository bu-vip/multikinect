import React, {Component, PropTypes} from "react";
import DateTable from "../DataTable";
import IconButton from "../IconButton";
import {
  cancelSelectSessionRequest,
  createSessionRequest,
  deleteSessionRequest,
  selectSessionRequest
} from "../../api/api";
import EditSessionDialog from "./EditSessionDialog";
import {Button, ButtonToolbar, Col, Grid, Row} from "react-bootstrap";
import ToggleDisplay from "react-toggle-display";

class SelectSessionView extends Component {
  constructor(props) {
    super(props);

    this.state = {
      newSession: false
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

  handleCreateCalibration = () => {
    // TODO(doug)
  };

  render() {
    const header = ["Name", "ID", "Date Created", ""];
    const tableKeys = ['name', 'id', 'dateCreated'];

    return (
        <div>
          <Grid>
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
                  editing={false}
                  onSaveClick={this.handleSaveNewSession}
                  onCancelClick={this.handleCancelNewSession}
                  onCreateCalibration={this.handleCreateCalibration}
              />
            </div>
          </ToggleDisplay>
        </div>
    );
  }
}

SelectSessionView.propTypes = {
  controllerState: PropTypes.object.isRequired
};

export default SelectSessionView;