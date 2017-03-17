import React, {Component, PropTypes} from "react";
import React3 from "react-three-renderer";
import * as THREE from 'three';
import Stats from "stats.js";
import AllSkeletons from "./AllSkeletons";
import {realTimeFeedSocket} from "../../api/api";
import {Protos} from "../../api/protos";

const width = window.innerWidth * 2 / 3 ;
const height = window.innerWidth  * 2 / 3 ;

let styles = {
  base: {
    height: (width + 20),
    width: (height + 20),
    borderStyle: 'solid',
    borderColor: 'rgba(100, 100, 100, 0.1)',
    borderWidth: 1
  }
};

class RealTimeView extends Component {
  constructor(props) {
    super(props);

    this.cameraPosition = new THREE.Vector3(0, 0, -1);
    this.cameraLookAt = new THREE.Vector3(0, 0, 0);

    this.state = {
      frame: null
    };
  }

  onAnimate = () => {
    this.stats.update();
  };

  componentDidMount() {
    this.stats = new Stats();
    this.stats.domElement.style.position = 'relative';
    this.stats.domElement.style.top = '-' + height + 'px';
    this.refs.container.appendChild(this.stats.domElement);
  }

  _base64ToArrayBuffer = (base64) => {
    let binary_string = window.atob(base64);
    let len = binary_string.length;
    let bytes = new Uint8Array(len);
    for (let i = 0; i < len; i++) {
      bytes[i] = binary_string.charCodeAt(i);
    }
    return bytes;
  };

  componentDidMount() {
    this.socket = realTimeFeedSocket();
    let first = false;
    this.socket.onmessage = (event) => {
      // TODO(doug) - fix the base64 issue in java
      let messageBuffer = this._base64ToArrayBuffer(event.data);
      let frame = Protos.SyncedFrame.decode(messageBuffer);
      // TODO(doug) - remove message logging
      if (!first && frame.skeletons.length > 0) {
        console.log(frame);
        first = true;
      }

      this.setState({
        frame: frame
      });
    };
  }

  componentWillUnmount() {
    if (this.socket) {
      this.socket.close();
    }
    delete this.stats;
  }

  render() {

    if (!this.state.frame) {
      return <div>Loading...</div>;
    }

    return (<div ref="container" style={styles.base}>
      <React3
          mainCamera="camera"
          width={width}
          height={height}
      >
        <scene>
          <perspectiveCamera
              name="camera"
              fov={75}
              aspect={width / height}
              near={0.1}
              far={100}
              position={this.cameraPosition}
              lookAt={this.cameraLookAt}
          />
          <AllSkeletons
              frame={this.state.frame}
          />
        </scene>
      </React3>
    </div>);
  }
}

RealTimeView.contextTypes = {
  router: React.PropTypes.object
};

RealTimeView.propTypes = {
};

export default RealTimeView;
