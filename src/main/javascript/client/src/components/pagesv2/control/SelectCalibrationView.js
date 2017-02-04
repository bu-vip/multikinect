import {connect} from 'react-redux';

import Radium from 'radium';
import React, {Component, PropTypes} from 'react';
import DataTable from '../DataTable';
import IconButton from '../IconButton';
import GlobalStyles from '../GlobalStyles';

let styles = {
  base: {
    padding: GlobalStyles.pagePadding,
    display: 'flex',
    flexDirection: 'column'
  },
  titleContainer: {
    display: 'flex',
    flexDirection: 'row'
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
      // Create calibration table
      const header = ["Name", "ID", "Error", "Date Created", ""];
      const tableKeys = ['name', 'id', 'error', 'dateCreated'];
      const calibrationTable = (<DataTable
          header={header}
          idKey="id"
          content={controllerState.calibrations}
          contentKeys={tableKeys}
          onRowClick={this.handleSelectCalibration}
          rightIcon="delete"
          onRightIconClick={this.handleDeleteCalibration}
          emptyMessage="No calibrations"
      />);

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
        {calibrationTable}
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

export default SelectCalibrationView;

