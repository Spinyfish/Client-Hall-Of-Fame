package javax.vecmath;

import java.io.*;

public class TexCoord2f extends Tuple2f implements Serializable
{
    static final long serialVersionUID = 7998248474800032487L;
    
    public TexCoord2f(final float x, final float y) {
        super(x, y);
    }
    
    public TexCoord2f(final float[] v) {
        super(v);
    }
    
    public TexCoord2f(final TexCoord2f v1) {
        super(v1);
    }
    
    public TexCoord2f(final Tuple2f t1) {
        super(t1);
    }
    
    public TexCoord2f() {
    }
}
