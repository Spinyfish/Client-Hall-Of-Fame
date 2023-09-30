package dev.eternal.client.util.animation.easing;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public abstract class Easing {
  public final EasingDirection direction;

  public abstract double calculate(double progress);
}
