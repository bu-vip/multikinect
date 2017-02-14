package edu.bu.vip.kinect.controllerv2.calibration;

import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.junit.MatcherAssert.assertThat;

import com.google.common.collect.ImmutableList;
import com.roeper.bu.kinect.Protos.Frame;
import edu.bu.vip.kinect.controller.calibration.Protos.CalibrationFrame;
import edu.bu.vip.kinect.controller.calibration.Protos.CameraPairCalibration;
import edu.bu.vip.kinect.controller.calibration.Protos.GroupOfFrames;
import edu.bu.vip.kinect.controllerv2.camera.FrameReader;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class CameraTransformTest {

  private static final double ERROR_THRESHOLD = 0.0001;
  private static final String ID1 = "camera1";
  private static final String ID2 = "camera2";

  private CameraTransform transform;
  private CalibrationDataDB dataDB;

  @Before
  public void setUp() throws Exception {
    dataDB = new InMemoryCalibrationDataDB();
    transform = new CameraTransform(ID1, ID2, dataDB);
  }

  @After
  public void tearDown() throws Exception {
    dataDB = null;
  }

  @Test
  public void sameRecordingTest() throws Exception {
    ImmutableList<Frame> testFrames = FrameReader
        .readAllFrames(getClass().getResourceAsStream("/kinect1.dat"));
    dataDB.storeFrames(ID1, testFrames);
    dataDB.storeFrames(ID2, testFrames);

    final long startTime = testFrames.get(0).getTime();
    final long endTime = testFrames.get(testFrames.size() - 1).getTime();
    GroupOfFrames.Builder builder = GroupOfFrames.newBuilder();
    builder.setCameraA(ID1);
    builder.setCameraB(ID2);
    builder.setStartTimeA(startTime).setEndTimeA(endTime);
    builder.setStartTimeB(startTime).setEndTimeB(endTime);
    transform.addFrame(builder.build());

    CameraPairCalibration pairCalibration = transform.call();

    // Check error is low
    assertThat(pairCalibration.getError(), closeTo(0, ERROR_THRESHOLD));
  }

}
