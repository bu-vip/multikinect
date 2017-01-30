import {connect} from 'react-redux';

import Radium from 'radium';
import React, {Component, PropTypes} from 'react';
import {push} from 'react-router-redux';

import NewCalibrationPage from './NewCalibrationPage';

let styles = {
  base : {
    padding: 16,
    display: 'flex',
    flexDirection: 'column'
  },
  button: {
    width: 120,
    height: 20
  }
};

@Radium
class HomeView extends Component {
  constructor(props) {
    super(props);
  }

  render() {
    return (<div style={[styles.base]}>
      Home Page
      <button
          onClick={this.props.handleClick}>
        New Calibration
      </button>
    </div>);
  }
}

HomeView.contextTypes = {
  router : PropTypes.object
};

HomeView.propTypes = {
  controllerState : PropTypes.object
};

const mapStateToProps = (state, ownProps) => {
  return {};
};

const mapDispatchToProps = (dispatch) => {
  return {
    handleClick: (event) => {
      dispatch(push(NewCalibrationPage.url()));
    },
  };
};

const HomePage = connect(mapStateToProps, mapDispatchToProps)(HomeView);

const baseUrl = '/';
HomePage.route = baseUrl;
HomePage.url = () => baseUrl;

export default HomePage;