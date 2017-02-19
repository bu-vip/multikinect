package edu.bu.vip.multikinect.controller.calibration;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.junit.MatcherAssert.assertThat;

import com.google.common.collect.ImmutableList;
import com.google.common.eventbus.EventBus;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import edu.bu.vip.multikinect.master.camera.Grpc.CameraProps;
import edu.bu.vip.kinect.controller.calibration.Protos.Calibration;
import edu.bu.vip.multikinect.controller.camera.FileFramePublisher;
import edu.bu.vip.multikinect.controller.camera.FrameBus;
import java.io.InputStream;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class CameraManagerIntegrationTest {

  private static final String CAL_NAME = "cal1";
  private static final String CAL_NOTES = "notes";

  private Injector injector;
  private EventBus frameBus = new EventBus("test-frame-test");
  private FileFramePublisher publisher;
  private CalibrationManager calibrationManager;

  @Before
  public void setUp() throws Exception {
    injector = Guice.createInjector(new CalibrationModule(),
        new AbstractModule() {
          @Override
          protected void configure() {
            bind(EventBus.class).annotatedWith(FrameBus.class).toInstance(frameBus);
          }
        }
    );

    calibrationManager = injector.getInstance(CalibrationManager.class);

    ImmutableList<InputStream> inputStreams = ImmutableList.of(
        getClass().getResourceAsStream("/kinect1.dat"),
        getClass().getResourceAsStream("/kinect2.dat")
    );
    ImmutableList<CameraProps> props = ImmutableList.of(
        CameraProps.newBuilder().setId("camera1").build(),
        CameraProps.newBuilder().setId("camera2").build()
    );
    publisher = new FileFramePublisher(inputStreams, props, frameBus);
  }

  @After
  public void tearDown() throws Exception {
    publisher.close();
    calibrationManager = null;
    injector = null;
  }

  @Test
  public void run() {
    calibrationManager.start(CAL_NAME, CAL_NOTES);

    for (int i = 0; i < 50; i++) {
      publisher.publishOneFrame("camera2");
    }

    calibrationManager.startRecording();

    publisher.publishAllFrames();

    calibrationManager.stopRecording();

    Calibration finalCal = calibrationManager.finish();

    assertThat(finalCal.getRecordingsCount(), equalTo(1));
  }
}
