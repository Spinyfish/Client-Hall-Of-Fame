/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  vip.astroline.client.service.module.value.Value
 *  vip.astroline.client.service.module.value.ValueManager
 */
package vip.astroline.client.service.module.value;

import java.awt.Color;
import vip.astroline.client.service.module.value.Value;
import vip.astroline.client.service.module.value.ValueManager;

public class ColorValue
extends Value {
    private boolean rainbow = false;
    private double rainbowSpeed = 1.0;

    public double getRainbowSpeed() {
        return this.rainbowSpeed;
    }

    public void setRainbowSpeed(double rainbowSpeed) {
        this.rainbowSpeed = rainbowSpeed;
    }

    public boolean isRainbow() {
        return this.rainbow;
    }

    public void setRainbow(boolean rainbow) {
        this.rainbow = rainbow;
    }

    public ColorValue(String group, String key, Color value, boolean fromAPI) {
        this.group = group;
        this.key = key;
        this.value = value;
        if (fromAPI) return;
        ValueManager.addValue((Value)this);
    }

    public ColorValue(String group, String key, Color value) {
        this(group, key, value, false);
    }

    public void setValue(Color value) {
        this.value = value;
    }

    public void setValueInt(int value) {
        this.value = new Color(value);
    }

    public Color getValue() {
        return (Color)this.value;
    }

    public Color getColor() {
        return this.getValue();
    }

    public int getColorInt() {
        return this.getValue().getRGB();
    }

    public String getColorString() {
        return this.getValue().getRGB() + "";
    }

    public float[] getColorHSB() {
        Color currentColor = (Color)this.value;
        return Color.RGBtoHSB(currentColor.getRed(), currentColor.getGreen(), currentColor.getBlue(), null);
    }

    public void setRed(int amount) {
        Color currentColor = (Color)this.value;
        Color newColor = new Color(amount, currentColor.getGreen(), currentColor.getBlue(), currentColor.getAlpha());
        this.value = newColor;
    }

    public void setGreen(int amount) {
        Color currentColor = (Color)this.value;
        Color newColor = new Color(currentColor.getRed(), amount, currentColor.getBlue(), currentColor.getAlpha());
        this.value = newColor;
    }

    public void setBlue(int amount) {
        Color currentColor = (Color)this.value;
        Color newColor = new Color(currentColor.getRed(), currentColor.getGreen(), amount, currentColor.getAlpha());
        this.value = newColor;
    }

    public int getRed() {
        return ((Color)this.value).getRed();
    }

    public int getGreen() {
        return ((Color)this.value).getGreen();
    }

    public int getBlue() {
        return ((Color)this.value).getBlue();
    }
}
