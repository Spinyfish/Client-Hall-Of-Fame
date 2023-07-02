package me.eldodebug.soar.utils.color;

import java.awt.Color;

import me.eldodebug.soar.Soar;
import me.eldodebug.soar.management.colors.AccentColor;
import me.eldodebug.soar.management.mods.impl.ClientMod;
import me.eldodebug.soar.management.mods.impl.HUDMod;
import me.eldodebug.soar.utils.MathUtils;
import me.eldodebug.soar.utils.animation.simple.SimpleAnimation;
import me.eldodebug.soar.utils.color.impl.ChristmasColorUtils;
import me.eldodebug.soar.utils.color.impl.HalloweenColorUtils;
import net.minecraft.client.renderer.GlStateManager;

public class ColorUtils {
	
	public static SimpleAnimation[] animation = {
			new SimpleAnimation(0.0F), new SimpleAnimation(0.0F), new SimpleAnimation(0.0F),
			new SimpleAnimation(0.0F), new SimpleAnimation(0.0F), new SimpleAnimation(0.0F),
			new SimpleAnimation(0.0F), new SimpleAnimation(0.0F), new SimpleAnimation(0.0F),
			new SimpleAnimation(0.0F), new SimpleAnimation(0.0F), new SimpleAnimation(0.0F),
			
			new SimpleAnimation(0.0F), new SimpleAnimation(0.0F), new SimpleAnimation(0.0F),
			new SimpleAnimation(0.0F), new SimpleAnimation(0.0F), new SimpleAnimation(0.0F)
	};
	
	public static Color getBackgroundColor(int id, int alpha) {
		
		int speed = 12;
		Color rawColor = getRawBackgroundColor(id);
		
		if(id == 1) {
			animation[0].setAnimation(rawColor.getRed(), speed);
			animation[1].setAnimation(rawColor.getGreen(), speed);
			animation[2].setAnimation(rawColor.getBlue(), speed);
			
			return new Color((int) animation[0].getValue(), (int) animation[1].getValue(), (int) animation[2].getValue(), alpha);
		}
		
		if(id == 2) {
			animation[3].setAnimation(rawColor.getRed(), speed);
			animation[4].setAnimation(rawColor.getGreen(), speed);
			animation[5].setAnimation(rawColor.getBlue(), speed);
			
			return new Color((int) animation[3].getValue(), (int) animation[4].getValue(), (int) animation[5].getValue(), alpha);
		}
		
		if(id == 3) {
			animation[6].setAnimation(rawColor.getRed(), speed);
			animation[7].setAnimation(rawColor.getGreen(), speed);
			animation[8].setAnimation(rawColor.getBlue(), speed);
			
			return new Color((int) animation[6].getValue(), (int) animation[7].getValue(), (int) animation[8].getValue(), alpha);
		}
		
		if(id == 4) {
			animation[9].setAnimation(rawColor.getRed(), speed);
			animation[10].setAnimation(rawColor.getGreen(), speed);
			animation[11].setAnimation(rawColor.getBlue(), speed);
			
			return new Color((int) animation[9].getValue(), (int) animation[10].getValue(), (int) animation[11].getValue(), alpha);
		}
		
		return rawColor;
	}
	
	public static Color getBackgroundColor(int id) {
		return getBackgroundColor(id, 255);
	}
	
	private static Color getRawBackgroundColor(int id) {
		Color color = new Color(255, 0, 0);
		boolean dark = Soar.instance.settingsManager.getSettingByClass(ClientMod.class, "DarkMode").getValBoolean();
		
		switch(id) {
			case 1:
				if(dark) {
					color = new Color(26, 33, 42);
				}else {
					color = new Color(232, 234, 240);
				}
				break;
			case 2:
				if(dark) {
					color = new Color(35, 40, 46);
				}else {
					color = new Color(239, 244, 249);
				}
				break;
			case 3:
				if(dark) {
					color = new Color(46, 51, 57);
				}else {
					color = new Color(247, 250, 252);
				}
				break;
			case 4:
				if(dark) {
					color = new Color(57, 61, 67);
				}else {
					color = new Color(253, 254, 254);
				}
				break;
		}
		return color;
	}
	
	public static Color getFontColor(int id, int alpha) {
		Color rawColor = getRawFontColor(id);
		int speed = 12;
		
		if(id == 1) {
			animation[12].setAnimation(rawColor.getRed(), speed);
			animation[13].setAnimation(rawColor.getGreen(), speed);
			animation[14].setAnimation(rawColor.getBlue(), speed);
			
			return new Color((int) animation[12].getValue(), (int) animation[13].getValue(), (int) animation[14].getValue(), alpha);
		}
		
		if(id == 2) {
			animation[15].setAnimation(rawColor.getRed(), speed);
			animation[16].setAnimation(rawColor.getGreen(), speed);
			animation[17].setAnimation(rawColor.getBlue(), speed);
			
			return new Color((int) animation[15].getValue(), (int) animation[16].getValue(), (int) animation[17].getValue(), alpha);
		}
		
		return rawColor;
	}
	
	private static Color getRawFontColor(int id) {
		Color color = new Color(0, 0, 255);
		boolean dark = Soar.instance.settingsManager.getSettingByClass(ClientMod.class, "DarkMode").getValBoolean();
		
		switch(id) {
			case 1:
				if(dark) {
					color = new Color(255, 255, 255);
				}else {
					color = new Color(27, 27, 27);
				}
				break;
			case 2:
				if(dark) {
					color = new Color(207, 209, 210);
				}else {
					color = new Color(96, 97, 97);
				}
				break;
		}
		
		return color;
	}
	
	public static Color getFontColor(int id) {
		return getFontColor(id, 255);
	}
	
	public static Color getClientColor(int index, int alpha) {
		
		for(AccentColor c : Soar.instance.colorManager.getColors()) {
			if(c.equals(Soar.instance.colorManager.getClientColor())) {
				return ColorUtils.interpolateColorsBackAndForth(15, index, new Color(c.getColor1().getRed(), c.getColor1().getGreen(), c.getColor1().getBlue(), alpha), new Color(c.getColor2().getRed(), c.getColor2().getGreen(), c.getColor2().getBlue(), alpha), false);
			}
		}
		
		return ColorUtils.interpolateColorsBackAndForth(15, index, new Color(234, 107, 149, alpha), new Color(238, 164, 123, alpha), false);
	}
	
	public static Color getClientColor(int index) {
		return ColorUtils.getClientColor(index, 255);
	}
    
    public static Color interpolateColorsBackAndForth(int speed, int index, Color start, Color end, boolean trueColor) {
        int angle = (int) (((System.currentTimeMillis()) / speed + index) % 360);
        angle = (angle >= 180 ? 360 - angle : angle) * 2;
        return trueColor ? ColorUtils.interpolateColorHue(start, end, angle / 360f) : ColorUtils.getInterpolateColor(start, end, angle / 360f);
    }
    
	private static Color getInterpolateColor(Color color1, Color color2, float amount) {
        amount = Math.min(1, Math.max(0, amount));
        return new Color(MathUtils.interpolateInt(color1.getRed(), color2.getRed(), amount), MathUtils.interpolateInt(color1.getGreen(), color2.getGreen(), amount), MathUtils.interpolateInt(color1.getBlue(), color2.getBlue(), amount), MathUtils.interpolateInt(color1.getAlpha(), color2.getAlpha(), amount));
	}
	
    private static Color interpolateColorHue(Color color1, Color color2, float amount) {
        amount = Math.min(1, Math.max(0, amount));

        float[] color1HSB = Color.RGBtoHSB(color1.getRed(), color1.getGreen(), color1.getBlue(), null);
        float[] color2HSB = Color.RGBtoHSB(color2.getRed(), color2.getGreen(), color2.getBlue(), null);

        Color resultColor = Color.getHSBColor(MathUtils.interpolateFloat(color1HSB[0], color2HSB[0], amount), MathUtils.interpolateFloat(color1HSB[1], color2HSB[1], amount), MathUtils.interpolateFloat(color1HSB[2], color2HSB[2], amount));

        return new Color(resultColor.getRed(), resultColor.getGreen(), resultColor.getBlue(), MathUtils.interpolateInt(color1.getAlpha(), color2.getAlpha(), amount));
    }
    
    public static void setColor(int color, float alpha) {
        float r = (float) (color >> 16 & 255) / 255.0F;
        float g = (float) (color >> 8 & 255) / 255.0F;
        float b = (float) (color & 255) / 255.0F;
        GlStateManager.color(r, g, b, alpha);
    }
    
    public static void setColor(int color) {
    	setColor(color, (float) (color >> 24 & 255) / 255.0F);
    }
    
    public static void resetColor() {
        GlStateManager.color(1, 1, 1, 1);
    }
    
    public static Color patchAlpha(int color, float alpha) {
        float r = (float) (color >> 16 & 255) / 255.0F;
        float g = (float) (color >> 8 & 255) / 255.0F;
        float b = (float) (color & 255) / 255.0F;
        
        return new Color(r, g, b, alpha);
    }
    
    public static int getClientOpacity() {
		int opacity = (int) (Soar.instance.settingsManager.getSettingByClass(HUDMod.class, "Opacity").getValDouble() * 255);
		
    	return opacity;
    }
    
    public static Color rainbow(int index, double speed, int opacity) {
        int angle = (int) ((System.currentTimeMillis() / speed + index) % 360);
        float hue = angle / 360f;
        int color = Color.HSBtoRGB(hue, 1F, 1);
        Color c =  new Color(color);
        return new Color(c.getRed(), c.getGreen(), c.getBlue(), opacity);
    }
    
    public static HalloweenColorUtils getHalloweenColor() {
    	return new HalloweenColorUtils();
    }
    
    public static ChristmasColorUtils getChristmasColor() {
    	return new ChristmasColorUtils();
    }
}
