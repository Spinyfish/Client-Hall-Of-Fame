package javax.vecmath;

import java.io.*;

public class Vector3f extends Tuple3f implements Serializable
{
    static final long serialVersionUID = -7031930069184524614L;
    
    public Vector3f(final float x, final float y, final float z) {
        super(x, y, z);
    }
    
    public Vector3f(final float[] v) {
        super(v);
    }
    
    public Vector3f(final Vector3f v1) {
        super(v1);
    }
    
    public Vector3f(final Vector3d v1) {
        super(v1);
    }
    
    public Vector3f(final Tuple3f t1) {
        super(t1);
    }
    
    public Vector3f(final Tuple3d t1) {
        super(t1);
    }
    
    public Vector3f() {
    }
    
    public final float lengthSquared() {
        return this.x * this.x + this.y * this.y + this.z * this.z;
    }
    
    public final float length() {
        return (float)Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
    }
    
    public final void cross(final Vector3f v1, final Vector3f v2) {
        final float x = v1.y * v2.z - v1.z * v2.y;
        final float y = v2.x * v1.z - v2.z * v1.x;
        this.z = v1.x * v2.y - v1.y * v2.x;
        this.x = x;
        this.y = y;
    }
    
    public final float dot(final Vector3f v1) {
        return this.x * v1.x + this.y * v1.y + this.z * v1.z;
    }
    
    public final void normalize(final Vector3f v1) {
        final float norm = (float)(1.0 / Math.sqrt(v1.x * v1.x + v1.y * v1.y + v1.z * v1.z));
        this.x = v1.x * norm;
        this.y = v1.y * norm;
        this.z = v1.z * norm;
    }
    
    public final void normalize() {
        final float norm = (float)(1.0 / Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z));
        this.x *= norm;
        this.y *= norm;
        this.z *= norm;
    }
    
    public final float angle(final Vector3f v1) {
        double vDot = this.dot(v1) / (this.length() * v1.length());
        if (vDot < -1.0) {
            vDot = -1.0;
        }
        if (vDot > 1.0) {
            vDot = 1.0;
        }
        return (float)Math.acos(vDot);
    }
}
