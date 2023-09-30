package dev.eternal.client.util.movement.data;

import dev.eternal.client.Client;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.entity.Entity;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class Position {
  private double posX, posY, posZ;

  public static List<Position> findPath(double maxDelta, Position from, Position to, boolean debug) {
    List<Position> positions = new ArrayList<>();
    Position currentPath = from.copy();
    double requiredMoveDist = from.getDist(to);
    double requiredDistPerMove = Math.min(requiredMoveDist, maxDelta);
    for (double d = 0; d < requiredMoveDist; d += requiredDistPerMove) {
      double yaw = Math.atan2(to.posZ() - from.posZ(), to.posX() - from.posX());
      double x = Math.cos(yaw) * requiredDistPerMove;
      double y = currentPath.posY() > to.posY() ? (to.posY() - from.posY()) / Math.max(1, requiredDistPerMove) : 0;
      double z = Math.sin(yaw) * requiredDistPerMove;
      currentPath = currentPath.copy().add(x, y, z);
      positions.add(currentPath);
      if (debug) {
        Client.singleton().displayMessage(x + " " + y + " " + z);
        Client.singleton().displayMessage(Double.toString(currentPath.getDist(from)));
      }
    }
    if (debug) Client.singleton().displayMessage(Integer.toString(positions.size()));
    return positions;
  }

  public static Position of(Entity entity) {
    return new Position(entity.posX, entity.posY, entity.posZ);
  }

  public Position copy() {
    return new Position(this.posX(), this.posY(), this.posZ());
  }

  public Position add(double x, double y, double z) {
    this.posX += x;
    this.posY += y;
    this.posZ += z;
    return this;
  }

  public double getDist(Position to) {
    return Math.sqrt(getDistSquared(to));
  }

  public double getDistSquared(Position to) {
    double x = this.posX() - to.posX();
    double y = this.posY() - to.posY();
    double z = this.posZ() - to.posZ();
    return x * x + y * y + z * z;
  }

}
