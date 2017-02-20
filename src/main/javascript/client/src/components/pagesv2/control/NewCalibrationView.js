import {connect} from 'react-redux';

import Radium from 'radium';
import React, {Component, PropTypes} from 'react';
import {push} from 'react-router-redux';
import DataForm from './DataForm';
import {Instant, ZonedDateTime, DateTimeFormatter, ZoneId} from 'js-joda';
import DataTable from '../DataTable';
import IconButton from '../IconButton';
import GlobalStyles from '../GlobalStyles';
import {
  newFrameRequest,
  finishCalibrationRequest,
  deleteFrameRequest
} from '../../../api/api';
import {Bar} from 'react-chartjs-2';

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
      const calibration = controllerState.calibration;
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
          <h2>Error</h2>
          {errorChart}
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

