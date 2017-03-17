import Radium from 'radium';
import React, {Component, PropTypes} from 'react';
import GlobalStyles from './GlobalStyles';

let styles = {
  button: {
    border: "none",
    background: "none"
  },
  font: {
    ':hover': {
      color: GlobalStyles.accentColor,
    }
  }
};

@Radium
class IconButton extends Component {
  constructor(props) {
    super(props);
  }

  render() {
    return (
        <button
            style={[styles.button]}
            onClick={this.props.onClick}
            >
          <i
              className="material-icons"
              style={[styles.font]}>
            {this.props.icon}
          </i>
        </button>
    );
  }
}

IconButton.propTypes = {
  icon: PropTypes.string.isRequired,
  onClick: PropTypes.func
};

export default IconButton;