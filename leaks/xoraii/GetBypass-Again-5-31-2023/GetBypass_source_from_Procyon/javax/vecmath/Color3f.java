// 
// Decompiled by Procyon v0.5.36
// 

package javax.vecmath;

import java.awt.Color;
import java.io.Serializable;

public class Color3f extends Tuple3f implements Serializable
{
    public Color3f(final float x, final float y, final float z) {
        super(x, y, z);
    }
    
    public Color3f(final float[] c) {
        super(c);
    }
    
    public Color3f(final Color3f c1) {
        super(c1);
    }
    
    public Color3f(final Tuple3d t1) {
        super(t1);
    }
    
    public Color3f(final Tuple3f t1) {
        super(t1);
    }
    
    public Color3f() {
    }
    
    public Color3f(final Color color) {
        super.x = color.getRed() / 255.0f;
        super.y = color.getGreen() / 255.0f;
        super.z = color.getBlue() / 255.0f;
    }
    
    public final void set(final Color color) {
        super.x = color.getRed() / 255.0f;
        super.y = color.getGreen() / 255.0f;
        super.z = color.getBlue() / 255.0f;
    }
    
    public final Color get() {
        return new Color(super.x, super.y, super.z);
    }
}
