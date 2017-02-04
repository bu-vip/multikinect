import {connect} from 'react-redux';

import Radium from 'radium';
import React, {Component, PropTypes} from 'react';
import DateTable from './DataTable';
import IconButton from './IconButton';
import GlobalStyles from './GlobalStyles';

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
class SelectSessionView extends Component {
  constructor(props) {
    super(props);
  }

  handleNewSessionClick = (event) => {
    // TODO(doug) - implement
    console.log("New session...");
  };

  handleSelectSession = (id) => {
    // TODO(doug) - implement
    console.log("Select session: " + id);
  };

  handleDeleteSession = (id) => {
    // TODO(doug) - implement
    console.log("Delete session: " + id);

    // Stop propagation so select is not called
    event.stopPropagation();
  };

  render() {
    const controllerState = this.props.controllerState;

    if (controllerState) {
      // Create session table
      const header = ["Name", "ID", "Date Created", ""];
      const tableKeys = ['name', 'id', 'dateCreated'];
      const sessions = (<DateTable
          header={header}
          idKey="id"
          content={controllerState.sessions}
          contentKeys={tableKeys}
          onRowClick={this.handleSelectSession}
          rightIcon="delete"
          onRightIconClick={this.handleDeleteSession}
          emptyMessage="No sessions"
      />);

      return (<div style={[styles.base]}>
        <div style={[styles.titleContainer]}>
          <div style={[styles.title]}>
            <h1>Select Session</h1>
            <p>Select a session from the list below or create a new one by
              clicking on the '+' button on the right:</p>
          </div>
          <IconButton
              icon="add"
              onClick={this.handleNewSessionClick}/>
        </div>
        {sessions}
      </div>);
    }
    else {
      return (<div>Loading...</div>);
    }
  }
}

SelectSessionView.contextTypes = {
  router: PropTypes.object
};

SelectSessionView.propTypes = {
  controllerState: PropTypes.object.isRequired
};

export default SelectSessionView;