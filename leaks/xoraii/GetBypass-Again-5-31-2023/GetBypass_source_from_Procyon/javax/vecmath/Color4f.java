// 
// Decompiled by Procyon v0.5.36
// 

package javax.vecmath;

import java.awt.Color;
import java.io.Serializable;

public class Color4f extends Tuple4f implements Serializable
{
    public Color4f(final float x, final float y, final float z, final float w) {
        super(x, y, z, w);
    }
    
    public Color4f(final float[] c) {
        super(c);
    }
    
    public Color4f(final Color4f c1) {
        super(c1);
    }
    
    public Color4f(final Tuple4d t1) {
        super(t1);
    }
    
    public Color4f(final Tuple4f t1) {
        super(t1);
    }
    
    public Color4f() {
    }
    
    public Color4f(final Color color) {
        super.x = color.getRed() / 255.0f;
        super.y = color.getGreen() / 255.0f;
        super.z = color.getBlue() / 255.0f;
        super.w = color.getAlpha() / 255.0f;
    }
    
    public final void set(final Color color) {
        super.x = color.getRed() / 255.0f;
        super.y = color.getGreen() / 255.0f;
        super.z = color.getBlue() / 255.0f;
        super.w = color.getAlpha() / 255.0f;
    }
    
    public final Color get() {
        return new Color(super.x, super.y, super.z, super.w);
    }
}
