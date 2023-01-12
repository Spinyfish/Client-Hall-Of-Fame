package dev.eternal.client.util.combat;

import net.minecraft.util.MouseFilter;

public class FixedMouseFilter extends MouseFilter {

  private float prevAngle;

  @Override
  public float smooth(float angle, float smoothing) {
    if (prevAngle - angle > 180) {
      angle += 360;
    }
    prevAngle = angle;
    return super.smooth(angle, smoothing);
  }
}
