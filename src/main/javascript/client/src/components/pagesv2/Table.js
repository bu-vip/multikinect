import Radium from 'radium';
import React, {Component, PropTypes} from 'react';
import GlobalStyles from './GlobalStyles';
import IconButton from './IconButton';

let styles = {
  table: {
    width: '100%',
    border: 'none',
    borderCollapse: 'collapse',
    borderSpacing: 0
  },
  titleRow: {},
  dataRow: {
    ':hover': {
      background: GlobalStyles.hoverColor
    }
  },
  allCells: {
    padding: 8,
    borderTop: 'none',
    borderLeft: 'none',
    borderRight: 'none',
    borderBottom: '1px solid rgba(100, 100, 100, 0.2)'
  },
  th: {
    textAlign: 'left',
  },
  td: {},
  iconTd: {
    textAlign: 'right'
  }
};

@Radium
class Table extends Component {
  constructor(props) {
    super(props);
  }

  handleRowClick = (event, rowIndex) => {
    if (this.props.onRowClick) {
      this.props.onRowClick(rowIndex);
    }
  };

  handleRightIconClick = (event, rowIndex) => {
    // Stop propagation to row click
    event.stopPropagation();
    if (this.props.onRightIconClick) {
      this.props.onRightIconClick(rowIndex);
    }
  };

  render() {
    // Process header
    let header = <tr/>
    if (this.props.header) {
      header = (<tr style={[styles.titleRow]}>
        {this.props.header.map((rowData, columnIndex) => {
          const item = (rowData != null ? rowData.toString() : "");
          return (<th key={columnIndex}
                      style={[styles.allCells, styles.th]}>
            {item}
          </th>);
        })}
      </tr>);
    }

    // Process rows
    const rows = this.props.content.map((rowContent, rowIndex) => {
      // Convert the data array into table elements
      const row = rowContent.map((rowData, columnIndex) => {
        const item = (rowData != null ? rowData.toString() : "");
        return (<td key={columnIndex}
                    style={[styles.allCells, styles.td]}>
          {item}
        </td>);
      });

      // Add right icon if needed
      let rightIcon = <td/>;
      if (this.props.rightIcon) {
        rightIcon = (<td style={[styles.allCells, styles.iconTd]}>
          <IconButton
              icon={this.props.rightIcon}
              onClick={(event) => {
                this.handleRightIconClick(event, rowIndex)
              }}
          />
        </td>);
      }

      return (<tr key={rowIndex}
                  style={[styles.dataRow]}
                  onClick={(event) => {
                    this.handleRowClick(event, rowIndex)
                  }}>
        {row}
        {rightIcon}
      </tr>);
    });

    return (
        <table
            style={[styles.table]}>
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