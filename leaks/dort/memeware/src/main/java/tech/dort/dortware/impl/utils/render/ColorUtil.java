package tech.dort.dortware.impl.utils.render;

import net.minecraft.entity.EntityLivingBase;
import tech.dort.dortware.Client;
import tech.dort.dortware.impl.modules.render.Hud;

import java.awt.*;

public class ColorUtil {

    public static int rainbow(int delay, double speed) {
        double rainbowState = Math.ceil(((System.currentTimeMillis() * speed) + -delay) / 20.0);
        rainbowState %= 360;
        return Color.getHSBColor((float) (rainbowState / 360.0f), 1f, 0.7f).getRGB();
    }

    public static int astolfoColors(int timeOffset, int yTotal) {
        final float speed = 2900F;
        float hue = (float) (System.currentTimeMillis() % (int) speed) + ((yTotal - timeOffset) * 9);
        while (hue > speed) {
            hue -= speed;
        }
        hue /= speed;
        if (hue > 0.5) {
            hue = 0.5F - (hue - 0.5f);
        }
        hue += 0.5F;
        return Color.HSBtoRGB(hue, .65F, 1F);
    }

    public static int getHealthColor(EntityLivingBase entityLivingBase) {
        final float percentage = 100 * ((entityLivingBase.getHealth() / 2) / (entityLivingBase.getMaxHealth() / 2));
        return percentage > 75 ? 0x19ff19 : percentage > 50 ? 0xffff00 : percentage > 25 ? 0xff5500 : 0xff0900;
    }

    public static int getModeColor() {
        final Hud hud = Client.INSTANCE.getModuleManager().get(Hud.class);
        final float red = hud.red.getCastedValue().floatValue();
        final float green = hud.green.getCastedValue().floatValue();
        final float blue = hud.blue.getCastedValue().floatValue();

        return new Color(red / 255.0F, green / 255.0F, blue / 255.0F).getRGB();
    }
}
