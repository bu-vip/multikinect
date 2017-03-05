package edu.bu.vip.multikinect.controller.calibration;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.junit.MatcherAssert.assertThat;

import edu.bu.vip.kinect.controller.calibration.Protos.Calibration;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;

public abstract class CalibrationDataStoreTest {

  public abstract CalibrationDataStore getDataStore();

  private CalibrationDataStore dataStore;

  @Before
  public void setUp() {
    dataStore = getDataStore();
  }

  @Test
  public void idTest() {
    Calibration.Builder builder = Calibration.newBuilder();
    builder.setId(0);
    builder.setName("myCalibration");

    Calibration updated = dataStore.createCalibration(builder.build());
    assertThat(updated.getId(), is(not(0)));
    assertThat(updated.getName(), is(builder.getName()));
  }

  @Test
  public void getTest() {
    // Create and update the calibration
    Calibration.Builder builder = Calibration.newBuilder();
    builder.setName("myCalibration");
    builder = dataStore.createCalibration(builder.build()).toBuilder();

    // Get the calibration
    Optional<Calibration> optCal = dataStore.getCalibration(builder.getId());
    assertThat(optCal.isPresent(), is(true));
    Calibration updated = optCal.get();
    assertThat(updated.getName(), is(builder.getName()));
  }

  @Test
  public void updateTest() {
    // Create and update the calibration
    Calibration.Builder builder = Calibration.newBuilder();
    builder.setId(0);
    builder.setName("myCalibration");
    builder = dataStore.createCalibration(builder.build()).toBuilder();
    builder.setName("updatedName");
    dataStore.updateCalibration(builder.build());

    // Get the calibration, check fields are updated
    Optional<Calibration> optCal = dataStore.getCalibration(builder.getId());
    Calibration updated = optCal.get();
    assertThat(updated.getName(), is(builder.getName()));
  }

  @Test
  public void deleteTest() {
    // Create and update the calibration
    Calibration.Builder builder = Calibration.newBuilder();
    builder = dataStore.createCalibration(builder.build()).toBuilder();

    assertThat(dataStore.getCalibration(builder.getId()).isPresent(), is(true));
    dataStore.deleteCalibration(builder.getId());
    assertThat(dataStore.getCalibration(builder.getId()).isPresent(), is(false));
  }
}
