import {connect} from 'react-redux';

import ConsoleView from './ConsoleView';

const mapStateToProps =
    (state, ownProps) => { return {controllerState : state.controllerState}; };

const mapDispatchToProps = (dispatch) => { return {}; };

const ConsolePage = connect(mapStateToProps, mapDispatchToProps)(ConsoleView);

const baseUrl = '/';
ConsolePage.route = baseUrl;
ConsolePage.url = () => baseUrl;

export default ConsolePage;
