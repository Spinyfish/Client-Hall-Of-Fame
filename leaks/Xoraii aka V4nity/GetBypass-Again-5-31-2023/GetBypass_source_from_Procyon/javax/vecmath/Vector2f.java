// 
// Decompiled by Procyon v0.5.36
// 

package javax.vecmath;

import java.io.Serializable;

public class Vector2f extends Tuple2f implements Serializable
{
    public Vector2f(final float x, final float y) {
        super(x, y);
    }
    
    public Vector2f(final float[] v) {
        super(v);
    }
    
    public Vector2f(final Vector2f v1) {
        super(v1);
    }
    
    public Vector2f(final Vector2d v1) {
        super(v1);
    }
    
    public Vector2f(final Tuple2f t1) {
        super(t1);
    }
    
    public Vector2f(final Tuple2d t1) {
        super(t1);
    }
    
    public Vector2f() {
    }
    
    public final float dot(final Vector2f v1) {
        return super.x * v1.x + super.y * v1.y;
    }
    
    public final float length() {
        return (float)Math.sqrt(super.x * super.x + super.y * super.y);
    }
    
    public final float lengthSquared() {
        return super.x * super.x + super.y * super.y;
    }
    
    public final void normalize() {
        final double d = this.length();
        super.x /= (float)d;
        super.y /= (float)d;
    }
    
    public final void normalize(final Vector2f v1) {
        this.set(v1);
        this.normalize();
    }
    
    public final float angle(final Vector2f v1) {
        return (float)Math.abs(Math.atan2(super.x * v1.y - super.y * v1.x, this.dot(v1)));
    }
}
