package dev.eternal.client.util.animate;

/**
 * The Position class holds two instances of the {@link Animate} class.
 */
public class Position {

  private final Animate posX;
  private final Animate posY;

  public Position(double posX, double posY, float speed) {
    this.posX = new Animate(posX, speed);
    this.posY = new Animate(posY, speed);
  }

  public void interpolate(float targetX, float targetY) {
    posX.interpolate(targetX);
    posY.interpolate(targetY);
  }

  public void setSpeed(float speed) {
    this.posX.setSpeed(speed);
    this.posY.setSpeed(speed);
  }

  public double getX() {
    return posX.getValue();
  }

  public double getY() {
    return posY.getValue();
  }

  public void setY(double y) {
    this.posY.setValue(y);
  }

  public void setX(double x) {
    this.posX.setValue(x);
  }
}
