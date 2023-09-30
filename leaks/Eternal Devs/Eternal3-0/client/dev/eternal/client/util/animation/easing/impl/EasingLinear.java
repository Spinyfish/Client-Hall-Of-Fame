package dev.eternal.client.util.animation.easing.impl;

import dev.eternal.client.util.animation.easing.Easing;
import dev.eternal.client.util.animation.easing.EasingDirection;

public class EasingLinear extends Easing {
  public EasingLinear(EasingDirection direction) {
    super(direction);
  }

  @Override
  public double calculate(double progress) {
    return progress;
  }
}
