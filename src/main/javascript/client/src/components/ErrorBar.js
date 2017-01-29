import Radium from 'radium';
import React, {Component, PropTypes} from 'react';

const PADDING = 16;
let styles = {
    base : {
      background: 'rgba(200, 0, 0, 0.7)',
      display: 'flex',
      flexDirection: 'column'
    },
    text: {
      paddingLeft: PADDING,
      color: 'white'
    }
};

@Radium
class ErrorBar extends Component {
  constructor(props) { super(props); }

  render() {
    if (this.props.errors == null) {
      return (<div> </div>);
    } else {
      return (<div style={[styles.base]}>
        {this.props.errors.map((text) => {
          return <div key={text} style={[styles.text]}> {text} </div>
        })}
      </div>);
    }
  }
}

ErrorBar.contextTypes = {
  router : PropTypes.object
};

ErrorBar.propTypes = {
  errors : PropTypes.arrayOf(PropTypes.string)
};

export default ErrorBar;
