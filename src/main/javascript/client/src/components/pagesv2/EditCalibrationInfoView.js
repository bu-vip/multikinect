import Radium from 'radium';
import React, {Component, PropTypes} from 'react';

let styles = {
  base : {
  },
};

@Radium
class EditCalibrationInfoView extends Component {
  constructor(props) {
    super(props);

    this.state = {
      name: props.initialInfo.name
    };
  }

  getFormState = () => {
    return {
      name: this.state.name
    };
  };

  handleSaveClick = () => {
    this.props.onSaveClick(this.getFormState());
  };

  handleCancelClick = () => {
    this.props.onCancelClick(this.getFormState());
  };

  handleInputChange = (event) => {
    const target = event.target;
    const value = target.type === 'checkbox' ? target.checked : target.value;
    const fieldName = target.name;
    this.setState({
      [fieldName]: value
    });
  };

  render() {
    return (<div style={[styles.base]}>
      <h1>Edit Calibration</h1>
      <form>
        <label>
          Name:
          <input
              name="name"
              type="text"
              value={this.state.name}
              onChange={this.handleInputChange} />
        </label>
      </form>
      <button onClick={this.handleSaveClick}>Save</button>
      <button onClick={this.handleCancelClick}>Cancel</button>
    </div>)
  }
}

EditCalibrationInfoView.propTypes = {
  initialInfo: PropTypes.object.isRequired,
  onSaveClick: PropTypes.func.isRequired,
  onCancelClick: PropTypes.func.isRequired
};

export default EditCalibrationInfoView;