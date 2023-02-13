package juul.util.misc;

import java.util.Random;

public class BypassUtils {

	public static float range(float min, float max) {
		return min + (new Random().nextFloat() * (max-min));
	}
	
	public static int range(int min, int max) {
		return min + new Random().nextInt(max-min);
	}
	
}
