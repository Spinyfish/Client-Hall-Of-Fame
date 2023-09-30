package de.fanta.utils;

import java.awt.Color;
import java.util.Random;

import net.minecraft.client.Minecraft;

public class ColorUtils {
	
	public static Color getClickGuiColor() {
		return new Color(218, 20, 23);
	}
	
	public static Color getClickGuiColor(final float alpha) {
		return new Color(218, 20, 23, alpha);
	}

	public static Color getRandomColor() {
		Random rand = new Random();
		float r = rand.nextFloat();
		float g = rand.nextFloat();
		float b = rand.nextFloat();
		Color randomColor = new Color(r, g, b);
		return randomColor;
	}

	public static Color rainBowEffectWithOffset(int offset, float custom) {
		Minecraft mc = Minecraft.getMinecraft();
		int tmp = offset;
		long l = System.currentTimeMillis() - (tmp * 10 - 0 * 10);
		int i = Color.HSBtoRGB(l % (int) custom / custom, 1F, 1F);
		return (new Color(i));
	}
	
	public static Color rainBowEffectWithOffset(int offset) {
		Minecraft mc = Minecraft.getMinecraft();
		int tmp = offset;
		long l = System.currentTimeMillis() - (tmp * 10 - 0 * 10);
		int i = Color.HSBtoRGB(l % (int) 2000.0F / 2000.0F, 1F, 1F);
		return (new Color(i));
	}
	
	public static Color colorWithOffset(Color c, int offset) {
		Minecraft mc = Minecraft.getMinecraft();
		int tmp = offset;
		long l = System.currentTimeMillis() - (tmp * 10 - 0 * 10);
		int i = Color.HSBtoRGB(c.getRGB(), l % (int) 4000.0F / 4000.0F, 1F);
		return (new Color(i));
	}
	
	public static Color rainBow() {
		long l = System.currentTimeMillis() - (1 * 10 - 1 * 10);
		int i = Color.HSBtoRGB(l % (int) 2000.0F / 2000.0F, 1F, 1F);
		return new Color(i);
	}
	
	public static Color getColorAlpha(Color color, int alpha) {
		return getColorAlpha(color.getRGB(), alpha);
	}

	public static Color getColorAlpha(int color, int alpha) {
		Color color2 = new Color(new Color(color).getRed(), new Color(color).getGreen(), new Color(color).getBlue(),
				alpha);
		return color2;
	}
	
	public static int getMulitpliedColor(Color color, double ammount) {
		return new Color((int)(color.getRed()*ammount), (int)(color.getGreen()*ammount), (int)(color.getBlue()*ammount), color.getAlpha()).getRGB();
	}
	
	public static int getMulitpliedColor(int color, double ammount) {
		return getMulitpliedColor(new Color(color), ammount);
	}

}
