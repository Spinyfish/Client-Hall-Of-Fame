package javax.vecmath;

import java.io.*;

public class Point3i extends Tuple3i implements Serializable
{
    static final long serialVersionUID = 6149289077348153921L;
    
    public Point3i(final int x, final int y, final int z) {
        super(x, y, z);
    }
    
    public Point3i(final int[] t) {
        super(t);
    }
    
    public Point3i(final Tuple3i t1) {
        super(t1);
    }
    
    public Point3i() {
    }
}
