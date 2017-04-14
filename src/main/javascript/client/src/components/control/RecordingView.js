import Radium from "radium";
import React, {Component, PropTypes} from "react";
import {Duration, Instant} from "js-joda";
import DataTable from "../DataTable";
import GlobalStyles from "../GlobalStyles";
import {stopRecordingRequest} from "../../api/api";
import RealTimeView from "../realtimeview/RealTimeView";
import {Button, ButtonToolbar, Col, Grid, Row} from "react-bootstrap";

let styles = {
  base: {
    padding: GlobalStyles.pagePadding,
    display: 'flex',
    flexDirection: 'column'
  },
  titleContainer: {
    display: 'flex',
    flexDirection: 'dataRow'
  },
  title: {
    flexGrow: 1
  },
  bodyDiv: {
    display: 'flex',
    flexDirection: 'row'
  },
  previewDiv: {
    display: 'flex',
    flexDirection: 'row',
    flex: 6,
    alignItems: 'stretch',
    justifyContent: 'center',
    paddingRight: GlobalStyles.pagePadding
  },
  previewView: {
    flexGrow: 1,
    background: 'red'
  },
  recordingsDiv: {
    flex: 4
  }
};

@Radium
class RecordingView extends Component {
  constructor(props) {
    super(props);
  }

  stopRecording = () => {
    stopRecordingRequest();
  };

  // Taken from: http://stackoverflow.com/a/19700358/7384501
  msToTime = (duration) => {
    let milliseconds = parseInt((duration % 1000) / 100)
        , seconds = parseInt((duration / 1000) % 60)
        , minutes = parseInt((duration / (1000 * 60)) % 60)
        , hours = parseInt((duration / (1000 * 60 * 60)) % 24);

    hours = (hours < 10) ? "0" + hours : hours;
    minutes = (minutes < 10) ? "0" + minutes : minutes;
    seconds = (seconds < 10) ? "0" + seconds : seconds;

    return hours + ":" + minutes + ":" + seconds;
  }

  render() {
    // TODO(doug) - calc stats
    const recording = this.props.controllerState.recording;
    let stats = [];
    stats.push({
      name: "Name",
      value: recording.name
    });
    stats.push({
      name: "Id",
      value: recording.id
    });
    const dateCreated = Instant.parse(recording.dateCreated);
    const timeElapsed = Duration.between(dateCreated, Instant.now());
    stats.push({
      name: "Length",
      value: this.msToTime(timeElapsed.toMillis())
    });

    const tableKeys = ['name', 'value'];

    return (
        <div>
          <Grid>
            <Row>
              <Col xs={12} md={8}>
                <h1>
                  {this.props.controllerState.recording.name}
                </h1>
              </Col>
              <Col xs={12} md={4}>
                <ButtonToolbar>
                  <Button bsStyle="primary"
                          onClick={this.stopRecording}>
                    Stop Recording
                  </Button>
                </ButtonToolbar>
              </Col>
            </Row>
            <Row>
              <RealTimeView />
            </Row>
            <Row>
              <h2>Stats</h2>
              <DataTable
                  idKey="id"
                  content={stats}
                  contentKeys={tableKeys}
                  emptyMessage="No stats"
              />
            </Row>
          </Grid>
        </div>
    );
  }
}

RecordingView.propTypes = {
  controllerState: PropTypes.object.isRequired
};

export default RecordingView;
