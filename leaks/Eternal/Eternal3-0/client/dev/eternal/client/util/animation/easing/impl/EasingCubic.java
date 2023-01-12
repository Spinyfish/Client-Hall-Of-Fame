package dev.eternal.client.util.animation.easing.impl;

import dev.eternal.client.util.animation.easing.Easing;
import dev.eternal.client.util.animation.easing.EasingDirection;

public class EasingCubic extends Easing {
  public EasingCubic(EasingDirection direction) {
    super(direction);
  }

  @Override
  public double calculate(double progress) {
    switch (direction) {
      case IN -> {
        return Math.pow(progress, 3);
      }
      case OUT -> {
        return 1 - Math.pow(progress, 3);
      }
      case INOUT -> {
        return progress < 0.5 ?
            progress * progress * progress * 4 :
            1 - Math.pow(-2 * progress + 2, 3) / 2;
      }
    }

    return progress;
  }
}
