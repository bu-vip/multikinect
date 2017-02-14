package edu.bu.vip.kinect.controllerv2.calibration;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import com.google.common.collect.ImmutableList;
import com.google.common.eventbus.EventBus;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.roeper.bu.kinect.Protos.Frame;
import com.roeper.bu.kinect.master.camera.Grpc.CameraProps;
import edu.bu.vip.kinect.controller.calibration.Protos.CalibrationFrame;
import edu.bu.vip.kinect.controller.calibration.Protos.GroupOfFrames;
import edu.bu.vip.kinect.controllerv2.camera.FileFramePublisher;
import edu.bu.vip.kinect.controllerv2.camera.FrameBus;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Consumer;
import jdk.nashorn.internal.ir.annotations.Immutable;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class FragmenterTest {

  private Injector injector;
  private EventBus frameBus = new EventBus("test-frame-test");
  private Fragmenter fragmenter;
  private FileFramePublisher publisher;
  @Mock
  private Consumer<GroupOfFrames> frameConsumer;

  @Before
  public void setUp() throws Exception {
    injector = Guice.createInjector(new AbstractModule() {
      @Override
      protected void configure() {
        bind(EventBus.class).annotatedWith(FrameBus.class).toInstance(frameBus);
        bind(CalibrationDataDB.class).toInstance(new InMemoryCalibrationDataDB());
      }
    });

    fragmenter = injector.getInstance(Fragmenter.class);

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
    fragmenter = null;
    injector = null;
  }

  @Test
  public void noFramesTest() {
    fragmenter.start(frameConsumer);
    fragmenter.stop();
    verifyNoMoreInteractions(frameConsumer);
  }

  @Test
  public void framesTest() {
    fragmenter.start(frameConsumer);
    publisher.publishAllFrames();
    fragmenter.stop();
    // TODO(doug) - Real test
    verify(frameConsumer, atLeastOnce()).accept(any(GroupOfFrames.class));
  }
}