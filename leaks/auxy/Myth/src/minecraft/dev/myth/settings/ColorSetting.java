/**
 * @project Myth
 * @author CodeMan
 * @at 20.08.22, 14:13
 */
package dev.myth.settings;

import dev.myth.api.setting.Setting;

import java.awt.*;
import java.util.function.Supplier;

public class ColorSetting extends Setting<Color> {

    private Color color;
    private float hue;
    private float saturation;
    private float brightness;

    public ColorSetting(String name, Color value) {
        super(name, value);
        this.setColor(value);
    }

    @Override
    public void setValueFromString(String value) {
        this.setColor(new Color(Integer.parseInt(value)));
    }

    public Color getValue() {
        return Color.getHSBColor(this.hue, this.saturation, this.brightness);
    }

    public void setValue(final int hex) {
        final float[] hsb = this.getHSBFromColor(hex);
        this.hue = hsb[0];
        this.saturation = hsb[1];
        this.brightness = hsb[2];
    }

    public int getColor() {
        return this.getValue().getRGB();
    }

    public int getRed() {
        return getValue().getRed();
    }

    public int getGreen() {
        return getValue().getGreen();
    }

    public int getBlue() {
        return getValue().getBlue();
    }

    public void setColor(final Color color) {
        if(color == null) return;
        final float[] colors = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
        this.hue = colors[0];
        this.saturation = colors[1];
        this.brightness = colors[2];
    }

    public float getSaturation() {
        return this.saturation;
    }

    public void setSaturation(final float saturation) {
        this.saturation = saturation;
    }

    public float getBrightness() {
        return this.brightness;
    }

    public void setBrightness(final float brightness) {
        this.brightness = brightness;
    }

    public float getHue() {
        return this.hue;
    }

    public void setHue(final float hue) {
        this.hue = hue;
    }

    public float[] getHSBFromColor(final int hex) {
        final int r = hex >> 16 & 0xFF;
        final int g = hex >> 8 & 0xFF;
        final int b = hex & 0xFF;
        return Color.RGBtoHSB(r, g, b, null);
    }

    public ColorSetting setDisplayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    public ColorSetting addDependency(Supplier<Boolean> visible) {
        this.visible = visible;
        return this;
    }

}
