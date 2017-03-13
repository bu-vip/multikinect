import React, {Component, PropTypes} from 'react';
import THREE from 'three';
import PureRenderMixin from 'react/lib/ReactComponentWithPureRenderMixin';
import toMaterialStyle from 'material-color-hash';

import {Protos} from '../../../api/protos';

class Skeleton extends Component {
  constructor(props, context) {
    super(props, context);

    this.drawOrder = [
      [Protos.JointType.HEAD_, Protos.JointType.NECK_],
      [Protos.JointType.NECK_, Protos.JointType.SPINE_SHOULDER],
      [Protos.JointType.SPINE_SHOULDER, Protos.JointType.SPINE_MID],
      [Protos.JointType.SPINE_MID, Protos.JointType.SPINE_BASE],
      // Left leg
      [Protos.JointType.SPINE_BASE, Protos.JointType.HIP_LEFT],
      [Protos.JointType.HIP_LEFT, Protos.JointType.KNEE_LEFT],
      [Protos.JointType.KNEE_LEFT, Protos.JointType.ANKLE_LEFT],
      [Protos.JointType.ANKLE_LEFT, Protos.JointType.FOOT_LEFT],
      // Right left
      [Protos.JointType.SPINE_BASE, Protos.JointType.HIP_RIGHT],
      [Protos.JointType.HIP_RIGHT, Protos.JointType.KNEE_RIGHT],
      [Protos.JointType.KNEE_RIGHT, Protos.JointType.ANKLE_RIGHT],
      [Protos.JointType.ANKLE_RIGHT, Protos.JointType.FOOT_RIGHT],
      // Left arm
      [Protos.JointType.SPINE_SHOULDER, Protos.JointType.SHOULDER_LEFT],
      [Protos.JointType.SHOULDER_LEFT, Protos.JointType.ELBOW_LEFT],
      [Protos.JointType.ELBOW_LEFT, Protos.JointType.WRIST_LEFT],
      [Protos.JointType.WRIST_LEFT, Protos.JointType.HAND_LEFT],
      // Right arm
      [Protos.JointType.SPINE_SHOULDER, Protos.JointType.SHOULDER_RIGHT],
      [Protos.JointType.SHOULDER_RIGHT, Protos.JointType.ELBOW_RIGHT],
      [Protos.JointType.ELBOW_RIGHT, Protos.JointType.WRIST_RIGHT],
      [Protos.JointType.WRIST_RIGHT, Protos.JointType.HAND_RIGHT],
    ];

    this.drawKeys = this.drawOrder.map((segment) => {
      return segment[0] + "-" + segment[1];
    });

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
      {this.drawOrder.map((segment, index) => {
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
          key={this.drawKeys[index]}
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
