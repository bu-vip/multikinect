package edu.bu.vip.multikinect.utils;

import static org.hamcrest.Matchers.arrayContaining;
import static org.hamcrest.Matchers.closeTo;

import java.util.ArrayList;
import java.util.List;
import org.hamcrest.Matcher;

public class Matchers {
  public static Matcher<Double[]> arrayCloseTo(double[] array, double error) {
    List<Matcher<? super Double>> matchers = new ArrayList<Matcher<? super Double>>();
    for (double d : array)
      matchers.add(closeTo(d, error));
    return arrayContaining(matchers);
  }
}
