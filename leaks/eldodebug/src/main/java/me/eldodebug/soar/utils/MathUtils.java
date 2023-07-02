package me.eldodebug.soar.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class MathUtils {
	
    public static float clamp(float value) {
        return (double) value < 0.0D ? 0.0F : ((double) value > 1.0D ? 1.0F : value);
    }
    
    public static float clamp(float number, float min, float max) {
        return number < min ? min : Math.min(number, max);
    }
    
    public static float lerp(float a, float b, float t) {
        return a + (b - a) * clamp(t);
    }

    public static float getPercent(float val, float min, float max) {
        return (val - min) / (max - min);
    }
    
    public static Double interpolate(double oldValue, double newValue, double interpolationValue){
        return (oldValue + (newValue - oldValue) * interpolationValue);
    }

    public static float interpolateFloat(float oldValue, float newValue, double interpolationValue){
        return interpolate(oldValue, newValue, (float) interpolationValue).floatValue();
    }

    public static int interpolateInt(int oldValue, int newValue, double interpolationValue){
        return interpolate(oldValue, newValue, (float) interpolationValue).intValue();
    }
    
    public static float calculateGaussianValue(float x, float sigma) {
        double PI = 3.141592653;
        double output = 1.0 / Math.sqrt(2.0 * PI * (sigma * sigma));
        return (float) (output * Math.exp(-(x * x) / (2.0 * (sigma * sigma))));
    }
    
	public static double roundToPlace(final double value, final int places) {
		if (places < 0) {
			throw new IllegalArgumentException();
		}
		BigDecimal bd = new BigDecimal(value);
		bd = bd.setScale(places, RoundingMode.HALF_UP);
		return bd.doubleValue();
	}
}
