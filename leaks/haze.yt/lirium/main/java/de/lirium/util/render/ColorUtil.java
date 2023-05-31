package de.lirium.util.render;

import java.awt.Color;

public class ColorUtil {

	public static final Color PRIMARY_COLOR = new Color(98, 63, 146), SECONDARY_COLOR = new Color(77, 60, 255);

	public static Color getColor(float speed, double offset) {
		return getFade(speed, offset, PRIMARY_COLOR, SECONDARY_COLOR);
	}

	public static Color getColor(float speed, double offset, final Color color, final Color color2) {
		return getFade(speed, offset, color, color2);
	}

	public static Color getRainbow(float speed, double offset, float saturation) {
		float hue = (float) (((System.currentTimeMillis() + offset) % speed) / speed);
		return new Color(Color.HSBtoRGB(hue, saturation, 1.0f));
	}
	
	public static Color getFade(float speed, double offset, Color color1, Color color2) {
		double h = Math.abs(1 - (((System.currentTimeMillis() + offset) % speed) / speed) * 2.0);
		int diffR = color1.getRed() - color2.getRed(), diffG = color1.getGreen() - color2.getGreen(), diffB = color1.getBlue() - color2.getBlue();
		return new Color((int)(color2.getRed() + (diffR * h)), (int)(color2.getGreen() + (diffG * h)), (int)(color2.getBlue() + (diffB * h)));
	}
	
}