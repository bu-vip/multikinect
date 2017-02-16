package edu.bu.vip.kinect.controllerv2.camera;

import com.google.common.eventbus.EventBus;
import edu.bu.vip.multikinect.master.camera.Grpc.CameraProps;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileFramePublisher {

  private final Logger logger = LoggerFactory.getLogger(getClass());
  private final Map<String, FrameReader> readers = new HashMap<>();
  private final Map<String, CameraProps> props = new HashMap<>();
  private final EventBus frameBus;

  public FileFramePublisher(List<InputStream> frameFiles, List<CameraProps> cameraProps,
      @FrameBus EventBus frameBus)
      throws IOException {
    this.frameBus = frameBus;

    for (int i = 0; i < frameFiles.size(); i++) {
      CameraProps prop = cameraProps.get(i);
      InputStream stream = frameFiles.get(i);
      String cameraId = prop.getId();
      readers.put(cameraId, new FrameReader(stream));
      props.put(cameraId, prop);
    }
  }

  public boolean hasFrame() {
    for (String cameraId : props.keySet()) {
      if (!hasFrame(cameraId)) {
        return false;
      }
    }

    return true;
  }

  public boolean hasFrame(String cameraId) {
    FrameReader reader = readers.get(cameraId);
    return reader.hasNext();
  }

  public void publishOneFrame() {
    props.forEach((cameraId, prop) -> {
      publishOneFrame(cameraId);
    });
  }

  public void publishOneFrame(String cameraId) {
    CameraProps prop = props.get(cameraId);
    FrameReader reader = readers.get(cameraId);
    if (reader.hasNext()) {
      frameBus.post(new FrameReceivedEvent(prop, reader.next()));
    } else {
      logger.warn("Camera {} does not have any more frames", cameraId);
    }
  }

  public void publishAllFrames() {
    while (hasFrame()) {
      publishOneFrame();
    }
  }

  public void publishAllFrames(String cameraId) {
    while (hasFrame(cameraId)) {
      publishOneFrame(cameraId);
    }
  }

  public void close() {
    props.forEach((cameraId, prop) -> {
      FrameReader reader = readers.get(cameraId);
      try {
        reader.close();
      } catch (IOException e) {
        logger.error("Error closing camera {}", cameraId, e);
      }
    });
  }
}
