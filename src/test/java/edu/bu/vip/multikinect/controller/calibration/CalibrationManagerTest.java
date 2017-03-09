package edu.bu.vip.multikinect.controller.calibration;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.google.common.eventbus.EventBus;
import edu.bu.vip.kinect.controller.calibration.Protos.Calibration;
import edu.bu.vip.multikinect.controller.camera.CameraManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class CalibrationManagerTest {

  private CalibrationManager calibrationManager;
  private EventBus frameBus = new EventBus("test-frame-bus");
  @Mock
  private CameraManager cameraManager;
  @Spy
  private InMemoryCalibrationDataStore dataStore;
  @Mock
  private CalibrationAlgorithm algorithm;

  @Before
  public void setUp() throws Exception {
    dataStore = spy(new InMemoryCalibrationDataStore());
    calibrationManager = new CalibrationManager(dataStore, frameBus, cameraManager, algorithm);
  }

  @After
  public void tearDown() throws Exception {
    calibrationManager = null;
  }

  @Test
  public void startTest() {
    calibrationManager.start("cal1", "my notes...");
    verify(dataStore).createCalibration(any(Calibration.class));
  }

  @Test
  public void startTwiceTest() {
    calibrationManager.start("cal1", "my notes...");
    calibrationManager.start("cal1", "my notes...");
    verify(dataStore, times(1)).createCalibration(any(Calibration.class));
  }
}
