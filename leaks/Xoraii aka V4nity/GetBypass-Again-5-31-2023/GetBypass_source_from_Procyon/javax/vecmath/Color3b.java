// 
// Decompiled by Procyon v0.5.36
// 

package javax.vecmath;

import java.awt.Color;
import java.io.Serializable;

public class Color3b extends Tuple3b implements Serializable
{
    public Color3b(final byte c1, final byte c2, final byte c3) {
        super(c1, c2, c3);
    }
    
    public Color3b(final byte[] c) {
        super(c);
    }
    
    public Color3b(final Color3b c1) {
        super(c1);
    }
    
    public Color3b(final Tuple3b t1) {
        super(t1);
    }
    
    public Color3b() {
    }
    
    public Color3b(final Color color) {
        super.x = (byte)color.getRed();
        super.y = (byte)color.getGreen();
        super.z = (byte)color.getBlue();
    }
    
    public final void set(final Color color) {
        super.x = (byte)color.getRed();
        super.y = (byte)color.getGreen();
        super.z = (byte)color.getBlue();
    }
    
    public final Color get() {
        return new Color(super.x, super.y, super.z);
    }
}
