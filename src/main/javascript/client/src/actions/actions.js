


export const RECEIVED_CONTROLLER_STATE = "RECEIVED_CONTROLLER_STATE";
export function receivedControllerState(state) {
  return {
    type: RECEIVED_CONTROLLER_STATE,
    state
  };
};

export const ERROR_GETTING_CONTROLLER_STATE = "ERROR_GETTING_CONTROLLER_STATE";
export function errorGettingControllerState(error) {
  return {
    type: ERROR_GETTING_CONTROLLER_STATE,
    error
  };
};
