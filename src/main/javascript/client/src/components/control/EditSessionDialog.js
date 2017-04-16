import {
  Button,
  ControlLabel,
  FormControl,
  FormGroup,
  HelpBlock,
  Modal
} from "react-bootstrap";

import React, {Component, PropTypes} from "react";
import ToggleDisplay from "react-toggle-display";

function FieldGroup({id, label, help, ...props}) {
  return (
      <FormGroup controlId={id}>
        <ControlLabel>{label}</ControlLabel>
        <FormControl {...props} />
        {help && <HelpBlock>{help}</HelpBlock>}
      </FormGroup>
  );
}

function getProperty(propName, initial, defaultVal) {
  let result = defaultVal;
  if (initial && initial[propName]) {
    result = initial[propName];
  }
  return result;
}

const KEY_NAME = "name";
const KEY_CAL_ID = "calibrationId";

class EditSessionDialog extends Component {
  constructor(props) {
    super(props);

    this.state = {
      name: getProperty(KEY_NAME, props.initialValues, ""),
      calibrationId: getProperty(KEY_CAL_ID, props.initialValues, undefined)
    };
  }

  getFormState = () => {
    let formState = {
      [KEY_NAME]: this.state.name,
      [KEY_CAL_ID]: this.state.calibrationId
    };
    return formState;
  };

  handleSaveClick = () => {
    this.props.onSaveClick(this.getFormState());
  };

  handleCancelClick = () => {
    this.props.onCancelClick(this.getFormState());
  };

  handleCreateCalibrationClick = () => {
    this.props.onCreateCalibration(this.getFormState());
  };

  handleInputChange = (event) => {
    const target = event.target;
    const value = target.type === 'checkbox' ? target.checked : target.value;
    const fieldName = target.id;
    this.setState({
      [fieldName]: value
    });
  };

  render() {
    return (
        <div className="static-modal">
          <Modal.Dialog>
            <Modal.Header>
              <Modal.Title>
                {(this.props.editing ? "Edit Session"
                    : "New Session")}
              </Modal.Title>
            </Modal.Header>

            <Modal.Body>
              <form>
                <FieldGroup
                    id="name"
                    type="text"
                    label="Name"
                    placeholder="Enter Name"
                    value={this.state.name}
                    onChange={this.handleInputChange}
                />
                <ToggleDisplay hide={this.props.editing}>
                  <FormGroup>
                    <ControlLabel>Calibration</ControlLabel>
                    <FormControl
                        id="calibrationId"
                        onChange={this.handleInputChange}
                        componentClass="select"
                        placeholder="select">
                      <option value="select">Select a Calibration</option>
                      {this.props.calibrations &&
                        this.props.calibrations.map(calibration => {
                        return (
                            <option
                                key={calibration.id}
                                value={calibration.id}>
                              {calibration.id}:{calibration.name}
                            </option>
                        );
                      })}
                    </FormControl>
                  </FormGroup>
                  <Button onClick={this.handleCreateCalibrationClick}>
                    Add Calibration
                  </Button>
                </ToggleDisplay>
              </form>
            </Modal.Body>

            <Modal.Footer>
              <Button
                  onClick={this.handleCancelClick}
              >
                Cancel
              </Button>
              <Button bsStyle="primary"
                      onClick={this.handleSaveClick}
              >
                {(this.props.editing ? "Save" : "Create")}
              </Button>
            </Modal.Footer>

          </Modal.Dialog>
        </div>
    );
  }
}

EditSessionDialog.propTypes = {
  initialValues: PropTypes.object,
  editing: PropTypes.bool.isRequired,
  onSaveClick: PropTypes.func.isRequired,
  onCancelClick: PropTypes.func.isRequired,
  onCreateCalibration: PropTypes.func,
  calibrations: PropTypes.array,
};

export default EditSessionDialog;