package javax.vecmath;

import java.io.*;

public class Matrix3f implements Serializable, Cloneable
{
    static final long serialVersionUID = 329697160112089834L;
    public float m00;
    public float m01;
    public float m02;
    public float m10;
    public float m11;
    public float m12;
    public float m20;
    public float m21;
    public float m22;
    private static final double EPS = 1.0E-8;
    
    public Matrix3f(final float m00, final float m01, final float m02, final float m10, final float m11, final float m12, final float m20, final float m21, final float m22) {
        this.m00 = m00;
        this.m01 = m01;
        this.m02 = m02;
        this.m10 = m10;
        this.m11 = m11;
        this.m12 = m12;
        this.m20 = m20;
        this.m21 = m21;
        this.m22 = m22;
    }
    
    public Matrix3f(final float[] v) {
        this.m00 = v[0];
        this.m01 = v[1];
        this.m02 = v[2];
        this.m10 = v[3];
        this.m11 = v[4];
        this.m12 = v[5];
        this.m20 = v[6];
        this.m21 = v[7];
        this.m22 = v[8];
    }
    
    public Matrix3f(final Matrix3d m1) {
        this.m00 = (float)m1.m00;
        this.m01 = (float)m1.m01;
        this.m02 = (float)m1.m02;
        this.m10 = (float)m1.m10;
        this.m11 = (float)m1.m11;
        this.m12 = (float)m1.m12;
        this.m20 = (float)m1.m20;
        this.m21 = (float)m1.m21;
        this.m22 = (float)m1.m22;
    }
    
    public Matrix3f(final Matrix3f m1) {
        this.m00 = m1.m00;
        this.m01 = m1.m01;
        this.m02 = m1.m02;
        this.m10 = m1.m10;
        this.m11 = m1.m11;
        this.m12 = m1.m12;
        this.m20 = m1.m20;
        this.m21 = m1.m21;
        this.m22 = m1.m22;
    }
    
    public Matrix3f() {
        this.m00 = 0.0f;
        this.m01 = 0.0f;
        this.m02 = 0.0f;
        this.m10 = 0.0f;
        this.m11 = 0.0f;
        this.m12 = 0.0f;
        this.m20 = 0.0f;
        this.m21 = 0.0f;
        this.m22 = 0.0f;
    }
    
    @Override
    public String toString() {
        return this.m00 + ", " + this.m01 + ", " + this.m02 + "\n" + this.m10 + ", " + this.m11 + ", " + this.m12 + "\n" + this.m20 + ", " + this.m21 + ", " + this.m22 + "\n";
    }
    
    public final void setIdentity() {
        this.m00 = 1.0f;
        this.m01 = 0.0f;
        this.m02 = 0.0f;
        this.m10 = 0.0f;
        this.m11 = 1.0f;
        this.m12 = 0.0f;
        this.m20 = 0.0f;
        this.m21 = 0.0f;
        this.m22 = 1.0f;
    }
    
    public final void setScale(final float scale) {
        final double[] tmp_rot = new double[9];
        final double[] tmp_scale = new double[3];
        this.getScaleRotate(tmp_scale, tmp_rot);
        this.m00 = (float)(tmp_rot[0] * scale);
        this.m01 = (float)(tmp_rot[1] * scale);
        this.m02 = (float)(tmp_rot[2] * scale);
        this.m10 = (float)(tmp_rot[3] * scale);
        this.m11 = (float)(tmp_rot[4] * scale);
        this.m12 = (float)(tmp_rot[5] * scale);
        this.m20 = (float)(tmp_rot[6] * scale);
        this.m21 = (float)(tmp_rot[7] * scale);
        this.m22 = (float)(tmp_rot[8] * scale);
    }
    
    public final void setElement(final int row, final int column, final float value) {
        Label_0234: {
            switch (row) {
                case 0: {
                    switch (column) {
                        case 0: {
                            this.m00 = value;
                            break Label_0234;
                        }
                        case 1: {
                            this.m01 = value;
                            break Label_0234;
                        }
                        case 2: {
                            this.m02 = value;
                            break Label_0234;
                        }
                        default: {
                            throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix3f0"));
                        }
                    }
                    break;
                }
                case 1: {
                    switch (column) {
                        case 0: {
                            this.m10 = value;
                            break Label_0234;
                        }
                        case 1: {
                            this.m11 = value;
                            break Label_0234;
                        }
                        case 2: {
                            this.m12 = value;
                            break Label_0234;
                        }
                        default: {
                            throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix3f0"));
                        }
                    }
                    break;
                }
                case 2: {
                    switch (column) {
                        case 0: {
                            this.m20 = value;
                            break Label_0234;
                        }
                        case 1: {
                            this.m21 = value;
                            break Label_0234;
                        }
                        case 2: {
                            this.m22 = value;
                            break Label_0234;
                        }
                        default: {
                            throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix3f0"));
                        }
                    }
                    break;
                }
                default: {
                    throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix3f0"));
                }
            }
        }
    }
    
    public final void getRow(final int row, final Vector3f v) {
        if (row == 0) {
            v.x = this.m00;
            v.y = this.m01;
            v.z = this.m02;
        }
        else if (row == 1) {
            v.x = this.m10;
            v.y = this.m11;
            v.z = this.m12;
        }
        else {
            if (row != 2) {
                throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix3f1"));
            }
            v.x = this.m20;
            v.y = this.m21;
            v.z = this.m22;
        }
    }
    
    public final void getRow(final int row, final float[] v) {
        if (row == 0) {
            v[0] = this.m00;
            v[1] = this.m01;
            v[2] = this.m02;
        }
        else if (row == 1) {
            v[0] = this.m10;
            v[1] = this.m11;
            v[2] = this.m12;
        }
        else {
            if (row != 2) {
                throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix3f1"));
            }
            v[0] = this.m20;
            v[1] = this.m21;
            v[2] = this.m22;
        }
    }
    
    public final void getColumn(final int column, final Vector3f v) {
        if (column == 0) {
            v.x = this.m00;
            v.y = this.m10;
            v.z = this.m20;
        }
        else if (column == 1) {
            v.x = this.m01;
            v.y = this.m11;
            v.z = this.m21;
        }
        else {
            if (column != 2) {
                throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix3f3"));
            }
            v.x = this.m02;
            v.y = this.m12;
            v.z = this.m22;
        }
    }
    
    public final void getColumn(final int column, final float[] v) {
        if (column == 0) {
            v[0] = this.m00;
            v[1] = this.m10;
            v[2] = this.m20;
        }
        else if (column == 1) {
            v[0] = this.m01;
            v[1] = this.m11;
            v[2] = this.m21;
        }
        else {
            if (column != 2) {
                throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix3f3"));
            }
            v[0] = this.m02;
            v[1] = this.m12;
            v[2] = this.m22;
        }
    }
    
    public final float getElement(final int row, final int column) {
        Label_0162: {
            switch (row) {
                case 0: {
                    switch (column) {
                        case 0: {
                            return this.m00;
                        }
                        case 1: {
                            return this.m01;
                        }
                        case 2: {
                            return this.m02;
                        }
                        default: {
                            break Label_0162;
                        }
                    }
                    break;
                }
                case 1: {
                    switch (column) {
                        case 0: {
                            return this.m10;
                        }
                        case 1: {
                            return this.m11;
                        }
                        case 2: {
                            return this.m12;
                        }
                        default: {
                            break Label_0162;
                        }
                    }
                    break;
                }
                case 2: {
                    switch (column) {
                        case 0: {
                            return this.m20;
                        }
                        case 1: {
                            return this.m21;
                        }
                        case 2: {
                            return this.m22;
                        }
                        default: {
                            break Label_0162;
                        }
                    }
                    break;
                }
            }
        }
        throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix3f5"));
    }
    
    public final void setRow(final int row, final float x, final float y, final float z) {
        switch (row) {
            case 0: {
                this.m00 = x;
                this.m01 = y;
                this.m02 = z;
                break;
            }
            case 1: {
                this.m10 = x;
                this.m11 = y;
                this.m12 = z;
                break;
            }
            case 2: {
                this.m20 = x;
                this.m21 = y;
                this.m22 = z;
                break;
            }
            default: {
                throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix3f6"));
            }
        }
    }
    
    public final void setRow(final int row, final Vector3f v) {
        switch (row) {
            case 0: {
                this.m00 = v.x;
                this.m01 = v.y;
                this.m02 = v.z;
                break;
            }
            case 1: {
                this.m10 = v.x;
                this.m11 = v.y;
                this.m12 = v.z;
                break;
            }
            case 2: {
                this.m20 = v.x;
                this.m21 = v.y;
                this.m22 = v.z;
                break;
            }
            default: {
                throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix3f6"));
            }
        }
    }
    
    public final void setRow(final int row, final float[] v) {
        switch (row) {
            case 0: {
                this.m00 = v[0];
                this.m01 = v[1];
                this.m02 = v[2];
                break;
            }
            case 1: {
                this.m10 = v[0];
                this.m11 = v[1];
                this.m12 = v[2];
                break;
            }
            case 2: {
                this.m20 = v[0];
                this.m21 = v[1];
                this.m22 = v[2];
                break;
            }
            default: {
                throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix3f6"));
            }
        }
    }
    
    public final void setColumn(final int column, final float x, final float y, final float z) {
        switch (column) {
            case 0: {
                this.m00 = x;
                this.m10 = y;
                this.m20 = z;
                break;
            }
            case 1: {
                this.m01 = x;
                this.m11 = y;
                this.m21 = z;
                break;
            }
            case 2: {
                this.m02 = x;
                this.m12 = y;
                this.m22 = z;
                break;
            }
            default: {
                throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix3f9"));
            }
        }
    }
    
    public final void setColumn(final int column, final Vector3f v) {
        switch (column) {
            case 0: {
                this.m00 = v.x;
                this.m10 = v.y;
                this.m20 = v.z;
                break;
            }
            case 1: {
                this.m01 = v.x;
                this.m11 = v.y;
                this.m21 = v.z;
                break;
            }
            case 2: {
                this.m02 = v.x;
                this.m12 = v.y;
                this.m22 = v.z;
                break;
            }
            default: {
                throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix3f9"));
            }
        }
    }
    
    public final void setColumn(final int column, final float[] v) {
        switch (column) {
            case 0: {
                this.m00 = v[0];
                this.m10 = v[1];
                this.m20 = v[2];
                break;
            }
            case 1: {
                this.m01 = v[0];
                this.m11 = v[1];
                this.m21 = v[2];
                break;
            }
            case 2: {
                this.m02 = v[0];
                this.m12 = v[1];
                this.m22 = v[2];
                break;
            }
            default: {
                throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix3f9"));
            }
        }
    }
    
    public final float getScale() {
        final double[] tmp_rot = new double[9];
        final double[] tmp_scale = new double[3];
        this.getScaleRotate(tmp_scale, tmp_rot);
        return (float)Matrix3d.max3(tmp_scale);
    }
    
    public final void add(final float scalar) {
        this.m00 += scalar;
        this.m01 += scalar;
        this.m02 += scalar;
        this.m10 += scalar;
        this.m11 += scalar;
        this.m12 += scalar;
        this.m20 += scalar;
        this.m21 += scalar;
        this.m22 += scalar;
    }
    
    public final void add(final float scalar, final Matrix3f m1) {
        this.m00 = m1.m00 + scalar;
        this.m01 = m1.m01 + scalar;
        this.m02 = m1.m02 + scalar;
        this.m10 = m1.m10 + scalar;
        this.m11 = m1.m11 + scalar;
        this.m12 = m1.m12 + scalar;
        this.m20 = m1.m20 + scalar;
        this.m21 = m1.m21 + scalar;
        this.m22 = m1.m22 + scalar;
    }
    
    public final void add(final Matrix3f m1, final Matrix3f m2) {
        this.m00 = m1.m00 + m2.m00;
        this.m01 = m1.m01 + m2.m01;
        this.m02 = m1.m02 + m2.m02;
        this.m10 = m1.m10 + m2.m10;
        this.m11 = m1.m11 + m2.m11;
        this.m12 = m1.m12 + m2.m12;
        this.m20 = m1.m20 + m2.m20;
        this.m21 = m1.m21 + m2.m21;
        this.m22 = m1.m22 + m2.m22;
    }
    
    public final void add(final Matrix3f m1) {
        this.m00 += m1.m00;
        this.m01 += m1.m01;
        this.m02 += m1.m02;
        this.m10 += m1.m10;
        this.m11 += m1.m11;
        this.m12 += m1.m12;
        this.m20 += m1.m20;
        this.m21 += m1.m21;
        this.m22 += m1.m22;
    }
    
    public final void sub(final Matrix3f m1, final Matrix3f m2) {
        this.m00 = m1.m00 - m2.m00;
        this.m01 = m1.m01 - m2.m01;
        this.m02 = m1.m02 - m2.m02;
        this.m10 = m1.m10 - m2.m10;
        this.m11 = m1.m11 - m2.m11;
        this.m12 = m1.m12 - m2.m12;
        this.m20 = m1.m20 - m2.m20;
        this.m21 = m1.m21 - m2.m21;
        this.m22 = m1.m22 - m2.m22;
    }
    
    public final void sub(final Matrix3f m1) {
        this.m00 -= m1.m00;
        this.m01 -= m1.m01;
        this.m02 -= m1.m02;
        this.m10 -= m1.m10;
        this.m11 -= m1.m11;
        this.m12 -= m1.m12;
        this.m20 -= m1.m20;
        this.m21 -= m1.m21;
        this.m22 -= m1.m22;
    }
    
    public final void transpose() {
        float temp = this.m10;
        this.m10 = this.m01;
        this.m01 = temp;
        temp = this.m20;
        this.m20 = this.m02;
        this.m02 = temp;
        temp = this.m21;
        this.m21 = this.m12;
        this.m12 = temp;
    }
    
    public final void transpose(final Matrix3f m1) {
        if (this != m1) {
            this.m00 = m1.m00;
            this.m01 = m1.m10;
            this.m02 = m1.m20;
            this.m10 = m1.m01;
            this.m11 = m1.m11;
            this.m12 = m1.m21;
            this.m20 = m1.m02;
            this.m21 = m1.m12;
            this.m22 = m1.m22;
        }
        else {
            this.transpose();
        }
    }
    
    public final void set(final Quat4f q1) {
        this.m00 = 1.0f - 2.0f * q1.y * q1.y - 2.0f * q1.z * q1.z;
        this.m10 = 2.0f * (q1.x * q1.y + q1.w * q1.z);
        this.m20 = 2.0f * (q1.x * q1.z - q1.w * q1.y);
        this.m01 = 2.0f * (q1.x * q1.y - q1.w * q1.z);
        this.m11 = 1.0f - 2.0f * q1.x * q1.x - 2.0f * q1.z * q1.z;
        this.m21 = 2.0f * (q1.y * q1.z + q1.w * q1.x);
        this.m02 = 2.0f * (q1.x * q1.z + q1.w * q1.y);
        this.m12 = 2.0f * (q1.y * q1.z - q1.w * q1.x);
        this.m22 = 1.0f - 2.0f * q1.x * q1.x - 2.0f * q1.y * q1.y;
    }
    
    public final void set(final AxisAngle4f a1) {
        float mag = (float)Math.sqrt(a1.x * a1.x + a1.y * a1.y + a1.z * a1.z);
        if (mag < 1.0E-8) {
            this.m00 = 1.0f;
            this.m01 = 0.0f;
            this.m02 = 0.0f;
            this.m10 = 0.0f;
            this.m11 = 1.0f;
            this.m12 = 0.0f;
            this.m20 = 0.0f;
            this.m21 = 0.0f;
            this.m22 = 1.0f;
        }
        else {
            mag = 1.0f / mag;
            final float ax = a1.x * mag;
            final float ay = a1.y * mag;
            final float az = a1.z * mag;
            final float sinTheta = (float)Math.sin(a1.angle);
            final float cosTheta = (float)Math.cos(a1.angle);
            final float t = 1.0f - cosTheta;
            final float xz = ax * az;
            final float xy = ax * ay;
            final float yz = ay * az;
            this.m00 = t * ax * ax + cosTheta;
            this.m01 = t * xy - sinTheta * az;
            this.m02 = t * xz + sinTheta * ay;
            this.m10 = t * xy + sinTheta * az;
            this.m11 = t * ay * ay + cosTheta;
            this.m12 = t * yz - sinTheta * ax;
            this.m20 = t * xz - sinTheta * ay;
            this.m21 = t * yz + sinTheta * ax;
            this.m22 = t * az * az + cosTheta;
        }
    }
    
    public final void set(final AxisAngle4d a1) {
        double mag = Math.sqrt(a1.x * a1.x + a1.y * a1.y + a1.z * a1.z);
        if (mag < 1.0E-8) {
            this.m00 = 1.0f;
            this.m01 = 0.0f;
            this.m02 = 0.0f;
            this.m10 = 0.0f;
            this.m11 = 1.0f;
            this.m12 = 0.0f;
            this.m20 = 0.0f;
            this.m21 = 0.0f;
            this.m22 = 1.0f;
        }
        else {
            mag = 1.0 / mag;
            final double ax = a1.x * mag;
            final double ay = a1.y * mag;
            final double az = a1.z * mag;
            final double sinTheta = Math.sin(a1.angle);
            final double cosTheta = Math.cos(a1.angle);
            final double t = 1.0 - cosTheta;
            final double xz = ax * az;
            final double xy = ax * ay;
            final double yz = ay * az;
            this.m00 = (float)(t * ax * ax + cosTheta);
            this.m01 = (float)(t * xy - sinTheta * az);
            this.m02 = (float)(t * xz + sinTheta * ay);
            this.m10 = (float)(t * xy + sinTheta * az);
            this.m11 = (float)(t * ay * ay + cosTheta);
            this.m12 = (float)(t * yz - sinTheta * ax);
            this.m20 = (float)(t * xz - sinTheta * ay);
            this.m21 = (float)(t * yz + sinTheta * ax);
            this.m22 = (float)(t * az * az + cosTheta);
        }
    }
    
    public final void set(final Quat4d q1) {
        this.m00 = (float)(1.0 - 2.0 * q1.y * q1.y - 2.0 * q1.z * q1.z);
        this.m10 = (float)(2.0 * (q1.x * q1.y + q1.w * q1.z));
        this.m20 = (float)(2.0 * (q1.x * q1.z - q1.w * q1.y));
        this.m01 = (float)(2.0 * (q1.x * q1.y - q1.w * q1.z));
        this.m11 = (float)(1.0 - 2.0 * q1.x * q1.x - 2.0 * q1.z * q1.z);
        this.m21 = (float)(2.0 * (q1.y * q1.z + q1.w * q1.x));
        this.m02 = (float)(2.0 * (q1.x * q1.z + q1.w * q1.y));
        this.m12 = (float)(2.0 * (q1.y * q1.z - q1.w * q1.x));
        this.m22 = (float)(1.0 - 2.0 * q1.x * q1.x - 2.0 * q1.y * q1.y);
    }
    
    public final void set(final float[] m) {
        this.m00 = m[0];
        this.m01 = m[1];
        this.m02 = m[2];
        this.m10 = m[3];
        this.m11 = m[4];
        this.m12 = m[5];
        this.m20 = m[6];
        this.m21 = m[7];
        this.m22 = m[8];
    }
    
    public final void set(final Matrix3f m1) {
        this.m00 = m1.m00;
        this.m01 = m1.m01;
        this.m02 = m1.m02;
        this.m10 = m1.m10;
        this.m11 = m1.m11;
        this.m12 = m1.m12;
        this.m20 = m1.m20;
        this.m21 = m1.m21;
        this.m22 = m1.m22;
    }
    
    public final void set(final Matrix3d m1) {
        this.m00 = (float)m1.m00;
        this.m01 = (float)m1.m01;
        this.m02 = (float)m1.m02;
        this.m10 = (float)m1.m10;
        this.m11 = (float)m1.m11;
        this.m12 = (float)m1.m12;
        this.m20 = (float)m1.m20;
        this.m21 = (float)m1.m21;
        this.m22 = (float)m1.m22;
    }
    
    public final void invert(final Matrix3f m1) {
        this.invertGeneral(m1);
    }
    
    public final void invert() {
        this.invertGeneral(this);
    }
    
    private final void invertGeneral(final Matrix3f m1) {
        final double[] temp = new double[9];
        final double[] result = new double[9];
        final int[] row_perm = new int[3];
        temp[0] = m1.m00;
        temp[1] = m1.m01;
        temp[2] = m1.m02;
        temp[3] = m1.m10;
        temp[4] = m1.m11;
        temp[5] = m1.m12;
        temp[6] = m1.m20;
        temp[7] = m1.m21;
        temp[8] = m1.m22;
        if (!luDecomposition(temp, row_perm)) {
            throw new SingularMatrixException(VecMathI18N.getString("Matrix3f12"));
        }
        for (int i = 0; i < 9; ++i) {
            result[i] = 0.0;
        }
        result[0] = 1.0;
        result[8] = (result[4] = 1.0);
        luBacksubstitution(temp, row_perm, result);
        this.m00 = (float)result[0];
        this.m01 = (float)result[1];
        this.m02 = (float)result[2];
        this.m10 = (float)result[3];
        this.m11 = (float)result[4];
        this.m12 = (float)result[5];
        this.m20 = (float)result[6];
        this.m21 = (float)result[7];
        this.m22 = (float)result[8];
    }
    
    static boolean luDecomposition(final double[] matrix0, final int[] row_perm) {
        final double[] row_scale = new double[3];
        int ptr = 0;
        int rs = 0;
        int i = 3;
        while (i-- != 0) {
            double big = 0.0;
            int j = 3;
            while (j-- != 0) {
                double temp = matrix0[ptr++];
                temp = Math.abs(temp);
                if (temp > big) {
                    big = temp;
                }
            }
            if (big == 0.0) {
                return false;
            }
            row_scale[rs++] = 1.0 / big;
        }
        final int mtx = 0;
        for (int k = 0; k < 3; ++k) {
            for (int l = 0; l < k; ++l) {
                final int target = mtx + 3 * l + k;
                double sum = matrix0[target];
                int m = l;
                int p1 = mtx + 3 * l;
                int p2 = mtx + k;
                while (m-- != 0) {
                    sum -= matrix0[p1] * matrix0[p2];
                    ++p1;
                    p2 += 3;
                }
                matrix0[target] = sum;
            }
            double big2 = 0.0;
            int imax = -1;
            for (int l = k; l < 3; ++l) {
                final int target = mtx + 3 * l + k;
                double sum = matrix0[target];
                int m = k;
                int p1 = mtx + 3 * l;
                int p2 = mtx + k;
                while (m-- != 0) {
                    sum -= matrix0[p1] * matrix0[p2];
                    ++p1;
                    p2 += 3;
                }
                matrix0[target] = sum;
                final double temp2;
                if ((temp2 = row_scale[l] * Math.abs(sum)) >= big2) {
                    big2 = temp2;
                    imax = l;
                }
            }
            if (imax < 0) {
                throw new RuntimeException(VecMathI18N.getString("Matrix3f13"));
            }
            if (k != imax) {
                int m = 3;
                int p1 = mtx + 3 * imax;
                int p2 = mtx + 3 * k;
                while (m-- != 0) {
                    final double temp2 = matrix0[p1];
                    matrix0[p1++] = matrix0[p2];
                    matrix0[p2++] = temp2;
                }
                row_scale[imax] = row_scale[k];
            }
            row_perm[k] = imax;
            if (matrix0[mtx + 3 * k + k] == 0.0) {
                return false;
            }
            if (k != 2) {
                final double temp2 = 1.0 / matrix0[mtx + 3 * k + k];
                int target = mtx + 3 * (k + 1) + k;
                int l = 2 - k;
                while (l-- != 0) {
                    final int n = target;
                    matrix0[n] *= temp2;
                    target += 3;
                }
            }
        }
        return true;
    }
    
    static void luBacksubstitution(final double[] matrix1, final int[] row_perm, final double[] matrix2) {
        final int rp = 0;
        for (int k = 0; k < 3; ++k) {
            final int cv = k;
            int ii = -1;
            for (int i = 0; i < 3; ++i) {
                final int ip = row_perm[rp + i];
                double sum = matrix2[cv + 3 * ip];
                matrix2[cv + 3 * ip] = matrix2[cv + 3 * i];
                if (ii >= 0) {
                    final int rv = i * 3;
                    for (int j = ii; j <= i - 1; ++j) {
                        sum -= matrix1[rv + j] * matrix2[cv + 3 * j];
                    }
                }
                else if (sum != 0.0) {
                    ii = i;
                }
                matrix2[cv + 3 * i] = sum;
            }
            int rv = 6;
            final int n = cv + 6;
            matrix2[n] /= matrix1[rv + 2];
            rv -= 3;
            matrix2[cv + 3] = (matrix2[cv + 3] - matrix1[rv + 2] * matrix2[cv + 6]) / matrix1[rv + 1];
            rv -= 3;
            matrix2[cv + 0] = (matrix2[cv + 0] - matrix1[rv + 1] * matrix2[cv + 3] - matrix1[rv + 2] * matrix2[cv + 6]) / matrix1[rv + 0];
        }
    }
    
    public final float determinant() {
        final float total = this.m00 * (this.m11 * this.m22 - this.m12 * this.m21) + this.m01 * (this.m12 * this.m20 - this.m10 * this.m22) + this.m02 * (this.m10 * this.m21 - this.m11 * this.m20);
        return total;
    }
    
    public final void set(final float scale) {
        this.m00 = scale;
        this.m01 = 0.0f;
        this.m02 = 0.0f;
        this.m10 = 0.0f;
        this.m11 = scale;
        this.m12 = 0.0f;
        this.m20 = 0.0f;
        this.m21 = 0.0f;
        this.m22 = scale;
    }
    
    public final void rotX(final float angle) {
        final float sinAngle = (float)Math.sin(angle);
        final float cosAngle = (float)Math.cos(angle);
        this.m00 = 1.0f;
        this.m01 = 0.0f;
        this.m02 = 0.0f;
        this.m10 = 0.0f;
        this.m11 = cosAngle;
        this.m12 = -sinAngle;
        this.m20 = 0.0f;
        this.m21 = sinAngle;
        this.m22 = cosAngle;
    }
    
    public final void rotY(final float angle) {
        final float sinAngle = (float)Math.sin(angle);
        final float cosAngle = (float)Math.cos(angle);
        this.m00 = cosAngle;
        this.m01 = 0.0f;
        this.m02 = sinAngle;
        this.m10 = 0.0f;
        this.m11 = 1.0f;
        this.m12 = 0.0f;
        this.m20 = -sinAngle;
        this.m21 = 0.0f;
        this.m22 = cosAngle;
    }
    
    public final void rotZ(final float angle) {
        final float sinAngle = (float)Math.sin(angle);
        final float cosAngle = (float)Math.cos(angle);
        this.m00 = cosAngle;
        this.m01 = -sinAngle;
        this.m02 = 0.0f;
        this.m10 = sinAngle;
        this.m11 = cosAngle;
        this.m12 = 0.0f;
        this.m20 = 0.0f;
        this.m21 = 0.0f;
        this.m22 = 1.0f;
    }
    
    public final void mul(final float scalar) {
        this.m00 *= scalar;
        this.m01 *= scalar;
        this.m02 *= scalar;
        this.m10 *= scalar;
        this.m11 *= scalar;
        this.m12 *= scalar;
        this.m20 *= scalar;
        this.m21 *= scalar;
        this.m22 *= scalar;
    }
    
    public final void mul(final float scalar, final Matrix3f m1) {
        this.m00 = scalar * m1.m00;
        this.m01 = scalar * m1.m01;
        this.m02 = scalar * m1.m02;
        this.m10 = scalar * m1.m10;
        this.m11 = scalar * m1.m11;
        this.m12 = scalar * m1.m12;
        this.m20 = scalar * m1.m20;
        this.m21 = scalar * m1.m21;
        this.m22 = scalar * m1.m22;
    }
    
    public final void mul(final Matrix3f m1) {
        final float m2 = this.m00 * m1.m00 + this.m01 * m1.m10 + this.m02 * m1.m20;
        final float m3 = this.m00 * m1.m01 + this.m01 * m1.m11 + this.m02 * m1.m21;
        final float m4 = this.m00 * m1.m02 + this.m01 * m1.m12 + this.m02 * m1.m22;
        final float m5 = this.m10 * m1.m00 + this.m11 * m1.m10 + this.m12 * m1.m20;
        final float m6 = this.m10 * m1.m01 + this.m11 * m1.m11 + this.m12 * m1.m21;
        final float m7 = this.m10 * m1.m02 + this.m11 * m1.m12 + this.m12 * m1.m22;
        final float m8 = this.m20 * m1.m00 + this.m21 * m1.m10 + this.m22 * m1.m20;
        final float m9 = this.m20 * m1.m01 + this.m21 * m1.m11 + this.m22 * m1.m21;
        final float m10 = this.m20 * m1.m02 + this.m21 * m1.m12 + this.m22 * m1.m22;
        this.m00 = m2;
        this.m01 = m3;
        this.m02 = m4;
        this.m10 = m5;
        this.m11 = m6;
        this.m12 = m7;
        this.m20 = m8;
        this.m21 = m9;
        this.m22 = m10;
    }
    
    public final void mul(final Matrix3f m1, final Matrix3f m2) {
        if (this != m1 && this != m2) {
            this.m00 = m1.m00 * m2.m00 + m1.m01 * m2.m10 + m1.m02 * m2.m20;
            this.m01 = m1.m00 * m2.m01 + m1.m01 * m2.m11 + m1.m02 * m2.m21;
            this.m02 = m1.m00 * m2.m02 + m1.m01 * m2.m12 + m1.m02 * m2.m22;
            this.m10 = m1.m10 * m2.m00 + m1.m11 * m2.m10 + m1.m12 * m2.m20;
            this.m11 = m1.m10 * m2.m01 + m1.m11 * m2.m11 + m1.m12 * m2.m21;
            this.m12 = m1.m10 * m2.m02 + m1.m11 * m2.m12 + m1.m12 * m2.m22;
            this.m20 = m1.m20 * m2.m00 + m1.m21 * m2.m10 + m1.m22 * m2.m20;
            this.m21 = m1.m20 * m2.m01 + m1.m21 * m2.m11 + m1.m22 * m2.m21;
            this.m22 = m1.m20 * m2.m02 + m1.m21 * m2.m12 + m1.m22 * m2.m22;
        }
        else {
            final float m3 = m1.m00 * m2.m00 + m1.m01 * m2.m10 + m1.m02 * m2.m20;
            final float m4 = m1.m00 * m2.m01 + m1.m01 * m2.m11 + m1.m02 * m2.m21;
            final float m5 = m1.m00 * m2.m02 + m1.m01 * m2.m12 + m1.m02 * m2.m22;
            final float m6 = m1.m10 * m2.m00 + m1.m11 * m2.m10 + m1.m12 * m2.m20;
            final float m7 = m1.m10 * m2.m01 + m1.m11 * m2.m11 + m1.m12 * m2.m21;
            final float m8 = m1.m10 * m2.m02 + m1.m11 * m2.m12 + m1.m12 * m2.m22;
            final float m9 = m1.m20 * m2.m00 + m1.m21 * m2.m10 + m1.m22 * m2.m20;
            final float m10 = m1.m20 * m2.m01 + m1.m21 * m2.m11 + m1.m22 * m2.m21;
            final float m11 = m1.m20 * m2.m02 + m1.m21 * m2.m12 + m1.m22 * m2.m22;
            this.m00 = m3;
            this.m01 = m4;
            this.m02 = m5;
            this.m10 = m6;
            this.m11 = m7;
            this.m12 = m8;
            this.m20 = m9;
            this.m21 = m10;
            this.m22 = m11;
        }
    }
    
    public final void mulNormalize(final Matrix3f m1) {
        final double[] tmp = new double[9];
        final double[] tmp_rot = new double[9];
        final double[] tmp_scale = new double[3];
        tmp[0] = this.m00 * m1.m00 + this.m01 * m1.m10 + this.m02 * m1.m20;
        tmp[1] = this.m00 * m1.m01 + this.m01 * m1.m11 + this.m02 * m1.m21;
        tmp[2] = this.m00 * m1.m02 + this.m01 * m1.m12 + this.m02 * m1.m22;
        tmp[3] = this.m10 * m1.m00 + this.m11 * m1.m10 + this.m12 * m1.m20;
        tmp[4] = this.m10 * m1.m01 + this.m11 * m1.m11 + this.m12 * m1.m21;
        tmp[5] = this.m10 * m1.m02 + this.m11 * m1.m12 + this.m12 * m1.m22;
        tmp[6] = this.m20 * m1.m00 + this.m21 * m1.m10 + this.m22 * m1.m20;
        tmp[7] = this.m20 * m1.m01 + this.m21 * m1.m11 + this.m22 * m1.m21;
        tmp[8] = this.m20 * m1.m02 + this.m21 * m1.m12 + this.m22 * m1.m22;
        Matrix3d.compute_svd(tmp, tmp_scale, tmp_rot);
        this.m00 = (float)tmp_rot[0];
        this.m01 = (float)tmp_rot[1];
        this.m02 = (float)tmp_rot[2];
        this.m10 = (float)tmp_rot[3];
        this.m11 = (float)tmp_rot[4];
        this.m12 = (float)tmp_rot[5];
        this.m20 = (float)tmp_rot[6];
        this.m21 = (float)tmp_rot[7];
        this.m22 = (float)tmp_rot[8];
    }
    
    public final void mulNormalize(final Matrix3f m1, final Matrix3f m2) {
        final double[] tmp = new double[9];
        final double[] tmp_rot = new double[9];
        final double[] tmp_scale = new double[3];
        tmp[0] = m1.m00 * m2.m00 + m1.m01 * m2.m10 + m1.m02 * m2.m20;
        tmp[1] = m1.m00 * m2.m01 + m1.m01 * m2.m11 + m1.m02 * m2.m21;
        tmp[2] = m1.m00 * m2.m02 + m1.m01 * m2.m12 + m1.m02 * m2.m22;
        tmp[3] = m1.m10 * m2.m00 + m1.m11 * m2.m10 + m1.m12 * m2.m20;
        tmp[4] = m1.m10 * m2.m01 + m1.m11 * m2.m11 + m1.m12 * m2.m21;
        tmp[5] = m1.m10 * m2.m02 + m1.m11 * m2.m12 + m1.m12 * m2.m22;
        tmp[6] = m1.m20 * m2.m00 + m1.m21 * m2.m10 + m1.m22 * m2.m20;
        tmp[7] = m1.m20 * m2.m01 + m1.m21 * m2.m11 + m1.m22 * m2.m21;
        tmp[8] = m1.m20 * m2.m02 + m1.m21 * m2.m12 + m1.m22 * m2.m22;
        Matrix3d.compute_svd(tmp, tmp_scale, tmp_rot);
        this.m00 = (float)tmp_rot[0];
        this.m01 = (float)tmp_rot[1];
        this.m02 = (float)tmp_rot[2];
        this.m10 = (float)tmp_rot[3];
        this.m11 = (float)tmp_rot[4];
        this.m12 = (float)tmp_rot[5];
        this.m20 = (float)tmp_rot[6];
        this.m21 = (float)tmp_rot[7];
        this.m22 = (float)tmp_rot[8];
    }
    
    public final void mulTransposeBoth(final Matrix3f m1, final Matrix3f m2) {
        if (this != m1 && this != m2) {
            this.m00 = m1.m00 * m2.m00 + m1.m10 * m2.m01 + m1.m20 * m2.m02;
            this.m01 = m1.m00 * m2.m10 + m1.m10 * m2.m11 + m1.m20 * m2.m12;
            this.m02 = m1.m00 * m2.m20 + m1.m10 * m2.m21 + m1.m20 * m2.m22;
            this.m10 = m1.m01 * m2.m00 + m1.m11 * m2.m01 + m1.m21 * m2.m02;
            this.m11 = m1.m01 * m2.m10 + m1.m11 * m2.m11 + m1.m21 * m2.m12;
            this.m12 = m1.m01 * m2.m20 + m1.m11 * m2.m21 + m1.m21 * m2.m22;
            this.m20 = m1.m02 * m2.m00 + m1.m12 * m2.m01 + m1.m22 * m2.m02;
            this.m21 = m1.m02 * m2.m10 + m1.m12 * m2.m11 + m1.m22 * m2.m12;
            this.m22 = m1.m02 * m2.m20 + m1.m12 * m2.m21 + m1.m22 * m2.m22;
        }
        else {
            final float m3 = m1.m00 * m2.m00 + m1.m10 * m2.m01 + m1.m20 * m2.m02;
            final float m4 = m1.m00 * m2.m10 + m1.m10 * m2.m11 + m1.m20 * m2.m12;
            final float m5 = m1.m00 * m2.m20 + m1.m10 * m2.m21 + m1.m20 * m2.m22;
            final float m6 = m1.m01 * m2.m00 + m1.m11 * m2.m01 + m1.m21 * m2.m02;
            final float m7 = m1.m01 * m2.m10 + m1.m11 * m2.m11 + m1.m21 * m2.m12;
            final float m8 = m1.m01 * m2.m20 + m1.m11 * m2.m21 + m1.m21 * m2.m22;
            final float m9 = m1.m02 * m2.m00 + m1.m12 * m2.m01 + m1.m22 * m2.m02;
            final float m10 = m1.m02 * m2.m10 + m1.m12 * m2.m11 + m1.m22 * m2.m12;
            final float m11 = m1.m02 * m2.m20 + m1.m12 * m2.m21 + m1.m22 * m2.m22;
            this.m00 = m3;
            this.m01 = m4;
            this.m02 = m5;
            this.m10 = m6;
            this.m11 = m7;
            this.m12 = m8;
            this.m20 = m9;
            this.m21 = m10;
            this.m22 = m11;
        }
    }
    
    public final void mulTransposeRight(final Matrix3f m1, final Matrix3f m2) {
        if (this != m1 && this != m2) {
            this.m00 = m1.m00 * m2.m00 + m1.m01 * m2.m01 + m1.m02 * m2.m02;
            this.m01 = m1.m00 * m2.m10 + m1.m01 * m2.m11 + m1.m02 * m2.m12;
            this.m02 = m1.m00 * m2.m20 + m1.m01 * m2.m21 + m1.m02 * m2.m22;
            this.m10 = m1.m10 * m2.m00 + m1.m11 * m2.m01 + m1.m12 * m2.m02;
            this.m11 = m1.m10 * m2.m10 + m1.m11 * m2.m11 + m1.m12 * m2.m12;
            this.m12 = m1.m10 * m2.m20 + m1.m11 * m2.m21 + m1.m12 * m2.m22;
            this.m20 = m1.m20 * m2.m00 + m1.m21 * m2.m01 + m1.m22 * m2.m02;
            this.m21 = m1.m20 * m2.m10 + m1.m21 * m2.m11 + m1.m22 * m2.m12;
            this.m22 = m1.m20 * m2.m20 + m1.m21 * m2.m21 + m1.m22 * m2.m22;
        }
        else {
            final float m3 = m1.m00 * m2.m00 + m1.m01 * m2.m01 + m1.m02 * m2.m02;
            final float m4 = m1.m00 * m2.m10 + m1.m01 * m2.m11 + m1.m02 * m2.m12;
            final float m5 = m1.m00 * m2.m20 + m1.m01 * m2.m21 + m1.m02 * m2.m22;
            final float m6 = m1.m10 * m2.m00 + m1.m11 * m2.m01 + m1.m12 * m2.m02;
            final float m7 = m1.m10 * m2.m10 + m1.m11 * m2.m11 + m1.m12 * m2.m12;
            final float m8 = m1.m10 * m2.m20 + m1.m11 * m2.m21 + m1.m12 * m2.m22;
            final float m9 = m1.m20 * m2.m00 + m1.m21 * m2.m01 + m1.m22 * m2.m02;
            final float m10 = m1.m20 * m2.m10 + m1.m21 * m2.m11 + m1.m22 * m2.m12;
            final float m11 = m1.m20 * m2.m20 + m1.m21 * m2.m21 + m1.m22 * m2.m22;
            this.m00 = m3;
            this.m01 = m4;
            this.m02 = m5;
            this.m10 = m6;
            this.m11 = m7;
            this.m12 = m8;
            this.m20 = m9;
            this.m21 = m10;
            this.m22 = m11;
        }
    }
    
    public final void mulTransposeLeft(final Matrix3f m1, final Matrix3f m2) {
        if (this != m1 && this != m2) {
            this.m00 = m1.m00 * m2.m00 + m1.m10 * m2.m10 + m1.m20 * m2.m20;
            this.m01 = m1.m00 * m2.m01 + m1.m10 * m2.m11 + m1.m20 * m2.m21;
            this.m02 = m1.m00 * m2.m02 + m1.m10 * m2.m12 + m1.m20 * m2.m22;
            this.m10 = m1.m01 * m2.m00 + m1.m11 * m2.m10 + m1.m21 * m2.m20;
            this.m11 = m1.m01 * m2.m01 + m1.m11 * m2.m11 + m1.m21 * m2.m21;
            this.m12 = m1.m01 * m2.m02 + m1.m11 * m2.m12 + m1.m21 * m2.m22;
            this.m20 = m1.m02 * m2.m00 + m1.m12 * m2.m10 + m1.m22 * m2.m20;
            this.m21 = m1.m02 * m2.m01 + m1.m12 * m2.m11 + m1.m22 * m2.m21;
            this.m22 = m1.m02 * m2.m02 + m1.m12 * m2.m12 + m1.m22 * m2.m22;
        }
        else {
            final float m3 = m1.m00 * m2.m00 + m1.m10 * m2.m10 + m1.m20 * m2.m20;
            final float m4 = m1.m00 * m2.m01 + m1.m10 * m2.m11 + m1.m20 * m2.m21;
            final float m5 = m1.m00 * m2.m02 + m1.m10 * m2.m12 + m1.m20 * m2.m22;
            final float m6 = m1.m01 * m2.m00 + m1.m11 * m2.m10 + m1.m21 * m2.m20;
            final float m7 = m1.m01 * m2.m01 + m1.m11 * m2.m11 + m1.m21 * m2.m21;
            final float m8 = m1.m01 * m2.m02 + m1.m11 * m2.m12 + m1.m21 * m2.m22;
            final float m9 = m1.m02 * m2.m00 + m1.m12 * m2.m10 + m1.m22 * m2.m20;
            final float m10 = m1.m02 * m2.m01 + m1.m12 * m2.m11 + m1.m22 * m2.m21;
            final float m11 = m1.m02 * m2.m02 + m1.m12 * m2.m12 + m1.m22 * m2.m22;
            this.m00 = m3;
            this.m01 = m4;
            this.m02 = m5;
            this.m10 = m6;
            this.m11 = m7;
            this.m12 = m8;
            this.m20 = m9;
            this.m21 = m10;
            this.m22 = m11;
        }
    }
    
    public final void normalize() {
        final double[] tmp_rot = new double[9];
        final double[] tmp_scale = new double[3];
        this.getScaleRotate(tmp_scale, tmp_rot);
        this.m00 = (float)tmp_rot[0];
        this.m01 = (float)tmp_rot[1];
        this.m02 = (float)tmp_rot[2];
        this.m10 = (float)tmp_rot[3];
        this.m11 = (float)tmp_rot[4];
        this.m12 = (float)tmp_rot[5];
        this.m20 = (float)tmp_rot[6];
        this.m21 = (float)tmp_rot[7];
        this.m22 = (float)tmp_rot[8];
    }
    
    public final void normalize(final Matrix3f m1) {
        final double[] tmp = new double[9];
        final double[] tmp_rot = new double[9];
        final double[] tmp_scale = new double[3];
        tmp[0] = m1.m00;
        tmp[1] = m1.m01;
        tmp[2] = m1.m02;
        tmp[3] = m1.m10;
        tmp[4] = m1.m11;
        tmp[5] = m1.m12;
        tmp[6] = m1.m20;
        tmp[7] = m1.m21;
        tmp[8] = m1.m22;
        Matrix3d.compute_svd(tmp, tmp_scale, tmp_rot);
        this.m00 = (float)tmp_rot[0];
        this.m01 = (float)tmp_rot[1];
        this.m02 = (float)tmp_rot[2];
        this.m10 = (float)tmp_rot[3];
        this.m11 = (float)tmp_rot[4];
        this.m12 = (float)tmp_rot[5];
        this.m20 = (float)tmp_rot[6];
        this.m21 = (float)tmp_rot[7];
        this.m22 = (float)tmp_rot[8];
    }
    
    public final void normalizeCP() {
        float mag = 1.0f / (float)Math.sqrt(this.m00 * this.m00 + this.m10 * this.m10 + this.m20 * this.m20);
        this.m00 *= mag;
        this.m10 *= mag;
        this.m20 *= mag;
        mag = 1.0f / (float)Math.sqrt(this.m01 * this.m01 + this.m11 * this.m11 + this.m21 * this.m21);
        this.m01 *= mag;
        this.m11 *= mag;
        this.m21 *= mag;
        this.m02 = this.m10 * this.m21 - this.m11 * this.m20;
        this.m12 = this.m01 * this.m20 - this.m00 * this.m21;
        this.m22 = this.m00 * this.m11 - this.m01 * this.m10;
    }
    
    public final void normalizeCP(final Matrix3f m1) {
        float mag = 1.0f / (float)Math.sqrt(m1.m00 * m1.m00 + m1.m10 * m1.m10 + m1.m20 * m1.m20);
        this.m00 = m1.m00 * mag;
        this.m10 = m1.m10 * mag;
        this.m20 = m1.m20 * mag;
        mag = 1.0f / (float)Math.sqrt(m1.m01 * m1.m01 + m1.m11 * m1.m11 + m1.m21 * m1.m21);
        this.m01 = m1.m01 * mag;
        this.m11 = m1.m11 * mag;
        this.m21 = m1.m21 * mag;
        this.m02 = this.m10 * this.m21 - this.m11 * this.m20;
        this.m12 = this.m01 * this.m20 - this.m00 * this.m21;
        this.m22 = this.m00 * this.m11 - this.m01 * this.m10;
    }
    
    public boolean equals(final Matrix3f m1) {
        try {
            return this.m00 == m1.m00 && this.m01 == m1.m01 && this.m02 == m1.m02 && this.m10 == m1.m10 && this.m11 == m1.m11 && this.m12 == m1.m12 && this.m20 == m1.m20 && this.m21 == m1.m21 && this.m22 == m1.m22;
        }
        catch (NullPointerException e2) {
            return false;
        }
    }
    
    @Override
    public boolean equals(final Object o1) {
        try {
            final Matrix3f m2 = (Matrix3f)o1;
            return this.m00 == m2.m00 && this.m01 == m2.m01 && this.m02 == m2.m02 && this.m10 == m2.m10 && this.m11 == m2.m11 && this.m12 == m2.m12 && this.m20 == m2.m20 && this.m21 == m2.m21 && this.m22 == m2.m22;
        }
        catch (ClassCastException e1) {
            return false;
        }
        catch (NullPointerException e2) {
            return false;
        }
    }
    
    public boolean epsilonEquals(final Matrix3f m1, final float epsilon) {
        boolean status = true;
        if (Math.abs(this.m00 - m1.m00) > epsilon) {
            status = false;
        }
        if (Math.abs(this.m01 - m1.m01) > epsilon) {
            status = false;
        }
        if (Math.abs(this.m02 - m1.m02) > epsilon) {
            status = false;
        }
        if (Math.abs(this.m10 - m1.m10) > epsilon) {
            status = false;
        }
        if (Math.abs(this.m11 - m1.m11) > epsilon) {
            status = false;
        }
        if (Math.abs(this.m12 - m1.m12) > epsilon) {
            status = false;
        }
        if (Math.abs(this.m20 - m1.m20) > epsilon) {
            status = false;
        }
        if (Math.abs(this.m21 - m1.m21) > epsilon) {
            status = false;
        }
        if (Math.abs(this.m22 - m1.m22) > epsilon) {
            status = false;
        }
        return status;
    }
    
    @Override
    public int hashCode() {
        long bits = 1L;
        bits = 31L * bits + VecMathUtil.floatToIntBits(this.m00);
        bits = 31L * bits + VecMathUtil.floatToIntBits(this.m01);
        bits = 31L * bits + VecMathUtil.floatToIntBits(this.m02);
        bits = 31L * bits + VecMathUtil.floatToIntBits(this.m10);
        bits = 31L * bits + VecMathUtil.floatToIntBits(this.m11);
        bits = 31L * bits + VecMathUtil.floatToIntBits(this.m12);
        bits = 31L * bits + VecMathUtil.floatToIntBits(this.m20);
        bits = 31L * bits + VecMathUtil.floatToIntBits(this.m21);
        bits = 31L * bits + VecMathUtil.floatToIntBits(this.m22);
        return (int)(bits ^ bits >> 32);
    }
    
    public final void setZero() {
        this.m00 = 0.0f;
        this.m01 = 0.0f;
        this.m02 = 0.0f;
        this.m10 = 0.0f;
        this.m11 = 0.0f;
        this.m12 = 0.0f;
        this.m20 = 0.0f;
        this.m21 = 0.0f;
        this.m22 = 0.0f;
    }
    
    public final void negate() {
        this.m00 = -this.m00;
        this.m01 = -this.m01;
        this.m02 = -this.m02;
        this.m10 = -this.m10;
        this.m11 = -this.m11;
        this.m12 = -this.m12;
        this.m20 = -this.m20;
        this.m21 = -this.m21;
        this.m22 = -this.m22;
    }
    
    public final void negate(final Matrix3f m1) {
        this.m00 = -m1.m00;
        this.m01 = -m1.m01;
        this.m02 = -m1.m02;
        this.m10 = -m1.m10;
        this.m11 = -m1.m11;
        this.m12 = -m1.m12;
        this.m20 = -m1.m20;
        this.m21 = -m1.m21;
        this.m22 = -m1.m22;
    }
    
    public final void transform(final Tuple3f t) {
        final float x = this.m00 * t.x + this.m01 * t.y + this.m02 * t.z;
        final float y = this.m10 * t.x + this.m11 * t.y + this.m12 * t.z;
        final float z = this.m20 * t.x + this.m21 * t.y + this.m22 * t.z;
        t.set(x, y, z);
    }
    
    public final void transform(final Tuple3f t, final Tuple3f result) {
        final float x = this.m00 * t.x + this.m01 * t.y + this.m02 * t.z;
        final float y = this.m10 * t.x + this.m11 * t.y + this.m12 * t.z;
        result.z = this.m20 * t.x + this.m21 * t.y + this.m22 * t.z;
        result.x = x;
        result.y = y;
    }
    
    void getScaleRotate(final double[] scales, final double[] rot) {
        final double[] tmp = { this.m00, this.m01, this.m02, this.m10, this.m11, this.m12, this.m20, this.m21, this.m22 };
        Matrix3d.compute_svd(tmp, scales, rot);
    }
    
    public Object clone() {
        Matrix3f m1 = null;
        try {
            m1 = (Matrix3f)super.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new InternalError();
        }
        return m1;
    }
    
    public final float getM00() {
        return this.m00;
    }
    
    public final void setM00(final float m00) {
        this.m00 = m00;
    }
    
    public final float getM01() {
        return this.m01;
    }
    
    public final void setM01(final float m01) {
        this.m01 = m01;
    }
    
    public final float getM02() {
        return this.m02;
    }
    
    public final void setM02(final float m02) {
        this.m02 = m02;
    }
    
    public final float getM10() {
        return this.m10;
    }
    
    public final void setM10(final float m10) {
        this.m10 = m10;
    }
    
    public final float getM11() {
        return this.m11;
    }
    
    public final void setM11(final float m11) {
        this.m11 = m11;
    }
    
    public final float getM12() {
        return this.m12;
    }
    
    public final void setM12(final float m12) {
        this.m12 = m12;
    }
    
    public final float getM20() {
        return this.m20;
    }
    
    public final void setM20(final float m20) {
        this.m20 = m20;
    }
    
    public final float getM21() {
        return this.m21;
    }
    
    public final void setM21(final float m21) {
        this.m21 = m21;
    }
    
    public final float getM22() {
        return this.m22;
    }
    
    public final void setM22(final float m22) {
        this.m22 = m22;
    }
}
