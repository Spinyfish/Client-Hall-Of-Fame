package dev.elysium.client.utils.render;

import java.awt.Color;

public class BColor {
    public int r, g, b, a;

    public BColor(int color) {
        Color c = new Color(color);
        r = c.getRed();
        g = c.getGreen();
        b = c.getBlue();
        a = c.getAlpha();
    }

    public String getHexString() {
        String red = Integer.toHexString(r);
        String green = Integer.toHexString(g);
        String blue = Integer.toHexString(b);

        if(red.length() == 1) red = "0" + red;
        if(green.length() == 1) green = "0" + green;
        if(blue.length() == 1) blue = "0" + blue;

        return red + green + blue;
    }

    public int[] getRGBvals() {
        return new int[] {r, g, b};
    }

    public int[] getRGBavals() {
        return new int[] {r, g, b, a};
    }

    public void setColor(int color) {
        Color c = new Color(color);
        r = c.getRed();
        g = c.getGreen();
        b = c.getBlue();
        a = c.getAlpha();
    }

    public BColor(int r, int g, int b) {
        this.r = r;
        this.g = g;
        this.b = b;
    }

    public void setRGBvals(int[] vals) {
        this.r = vals[0];
        this.g = vals[1];
        this.b = vals[2];
    }

    public void setVar(int index, int value) {
        switch(index) {
            case 0:
                r = value;
                break;
            case 1:
                g = value;
                break;
            case 2:
                b = value;
                break;
            case 3:
                a = value;
                break;
        }
    }

    public BColor(int r, int g, int b, int a) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
    }

    public int getColor() {
        return new Color(r, g, b, a).getRGB();
    }

    public float[] getHSB() {
        return Color.RGBtoHSB(r, g, b, null);
    }

    public void setHSB(float[] vals) {
        Color clrr = new Color(Color.HSBtoRGB(vals[0], vals[1], vals[2]));
        r = clrr.getRed();
        g = clrr.getGreen();
        b = clrr.getBlue();
        a = clrr.getAlpha();
    }
}