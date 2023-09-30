package dev.eternal.client.util.animation.easing.impl;

import dev.eternal.client.util.animation.easing.Easing;
import dev.eternal.client.util.animation.easing.EasingDirection;

public class EasingBounce extends Easing {
  public EasingBounce(EasingDirection direction) {
    super(direction);
  }

  @Override
  public double calculate(double progress) {
    switch (direction) {
      case IN -> {
        return 1 - easeOut(1 - progress);
      }
      case OUT -> {
        return easeOut(progress);
      }
      case INOUT -> {
        return progress < 0.5 ?
            (1 - easeOut(1 - 2 * progress)) / 2 :
            (1 + easeOut(2 * progress - 1)) / 2;
      }
    }

    return progress;
  }

  private double easeOut(double progress) {
    double constant = 7.5625;
    double constant2 = 2.75;
    if (progress < 1 / constant2) {
      return constant * progress * progress;
    } else if (progress < 2 / constant2) {
      return constant * (progress -= 1.5 / constant2) * progress + 0.75;
    } else if (progress < 2.5 / constant2) {
      return constant * (progress -= 2.25 / constant2) * progress + 0.9375;
    } else {
      return constant * (progress -= 2.625 / constant2) * progress + 0.984375;
    }
  }
}
