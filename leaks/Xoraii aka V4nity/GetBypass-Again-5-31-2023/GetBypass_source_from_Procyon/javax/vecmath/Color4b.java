// 
// Decompiled by Procyon v0.5.36
// 

package javax.vecmath;

import java.awt.Color;
import java.io.Serializable;

public class Color4b extends Tuple4b implements Serializable
{
    public Color4b(final byte c1, final byte c2, final byte c3, final byte c4) {
        super(c1, c2, c3, c4);
    }
    
    public Color4b(final byte[] c) {
        super(c);
    }
    
    public Color4b(final Color4b c1) {
        super(c1);
    }
    
    public Color4b(final Tuple4b t1) {
        super(t1);
    }
    
    public Color4b() {
    }
    
    public Color4b(final Color color) {
        super.x = (byte)color.getRed();
        super.y = (byte)color.getGreen();
        super.z = (byte)color.getBlue();
        super.w = (byte)color.getAlpha();
    }
    
    public final void set(final Color color) {
        super.x = (byte)color.getRed();
        super.y = (byte)color.getGreen();
        super.z = (byte)color.getBlue();
        super.w = (byte)color.getAlpha();
    }
    
    public final Color get() {
        return new Color(super.x, super.y, super.z, super.w);
    }
}
