// 
// Decompiled by Procyon v0.5.36
// 

package javax.vecmath;

import java.io.Serializable;

public class Point3i extends Tuple3i implements Serializable
{
    public Point3i(final int x, final int y, final int z) {
        super(x, y, z);
    }
    
    public Point3i(final int[] t) {
        super(t);
    }
    
    public Point3i(final Point3i t1) {
        super(t1);
    }
    
    public Point3i() {
    }
}
