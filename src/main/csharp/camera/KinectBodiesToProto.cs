using Microsoft.Kinect;
using Roeper.Bu.Kinect;
using System;
using System.Collections.Generic;

namespace Roeper.Bu.Kinect
{
    public class KinectBodiesToProto
    {
        public static Frame ConvertFrame(TimeSpan frameTime, Body[] bodies)
        {
            Frame frame = new Frame {
                Time = frameTime.Ticks
            };
            frame.Skeletons.AddRange(ConvertBodies(bodies));

            return frame;

        }

        private static List<Skeleton> ConvertBodies(Body[] bodies)
        {
            List<Skeleton> skeletons = new List<Skeleton>();
            foreach (Body body in bodies)
            {
                if (body.IsTracked)
                {
                    Skeleton skel = new Skeleton
                    {
                        Id = body.TrackingId,
                        HandRightState = (Skeleton.Types.HandState)body.HandRightState,
                        HandRightConfidence = (Skeleton.Types.TrackingConfidence)body.HandRightConfidence,
                        HandLeftState = (Skeleton.Types.HandState)body.HandLeftState,
                        HandLeftConfidence = (Skeleton.Types.TrackingConfidence)body.HandLeftConfidence
                    };
                    foreach (JointType jointType in body.Joints.Keys)
                    {
                        skel.Joints.Add(ConvertJoint(body.Joints[jointType], body.JointOrientations[jointType]));
                    }
                    skeletons.Add(skel);
                }
            }

            return skeletons;
        }

        private static Roeper.Bu.Kinect.Joint ConvertJoint(Microsoft.Kinect.Joint joint, JointOrientation orientation)
        {
            return new Roeper.Bu.Kinect.Joint
            {
                Type = (Roeper.Bu.Kinect.Joint.Types.JointType)joint.JointType,
                Position = new Position
                {
                    X = joint.Position.X,
                    Y = joint.Position.Y,
                    Z = joint.Position.Z
                },
                Orientation = new Orientation
                {
                    X = orientation.Orientation.X,
                    Y = orientation.Orientation.Y,
                    Z = orientation.Orientation.Z,
                    W = orientation.Orientation.W
                },
                TrackingState = (Roeper.Bu.Kinect.Joint.Types.TrackingState)joint.TrackingState,
            };
        }
    }
}
