package dev.elysium.client.utils.render;

import dev.elysium.client.Elysium;
import dev.elysium.client.mods.impl.settings.Colors;

import java.awt.Color;

public class ColorUtil {

    public static int astolfocount;
    public static Colors colors = (Colors) Elysium.getInstance().getModManager().getModByName("Colors");

    public static int getRainbow(float seconds, float saturation, float brightness, long index) {
        switch(colors.rainbowmode.getMode()) {
            case "Astolfo":
                return getAstolfoRainbow(seconds,saturation,brightness, index);
            case "Custom":
                return getCustom(seconds,saturation,brightness, index);
            case "Rainbow":
                return OldgetRainbow(seconds,saturation,brightness, index);
            case "Christmas":
                return getChristmas(seconds,saturation, index);

        }
        return 0xFF000000;
    }

    public static int getCustom(float seconds, float saturation, float hue, long index) {
        float brightness = ((System.currentTimeMillis() + index) % (int)(seconds * 1000)) / (float)(seconds * 1000);

        int brigtness_step = 0;

        if(brightness <= 0.5){
            if(brightness >= 0.25){
                brightness = (float) (0.25f + (0.5-brightness));
            } else if(brightness < 0.25){
                brightness += 0.25;
            }
        } else {
            brightness -= 0.5f;
            if(brightness >= 0.25){
                brightness = (float) (0.25f + (0.5-brightness));
            } else if(brightness < 0.25){
                brightness += 0.25;
            }
        }
        brightness *= 2;
        brightness -= 0.2;

        int color = Color.HSBtoRGB((float) ( (brightness/ colors.custommultiplier.getValue())+(colors.customcolour.getValue()/100) ), 0.7F, brightness*1.2F);

        if(colors.custommultiplier.getValue() == 0) {
            color = Color.HSBtoRGB((float) ( (colors.customcolour.getValue()/100) ), 0.7F, brightness*1.2F);
        }
        return color;
    }

    public static int OldgetRainbow(float seconds, float saturation, float brightness, long index) {
        float hue = ((System.currentTimeMillis() + index) % (int)(seconds * 1000)) / (float)(seconds * 1000);
        int color = Color.HSBtoRGB(hue, saturation, brightness);
        return color;
    }

    public static int getChristmas(float seconds, float brightness, long index) {
        if(index > 10) seconds *= 0.5;
        float saturation = ((System.currentTimeMillis() + index) % (int)(seconds * 1000)) / (float)(seconds * 1000);

        float hue = 0F;

        if(saturation >= 0.5){
            saturation = (1-saturation);
        }

        saturation *= 2;

        int color = Color.HSBtoRGB(hue, saturation, 1);
        return color;
    }

    public static int getAstolfoRainbow(float seconds, float saturation, float brightness, long index) {
        float hue = ((System.currentTimeMillis() + index) % (int)(seconds * 1000)) / (float)(seconds * 1000);

        if(hue >= 0.5){
            hue = 0.5f + (1-hue);
        } else if(hue < 0.5){
            hue += 0.5;
        }


        int color = Color.HSBtoRGB(hue, saturation, brightness);
        return color;
    }

}
