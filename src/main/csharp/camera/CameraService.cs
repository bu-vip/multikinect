using Bu.Vip.Multikinect;
using Bu.Vip.Multikinect.Camera;
using Grpc.Core;
using Microsoft.Kinect;
using NodaTime;
using System;
using System.Collections.Concurrent;
using System.Collections.Generic;
using System.Threading;
using System.Threading.Tasks;

namespace Roeper.Bu.Kinect
{
    class CameraService : Camera.CameraBase
    {
        private KinectSensor kinect;
        private MultiSourceFrameReader frameReader;
        private ConcurrentQueue<Frame> frameQueue;

        private bool accessing = false;
        private Object accessLock = new object();
        private Body[] bodies = null;
        private NetworkClock ntpClock = NetworkClock.Instance;

        public CameraService()
        {
            if (KinectSensor.GetDefault() != null)
            {
                kinect = KinectSensor.GetDefault();
                kinect.Open();
                frameReader = kinect.OpenMultiSourceFrameReader(FrameSourceTypes.Color | FrameSourceTypes.Depth | FrameSourceTypes.Body | FrameSourceTypes.Infrared);
                frameReader.IsPaused = true;
                frameReader.MultiSourceFrameArrived += FrameReader_MultiSourceFrameArrived;
                frameReader.IsPaused = false;

                ntpClock.CacheTimeout = Duration.FromMinutes(1);
            }
            else
            {
                throw new Exception("No kinect");
            }
        }

        public void Stop()
        {
            frameReader.IsPaused = true;
            frameReader.MultiSourceFrameArrived -= FrameReader_MultiSourceFrameArrived;
            kinect.Close();
        }

        private void FrameReader_MultiSourceFrameArrived(object sender, MultiSourceFrameArrivedEventArgs ev)
        {
            MultiSourceFrame kinectFrame = ev.FrameReference.AcquireFrame();
            if (kinectFrame != null)
            {
                bool dataReceived = false;
                TimeSpan frameTime = TimeSpan.MinValue;

                // Read the body frame data
                using (BodyFrame bodyFrame = kinectFrame.BodyFrameReference.AcquireFrame())
                {
                    if (bodyFrame != null)
                    {
                        frameTime = bodyFrame.RelativeTime;
                        if (this.bodies == null)
                        {
                            this.bodies = new Body[bodyFrame.BodyCount];
                        }
                        bodyFrame.GetAndRefreshBodyData(this.bodies);
                        dataReceived = true;
                    }
                }

                // If we received data, add to queue if needed
                if (dataReceived)
                {
                    Frame frame = KinectBodiesToProto.ConvertFrame(frameTime, bodies, ntpClock.Now);
                    lock (accessLock)
                    {
                        if (accessing)
                        {
                            frameQueue.Enqueue(frame);
                        }
                    }
                }
            }
        }

        public override async Task record(IAsyncStreamReader<RecordOptions> requestStream, IServerStreamWriter<Frame> responseStream, ServerCallContext context)
        {
            // "Clear" the queue
            frameQueue = new ConcurrentQueue<Frame>();

            CancellationTokenSource source = new CancellationTokenSource();
            CancellationToken token = source.Token;

            // Wait for first record options
            await requestStream.MoveNext();
            RecordOptions initialOptions = requestStream.Current;

            // Increment the number of users accessing the frames
            lock (accessLock)
            {
                accessing = true;
            }

            // Start a task to handle option updates
            Task t = Task.Factory.StartNew(async () =>
            {
                while (!token.IsCancellationRequested)
                {
                    await requestStream.MoveNext(token);
                    RecordOptions options = requestStream.Current;
                    // TODO(doug) - handle new options
                }
            }); 

            // Start sending frames
            while (!token.IsCancellationRequested)
            {
                // Try to get a frame from the queue and send it 
                Frame next = null;
                if (frameQueue.TryDequeue(out next))
                {
                    await responseStream.WriteAsync(next);
                }
            }


            // Finished
            lock (accessLock)
            {
                accessing = false;
            }
        }
    }
}
