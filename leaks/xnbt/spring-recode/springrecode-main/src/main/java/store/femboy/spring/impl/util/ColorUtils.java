package store.femboy.spring.impl.util;

import java.awt.*;

public final class ColorUtils {

    //fuck off ik this is intent base rainbow but its so good
    public static int getRainbow(float seconds, float saturation, float brightness) {
        float hue = (System.currentTimeMillis() % (int)(seconds * 1000)) / (float)(seconds*1000);
        int color = Color.HSBtoRGB(hue, saturation, brightness);
        return color;
    }

    public static int getRainbow(float seconds, float saturation, float brightness, long index) {
        float hue = ((System.currentTimeMillis() + index) % (int)(seconds * 1000)) / (float)(seconds*1000);
        int color = Color.HSBtoRGB(hue, saturation, brightness);
        return color;
    }

    //sorryr for skid mouseware ;-;

    public static Color fade(int offset, int count, Color color) {
        float[] hsb = new float[3];
        Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), hsb);
        float brightness = Math.abs(((float) (System.currentTimeMillis() % 2000L) / 1000.0F + (float) offset / (float) count * 2.0F) % 2.0F - 1.0F);
        brightness = 0.5F + 0.5F * brightness;
        hsb[2] = brightness % 2.0F;
        return new Color(Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]));
    }

    public static int astolfo(double speed, double modulo, double saturation, double brightness, double offset) {
        int i = (int) ((System.currentTimeMillis() / speed + offset) % modulo);
        i = (int) ((i > modulo / 2 ? modulo - i : i) + modulo / 2);

        return Color.HSBtoRGB((float) (i / modulo), (float) saturation, (float) brightness);
    }
}
