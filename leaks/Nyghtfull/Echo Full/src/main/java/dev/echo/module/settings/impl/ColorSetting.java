package dev.echo.module.settings.impl;

import com.google.gson.JsonObject;
import dev.echo.module.settings.Setting;
import dev.echo.utils.render.ColorUtil;


import java.awt.*;

public class ColorSetting extends Setting {
    private float hue = 0;
    private float saturation = 1;
    private float brightness = 1;
    private Rainbow rainbow = null;

    public ColorSetting(String name, Color defaultColor) {
        this.name = name;
        this.setColor(defaultColor);
    }

   
    public Color getColor() {
        return isRainbow() ? getRainbow().getColor() : Color.getHSBColor(hue, saturation, brightness);
    }

    public Color getAltColor() {
        return isRainbow() ? getRainbow().getColor(40) : ColorUtil.darker(getColor(), .6f);
    }


    public void setColor(Color color) {
        float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
        hue = hsb[0];
        saturation = hsb[1];
        brightness = hsb[2];
    }

    public void setColor(float hue, float saturation, float brightness) {
        this.hue = hue;
        this.saturation = saturation;
        this.brightness = brightness;
    }

   
    public double getHue() {
        return hue;
    }

    public void setHue(float hue) {
        this.hue = hue;
    }

   
    public double getSaturation() {
        return saturation;
    }

    public void setSaturation(float saturation) {
        this.saturation = saturation;
    }

   
    public double getBrightness() {
        return brightness;
    }

    public void setBrightness(float brightness) {
        this.brightness = brightness;
    }

    public String getHexCode() {
        Color color = getColor();
        return String.format("%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
    }

   
    public Rainbow getRainbow() {
        return rainbow;
    }

   
    public void setRainbow(boolean rainbow) {
        if (rainbow) {
            this.rainbow = new Rainbow();
        } else {
            this.rainbow = null;
        }
    }

   
    public boolean isRainbow() {
        return rainbow != null;
    }

    @Override
    public Object getConfigValue() {
        return isRainbow() ? getRainbow().getJsonObject() : getColor().getRGB();
    }

    public static class Rainbow {
        private float saturation = 1;
        private int speed = 15;

       
        public Color getColor() {
            return getColor(0);
        }

       
        public Color getColor(int index) {
            return ColorUtil.rainbow(speed, index, saturation, 1, 1);
        }

        public JsonObject getJsonObject() {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("saturation", saturation);
            jsonObject.addProperty("speed", speed);
            return jsonObject;
        }

       
        public float getSaturation() {
            return Math.max(0, Math.min(1, saturation));
        }

       
        public void setSaturation(float saturation) { this.saturation = saturation; }

       
        public int getSpeed() { return speed; }

       
        public void setSpeed(int speed) { this.speed = speed; }
    }

}
