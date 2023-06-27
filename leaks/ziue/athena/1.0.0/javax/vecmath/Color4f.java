package javax.vecmath;

import java.io.*;
import java.awt.*;

public class Color4f extends Tuple4f implements Serializable
{
    static final long serialVersionUID = 8577680141580006740L;
    
    public Color4f(final float x, final float y, final float z, final float w) {
        super(x, y, z, w);
    }
    
    public Color4f(final float[] c) {
        super(c);
    }
    
    public Color4f(final Color4f c1) {
        super(c1);
    }
    
    public Color4f(final Tuple4f t1) {
        super(t1);
    }
    
    public Color4f(final Tuple4d t1) {
        super(t1);
    }
    
    public Color4f(final Color color) {
        super(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, color.getAlpha() / 255.0f);
    }
    
    public Color4f() {
    }
    
    public final void set(final Color color) {
        this.x = color.getRed() / 255.0f;
        this.y = color.getGreen() / 255.0f;
        this.z = color.getBlue() / 255.0f;
        this.w = color.getAlpha() / 255.0f;
    }
    
    public final Color get() {
        final int r = Math.round(this.x * 255.0f);
        final int g = Math.round(this.y * 255.0f);
        final int b = Math.round(this.z * 255.0f);
        final int a = Math.round(this.w * 255.0f);
        return new Color(r, g, b, a);
    }
}
