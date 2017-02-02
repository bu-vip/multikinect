import {connect} from 'react-redux';

import Radium from 'radium';
import React, {Component, PropTypes} from 'react';
import {Instant, ZonedDateTime, DateTimeFormatter, ZoneId} from 'js-joda';
import Table from './Table';
import IconButton from './IconButton';

let styles = {
  base: {
    padding: 16,
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
class SelectCalibrationView extends Component {
  constructor(props) {
    super(props);
  }

  handleNewCalibrationClick = (event) => {
    // TODO(doug) - implement
    console.log("New calibration...");
  };

  handleSelectCalibration = (id) => {
    // TODO(doug) - implement
    console.log("Select calibration: " + id);
  };

  handleDeleteCalibration = (id) => {
    // TODO(doug) - implement
    console.log("Delete calibration: " + id);

    // Stop propagation so select is not called
    event.stopPropagation();
  };

  render() {
    const controllerState = this.props.controllerState;

    if (controllerState) {
      let calibrations;
      if (controllerState.calibrations && controllerState.calibrations.length
          > 0) {

        // Convert calibrations into table content
        const formatter = DateTimeFormatter.ofPattern("hh:mm:ss MM-d-yyyy");
        let rowIds = [];
        const calibrationTableContent = controllerState.calibrations.map(
            (calibration) => {
              // Get the local date created
              const dateCreated = Instant.ofEpochSecond(
                  calibration.dateCreated.epochSecond,
                  calibration.dateCreated.nano);
              const localTime = ZonedDateTime.ofInstant2(dateCreated,
                  ZoneId.SYSTEM);
              const calibrationDate = localTime.format(formatter);

              rowIds.push(calibration.id);
              return [calibration.name, calibration.id, calibration.error, calibrationDate];
            });

        // Create calibration table
        const header = ["Name", "ID", "Error", "Date Created", ""];
        calibrations = (<Table
            header={header}
            content={calibrationTableContent}
            onRowClick={(rowIndex) => {
              this.handleSelectCalibration(rowIds[rowIndex]);
            }}
            rightIcon="delete"
            onRightIconClick={(rowIndex) => {
              this.handleDeleteCalibration(rowIds[rowIndex]);
            }}
        />);
      } else {
        // Display a no calibrations message
        calibrations = (<div>No calibrations available</div>)
      }

      return (<div style={[styles.base]}>
        <div style={[styles.titleContainer]}>
          <div style={[styles.title]}>
            <h1>Select Calibration</h1>
            <p>Select a calibration from the list below or create a new one by clicking on the '+' button on the right:</p>
          </div>
          <IconButton
              icon="add"
              onClick={this.handleNewCalibrationClick} />
        </div>
        {calibrations}
      </div>);
    }
    else {
      return (<div>Loading...</div>);
    }
  }
}

SelectCalibrationView.contextTypes = {
  router: PropTypes.object
};

SelectCalibrationView.propTypes = {
  controllerState: PropTypes.object.isRequired
};

const mapStateToProps = (state, ownProps) => {
  return {
    controllerState: state.controllerState
  };
};

const mapDispatchToProps = (dispatch) => {
  return {};
};

const SelectCalibrationPage = connect(mapStateToProps, mapDispatchToProps)(
    SelectCalibrationView);

const baseUrl = '/';
SelectCalibrationPage.route = baseUrl;
SelectCalibrationPage.url = () => baseUrl;

export default SelectCalibrationPage;