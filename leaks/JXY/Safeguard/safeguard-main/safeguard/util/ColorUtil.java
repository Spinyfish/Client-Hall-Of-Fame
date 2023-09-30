package intentions.util;

import java.awt.Color;

public class ColorUtil {

	public static int getRainbowColor() {
		float hue = (System.currentTimeMillis() % 1000) / 1000f;
		return Color.HSBtoRGB(hue, 1, 1);
	}

	public static int getDarkRainbowColor() {
		float hue = (System.currentTimeMillis() % 1000) / 1000f;
		return Color.HSBtoRGB(hue, 0.9f, 0.5f);
	}
	
}
