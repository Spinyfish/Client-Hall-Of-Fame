package dev.eternal.client.util.math;

import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.math.RoundingMode;

@UtilityClass
public class MathUtil {

  public static final float DEG_TO_RAD = 0.017453292519943295F;
  public static final float RAD_TO_DEG = 57.29577951308232F;

  public double round(double value, double modulo) {
    return value - value % modulo;
  }

  public double roundToPlace(double value, int places) {
    return BigDecimal.valueOf(value).setScale(places, RoundingMode.HALF_UP).doubleValue();
  }

  public static double lerp(double a, double b, double progress) {
    return a * (1 - progress) + b * progress;
  }
}
