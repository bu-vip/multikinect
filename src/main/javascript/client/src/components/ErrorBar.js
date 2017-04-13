import React, {Component, PropTypes} from "react";
import {Panel} from "react-bootstrap";

class ErrorBar extends Component {
  constructor(props) {
    super(props);
  }

  render() {
    // Check if there are any errors
    if (this.props.errors === null || this.props.errors.length === 0) {
      return (<div/>);
    } else {
      return (
          <div>
            <Panel header="Error" bsStyle="danger">
              {this.props.errors.map((text) => {
                return <div key={text}> {text} </div>
              })}
            </Panel>
          </div>
      );
    }
  }
}

ErrorBar.propTypes = {
  errors: PropTypes.arrayOf(PropTypes.string)
};

export default ErrorBar;
