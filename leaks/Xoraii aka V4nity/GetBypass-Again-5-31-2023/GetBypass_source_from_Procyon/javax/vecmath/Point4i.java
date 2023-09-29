// 
// Decompiled by Procyon v0.5.36
// 

package javax.vecmath;

import java.io.Serializable;

public class Point4i extends Tuple4i implements Serializable
{
    public Point4i(final int x, final int y, final int z, final int w) {
        super(x, y, z, w);
    }
    
    public Point4i(final int[] t) {
        super(t);
    }
    
    public Point4i(final Point4i t1) {
        super(t1);
    }
    
    public Point4i() {
    }
}
