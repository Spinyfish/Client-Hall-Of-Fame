package javax.vecmath;

import java.io.*;
import java.awt.*;

public class Color3b extends Tuple3b implements Serializable
{
    static final long serialVersionUID = 6632576088353444794L;
    
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
    
    public Color3b(final Color color) {
        super((byte)color.getRed(), (byte)color.getGreen(), (byte)color.getBlue());
    }
    
    public Color3b() {
    }
    
    public final void set(final Color color) {
        this.x = (byte)color.getRed();
        this.y = (byte)color.getGreen();
        this.z = (byte)color.getBlue();
    }
    
    public final Color get() {
        final int r = this.x & 0xFF;
        final int g = this.y & 0xFF;
        final int b = this.z & 0xFF;
        return new Color(r, g, b);
    }
}
