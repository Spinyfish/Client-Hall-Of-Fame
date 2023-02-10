/**
 * @project Myth
 * @author CodeMan
 * @at 05.08.22, 19:39
 */
package dev.myth.api.utils.render;

import dev.myth.features.display.HUDFeature;
import dev.myth.main.ClientMain;
import dev.myth.managers.FeatureManager;
import lombok.experimental.UtilityClass;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.awt.image.BufferedImage;

@UtilityClass
public class ColorUtil {

    public final Color GRAY_17 = new Color(17, 17, 17);
    public final Color GRAY_29 = new Color(29, 29, 29);
    public final Color GRAY_45 = new Color(45, 45, 45);
    public final Color RED = new Color(200, 30, 30);
    public final Color GREEN = new Color(146, 224, 155);

    public int getClientColor(float counter) {
        HUDFeature hud = (HUDFeature) ClientMain.INSTANCE.manager.getManager(FeatureManager.class).getFeature(HUDFeature.class);
        float brightness = 1F - MathHelper.abs(MathHelper.sin((float) (counter % 6000F / 6000F * Math.PI * 2.0F)) * 0.5F);
        final float[] hsb = hud.arrayListColor.getHSBFromColor(hud.arrayListColor.getColor());
        Color color = Color.getHSBColor(hsb[0], hsb[1], brightness);
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), 255).getRGB();
    }

    public float[] toGLColor(final int color) {
        final float f = (float) (color >> 16 & 255) / 255.0F;
        final float f1 = (float) (color >> 8 & 255) / 255.0F;
        final float f2 = (float) (color & 255) / 255.0F;
        final float f3 = (float) (color >> 24 & 255) / 255.0F;
        return new float[]{f, f1, f2, f3};
    }

    public void doColor(final int color) {
        final float[] rgba = toGLColor(color);
        GL11.glColor4f(rgba[0], rgba[1], rgba[2], rgba[3]);
    }

    public int getHealthColor(final EntityLivingBase player) {
        final float f = player.getHealth();
        final float f2 = player.getMaxHealth();
        final float f3 = Math.max(0.0f, Math.min(f, f2) / f2);
        return Color.HSBtoRGB(f3 / 3.0f, 1.0f, 0.75f) | 0xFF000000;
    }

    public Color rainbow(int delay) {
        double rainbow;
        rainbow = Math.ceil((double)((System.currentTimeMillis() + (long)delay) / 75L));
        rainbow %= 90.0D;
        return Color.getHSBColor((float)(rainbow / 45.0D), 0.5F, 1.0F);
    }

    public Color getGradient(Color color1, Color color2, double percent) {
        if (percent > 1) {
            double left = percent % 1;
            int off = (int) percent;
            percent = off % 2 == 0 ? left : 1 - left;
        }
        double inverse_percent = 1 - percent;
        int redPart = (int) (color1.getRed() * inverse_percent + color2.getRed() * percent);
        int greenPart = (int) (color1.getGreen() * inverse_percent + color2.getGreen() * percent);
        int bluePart = (int) (color1.getBlue() * inverse_percent + color2.getBlue() * percent);
        return new Color(redPart, greenPart, bluePart);
    }

    public Color getGradientByTime(Color color1, Color color2, double offset) {
        double offs = (Math.abs(((System.currentTimeMillis()) / 16)) / 60D) + offset;
        return getGradient(color1, color2, offs);
    }

    /*
     https://stackoverflow.com/questions/28162488/get-average-color-on-bufferedimage-and-bufferedimage-portion-as-fast-as-possible
     */
    public Color getAverageColor(BufferedImage bi) {
        int step = 5;

        int sampled = 0;
        long sumr = 0, sumg = 0, sumb = 0;
        for (int x = 0; x < bi.getWidth(); x++) {
            for (int y = 0; y < bi.getHeight(); y++) {
                if (x % step == 0 && y % step == 0) {
                    Color pixel = new Color(bi.getRGB(x, y));
                    sumr += pixel.getRed();
                    sumg += pixel.getGreen();
                    sumb += pixel.getBlue();
                    sampled++;
                }
            }
        }
        return new Color(Math.round(sumr / sampled), Math.round(sumg / sampled), Math.round(sumb / sampled));
    }

}
