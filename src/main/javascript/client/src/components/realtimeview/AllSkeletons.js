import React, {Component, PropTypes} from "react";
import PureRenderMixin from "react/lib/ReactComponentWithPureRenderMixin";
import Skeleton from "./Skeleton";

class AllSkeletons extends Component {
  constructor(props, context) {
    super(props, context);
  }

  componentDidMount() {
  }

  shouldComponentUpdate = PureRenderMixin.shouldComponentUpdate;

  render() {
    if (this.props.frame == null) {
      return <group></group>;
    }

    return (<group>
        {this.props.frame.skeletons.map((skeleton) => {
            return (<Skeleton
              key={skeleton.id}
              skeleton={skeleton.skeleton} />);
          })}
    </group>);
  }
}

AllSkeletons.propTypes = {
  frame: PropTypes.object.isRequired
};

export default AllSkeletons;
