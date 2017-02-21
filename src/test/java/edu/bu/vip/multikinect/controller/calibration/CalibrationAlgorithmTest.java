package edu.bu.vip.multikinect.controller.calibration;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.junit.MatcherAssert.assertThat;

import com.google.common.collect.ImmutableList;
import edu.bu.vip.kinect.controller.calibration.Protos.CameraPairCalibration;
import edu.bu.vip.kinect.controller.calibration.Protos.GroupOfFrames;
import edu.bu.vip.multikinect.Protos.Frame;
import edu.bu.vip.multikinect.controller.camera.FrameReader;
import java.io.InputStream;
import java.util.concurrent.Callable;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public abstract class CalibrationAlgorithmTest {

  protected static final long CAL_ID = 1;
  protected static final String CAMERA_A = "camera1";
  protected static final String CAMERA_B = "camera2";

  public abstract CalibrationAlgorithm getAlgorithm();

  private CalibrationAlgorithm algorithm;
  private CalibrationDataStore dataStore;

  @Before
  public void setUp() throws Exception {
    dataStore = new InMemoryCalibrationDataStore();

    ImmutableList<Long> recordings = ImmutableList.of(1L, 2L, 3L, 4L, 5L, 6L);
    ImmutableList<String> cameraIds = ImmutableList.of(CAMERA_A, CAMERA_B);
    for (long recording : recordings) {
      for (String cameraId : cameraIds) {
        String fileName = "/calibration/" + recording + "/" + cameraId + ".pbdat";
        InputStream stream = getClass().getResourceAsStream(fileName);
        ImmutableList<Frame> frames = FrameReader.readAllFrames(stream);
        dataStore.storeFrames(CAL_ID, recording, cameraId, frames);
      }
    }

    algorithm = getAlgorithm();
  }

  protected CalibrationDataStore getDataStore() {
    return dataStore;
  }

  @After
  public void tearDown() throws Exception {

  }

  @Test
  public void test() throws Exception {
    Callable<ImmutableList<GroupOfFrames>> job = algorithm
        .createJob(dataStore.getAllFrames(CAL_ID, 1, CAMERA_A),
            dataStore.getAllFrames(CAL_ID, 1, CAMERA_B));
    ImmutableList<GroupOfFrames> gofs = job.call();

    assertThat(gofs.size(), greaterThan(0));
  }

}
