import React, {Component, PropTypes} from 'react';
import React3 from 'react-three-renderer';
import THREE from 'three';
import ReactDOM from 'react-dom';
import Stats from 'stats.js';

import AllSkeletons from './AllSkeletons';

const width = window.innerWidth / 2.2;
const height = window.innerWidth / 2.2;

let styles = {
    base: {
        height: (width + 20),
        width: (height + 20),
        borderStyle: 'solid',
        borderColor: 'rgba(100, 100, 100, 0.1)',
        borderWidth: 1
    }
};

class FeedPanel extends Component {
  constructor(props) {
    super(props);

    this.cameraPosition = new THREE.Vector3(0, 0, -1);
    this.cameraLookAt = new THREE.Vector3(0, 0, 0);
  }

  onAnimate = () => { this.stats.update(); };

  componentDidMount() {
    this.stats = new Stats();
    this.stats.domElement.style.position = 'relative';
    this.stats.domElement.style.top = '-' + height + 'px';
    this.refs.container.appendChild(this.stats.domElement);
  }

  componentWillUnmount() { delete this.stats; }

  render() {
      return (<div ref="container" style={styles.base}>
                <div>Camera: {this.props.feed.camera_id}</div>
                  <React3
                      mainCamera="camera"
                      width={width}
                      height={height}
                      onAnimate={this.onAnimate}
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
                              frame={this.props.feed.frame}
                              />
                      </scene>
                  </React3>
              </div>);
    }
}

FeedPanel.contextTypes = {
  router : React.PropTypes.object
};

FeedPanel.propTypes = {
  feed: PropTypes.object.isRequired
};

export default FeedPanel;
