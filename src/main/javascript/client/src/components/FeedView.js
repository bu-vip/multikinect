import Radium from 'radium';
import React, {Component, PropTypes} from 'react';
import {Map} from 'immutable';

import {feedSocket} from '../api/api';
import FeedPanel from './FeedPanel';
import {FeedMessage} from '../api/protos';

let styles = {
    base : {
        padding: 16,
        display: 'flex',
        flexDirection: 'column'
    },
    button: {
        width: 80,
        height: 20
    },
    groupBody: {
        display: 'flex',
        justifyContent: 'flex-start',
        alignItems: 'flex-start',
        flexWrap: 'wrap',
        alignContent: 'flex-start'
    }
};

@Radium
class FeedView extends Component {
  constructor(props) {
    super(props);

    this.state = {
        feeds: new Map()
    };
  }

  _base64ToArrayBuffer = (base64) => {
      var binary_string =  window.atob(base64);
      var len = binary_string.length;
      var bytes = new Uint8Array( len );
      for (var i = 0; i < len; i++)        {
          bytes[i] = binary_string.charCodeAt(i);
      }
      return bytes.buffer;
  };

  componentDidMount() {
    this.socket = feedSocket();
    let first = false;
    this.socket.onmessage = (event) => {
        // TODO(doug) - fix the base64 issue in java
        let feed = FeedMessage.decode(this._base64ToArrayBuffer(event.data));
        // TODO(doug) - remove message logging
        if (!first && feed.frame.skeletons.length > 0) {
            console.log(feed);
            first = true;
        }

        let updatedFeeds = this.state.feeds.set(feed.camera_id, feed);
        this.setState({
            feeds: updatedFeeds
        });
    };
  }

  componentWillUnmount() {
    if (this.socket) {
      this.socket.close();
    }
  }

  render() {
      let panels = [];
      this.state.feeds.forEach((feed, id) => {
          panels.push(<FeedPanel
              key={id}
              feed={feed}
              />);
          return true;
      });

      return (<div style={[styles.base]}>
          <div style={[styles.groupBody]}>{panels}</div>
      </div>);
  }
}

FeedView.contextTypes = {
  router : React.PropTypes.object
};

FeedView.propTypes = {};

export default FeedView;
