import {connect} from 'react-redux';

import Radium from 'radium';
import React, {Component, PropTypes} from 'react';
import DataTable from '../DataTable';
import IconButton from '../IconButton';
import GlobalStyles from '../GlobalStyles';
import DataForm from './DataForm';
import {newCalibration, selectCalibration, deleteCalibration} from '../../../api/api';

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
    if (this.state.newCalibration) {
      return (<div style={[styles.base]}>
        <DataForm
            title="New Calibration"
            fields={[{
              key: "name",
              title: "Name",
              type: "text"
            }]}
            onSaveClick={this.handleSaveNewCalibration}
            onCancelClick={this.handleCancelNewCalibration}
        />
      </div>);
    } else {
      // Create calibration table
      const header = ["Name", "ID", "Error", "Date Created", ""];
      const tableKeys = ['name', 'id', 'error', 'dateCreated'];
      const calibrationTable = (<DataTable
          header={header}
          idKey="id"
          content={this.props.controllerState.calibrations}
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
  }
}

SelectCalibrationView.contextTypes = {
  router: PropTypes.object
};

SelectCalibrationView.propTypes = {
  controllerState: PropTypes.object.isRequired
};

export default SelectCalibrationView;

