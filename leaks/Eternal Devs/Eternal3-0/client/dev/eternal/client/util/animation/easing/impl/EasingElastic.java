package dev.eternal.client.util.animation.easing.impl;

import dev.eternal.client.util.animation.easing.Easing;
import dev.eternal.client.util.animation.easing.EasingDirection;

public class EasingElastic extends Easing {
  public EasingElastic(EasingDirection direction) {
    super(direction);
  }

  final double constant = (2 * Math.PI) / 3;
  final double constant2 = (2 * Math.PI) / 4.5;

  @Override
  public double calculate(double progress) {
    switch (direction) {
      case IN -> {
        return -Math.pow(2, 10 * progress - 10)
            * Math.sin((progress * 10 - 10.75) * constant);
      }
      case OUT -> {
        return Math.pow(2, -10 * progress)
            * Math.sin((progress * 10 - 0.75) * constant) + 1;
      }
      case INOUT -> {
        double sin = Math.sin((20 * progress - 11.125) * constant2);

        return progress < 0.5 ?
            -(Math.pow(2, 20 * progress - 10) * sin) / 2 :
            (Math.pow(2, -20 * progress + 10) * sin) / 2 + 1;
      }
    }

    return progress;
  }
}
