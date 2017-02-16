package edu.bu.vip.multikinect.util;

import java.time.Instant;

import com.google.protobuf.Timestamp;

public class TimestampUtils {
  public static Timestamp now() {
    Instant time = Instant.now();
    return Timestamp.newBuilder().setSeconds(time.getEpochSecond()).setNanos(time.getNano())
        .build();
  }

  public static Instant from(Timestamp timestamp) {
    return Instant.ofEpochSecond(timestamp.getSeconds(), timestamp.getNanos());
  }
}
