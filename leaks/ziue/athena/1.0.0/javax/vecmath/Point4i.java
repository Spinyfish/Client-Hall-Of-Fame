package javax.vecmath;

import java.io.*;

public class Point4i extends Tuple4i implements Serializable
{
    static final long serialVersionUID = 620124780244617983L;
    
    public Point4i(final int x, final int y, final int z, final int w) {
        super(x, y, z, w);
    }
    
    public Point4i(final int[] t) {
        super(t);
    }
    
    public Point4i(final Tuple4i t1) {
        super(t1);
    }
    
    public Point4i() {
    }
}
