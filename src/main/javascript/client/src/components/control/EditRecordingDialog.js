import {
  Button,
  ControlLabel,
  FormControl,
  FormGroup,
  HelpBlock,
  Modal
} from "react-bootstrap";

import React, {Component, PropTypes} from "react";

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

class EditRecordingDialog extends Component {
  constructor(props) {
    super(props);

    this.state = {
      name: getProperty("name", props.initialValues, "")
    };
  }

  getFormState = () => {
    let formState = {
      name: this.state.name
    };
    return formState;
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

EditRecordingDialog.propTypes = {
  initialValues: PropTypes.object,
  editing: PropTypes.bool.isRequired,
  onSaveClick: PropTypes.func.isRequired,
  onCancelClick: PropTypes.func.isRequired
};

export default EditRecordingDialog;