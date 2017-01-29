using System;
using System.ComponentModel;
using System.Windows;
using System.Windows.Media;
using Microsoft.Kinect;
using Grpc.Core;
using static Roeper.Bu.Kinect.Master.Camera.CameraManager;
using Roeper.Bu.Kinect.Master.Camera;
using System.Net;

namespace Roeper.Bu.Kinect
{
    public partial class MainWindow : Window, INotifyPropertyChanged
    {
        // TODO(doug) - Grpc deadline in seconds. Large for debugging.
        private const int GRPC_DEADLINE = 500000;
        private const int PORT = 45554;

        private string statusText = null;

        private SkeletonDrawer skeletonDrawer;
        private Server grpcServer;
        private Channel channel;
        private CameraManagerClient client;
        private CameraProps props;

        public MainWindow()
        {
            this.skeletonDrawer = new SkeletonDrawer();

            // Start the gRPC server
            grpcServer = new Server
            {
                Services = { Camera.Camera.BindService(new CameraService()) },
                Ports = { new ServerPort("0.0.0.0", PORT, ServerCredentials.Insecure) }
            };
            grpcServer.Start();

            // use the window object as the view model in this simple example
            this.DataContext = this;

            // initialize the components (controls) of the window
            this.InitializeComponent();
        }

        public event PropertyChangedEventHandler PropertyChanged;

        public string StatusText
        {
            get
            {
                return this.statusText;
            }

            set
            {
                if (this.statusText != value)
                {
                    this.statusText = value;

                    // notify any bound elements that the text has changed
                    if (this.PropertyChanged != null)
                    {
                        this.PropertyChanged(this, new PropertyChangedEventArgs("StatusText"));
                    }
                }
            }
        }

        public ImageSource ImageSource
        {
            get
            {
                return this.skeletonDrawer.ImageSource;
            }
        }

        private void MainWindow_Loaded(object sender, RoutedEventArgs e)
        {
            this.skeletonDrawer.Start();
        }

        private void MainWindow_Closing(object sender, CancelEventArgs e)
        {
            this.skeletonDrawer.Dispose();
            grpcServer.ShutdownAsync().Wait();
            if (channel != null)
            {
                channel.ShutdownAsync().Wait();
            }
        }

        private void ConnectToMaster(object sender, RoutedEventArgs e)
        {
            // Update UI
            this.connectControls.Visibility = Visibility.Hidden;
            this.StatusText = "Connecting to master...";

            // Open connection to controller
            string masterHostname = this.masterIP.Text;
            channel = new Channel(masterHostname, ChannelCredentials.Insecure);
            client = new CameraManager.CameraManagerClient(channel);

            // Look for a local IPv4 address to use
            IPHostEntry IPHost = System.Net.Dns.GetHostEntry(System.Net.Dns.GetHostName());
            IPAddress address = null;
            foreach (var ipAddress in IPHost.AddressList)
            {
                if (ipAddress.AddressFamily == System.Net.Sockets.AddressFamily.InterNetwork)
                {
                    address = ipAddress;
                    break;
                }
            }

            // Check if we got an IP address
            if (address != null)
            {
                try
                {
                    // Register with server
                    RegistrationResponse response = client.registerCamera(new RegistrationRequest
                    {
                        Props = new CameraProps
                        {
                            Host = address.ToString(),
                            Port = PORT
                        }
                    }, deadline: DateTime.UtcNow.AddSeconds(GRPC_DEADLINE));
                    this.props = response.Props;

                    // Update UI
                    this.disconnectControls.Visibility = Visibility.Visible;
                    this.StatusText = "Connected with id: " + this.props.Id;
                }
                catch (RpcException ex)
                {
                    // Registration failed
                    this.connectControls.Visibility = Visibility.Visible;
                    this.StatusText = "ERROR: Couldn't register with controller, probably couldn't connect";
                }
            }
            else
            {
                this.connectControls.Visibility = Visibility.Visible;
                this.StatusText = "ERROR: Couldn't detect suitable IP address";
            }
        }

        private void Sensor_IsAvailableChanged(object sender, IsAvailableChangedEventArgs e)
        {
            // on failure, set the status text
        }
    }
}
