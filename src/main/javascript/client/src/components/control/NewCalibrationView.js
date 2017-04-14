import React, {Component, PropTypes} from "react";
import DataTable from "../DataTable";
import IconButton from "../IconButton";
import {
  deleteFrameRequest,
  finishCalibrationRequest,
  newFrameRequest
} from "../../api/api";
import {Bar} from "react-chartjs-2";
import {Button, ButtonToolbar, Col, Grid, Row} from "react-bootstrap";
import ToggleDisplay from "react-toggle-display";
import EditCalibrationDialog from "./EditCalibrationDialog";

class NewCalibrationView extends Component {
  constructor(props) {
    super(props);

    this.state = {
      editingInfo: false
    };
  }

  handleEditInfoClick = () => {
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
    this.setState({
      editingInfo: false
    });
  };

  handleNewFrameClick = () => {
    newFrameRequest();
  };

  handleDeleteFrame = (id) => {
    deleteFrameRequest(id);
  };

  handleFinishClicked = () => {
    finishCalibrationRequest();
  };

  render() {
    const controllerState = this.props.controllerState;
    const calibration = controllerState.calibration;
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

    let errorChart = (<div>No data</div>);
    if (calibration.cameraCalibrations != null) {
      errorChart = calibration.cameraCalibrations.map((cameraPair) => {
        const chartKey = cameraPair.cameraA + "-" + cameraPair.cameraB;
        const errorStats = cameraPair.errorStats;

        const numBins = 10;
        const range = errorStats.max - errorStats.min;
        const binSize = range / numBins;
        let bins = [];
        let labels = [];
        for (let i = 0; i < numBins; i++) {
          bins.push(0);
          labels.push((i * binSize + errorStats.min).toFixed(5));
        }

        for (let i = 0; i < errorStats.errors.length; i++) {
          const value = errorStats.errors[i];
          let binIndex = Math.floor((value - errorStats.min) / binSize);
          bins[binIndex]++;
        }

        let chartData = {
          labels: labels,
          datasets: [
            {
              label: "Errors (m)",
              data: bins
            }
          ]
        };

        // Prints the transformation matrix
        let matrix = "\n[";
        for (let i = 0; i < cameraPair.transform.length; i++) {
          matrix += " " + cameraPair.transform[i].toFixed(5);
          if (i % 4 == 3 && i != cameraPair.transform.length - 1) {
            matrix += "\n"
          }
        }
        matrix += "]";

        return (<div key={chartKey}>
          <h3>{chartKey}</h3>
          <Bar
              data={chartData}
          />
          <br/>
          Max: {errorStats.max}
          <br/>
          Min: {errorStats.min}
          <br/>
          Mean: {errorStats.mean}
          <br/>
          Stddev: {errorStats.stddev}
          <br/>
          Num: {errorStats.errors.length}
          <br/>
          <pre>
              Transform: {matrix}
              </pre>
        </div>);
      });
    }

    return (<div>
      <Grid>
        <Row>
          <Col xs={12} md={8}>
            <h1>
              {calibration.name}
              {
                /** Editing is not supported on the server
              <IconButton
                  icon="edit"
                  onClick={this.handleEditInfoClick}/>
                  */
              }
            </h1>
          </Col>
          <Col xs={12} md={4}>
            <ButtonToolbar>
              <Button bsStyle="primary"
                      onClick={this.handleNewFrameClick}>
                New Recording
              </Button>
              <Button bsStyle="success"
                      onClick={this.handleFinishClicked}>
                Finish
              </Button>
            </ButtonToolbar>
          </Col>
        </Row>
        <Row>
          <Col xs={12}>
            <h2>Recordings</h2>
            {recordingsTable}
          </Col>
        </Row>
        <Row>
          <Col xs={12}>
            <h2>Error</h2>
            {errorChart}
          </Col>
        </Row>
      </Grid>
      <ToggleDisplay show={this.state.editingInfo}>
        <div>
          <EditCalibrationDialog
              editing={true}
              initialValues={controllerState.calibration}
              onSaveClick={this.onSaveInfo}
              onCancelClick={this.onCancelEditInfo}
          />
        </div>
      </ToggleDisplay>
    </div>);
  }
}

NewCalibrationView.contextTypes = {
  router: PropTypes.object
};

NewCalibrationView.propTypes = {
  controllerState: PropTypes.object.isRequired
};

export default NewCalibrationView;

