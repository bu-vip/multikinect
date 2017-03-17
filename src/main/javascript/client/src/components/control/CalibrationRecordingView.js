import {connect} from 'react-redux';

import Radium from 'radium';
import React, {Component, PropTypes} from 'react';
import IconButton from '../IconButton';
import GlobalStyles from '../GlobalStyles';
import {finishFrameRequest} from '../../api/api';

let styles = {
  base: {
    padding: GlobalStyles.pagePadding,
    display: 'flex',
    flexDirection: 'column'
  }
};

@Radium
class CalibrationRecordingView extends Component {
  constructor(props) {
    super(props);
  }


  handleFinishClicked = () => {
    // TODO(doug) - implement
    console.log("Finished clicked");
    finishFrameRequest();
  };

  render() {
    // TODO(doug) - feeds from each camera
        return (<div style={[styles.base]}>
          <div style={[styles.titleContainer]}>
            <div style={[styles.title]}>
            <IconButton
                icon="done"
                onClick={this.handleFinishClicked}/>
          </div>
        </div>
        </div>);
  }
}

CalibrationRecordingView.contextTypes = {
  router: PropTypes.object
};

CalibrationRecordingView.propTypes = {
  controllerState: PropTypes.object.isRequired
};

export default CalibrationRecordingView;

