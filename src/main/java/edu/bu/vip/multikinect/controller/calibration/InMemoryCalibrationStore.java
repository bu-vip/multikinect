package edu.bu.vip.multikinect.controller.calibration;

import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import edu.bu.vip.kinect.controller.calibration.Protos.Calibration;
import edu.bu.vip.kinect.controller.calibration.Protos.Calibration.Builder;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class InMemoryCalibrationStore implements CalibrationStore {

  private final Logger logger = LoggerFactory.getLogger(getClass());
  private long idCounter = (new Random()).nextInt();
  private Map<Long, Calibration> calibrations = new HashMap<>();

  @Inject
  public InMemoryCalibrationStore() {

  }

  @Override
  public Calibration createCalibration(Calibration calibration) {
    Builder builder = calibration.toBuilder();
    builder.setId(idCounter);
    idCounter++;

    Calibration finalCal = builder.build();
    calibrations.put(finalCal.getId(), finalCal);
    return finalCal;
  }

  @Override
  public Calibration updateCalibration(Calibration calibration) {
    // Check that calibration exists
    if (!calibrations.containsKey(calibration.getId())) {
      throw new RuntimeException("Couldn't find calibration to update");
    }

    calibrations.put(calibration.getId(), calibration);

    return calibration;
  }

  @Override
  public Optional<Calibration> getCalibration(long id) {
    return Optional.ofNullable(calibrations.get(id));
  }

  @Override
  public ImmutableList<Calibration> getCalibrations() {
    return ImmutableList.copyOf(calibrations.values());
  }

  @Override
  public void deleteCalibration(long id) {
    if (calibrations.containsKey(id)) {
      calibrations.remove(id);
    }
  }
}
