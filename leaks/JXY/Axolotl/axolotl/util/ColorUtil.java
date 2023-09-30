package axolotl.util;

import java.awt.Color;

public class ColorUtil {

	public static int getRainbowColor(long index) {
		float hue = ((System.currentTimeMillis() - (index*50)) % 1000) / 1000f;
		return Color.HSBtoRGB(hue, 1, 1);
	}
	
	public static int getRainbowColor() {
		float hue = (System.currentTimeMillis() % 1000) / 1000f;
		return Color.HSBtoRGB(hue, 1, 1);
	}
	
	public static int getDynamicColor(int index)
	{
		int q = (int)((System.currentTimeMillis()) % 1000) / 50;
		return new Color(255, 192-q, 203-q).getRGB();
	}

    public static int getDynamicColor2(int i, int count, int i1, int i2) {
		float hue = (float) (i + ((count % i1) * i2) + ((System.currentTimeMillis() / 80) % 10 * 5));
		return (int)Math.floor(hue);
    }
}
