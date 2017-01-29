import React, {Component, PropTypes} from 'react';
import THREE from 'three';
import PureRenderMixin from 'react/lib/ReactComponentWithPureRenderMixin';
import toMaterialStyle from 'material-color-hash';

import {JointType} from '../api/protos';

const drawOrder = [
  [JointType.HEAD_, JointType.NECK_],
  [JointType.NECK_, JointType.SPINE_SHOULDER],
  [JointType.SPINE_SHOULDER, JointType.SPINE_MID],
  [JointType.SPINE_MID, JointType.SPINE_BASE],
  // Left leg
  [JointType.SPINE_BASE, JointType.HIP_LEFT],
  [JointType.HIP_LEFT, JointType.KNEE_LEFT],
  [JointType.KNEE_LEFT, JointType.ANKLE_LEFT],
  [JointType.ANKLE_LEFT, JointType.FOOT_LEFT],
  // Right left
  [JointType.SPINE_BASE, JointType.HIP_RIGHT],
  [JointType.HIP_RIGHT, JointType.KNEE_RIGHT],
  [JointType.KNEE_RIGHT, JointType.ANKLE_RIGHT],
  [JointType.ANKLE_RIGHT, JointType.FOOT_RIGHT],
  // Left arm
  [JointType.SPINE_SHOULDER, JointType.SHOULDER_LEFT],
  [JointType.SHOULDER_LEFT, JointType.ELBOW_LEFT],
  [JointType.ELBOW_LEFT, JointType.WRIST_LEFT],
  [JointType.WRIST_LEFT, JointType.HAND_LEFT],
  // Right arm
  [JointType.SPINE_SHOULDER, JointType.SHOULDER_RIGHT],
  [JointType.SHOULDER_RIGHT, JointType.ELBOW_RIGHT],
  [JointType.ELBOW_RIGHT, JointType.WRIST_RIGHT],
  [JointType.WRIST_RIGHT, JointType.HAND_RIGHT],
];

const drawKeys = drawOrder.map((segment) => {
  return segment[0] + "-" + segment[1];
});

class Skeleton extends Component {
  constructor(props, context) {
    super(props, context);

    // Generate a random color based on the skeleton id
    let numString = this.props.skeleton.id.high + " " + this.props.skeleton.id.low;
    let hashStyle = toMaterialStyle(numString);
    this.state = {
        color: new THREE.Color(hashStyle.backgroundColor)
    }
  }

  shouldComponentUpdate = PureRenderMixin.shouldComponentUpdate;

  componentDidMount() {

  }

  componentWillUnmount() {

  }

  render() {
    return (<group>
      {drawOrder.map((segment, index) => {
        let pos1 = this.props.skeleton.joints[segment[0]].position;
        let pos2 = this.props.skeleton.joints[segment[1]].position;
        let vector = new THREE.Vector3(pos2.x - pos1.x, pos2.y - pos1.y, pos2.z - pos1.z);
        let length = vector.length();
        vector.normalize();

        // Check if really small, remove so not to get determinant == 0 warning
        if (length < 0.001) {
            return undefined;
        }

        return (<arrowHelper
          key={drawKeys[index]}
          dir={vector}
          origin={new THREE.Vector3(pos1.x, pos1.y, pos1.z)}
          length={length}
          headLength={0.0000001}
          color={this.state.color}
        />);
      })}
    </group>);
  }
}

Skeleton.propTypes = {
  skeleton: PropTypes.object.isRequired
};

export default Skeleton;
