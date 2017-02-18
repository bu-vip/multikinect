import Radium from 'radium';
import React, {Component, PropTypes} from 'react';
import {Instant, ZonedDateTime, DateTimeFormatter, ZoneId} from 'js-joda';
import Table from './Table';

let styles = {
  base: {}
};

/**
 * Convenience class for creating tables from an array of data models. Handles
 * converting Java Instant classes in JSON to human readable dates.
 */
@Radium
class DataTable extends Component {
  constructor(props) {
    super(props);
  }

  handleRowClick = (dataId) => {
    if (this.props.onRowClick) {
      this.props.onRowClick(dataId);
    }
  };

  handleRightIconClick = (dataId) => {
    if (this.props.onRightIconClick) {
      this.props.onRightIconClick(dataId);
    }
  };

  render() {
    let table = (<div>{this.props.emptyMessage}</div>);
    if (this.props.content && this.props.content.length > 0) {
      const formatter = DateTimeFormatter.ofPattern("hh:mm:ss MM-d-yyyy");
      let rowIds = [];
      const tableContent = this.props.content.map((rowData) => {

        // Enumerate through each key and get the cells content
        let rowContent = [];
        this.props.contentKeys.forEach((key) => {
          let data = (rowData.hasOwnProperty(key) ? rowData[key] : "");
          rowContent.push(data);
        });

        rowIds.push(rowData[this.props.idKey]);
        return rowContent;
      });


      // Create session table
      table = (<Table
          header={this.props.header}
          content={tableContent}
          onRowClick={(rowIndex) => {
            this.handleRowClick(rowIds[rowIndex]);
          }}
          rightIcon={this.props.rightIcon}
          onRightIconClick={(rowIndex) => {
            this.handleRightIconClick(rowIds[rowIndex]);
          }}
      />);
    }

    return (<div style={[styles.base]}>{table}</div>);
  }
}

DataTable.propTypes = {
  header: PropTypes.array,
  idKey: PropTypes.string,
  content: PropTypes.arrayOf(PropTypes.object),
  contentKeys: PropTypes.arrayOf(PropTypes.string),
  onRowClick: PropTypes.func,
  rightIcon: PropTypes.string,
  onRightIconClick: PropTypes.func,
  emptyMessage: PropTypes.string.isRequired
};

export default DataTable;