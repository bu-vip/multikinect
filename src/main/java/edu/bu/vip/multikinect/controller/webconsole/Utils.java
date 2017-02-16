package edu.bu.vip.multikinect.controller.webconsole;

public class Utils {
  public static <T extends Enum<?>> T enumIgnoreCase(Class<T> enumeration, String search) {
    for (T each : enumeration.getEnumConstants()) {
      if (each.name().compareToIgnoreCase(search) == 0) {
        return each;
      }
    }
    return null;
  }
}
