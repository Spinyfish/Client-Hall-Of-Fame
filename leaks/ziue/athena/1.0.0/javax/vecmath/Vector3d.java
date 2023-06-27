package javax.vecmath;

import java.io.*;

public class Vector3d extends Tuple3d implements Serializable
{
    static final long serialVersionUID = 3761969948420550442L;
    
    public Vector3d(final double x, final double y, final double z) {
        super(x, y, z);
    }
    
    public Vector3d(final double[] v) {
        super(v);
    }
    
    public Vector3d(final Vector3d v1) {
        super(v1);
    }
    
    public Vector3d(final Vector3f v1) {
        super(v1);
    }
    
    public Vector3d(final Tuple3f t1) {
        super(t1);
    }
    
    public Vector3d(final Tuple3d t1) {
        super(t1);
    }
    
    public Vector3d() {
    }
    
    public final void cross(final Vector3d v1, final Vector3d v2) {
        final double x = v1.y * v2.z - v1.z * v2.y;
        final double y = v2.x * v1.z - v2.z * v1.x;
        this.z = v1.x * v2.y - v1.y * v2.x;
        this.x = x;
        this.y = y;
    }
    
    public final void normalize(final Vector3d v1) {
        final double norm = 1.0 / Math.sqrt(v1.x * v1.x + v1.y * v1.y + v1.z * v1.z);
        this.x = v1.x * norm;
        this.y = v1.y * norm;
        this.z = v1.z * norm;
    }
    
    public final void normalize() {
        final double norm = 1.0 / Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
        this.x *= norm;
        this.y *= norm;
        this.z *= norm;
    }
    
    public final double dot(final Vector3d v1) {
        return this.x * v1.x + this.y * v1.y + this.z * v1.z;
    }
    
    public final double lengthSquared() {
        return this.x * this.x + this.y * this.y + this.z * this.z;
    }
    
    public final double length() {
        return Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
    }
    
    public final double angle(final Vector3d v1) {
        double vDot = this.dot(v1) / (this.length() * v1.length());
        if (vDot < -1.0) {
            vDot = -1.0;
        }
        if (vDot > 1.0) {
            vDot = 1.0;
        }
        return Math.acos(vDot);
    }
}
