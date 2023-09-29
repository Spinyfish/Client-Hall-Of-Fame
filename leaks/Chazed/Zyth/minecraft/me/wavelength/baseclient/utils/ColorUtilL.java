package me.wavelength.baseclient.utils;


import java.awt.Color;

public class ColorUtilL {
	
	public static int getRGB(float seconds, float saturation, float brightness, long index) {
		float hue = ((System.currentTimeMillis() + index) % (int)(seconds * 1000)) / (float)(seconds * 1000);
		int color = Color.HSBtoRGB(hue, saturation, brightness);
		return color;
	}
	
	public static Color getGradientOffset(Color color1, Color color2, double offset) {
        if (offset > 1) {
            double left = offset % 1;
            int off = (int) offset;
            offset = off % 2 == 0 ? left : 1 - left;
        }
        double inverse_percent = 1 - offset;
        int redPart = (int) (color1.getRed() * inverse_percent + color2.getRed() * offset);
        int greenPart = (int) (color1.getGreen() * inverse_percent + color2.getGreen() * offset);
        int bluePart = (int) (color1.getBlue() * inverse_percent + color2.getBlue() * offset);
        return new Color(redPart, greenPart, bluePart);
    }

    public static int astolfoColors(int yOffset, int yTotal) {
        float speed = 2900F;
        float hue = (float) (System.currentTimeMillis() % (int)speed) + ((yTotal - yOffset) * 7);
        while (hue > speed) {
            hue -= speed;
        }
        hue /= speed;
        if (hue > 0.5) {
            hue = 0.5F - (hue - 0.5f);
        }
        hue += 0.5F;
        return Color.HSBtoRGB(hue, 0.64F, 1F);
    }

    public static int Basic(float seconds, long index) {
        index += 40;
        float hue = ((System.currentTimeMillis() + index) % (int)(seconds * 1000)) / (seconds * 1000);
        float hue2 = hue * 2;
        if(hue2 > 1) {
            hue2 = 2 - hue2;
        }
        return getGradientOffset(new Color(5, 138, 255), new Color(0, 36, 217), hue2).getRGB();
    }


    public static int GreenFlash(float seconds, long index) {
        index += 40;
        float hue = ((System.currentTimeMillis() + index) % (int)(seconds * 1000)) / (seconds * 1000);
        float hue2 = hue * 2;
        if(hue2 > 1) {
            hue2 = 2 - hue2;
        }
        return getGradientOffset(new Color(24, 219, 29), new Color(19, 128, 22), hue2).getRGB();
    }
    
    public static int Colors2(float seconds, long index) {
        index += 40;
        float hue = ((System.currentTimeMillis() + index) % (int)(seconds * 1000)) / (seconds * 1000);
        float hue2 = hue * 2;
        if(hue2 > 1) {
            hue2 = 2 - hue2;
        }
        return getGradientOffset(new Color(0, 200, 235), new Color(20, 75, 230), hue2).getRGB();
    }
    


}