import Radium from "radium";
import React, {Component, PropTypes} from "react";
import DateTable from "../DataTable";
import DataForm from "../DataForm";
import IconButton from "../IconButton";
import GlobalStyles from "../GlobalStyles";
import {
  createSessionRequest,
  selectSessionRequest,
  deleteSessionRequest,
  cancelSelectSessionRequest
} from "../../api/api";

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
    //  TODO(doug) implement
    console.log("Create new session");
    createSessionRequest(info);
    this.setState({
      newSession: false
    });
  };

  handleCancelNewSession = (info) => {
    console.log("Cancel new session");
    this.setState({
      newSession: false
    });
  };

  handleSelectSession = (id) => {
    // TODO(doug) - implement
    console.log("Select session: " + id);
    selectSessionRequest(id);
  };

  handleDeleteSession = (id) => {
    // TODO(doug) - implement
    console.log("Delete session: " + id);

    deleteSessionRequest(id);

    // Stop propagation so select is not called
    event.stopPropagation();
  };

  handleBackNavigation = () => {
    cancelSelectSessionRequest();
  };

  render() {
    if (this.state.newSession) {
      return (<div style={[styles.base]}>
        <DataForm
            title="New Session"
            fields={[{
              key: "name",
              title: "Name",
              type: "text"
            }]}
            onSaveClick={this.handleSaveNewSession}
            onCancelClick={this.handleCancelNewSession}
        />
      </div>);
    } else {
      // Create session table
      const header = ["Name", "ID", "Date Created", ""];
      const tableKeys = ['name', 'id', 'dateCreated'];
      const sessions = (<DateTable
          header={header}
          idKey="id"
          content={this.props.controllerState.sessions}
          contentKeys={tableKeys}
          onRowClick={this.handleSelectSession}
          rightIcon="delete"
          onRightIconClick={this.handleDeleteSession}
          emptyMessage="No sessions"
      />);

      return (<div style={[styles.base]}>
        <div style={[styles.titleContainer]}>
          <IconButton
              icon="arrow_back"
              onClick={this.handleBackNavigation}/>
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
  }
}

SelectSessionView.contextTypes = {
  router: PropTypes.object
};

SelectSessionView.propTypes = {
  controllerState: PropTypes.object.isRequired
};

export default SelectSessionView;