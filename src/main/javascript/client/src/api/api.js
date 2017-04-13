import {checkHttpResponseStatus, createJsonPostRequest} from './http';

const BASE_URL = 'http://' + IP.HOST + '/_';
export const getStateUrl = () => BASE_URL + '/state';
export const updateModeUrl = () => BASE_URL + "/mode";
export const updateCalibrationUrl = () => BASE_URL + "/calibration";
export const realtimeFeedUrl = () => 'ws://localhost:8080/_' + "/syncedFeed";

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

export function realTimeFeedSocket() {
  let socket = new WebSocket(realtimeFeedUrl());
  socket.binaryType = "arraybuffer";
  return socket;
}

const newCalibrationUrl = () => BASE_URL + "/newCalibration";
export function newCalibration(calibration) {
  return fetch(newCalibrationUrl(), createJsonPostRequest(calibration))
  .then(checkHttpResponseStatus);
}

const selectCalibrationUrl = (id) => BASE_URL + "/selectCalibration/" + id;
export function selectCalibration(id) {
  return fetch(selectCalibrationUrl(id))
  .then(checkHttpResponseStatus);
}

const deleteCalibrationUrl = (id) => BASE_URL + "/deleteCalibration/" + id;
export function deleteCalibration(id) {
  return fetch(deleteCalibrationUrl(id))
  .then(checkHttpResponseStatus);
}

const newFrameUrl = () => BASE_URL + "/newFrame";
export function newFrameRequest(frame) {
  return fetch(newFrameUrl(), createJsonPostRequest(frame))
  .then(checkHttpResponseStatus);
}

const deleteFrameUrl = (id) => BASE_URL + "/deleteFrame/" + id;
export function deleteFrameRequest(id) {
  return fetch(deleteFrameUrl(id))
  .then(checkHttpResponseStatus);
}

const finishCalibrationUrl = () => BASE_URL + "/finishCalibration";
export function finishCalibrationRequest() {
  return fetch(finishCalibrationUrl(), createJsonPostRequest({}))
  .then(checkHttpResponseStatus);
}

const finishFrameUrl = () => BASE_URL + "/finishFrame";
export function finishFrameRequest() {
  return fetch(finishFrameUrl(), createJsonPostRequest({}))
  .then(checkHttpResponseStatus);
}

const createSessionUrl = () => BASE_URL + "/createSession";
export function createSessionRequest(session) {
  return fetch(createSessionUrl(), createJsonPostRequest(session))
  .then(checkHttpResponseStatus);
}

const selectSessionUrl = (id) => BASE_URL + "/selectSession/" + id;
export function selectSessionRequest(id) {
  return fetch(selectSessionUrl(id))
  .then(checkHttpResponseStatus);
}

const deleteSessionUrl = (id) => BASE_URL + "/deleteSession/" + id;
export function deleteSessionRequest(id) {
  return fetch(deleteSessionUrl(id))
  .then(checkHttpResponseStatus);
}

const cancelSelectSessionUrl = () => BASE_URL + "/cancelSelectSession";
export function cancelSelectSessionRequest() {
  return fetch(cancelSelectSessionUrl(), createJsonPostRequest({}))
  .then(checkHttpResponseStatus);
}

const newRecordingUrl = () => BASE_URL + "/newRecording";
export function createRecordingRequest(recording) {
  return fetch(newRecordingUrl(), createJsonPostRequest(recording))
  .then(checkHttpResponseStatus);
}

const deleteRecordingUrl = (id) => BASE_URL + "/deleteRecording/" + id;
export function deleteRecordingRequest(id) {
  return fetch(deleteRecordingUrl(id))
  .then(checkHttpResponseStatus);
}

const finishSessionUrl = () => BASE_URL + "/finishSession";
export function finishSessionRequest() {
  return fetch(finishSessionUrl(), createJsonPostRequest({}))
  .then(checkHttpResponseStatus);
}

const stopRecordingUrl = () => BASE_URL + "/stopRecording";
export function stopRecordingRequest() {
  return fetch(stopRecordingUrl(), createJsonPostRequest({}))
  .then(checkHttpResponseStatus);
}
