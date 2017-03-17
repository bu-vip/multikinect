import Radium from 'radium';
import React, {Component, PropTypes} from 'react';

let styles = {
  base: {},
};

@Radium
class DataForm extends Component {
  constructor(props) {
    super(props);

    this.state = {
      ...this.props.initialValues
    };
  }

  getFormState = () => {
    let formState = {};
    this.props.fields.forEach((fieldInfo) => {
      formState[fieldInfo.key] = this.state[fieldInfo.key];
    });

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
    const fieldName = target.name;
    this.setState({
      [fieldName]: value
    });
  };

  render() {
    let title = <div/>;
    if (this.props.title) {
      title = <h1>{this.props.title}</h1>;
    }

    const fields = this.props.fields.map((fieldInfo) => {
      return (
          <label key={fieldInfo.key}>
            {fieldInfo.title}
            <input
                name={fieldInfo.key}
                type={fieldInfo.type}
                value={this.state[fieldInfo.key]}
                onChange={this.handleInputChange}/>
          </label>);
    });

    return (<div style={[styles.base]}>
      {title}
      <form>
        {fields}
      </form>
      <button onClick={this.handleSaveClick}>Save</button>
      <button onClick={this.handleCancelClick}>Cancel</button>
    </div>)
  }
}

DataForm.propTypes = {
  title: PropTypes.string,
  initialValues: PropTypes.object,
  fields: PropTypes.arrayOf(PropTypes.object).isRequired,
  onSaveClick: PropTypes.func.isRequired,
  onCancelClick: PropTypes.func.isRequired
};

export default DataForm;