package me.kansio.client.utils.render;

import me.kansio.client.Client;
import me.kansio.client.modules.impl.visuals.HUD;
import me.kansio.client.utils.Util;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class ColorUtils extends Util {

    public static void glColor(int hex) {
        float red = (hex >> 16 & 0xFF) / 255.0F;
        float green = (hex >> 8 & 0xFF) / 255.0F;
        float blue = (hex & 0xFF) / 255.0F;
        float alpha = (hex >> 24 & 0xFF) / 255.0F;
        GL11.glColor4f(red, green, blue, alpha);
    }

    public static void glColor(int redRGB, int greenRGB, int blueRGB, float alpha) {
        float red = 0.003921569F * redRGB;
        float green = 0.003921569F * greenRGB;
        float blue = 0.003921569F * blueRGB;
        GL11.glColor4f(red, green, blue, alpha);
    }

    public static void glColor(Color color) {
        GlStateManager.color((float) color.getRed() / 255F, (float) color.getGreen() / 255F, (float) color.getBlue() / 255F, (float) color.getAlpha() / 255F);
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

    public static int getRainbow(int speed, int offset) {
        float hue = (System.currentTimeMillis() + offset) % speed;
        hue /= speed;
        return Color.getHSBColor(hue, 0.75f, 1f).getRGB();
    }

    public static Color getColorFromHud(int y) {
        Color color = null;
        HUD hud = Client.getInstance().getModuleManager().getModuleByClass(HUD.class);

        switch (((HUD) Client.getInstance().getModuleManager().getModuleByName("HUD")).getColorMode().getValue()) {
            case "Sleek": {
                color = ColorUtils.getGradientOffset(new Color(0, 255, 128), new Color(212, 1, 1), (Math.abs(((System.currentTimeMillis()) / 10)) / 100D) + y / mc.fontRendererObj.FONT_HEIGHT * 9.95);
                break;
            }
            case "Nitrogen": {
                color = ColorUtils.getGradientOffset(new Color(128, 171, 255), new Color(160, 72, 255), (Math.abs(((System.currentTimeMillis()) / 10)) / 100D) + y / mc.fontRendererObj.FONT_HEIGHT * 9.95);
                break;
            }
            case "Rainbow": {
                color = new Color(ColorUtils.getRainbow(6000, y * 15));
                break;
            }
            case "Astolfo": {
                color = ColorUtils.getGradientOffset(new Color(255, 60, 234), new Color(27, 179, 255), (Math.abs(((System.currentTimeMillis()) / 10)) / 100D) + y / mc.fontRendererObj.FONT_HEIGHT * 9.95);
                break;
            }
            case "Gradient": {
                color = ColorUtils.getGradientOffset(new Color(hud.getTopRed().getValue().intValue(), hud.getTopGreen().getValue().intValue(), hud.getTopBlue().getValue().intValue()), new Color(hud.getBottomRed().getValue().intValue(), hud.getBottomGreen().getValue().intValue(), hud.getBottomBlue().getValue().intValue()), (Math.abs(((System.currentTimeMillis()) / 10)) / 100D) + y / mc.fontRendererObj.FONT_HEIGHT * 9.95);
                break;
            }
            case "Wave": {
                color = ColorUtils.getGradientOffset(new Color(hud.getStaticRed().getValue().intValue(), hud.getStaticGreen().getValue().intValue(), hud.getStaticBlue().getValue().intValue()), new Color(hud.getStaticRed().getValue().intValue(), hud.getStaticGreen().getValue().intValue(), hud.getStaticBlue().getValue().intValue()).darker(), (Math.abs(((System.currentTimeMillis()) / 10)) / 100D) + y / mc.fontRendererObj.FONT_HEIGHT * 9.95);
                break;
            }
            case "Static": {
                color = new Color(hud.getStaticRed().getValue().intValue(), hud.getStaticGreen().getValue().intValue(), hud.getStaticBlue().getValue().intValue());
                break;
            }
        }

        return color;
    }

    public static Color setAlpha(Color color, int alpha) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
    }

}