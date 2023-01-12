package dev.eternal.client.util.animate;

import net.minecraft.client.Minecraft;

/**
 * Base animation class that holds the current value and the speed.
 *
 * @author Eternal
 */
public class Animate {

  private double value;
  private float speed;

  public Animate(double value, float speed) {
    this.value = value;
    this.speed = speed;
  }

  /**
   * This will interpolate the current value towards the target value passed in.
   *
   * @param target The target position.
   * @author Eternal
   */
  public void interpolate(double target) {
    long delta = System.currentTimeMillis() - Minecraft.getMinecraft().lastRenderTime;
    if (value == target) return;
    this.value = move(target, value, delta, speed);
  }

  public double getValue() {
    return value;
  }

  public float getValueF() {
    return (float) value;
  }

  public void setValue(double value) {
    this.value = value;
  }

  public float getSpeed() {
    return speed;
  }

  public void setSpeed(float speed) {
    this.speed = speed;
  }

  //  It just works

  /**
   * @param target  The target value.
   * @param current The current value.
   * @param delta   The time since the last frame.
   * @param speed   The speed at which to interpolate.
   * @return Returns a value between the target and current dictated by the speed and delta.
   * @author Eternal
   */
  public static double move(double target, double current, long delta, float speed) {
    if (delta < 1)
      delta = 1;

    double diff = target - current;

    boolean dir = target > current;

    current += (diff / 50) * (delta * speed) + (dir ? 0.001 : -0.001);
    if (dir)
      if (current > target)
        current = target;
    if (!dir)
      if (current < target)
        current = target;
    return current;
  }

}
