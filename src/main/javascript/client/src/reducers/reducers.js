import {routerReducer} from 'react-router-redux';
import {combineReducers} from 'redux';
import {Map} from 'immutable';

import * as ACTIONS from '../actions/actions';

function controllerState(state = null, action) {
  let newState = state;
  switch (action.type) {
    case ACTIONS.RECEIVED_CONTROLLER_STATE:
      newState = action.state;
      break;

    case ACTIONS.ERROR_GETTING_CONTROLLER_STATE:
      newState = null;
      break;

    default:
      break;
  }
  return newState;
}

function error(state = new Map(), action) {
  let newState = state;
  switch (action.type) {
    case ACTIONS.RECEIVED_CONTROLLER_STATE:
      newState = state.delete(ACTIONS.ERROR_GETTING_CONTROLLER_STATE);
      break;
    case ACTIONS.ERROR_GETTING_CONTROLLER_STATE:
      newState = state.set(ACTIONS.ERROR_GETTING_CONTROLLER_STATE, action.error.message);
      break;
    default:
      break;
  }

  return newState;
}

const reducers = combineReducers({
  controllerState,
  error,
  routing : routerReducer
});
export default reducers;
