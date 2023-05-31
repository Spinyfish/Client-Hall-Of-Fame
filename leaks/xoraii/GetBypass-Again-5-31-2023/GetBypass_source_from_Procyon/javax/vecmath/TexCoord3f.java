// 
// Decompiled by Procyon v0.5.36
// 

package javax.vecmath;

import java.io.Serializable;

public class TexCoord3f extends Tuple3f implements Serializable
{
    public TexCoord3f(final float x, final float y, final float z) {
        super(x, y, z);
    }
    
    public TexCoord3f(final float[] v) {
        super(v);
    }
    
    public TexCoord3f(final TexCoord3f v1) {
        super(v1);
    }
    
    public TexCoord3f() {
    }
}
