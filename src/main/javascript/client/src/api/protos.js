import protobuf from 'protobufjs';

import FRAME_PROTO from '../../../../proto/frame.proto';
import FEED_PROTO from '../../../../proto/skeleton_feed.proto';

var builder = protobuf.newBuilder({ convertFieldsToCamelCase: false });
protobuf.loadProto(FRAME_PROTO, builder, './src/main/proto/frame.proto');
protobuf.loadProto(FEED_PROTO, builder, './skeleton_feed.proto');

const Frame = builder.build("roeper.bu.kinect.Frame");
export {Frame};

const Skeleton = builder.build("roeper.bu.kinect.Skeleton");
export {Skeleton};

const Joint = builder.build("roeper.bu.kinect.Joint");
export {Joint};

const JointType = builder.build("roeper.bu.kinect.Joint.JointType");
export {JointType};

const FeedMessage = builder.build("roeper.bu.kinect.controller.webconsole.FeedMessage");
export {FeedMessage};
