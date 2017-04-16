import React, {Component, PropTypes} from "react";
import {finishFrameRequest} from "../../api/api";
import {Button, Col, Grid, Row} from "react-bootstrap";

class CalibrationRecordingView extends Component {
  constructor(props) {
    super(props);
  }

  handleFinishClicked = () => {
    finishFrameRequest();
  };

  render() {
    // TODO(doug) - feeds from each camera
    return (
        <div>
          <Grid>
            <Row>
              <Col xs={12}>
                <Button
                    bsStyle="danger"
                    onClick={this.handleFinishClicked}
                >Stop Recording</Button>
              </Col>
            </Row>
          </Grid>
        </div>
    );
  }
}

CalibrationRecordingView.propTypes = {
  controllerState: PropTypes.object.isRequired
};

export default CalibrationRecordingView;

