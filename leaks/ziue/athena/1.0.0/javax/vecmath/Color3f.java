package javax.vecmath;

import java.io.*;
import java.awt.*;

public class Color3f extends Tuple3f implements Serializable
{
    static final long serialVersionUID = -1861792981817493659L;
    
    public Color3f(final float x, final float y, final float z) {
        super(x, y, z);
    }
    
    public Color3f(final float[] v) {
        super(v);
    }
    
    public Color3f(final Color3f v1) {
        super(v1);
    }
    
    public Color3f(final Tuple3f t1) {
        super(t1);
    }
    
    public Color3f(final Tuple3d t1) {
        super(t1);
    }
    
    public Color3f(final Color color) {
        super(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f);
    }
    
    public Color3f() {
    }
    
    public final void set(final Color color) {
        this.x = color.getRed() / 255.0f;
        this.y = color.getGreen() / 255.0f;
        this.z = color.getBlue() / 255.0f;
    }
    
    public final Color get() {
        final int r = Math.round(this.x * 255.0f);
        final int g = Math.round(this.y * 255.0f);
        final int b = Math.round(this.z * 255.0f);
        return new Color(r, g, b);
    }
}
