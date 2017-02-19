package edu.bu.vip.multikinect.controller.camera;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import edu.bu.vip.multikinect.Protos.Frame;
import edu.bu.vip.multikinect.master.camera.CameraManagerGrpc.CameraManagerImplBase;
import edu.bu.vip.multikinect.master.camera.Grpc.CameraProps;
import edu.bu.vip.multikinect.master.camera.Grpc.RegistrationRequest;
import edu.bu.vip.multikinect.master.camera.Grpc.RegistrationResponse;
import edu.bu.vip.multikinect.controller.camera.CameraChangeEvent.Type;
import edu.bu.vip.multikinect.camera.CameraGrpc;
import edu.bu.vip.multikinect.camera.CameraGrpc.CameraStub;
import edu.bu.vip.multikinect.camera.Grpc.RecordOptions;
import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
 
@Singleton
public class CameraManager extends CameraManagerImplBase {
  private final Logger logger = LoggerFactory.getLogger(getClass());
  private final ConcurrentHashMap<String, Camera> camerasHostPortMap = new ConcurrentHashMap<>();
  private final ConcurrentHashMap<String, Camera> cameras = new ConcurrentHashMap<>();
  private final EventBus cameraBus;
  private final EventBus frameBus;
  private RecordOptions currentCameraOptions = RecordOptions.newBuilder().build();

  @Inject
  protected CameraManager(@FrameBus EventBus frameBus, @CameraBus EventBus cameraBus) {
    this.frameBus = frameBus;
    this.cameraBus = cameraBus;
  }

  @Override
  public void registerCamera(RegistrationRequest request,
      StreamObserver<RegistrationResponse> responseObserver) {
    try {
      CameraProps sentProps = request.getProps();

      // Check if the camera has been registered before
      final Camera camera;
      synchronized (camerasHostPortMap) {
        String hostPort = hostPortKey(sentProps);
        if (!camerasHostPortMap.containsKey(hostPort)) {
          // Camera not registered
          CameraProps.Builder propsUpdated = CameraProps.newBuilder(sentProps);
          CameraProps props = propsUpdated.build();

          camera = new Camera(props);
          cameras.put(props.getId(), camera);
          camerasHostPortMap.put(hostPort, camera);
          cameraBus.post(new CameraChangeEvent(props, Type.Added));
          logger.info("Registered camera: {}", camera.getProps().toString().replace('\n', ' '));
        } else {
          // Use host + port to get the camera
          camera = camerasHostPortMap.get(hostPort);
          logger.info("Camera already registered: {}", camera.getProps().toString().replace('\n', ' '));
        }
      }

      // Return the full props, aka. props with ID, to camera
      RegistrationResponse.Builder builder = RegistrationResponse.newBuilder();
      builder.setProps(camera.getProps());
      responseObserver.onNext(builder.build());
      responseObserver.onCompleted();

      // New gRPC call must be done on a separate thread
      new Thread(new Runnable() {
        @Override
        public void run() {
          camera.accessCamera(currentCameraOptions);
        }
      }).start();

    } catch (Exception e) {
      responseObserver.onError(e);
    }

  }

  private String hostPortKey(CameraProps props) {
    String hostPort = props.getHost() + ":" + props.getPort();
    return hostPort;
  }

  private void removeCamera(String cameraId) {
    logger.info("Removing camera: {} ", cameraId);
    Camera removed = cameras.remove(cameraId);
    camerasHostPortMap.remove(hostPortKey(removed.getProps()));
    cameraBus.post(new CameraChangeEvent(removed.getProps(), Type.Removed));
  }

  private void handleIncomingFrame(Camera camera, Frame frame) {
    frameBus.post(new FrameReceivedEvent(camera.getProps(), frame));
  }

  public void updateCameraOptions(RecordOptions options) {
    logger.info("Updating camera options: {}", options.toString());

    // Send out new options
    cameras.forEach((id, camera) -> {
      camera.updateRecordOptions(options);
    });
    currentCameraOptions = options;
  }

  private class Camera {
    private CameraStub service;
    private CameraProps props;
    private StreamObserver<RecordOptions> accessStream;

    public Camera(CameraProps props) {
      this.props = props;
      Channel channel = ManagedChannelBuilder.forAddress(props.getHost(), props.getPort())
          .usePlaintext(true).build();
      this.service = CameraGrpc.newStub(channel);
    }

    public CameraProps getProps() {
      return props;
    }

    public void accessCamera(RecordOptions initialOptions) {
      accessStream = service.record(new StreamObserver<Frame>() {
        @Override
        public void onNext(Frame value) {
          handleIncomingFrame(Camera.this, value);
        }

        @Override
        public void onError(Throwable t) {
          t.printStackTrace();
          // TODO(doug) handle
          removeCamera(props.getId());
        }

        @Override
        public void onCompleted() {
          // TODO(doug) handle
          removeCamera(props.getId());
        }
      });

      accessStream.onNext(initialOptions);
    }

    public void updateRecordOptions(RecordOptions options) {
      accessStream.onNext(options);
    }
  }
}
