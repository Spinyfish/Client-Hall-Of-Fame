package javax.vecmath;

import java.io.*;
import java.awt.*;

public class Color4b extends Tuple4b implements Serializable
{
    static final long serialVersionUID = -105080578052502155L;
    
    public Color4b(final byte b1, final byte b2, final byte b3, final byte b4) {
        super(b1, b2, b3, b4);
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
    
    public Color4b(final Color color) {
        super((byte)color.getRed(), (byte)color.getGreen(), (byte)color.getBlue(), (byte)color.getAlpha());
    }
    
    public Color4b() {
    }
    
    public final void set(final Color color) {
        this.x = (byte)color.getRed();
        this.y = (byte)color.getGreen();
        this.z = (byte)color.getBlue();
        this.w = (byte)color.getAlpha();
    }
    
    public final Color get() {
        final int r = this.x & 0xFF;
        final int g = this.y & 0xFF;
        final int b = this.z & 0xFF;
        final int a = this.w & 0xFF;
        return new Color(r, g, b, a);
    }
}
