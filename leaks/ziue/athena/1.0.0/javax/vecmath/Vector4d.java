package javax.vecmath;

import java.io.*;

public class Vector4d extends Tuple4d implements Serializable
{
    static final long serialVersionUID = 3938123424117448700L;
    
    public Vector4d(final double x, final double y, final double z, final double w) {
        super(x, y, z, w);
    }
    
    public Vector4d(final double[] v) {
        super(v);
    }
    
    public Vector4d(final Vector4d v1) {
        super(v1);
    }
    
    public Vector4d(final Vector4f v1) {
        super(v1);
    }
    
    public Vector4d(final Tuple4f t1) {
        super(t1);
    }
    
    public Vector4d(final Tuple4d t1) {
        super(t1);
    }
    
    public Vector4d(final Tuple3d t1) {
        super(t1.x, t1.y, t1.z, 0.0);
    }
    
    public Vector4d() {
    }
    
    public final void set(final Tuple3d t1) {
        this.x = t1.x;
        this.y = t1.y;
        this.z = t1.z;
        this.w = 0.0;
    }
    
    public final double length() {
        return Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z + this.w * this.w);
    }
    
    public final double lengthSquared() {
        return this.x * this.x + this.y * this.y + this.z * this.z + this.w * this.w;
    }
    
    public final double dot(final Vector4d v1) {
        return this.x * v1.x + this.y * v1.y + this.z * v1.z + this.w * v1.w;
    }
    
    public final void normalize(final Vector4d v1) {
        final double norm = 1.0 / Math.sqrt(v1.x * v1.x + v1.y * v1.y + v1.z * v1.z + v1.w * v1.w);
        this.x = v1.x * norm;
        this.y = v1.y * norm;
        this.z = v1.z * norm;
        this.w = v1.w * norm;
    }
    
    public final void normalize() {
        final double norm = 1.0 / Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z + this.w * this.w);
        this.x *= norm;
        this.y *= norm;
        this.z *= norm;
        this.w *= norm;
    }
    
    public final double angle(final Vector4d v1) {
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
