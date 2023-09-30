/*
 * Decompiled with CFR 0.152.
 */
package wtf.opengui;

import java.awt.Color;

public class CStyle {
    public Color fill_color = new Color(0, true);
    public float radius;
    public float width;
    public float height;

    public CStyle set(Object in, Object setter) {
        in = setter;
        return this;
    }
}

