package club.pulsive.impl.util.render;

import club.pulsive.impl.util.math.apache.ApacheMath;
import lombok.experimental.UtilityClass;

import java.awt.*;

@UtilityClass
public class ColorUtil {
    public float getOffset(int index) {
        long ms = (long) (1.3 * 1000L);
        long currentMillis = -1;
        currentMillis = System.currentTimeMillis();
        final float offset = (currentMillis + (3 * 2 / (index + 1) * 50)) % ms / (ms / 2.0F);
        return offset;
    }
    public Color interpolateColorC(Color color1, Color color2, float amount) {
        amount = ApacheMath.min(1, ApacheMath.max(0, amount));
        return new Color(interpolateInt(color1.getRed(), color2.getRed(), amount),
                interpolateInt(color1.getGreen(), color2.getGreen(), amount),
                interpolateInt(color1.getBlue(), color2.getBlue(), amount),
                interpolateInt(color1.getAlpha(), color2.getAlpha(), amount));
    }

    public int interpolateInt(int oldValue, int newValue, double interpolationValue){
        return interpolate(oldValue, newValue, (float) interpolationValue).intValue();
    }
    public Double interpolate(double oldValue, double newValue, double interpolationValue){
        return (oldValue + (newValue - oldValue) * interpolationValue);
    }

}
