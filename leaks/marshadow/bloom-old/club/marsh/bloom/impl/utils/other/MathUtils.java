package club.marsh.bloom.impl.utils.other;

import java.math.BigDecimal;
import java.math.RoundingMode;


public class MathUtils {
	//made by google bard
	public static double clamp(double value, double min, double max) {
        if (value < min) {
            return min;
        } else if (value > max) {
            return max;
        } else {
            return value;
        }
    }
	
	public static float clamp(float value, float min, float max) {
        if (value < min) {
            return min;
        } else if (value > max) {
            return max;
        } else {
            return value;
        }
    }

    public static double roundToPlace(double value, int places) {//:O FLASHY GONNA SIKD THIS!??!??
        if (places < 0) {
            throw new IllegalArgumentException();
        }
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
    
    public static double interpolate(double start, double end, double fraction) {
        return start + (end - start) * fraction;
    }
    
}
