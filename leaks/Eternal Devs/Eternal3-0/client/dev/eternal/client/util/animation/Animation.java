package dev.eternal.client.util.animation;

import dev.eternal.client.util.animation.easing.Easing;
import dev.eternal.client.util.animation.easing.EasingDirection;
import dev.eternal.client.util.animation.easing.impl.EasingLinear;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Hazsi
 */
@Getter
@Setter
public class Animation {
  private final Easing easing;
  private final double length;
  private long startTime;

  public Animation(Easing easing, double length) {
    this.easing = easing;
    this.length = length;
    this.startTime = System.currentTimeMillis();
  }

  public Animation(double length) {
    this(new EasingLinear(EasingDirection.IN), length);
  }

  public boolean isAnimating() {
    return isStarted() && !isDone();
  }

  public boolean isStarted() {
    return System.currentTimeMillis() > startTime;
  }

  public boolean isDone() {
    return getTimeElapsed() > length;
  }

  public double getTimeElapsed() {
    return System.currentTimeMillis() - startTime;
  }

  public double getProgress() {
    long timeElapsed = System.currentTimeMillis() - startTime;
    return timeElapsed / length;
  }

  public double calculate() {
    /* Optimization to avoid calling heavy methods when it's not needed */
    return (
        getProgress() >= 1 ? 1 :
            getProgress() <= 0 ? 0 :
                easing.calculate(getProgress())
    );
  }
}
