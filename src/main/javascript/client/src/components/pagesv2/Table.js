import Radium from 'radium';
import React, {Component, PropTypes} from 'react';
import GlobalStyles from './GlobalStyles';
import IconButton from './IconButton';

let styles = {
  row: {},
  th: {
    textAlign: 'left'
  },
  td: {}
};

@Radium
class Table extends Component {
  constructor(props) {
    super(props);
  }

  render() {
    // Process header
    let header = <tr/>
    if (this.props.header) {
      header = (<tr>
        {this.props.header.map((rowData, columnIndex) => {
          const item = (rowData ? rowData.toString() : "");
          return (<th key={columnIndex} style={[styles.th]}>{item}</th>);
        })}
      </tr>);
    }

    // Process rows
    const rows = this.props.content.map((rowContent, rowIndex) => {
      // Convert the data array into table elements
      const row = rowContent.map((rowData, columnIndex) => {
        const item = (rowData ? rowData.toString() : "");
        return (<td key={columnIndex} style={[styles.td]}>{item}</td>);
      });

      // Add right icon if needed
      let rightIcon = <div/>;
      if (this.props.rightIcon) {
        rightIcon = (<td>
          <IconButton
              icon={this.props.rightIcon}
              onClick={this.props.onRightIconClick}
          /></td>);
      }

      return (<tr key={rowIndex}>
        {row}
        {rightIcon}
      </tr>);
    });

    return (
        <table>
          <thead>
          {header}
          </thead>
          <tbody>
          {rows}
          </tbody>
        </table>
    );
  }
}

Table.propTypes = {
  header: PropTypes.array,
  content: PropTypes.array.isRequired,
  onRowClick: PropTypes.func,
  rightIcon: PropTypes.string,
  onRightIconClick: PropTypes.func
};

export default Table;