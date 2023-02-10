/**
 * @project Myth
 * @author CodeMan
 * @at 05.08.22, 20:27
 */
package dev.myth.api.utils.math;

import dev.myth.api.utils.font.CFontRenderer;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.ThreadLocalRandom;

public class MathUtil {

    public static double round(double val, double inc) {
        double v = Math.round(val / inc) * inc;
        BigDecimal bd = new BigDecimal(v);
        bd = bd.setScale(10, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public static int calculateMiddle(String text, CFontRenderer fontRenderer, double x, double width) {
        return (int) (x + (width - fontRenderer.getStringWidth(text)) / 2);
    }

    public static float calculate(float x, float sigma) {
        double PI = Math.PI;
        double output = 1.0 / Math.sqrt(2.0 * PI * (sigma * sigma));
        return (float) (output * Math.exp(-(x * x) / (2.0 * (sigma * sigma))));
    }

    public static double getRandomValue(double minValue, double maxValue) {
        return Math.random() * (maxValue - minValue) + minValue;
    }

}
