import 'isomorphic-fetch';
import {checkHttpResponseStatus, createJsonPostRequest} from './http';

const BASE_URL = 'http://localhost:8080/_';
export const getStateUrl = () => BASE_URL + '/state';
export const updateModeUrl = () => BASE_URL + "/mode";
export const updateCalibrationUrl = () => BASE_URL + "/calibration";
export const feedUrl = () => 'ws://localhost:8080/_' + "/feed";

export const MODES = {
  IDLE: "IDLE",
  CALIBRATION: "CALIBRATION",
  REALTIME: "REALTIME"
};

export function sendGetStateRequest() {
  return fetch(getStateUrl())
      .then(checkHttpResponseStatus)
      .then(response => response.json());
}

export function updateModeRequest(mode) {
  return fetch(updateModeUrl(), createJsonPostRequest(mode))
        .then(checkHttpResponseStatus)
        .then(response => response.json());
}

export function updateCalibrationRequest(mode) {
  return fetch(updateCalibrationUrl(), createJsonPostRequest(mode))
        .then(checkHttpResponseStatus)
        .then(response => response.json());
}

export function feedSocket() {
  let socket = new WebSocket(feedUrl());
  socket.binaryType = "arraybuffer";
  return socket;
}
