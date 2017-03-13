import protobuf from 'protobufjs';

const Protos = {
  loaded: false
};
export {Protos};

protobuf.load("/protos.json", function(err, root) {

  if (err) {
    console.error(err);
  }

  Protos.Frame = root.lookup("bu.vip.multikinect.Frame");
  Protos.JointType = root.lookup("bu.vip.multikinect.Joint.JointType");
  Protos.SyncedFrame = root.lookup("bu.vip.multikinect.controller.realtime.SyncedFrame");

  Protos.loaded = true;
});
