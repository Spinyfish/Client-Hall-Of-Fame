package dev.eternal.client.util.animation.easing.impl;

import dev.eternal.client.util.animation.easing.Easing;
import dev.eternal.client.util.animation.easing.EasingDirection;

public class EasingExpo extends Easing {
  public EasingExpo(EasingDirection direction) {
    super(direction);
  }

  @Override
  public double calculate(double progress) {
    switch (direction) {
      case IN -> {
        return Math.pow(2, 10 * progress - 10);
      }
      case OUT -> {
        return 1 - Math.pow(2, -10 * progress);
      }
      case INOUT -> {
        return progress < 0.5 ?
            Math.pow(2, 20 * progress - 10) / 2 :
            (2 - Math.pow(2, -20 * progress + 10)) / 2;
      }
    }

    return progress;
  }
}
