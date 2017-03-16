package edu.bu.vip.multikinect.sync;

import edu.bu.vip.multikinect.Protos.Position;
import java.util.List;

public class PositionUtils {

  public static Position average(List<Position> positions) {
    float xSum = 0;
    float ySum = 0;
    float zSum = 0;
    for (Position position : positions) {
      xSum += position.getX();
      ySum += position.getY();
      zSum += position.getZ();
    }

    Position.Builder builder = Position.newBuilder();
    builder.setX(xSum / positions.size());
    builder.setY(ySum / positions.size());
    builder.setZ(zSum / positions.size());
    return builder.build();
  }

  public static float distanceXYZ(Position posA, Position posB) {
    float xDiff = posA.getX() - posB.getX();
    float yDiff = posA.getY() - posB.getY();
    float zDiff = posA.getZ() - posB.getZ();
    return (float)Math.sqrt(xDiff * xDiff + yDiff * yDiff + zDiff * zDiff);
  }

  public static float distanceXZ(Position posA, Position posB) {
    float xDiff = posA.getX() - posB.getX();
    float zDiff = posA.getZ() - posB.getZ();
    return (float)Math.sqrt(xDiff * xDiff + zDiff * zDiff);
  }
}
