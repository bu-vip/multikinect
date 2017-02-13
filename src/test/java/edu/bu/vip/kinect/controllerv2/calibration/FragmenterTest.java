package edu.bu.vip.kinect.controllerv2.calibration;

import static org.mockito.Mockito.verifyNoMoreInteractions;

import com.google.common.eventbus.EventBus;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import edu.bu.vip.kinect.controller.calibration.Protos.CalibrationFrame;
import edu.bu.vip.kinect.controllerv2.camera.FrameBus;
import java.util.function.Consumer;
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
  @Mock
  private Consumer<CalibrationFrame> frameConsumer;

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
  }

  @After
  public void tearDown() throws Exception {
    fragmenter = null;
    injector = null;
  }

  @Test
  public void noFramesTest() {
    fragmenter.start(frameConsumer);
    fragmenter.stop();
    verifyNoMoreInteractions(frameConsumer);
  }
}