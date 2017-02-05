import {connect} from 'react-redux';

import Radium from 'radium';
import React, {Component, PropTypes} from 'react';
import {push} from 'react-router-redux';
import {
  Instant,
  ZonedDateTime,
  DateTimeFormatter,
  ZoneId,
  Duration
} from 'js-joda';
import DataTable from '../DataTable';
import IconButton from '../IconButton';
import GlobalStyles from '../GlobalStyles';
import {stopRecordingRequest} from '../../../api/api';

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
    console.log("stop recording");
    // TODO(doug) - implement
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
    const recording = this.props.controllerState.recordingData;
    let stats = [];
    stats.push({
      name: "Name",
      value: recording.name
    });
    stats.push({
      name: "Id",
      value: recording.id
    });
    const dateCreated = Instant.ofEpochSecond(recording.dateCreated.epochSecond,
        recording.dateCreated.nano);
    const timeElapsed = Duration.between(dateCreated, Instant.now());
    stats.push({
      name: "Length",
      value: this.msToTime(timeElapsed.toMillis())
    });

    const tableKeys = ['name', 'value'];
    const statsTable = (<DataTable
        idKey="id"
        content={stats}
        contentKeys={tableKeys}
        emptyMessage="No stats"
    />);

    return (<div style={[styles.base]}>
      <div style={[styles.titleContainer]}>
        <div style={[styles.title]}>
          <h1>{this.props.controllerState.recordingData.name}</h1>
        </div>
        <IconButton
            icon="done"
            onClick={this.stopRecording}/>
      </div>
      <div style={[styles.bodyDiv]}>

        <div style={[styles.previewDiv]}>
          <div style={[styles.previewView]}>
            3D View
          </div>
        </div>

        <div style={[styles.recordingsDiv]}>
          <h2>Stats</h2>
          {statsTable}
        </div>
      </div>
    </div>);
  }
}

RecordingView.propTypes = {
  controllerState: PropTypes.object.isRequired
};

export default RecordingView;
