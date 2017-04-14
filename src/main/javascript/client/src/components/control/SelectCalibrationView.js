import React, {Component, PropTypes} from "react";
import DataTable from "../DataTable";
import {
  deleteCalibration,
  newCalibration,
  selectCalibration
} from "../../api/api";
import {Button, ButtonToolbar, Col, Grid, Row} from "react-bootstrap";
import EditCalibrationDialog from "./EditCalibrationDialog";
import ToggleDisplay from "react-toggle-display";
import "react-table/react-table.css";

class SelectCalibrationView extends Component {
  constructor(props) {
    super(props);

    this.state = {
      newCalibration: false
    }
  }

  handleNewCalibrationClick = (event) => {
    // TODO(doug) - implement
    console.log("New calibration...");
    this.setState({
      newCalibration: true
    });
  };

  handleSelectCalibration = (id) => {
    selectCalibration(id);
  };

  handleDeleteCalibration = (id) => {
    console.log("Delete calibration: " + id);
    deleteCalibration(id);

    // Stop propagation so select is not called
    event.stopPropagation();
  };

  handleSaveNewCalibration = (info) => {
    //  TODO(doug) implement
    console.log("Create new calibration");
    newCalibration(info);
  };

  handleCancelNewCalibration = (info) => {
    console.log("Cancel new calibration");
    this.setState({
      newCalibration: false
    });
  };

  render() {
    // Create calibration table
    const header = ["Name", "ID", "Date Created", ""];
    const tableKeys = ['name', 'id', 'dateCreated'];

    //const formatter = DateTimeFormatter.ofPattern("hh:mm:ss MM-d-yyyy");
    //formatter.format(ZonedDateTime.parse(props.value))
    return (
        <div>
          <Grid>
            <Row>
              <Col xs={12} md={8}>
                <h1>Select Calibration</h1>
              </Col>
              <Col xs={12} md={4}>
                <ButtonToolbar>
                  <Button bsStyle="primary"
                          onClick={this.handleNewCalibrationClick}>
                    New Calibration
                  </Button>
                </ButtonToolbar>
              </Col>
            </Row>
            <Row>
              <Col xs={12}>
                <p>
                  Select a calibration from the list below or create a new one
                  by clicking on the '+' button on the right:
                </p>
              </Col>
            </Row>
            <Row>
              <Col xs={12}>
                <DataTable
                    header={header}
                    idKey="id"
                    content={this.props.controllerState.calibrations}
                    contentKeys={tableKeys}
                    onRowClick={this.handleSelectCalibration}
                    rightIcon="delete"
                    onRightIconClick={this.handleDeleteCalibration}
                    emptyMessage="No calibrations"
                />
              </Col>
            </Row>
          </Grid>
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

SelectCalibrationView.contextTypes = {
  router: PropTypes.object
};

SelectCalibrationView.propTypes = {
  controllerState: PropTypes.object.isRequired
};

export default SelectCalibrationView;

