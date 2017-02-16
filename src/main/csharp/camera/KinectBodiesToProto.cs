using Bu.Vip.Multikinect;
using Microsoft.Kinect;
using NodaTime;
using System;
using System.Collections.Generic;

namespace Bu.Vip.Multikinect
{
    public class KinectBodiesToProto
    {
        public static Frame ConvertFrame(TimeSpan frameTime, Body[] bodies, Instant ntpCaptureTime)
        {
            long seconds = ntpCaptureTime.Ticks / NodaConstants.TicksPerSecond;
            int nanos = (int)(ntpCaptureTime.Ticks - seconds * NodaConstants.TicksPerSecond) * 100; // Nodatime Ticks are 100 nanos
            Frame frame = new Frame {
                Time = frameTime.Ticks,
                NtpCaptureTime = new Google.Protobuf.WellKnownTypes.Timestamp
                {
                    Seconds = seconds,
                    Nanos = nanos
                }
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

        private static Joint ConvertJoint(Microsoft.Kinect.Joint joint, JointOrientation orientation)
        {
            return new Joint
            {
                Type = (Joint.Types.JointType)joint.JointType,
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
                TrackingState = (Bu.Vip.Multikinect.Joint.Types.TrackingState)joint.TrackingState,
            };
        }
    }
}
