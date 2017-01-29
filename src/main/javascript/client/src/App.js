import React, {Component} from 'react';
import {connect} from 'react-redux';
import Radium from 'radium';
import {push} from 'react-router-redux';

import ErrorBar from './components/ErrorBar';
import ConsolePage from './components/ConsolePage';
import FeedPage from './components/FeedPage';
import {receivedControllerState, errorGettingControllerState} from './actions/actions';
import {sendGetStateRequest} from './api/api';

const PADDING = 16;
let styles = {
    base: {
        height: '100%',
        minHeight: 400,
        display: 'flex',
        flexDirection: 'column',
        justifyContent: 'stretch'
    },
    topBar: {
        background: 'rgba(0, 100, 0, 0.7)'
    },
    topBarTitle: {
        paddingLeft: PADDING,
        paddingTop: PADDING,
        paddingRight: PADDING,
        color: 'white',
        fontSize: 40
    },
    linkBar: {
        display: 'flex',
        flexDirection: 'row',
        height: 32
    },
    linkButton: {
        display: 'flex',
        flexDirection: 'column',
        justifyContent: 'center',
        paddingLeft: PADDING,
        paddingRight: PADDING,
        ':hover' : {
            background: 'rgba(255, 255, 255, 0.1)'
        }
    },
    link: {
        color: 'white'
    },
    error: {
        color: 'red'
    }
};

@Radium
class AppView extends Component {
  constructor(props) { super(props); }

  componentDidMount() { this.timer = setInterval(this.tick, 1000); }

  componentWillUnmount() { clearInterval(this.timer); }

  tick = () => {
    sendGetStateRequest()
        .then(json => { this.props.handleControllerStateUpdate(json); })
        .catch(error => { this.props.handleErrorGettingControllerState(error); });
  };

  render() {
    return (<div style={[styles.base]}>
        <div style={[styles.topBar]}>
            <div style={[styles.topBarTitle]}>Web Console</div>
            <div style={[styles.linkBar]}>
                <span style={[styles.linkButton]} key={'linkConsole'} onClick={this.props.handleConsoleLink}>
                    <div style={[styles.link]}>Console</div>
                </span>
                <span style={[styles.linkButton]} key={'linkFeed'} onClick={this.props.handleFeedLink}>
                    <div style={[styles.link]}>Feed</div>
                </span>
            </div>
            <ErrorBar errors={this.props.error.toArray()} />
        </div>
        {/*This will be replaced by react router */}
        {this.props.children && React.cloneElement(this.props.children, {})}
    </div>);
  }
}

AppView.contextTypes = {
    router : React.PropTypes.object
};

const mapStateToProps = (state, ownProps) => {
    return {
        error: state.error
    };
};

const mapDispatchToProps = (dispatch) => {
    return {
        handleControllerStateUpdate: (state) => {
            dispatch(receivedControllerState(state));
        },
        handleErrorGettingControllerState: (error) => {
            dispatch(errorGettingControllerState(error));
        },
        handleConsoleLink: (event) => {
            dispatch(push(ConsolePage.url()));
        },
        handleFeedLink: (event) => {
            dispatch(push(FeedPage.url()));
        }
    };
};

const App = connect(mapStateToProps, mapDispatchToProps)(AppView);

export default App;
