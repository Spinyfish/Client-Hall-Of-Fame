package dev.thread.api.util.math.animation;

/**
 * @author aesthetical, Easings.net
 * @since 03/26/23
 * @link {<a href="https://easings.net/">Easings.net</a>}
 */
public interface Easing {
    Easing EXPO_IN_OUT = (x) -> x == 0 ? 0 : x == 1 ? 1 : x < 0.5 ? Math.pow(2, 20 * x - 10) / 2 : (2 - Math.pow(2, -20 * x + 10)) / 2;

    Easing CUBIC_IN_OUT = (x) -> x < 0.5 ? 4 * x * x * x : 1 - Math.pow(-2 * x + 2, 3) / 2;

    double ease(double x);
}
