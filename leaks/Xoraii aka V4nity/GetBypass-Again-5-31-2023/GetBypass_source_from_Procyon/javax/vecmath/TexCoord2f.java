// 
// Decompiled by Procyon v0.5.36
// 

package javax.vecmath;

import java.io.Serializable;

public class TexCoord2f extends Tuple2f implements Serializable
{
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
