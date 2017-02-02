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
    flexDirection: 'row'
  },
  title: {
    flexGrow: 1
  },
  button: {
    width: 120,
    height: 20
  },
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

  handleSelectCalibration = (event, id) => {
    // TODO(doug) - implement
    console.log("Select calibration: " + id);
  };

  handleDeleteCalibration = (event, id) => {
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
        const calibrationTableContent = controllerState.calibrations.map(
            (calibration) => {
              // Get the local date created
              const dateCreated = Instant.ofEpochSecond(
                  calibration.dateCreated.epochSecond,
                  calibration.dateCreated.nano);
              const localTime = ZonedDateTime.ofInstant2(dateCreated,
                  ZoneId.SYSTEM);
              const calibrationDate = localTime.format(formatter);

              return [calibration.name, calibration.error, calibrationDate];
            });

        // Create calibration table
        const header = ["Name", "Error", "Date Created", ""];
        calibrations = (<Table
            header={header}
            content={calibrationTableContent}
            onRowClick={(event, rowIndex) => {
              // TODO(doug)
            }}
            rightIcon="delete"
            onRightIconClick={(event, rowIndex) => {
              this.handleDeleteCalibration(event, calibration.id)
            }}
        />);
      } else {
        // Display a no calibrations message
        calibrations = (<div>No calibrations available</div>)
      }

      return (<div style={[styles.base]}>
        <div style={[styles.titleContainer]}>
          <h1 style={[styles.title]}>Select Calibration</h1>
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