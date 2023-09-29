// 
// Decompiled by Procyon v0.5.36
// 

package javax.vecmath;

import java.io.Serializable;

public class Matrix4f implements Serializable
{
    public float m00;
    public float m01;
    public float m02;
    public float m03;
    public float m10;
    public float m11;
    public float m12;
    public float m13;
    public float m20;
    public float m21;
    public float m22;
    public float m23;
    public float m30;
    public float m31;
    public float m32;
    public float m33;
    
    public Matrix4f(final float m00, final float m01, final float m02, final float m03, final float m10, final float m11, final float m12, final float m13, final float m20, final float m21, final float m22, final float m23, final float m30, final float m31, final float m32, final float m33) {
        this.set(m00, m01, m02, m03, m10, m11, m12, m13, m20, m21, m22, m23, m30, m31, m32, m33);
    }
    
    public Matrix4f(final float[] v) {
        this.set(v);
    }
    
    public Matrix4f(final Quat4f q1, final Vector3f t1, final float s) {
        this.set(q1, t1, s);
    }
    
    public Matrix4f(final Matrix4d m1) {
        this.set(m1);
    }
    
    public Matrix4f(final Matrix4f m1) {
        this.set(m1);
    }
    
    public Matrix4f(final Matrix3f m1, final Vector3f t1, final float s) {
        this.set(m1);
        this.mulRotationScale(s);
        this.setTranslation(t1);
        this.m33 = 1.0f;
    }
    
    public Matrix4f() {
        this.setZero();
    }
    
    public String toString() {
        final String nl = System.getProperty("line.separator");
        return "[" + nl + "  [" + this.m00 + "\t" + this.m01 + "\t" + this.m02 + "\t" + this.m03 + "]" + nl + "  [" + this.m10 + "\t" + this.m11 + "\t" + this.m12 + "\t" + this.m13 + "]" + nl + "  [" + this.m20 + "\t" + this.m21 + "\t" + this.m22 + "\t" + this.m23 + "]" + nl + "  [" + this.m30 + "\t" + this.m31 + "\t" + this.m32 + "\t" + this.m33 + "] ]";
    }
    
    public final void setIdentity() {
        this.m00 = 1.0f;
        this.m01 = 0.0f;
        this.m02 = 0.0f;
        this.m03 = 0.0f;
        this.m10 = 0.0f;
        this.m11 = 1.0f;
        this.m12 = 0.0f;
        this.m13 = 0.0f;
        this.m20 = 0.0f;
        this.m21 = 0.0f;
        this.m22 = 1.0f;
        this.m23 = 0.0f;
        this.m30 = 0.0f;
        this.m31 = 0.0f;
        this.m32 = 0.0f;
        this.m33 = 1.0f;
    }
    
    public final void setElement(final int row, final int column, final float value) {
        if (row == 0) {
            if (column == 0) {
                this.m00 = value;
            }
            else if (column == 1) {
                this.m01 = value;
            }
            else if (column == 2) {
                this.m02 = value;
            }
            else {
                if (column != 3) {
                    throw new ArrayIndexOutOfBoundsException("column must be 0 to 3 and is " + column);
                }
                this.m03 = value;
            }
        }
        else if (row == 1) {
            if (column == 0) {
                this.m10 = value;
            }
            else if (column == 1) {
                this.m11 = value;
            }
            else if (column == 2) {
                this.m12 = value;
            }
            else {
                if (column != 3) {
                    throw new ArrayIndexOutOfBoundsException("column must be 0 to 3 and is " + column);
                }
                this.m13 = value;
            }
        }
        else if (row == 2) {
            if (column == 0) {
                this.m20 = value;
            }
            else if (column == 1) {
                this.m21 = value;
            }
            else if (column == 2) {
                this.m22 = value;
            }
            else {
                if (column != 3) {
                    throw new ArrayIndexOutOfBoundsException("column must be 0 to 3 and is " + column);
                }
                this.m23 = value;
            }
        }
        else {
            if (row != 3) {
                throw new ArrayIndexOutOfBoundsException("row must be 0 to 2 and is " + row);
            }
            if (column == 0) {
                this.m30 = value;
            }
            else if (column == 1) {
                this.m31 = value;
            }
            else if (column == 2) {
                this.m32 = value;
            }
            else {
                if (column != 3) {
                    throw new ArrayIndexOutOfBoundsException("column must be 0 to 3 and is " + column);
                }
                this.m33 = value;
            }
        }
    }
    
    public final float getElement(final int row, final int column) {
        if (row == 0) {
            if (column == 0) {
                return this.m00;
            }
            if (column == 1) {
                return this.m01;
            }
            if (column == 2) {
                return this.m02;
            }
            if (column == 3) {
                return this.m03;
            }
            throw new ArrayIndexOutOfBoundsException("column must be 0 to 3 and is " + column);
        }
        else if (row == 1) {
            if (column == 0) {
                return this.m10;
            }
            if (column == 1) {
                return this.m11;
            }
            if (column == 2) {
                return this.m12;
            }
            if (column == 3) {
                return this.m13;
            }
            throw new ArrayIndexOutOfBoundsException("column must be 0 to 3 and is " + column);
        }
        else if (row == 2) {
            if (column == 0) {
                return this.m20;
            }
            if (column == 1) {
                return this.m21;
            }
            if (column == 2) {
                return this.m22;
            }
            if (column == 3) {
                return this.m23;
            }
            throw new ArrayIndexOutOfBoundsException("column must be 0 to 3 and is " + column);
        }
        else {
            if (row != 3) {
                throw new ArrayIndexOutOfBoundsException("row must be 0 to 3 and is " + row);
            }
            if (column == 0) {
                return this.m30;
            }
            if (column == 1) {
                return this.m31;
            }
            if (column == 2) {
                return this.m32;
            }
            if (column == 3) {
                return this.m33;
            }
            throw new ArrayIndexOutOfBoundsException("column must be 0 to 3 and is " + column);
        }
    }
    
    public final void setScale(final float scale) {
        this.SVD(null, this);
        this.mulRotationScale(scale);
    }
    
    public final void get(final Matrix3d m1) {
        this.SVD(m1);
    }
    
    public final void get(final Matrix3f m1) {
        this.SVD(m1, null);
    }
    
    public final float get(final Matrix3f m1, final Vector3f t1) {
        this.get(t1);
        return this.SVD(m1, null);
    }
    
    public final void get(final Quat4f q1) {
        q1.set(this);
        q1.normalize();
    }
    
    public final void get(final Vector3f trans) {
        trans.x = this.m03;
        trans.y = this.m13;
        trans.z = this.m23;
    }
    
    public final void getRotationScale(final Matrix3f m1) {
        m1.m00 = this.m00;
        m1.m01 = this.m01;
        m1.m02 = this.m02;
        m1.m10 = this.m10;
        m1.m11 = this.m11;
        m1.m12 = this.m12;
        m1.m20 = this.m20;
        m1.m21 = this.m21;
        m1.m22 = this.m22;
    }
    
    public final float getScale() {
        return this.SVD(null);
    }
    
    public final void setRotationScale(final Matrix3f m1) {
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
    
    public final void setRow(final int row, final float x, final float y, final float z, final float w) {
        if (row == 0) {
            this.m00 = x;
            this.m01 = y;
            this.m02 = z;
            this.m03 = w;
        }
        else if (row == 1) {
            this.m10 = x;
            this.m11 = y;
            this.m12 = z;
            this.m13 = w;
        }
        else if (row == 2) {
            this.m20 = x;
            this.m21 = y;
            this.m22 = z;
            this.m23 = w;
        }
        else {
            if (row != 3) {
                throw new ArrayIndexOutOfBoundsException("row must be 0 to 3 and is " + row);
            }
            this.m30 = x;
            this.m31 = y;
            this.m32 = z;
            this.m33 = w;
        }
    }
    
    public final void setRow(final int row, final Vector4f v) {
        if (row == 0) {
            this.m00 = v.x;
            this.m01 = v.y;
            this.m02 = v.z;
            this.m03 = v.w;
        }
        else if (row == 1) {
            this.m10 = v.x;
            this.m11 = v.y;
            this.m12 = v.z;
            this.m13 = v.w;
        }
        else if (row == 2) {
            this.m20 = v.x;
            this.m21 = v.y;
            this.m22 = v.z;
            this.m23 = v.w;
        }
        else {
            if (row != 3) {
                throw new ArrayIndexOutOfBoundsException("row must be 0 to 3 and is " + row);
            }
            this.m30 = v.x;
            this.m31 = v.y;
            this.m32 = v.z;
            this.m33 = v.w;
        }
    }
    
    public final void setRow(final int row, final float[] v) {
        if (row == 0) {
            this.m00 = v[0];
            this.m01 = v[1];
            this.m02 = v[2];
            this.m03 = v[3];
        }
        else if (row == 1) {
            this.m10 = v[0];
            this.m11 = v[1];
            this.m12 = v[2];
            this.m13 = v[3];
        }
        else if (row == 2) {
            this.m20 = v[0];
            this.m21 = v[1];
            this.m22 = v[2];
            this.m23 = v[3];
        }
        else {
            if (row != 3) {
                throw new ArrayIndexOutOfBoundsException("row must be 0 to 3 and is " + row);
            }
            this.m30 = v[0];
            this.m31 = v[1];
            this.m32 = v[2];
            this.m33 = v[3];
        }
    }
    
    public final void getRow(final int row, final Vector4f v) {
        if (row == 0) {
            v.x = this.m00;
            v.y = this.m01;
            v.z = this.m02;
            v.w = this.m03;
        }
        else if (row == 1) {
            v.x = this.m10;
            v.y = this.m11;
            v.z = this.m12;
            v.w = this.m13;
        }
        else if (row == 2) {
            v.x = this.m20;
            v.y = this.m21;
            v.z = this.m22;
            v.w = this.m23;
        }
        else {
            if (row != 3) {
                throw new ArrayIndexOutOfBoundsException("row must be 0 to 3 and is " + row);
            }
            v.x = this.m30;
            v.y = this.m31;
            v.z = this.m32;
            v.w = this.m33;
        }
    }
    
    public final void getRow(final int row, final float[] v) {
        if (row == 0) {
            v[0] = this.m00;
            v[1] = this.m01;
            v[2] = this.m02;
            v[3] = this.m03;
        }
        else if (row == 1) {
            v[0] = this.m10;
            v[1] = this.m11;
            v[2] = this.m12;
            v[3] = this.m13;
        }
        else if (row == 2) {
            v[0] = this.m20;
            v[1] = this.m21;
            v[2] = this.m22;
            v[3] = this.m23;
        }
        else {
            if (row != 3) {
                throw new ArrayIndexOutOfBoundsException("row must be 0 to 3 and is " + row);
            }
            v[0] = this.m30;
            v[1] = this.m31;
            v[2] = this.m32;
            v[3] = this.m33;
        }
    }
    
    public final void setColumn(final int column, final float x, final float y, final float z, final float w) {
        if (column == 0) {
            this.m00 = x;
            this.m10 = y;
            this.m20 = z;
            this.m30 = w;
        }
        else if (column == 1) {
            this.m01 = x;
            this.m11 = y;
            this.m21 = z;
            this.m31 = w;
        }
        else if (column == 2) {
            this.m02 = x;
            this.m12 = y;
            this.m22 = z;
            this.m32 = w;
        }
        else {
            if (column != 3) {
                throw new ArrayIndexOutOfBoundsException("column must be 0 to 3 and is " + column);
            }
            this.m03 = x;
            this.m13 = y;
            this.m23 = z;
            this.m33 = w;
        }
    }
    
    public final void setColumn(final int column, final Vector4f v) {
        if (column == 0) {
            this.m00 = v.x;
            this.m10 = v.y;
            this.m20 = v.z;
            this.m30 = v.w;
        }
        else if (column == 1) {
            this.m01 = v.x;
            this.m11 = v.y;
            this.m21 = v.z;
            this.m31 = v.w;
        }
        else if (column == 2) {
            this.m02 = v.x;
            this.m12 = v.y;
            this.m22 = v.z;
            this.m32 = v.w;
        }
        else {
            if (column != 3) {
                throw new ArrayIndexOutOfBoundsException("column must be 0 to 3 and is " + column);
            }
            this.m03 = v.x;
            this.m13 = v.y;
            this.m23 = v.z;
            this.m33 = v.w;
        }
    }
    
    public final void setColumn(final int column, final float[] v) {
        if (column == 0) {
            this.m00 = v[0];
            this.m10 = v[1];
            this.m20 = v[2];
            this.m30 = v[3];
        }
        else if (column == 1) {
            this.m01 = v[0];
            this.m11 = v[1];
            this.m21 = v[2];
            this.m31 = v[3];
        }
        else if (column == 2) {
            this.m02 = v[0];
            this.m12 = v[1];
            this.m22 = v[2];
            this.m32 = v[3];
        }
        else {
            if (column != 3) {
                throw new ArrayIndexOutOfBoundsException("column must be 0 to 3 and is " + column);
            }
            this.m03 = v[0];
            this.m13 = v[1];
            this.m23 = v[2];
            this.m33 = v[3];
        }
    }
    
    public final void getColumn(final int column, final Vector4f v) {
        if (column == 0) {
            v.x = this.m00;
            v.y = this.m10;
            v.z = this.m20;
            v.w = this.m30;
        }
        else if (column == 1) {
            v.x = this.m01;
            v.y = this.m11;
            v.z = this.m21;
            v.w = this.m31;
        }
        else if (column == 2) {
            v.x = this.m02;
            v.y = this.m12;
            v.z = this.m22;
            v.w = this.m32;
        }
        else {
            if (column != 3) {
                throw new ArrayIndexOutOfBoundsException("column must be 0 to 3 and is " + column);
            }
            v.x = this.m03;
            v.y = this.m13;
            v.z = this.m23;
            v.w = this.m33;
        }
    }
    
    public final void getColumn(final int column, final float[] v) {
        if (column == 0) {
            v[0] = this.m00;
            v[1] = this.m10;
            v[2] = this.m20;
            v[3] = this.m30;
        }
        else if (column == 1) {
            v[0] = this.m01;
            v[1] = this.m11;
            v[2] = this.m21;
            v[3] = this.m31;
        }
        else if (column == 2) {
            v[0] = this.m02;
            v[1] = this.m12;
            v[2] = this.m22;
            v[3] = this.m32;
        }
        else {
            if (column != 3) {
                throw new ArrayIndexOutOfBoundsException("column must be 0 to 3 and is " + column);
            }
            v[0] = this.m03;
            v[1] = this.m13;
            v[2] = this.m23;
            v[3] = this.m33;
        }
    }
    
    public final void add(final float scalar) {
        this.m00 += scalar;
        this.m01 += scalar;
        this.m02 += scalar;
        this.m03 += scalar;
        this.m10 += scalar;
        this.m11 += scalar;
        this.m12 += scalar;
        this.m13 += scalar;
        this.m20 += scalar;
        this.m21 += scalar;
        this.m22 += scalar;
        this.m23 += scalar;
        this.m30 += scalar;
        this.m31 += scalar;
        this.m32 += scalar;
        this.m33 += scalar;
    }
    
    public final void add(final float scalar, final Matrix4f m1) {
        this.set(m1);
        this.add(scalar);
    }
    
    public final void add(final Matrix4f m1, final Matrix4f m2) {
        this.set(m1);
        this.add(m2);
    }
    
    public final void add(final Matrix4f m1) {
        this.m00 += m1.m00;
        this.m01 += m1.m01;
        this.m02 += m1.m02;
        this.m03 += m1.m03;
        this.m10 += m1.m10;
        this.m11 += m1.m11;
        this.m12 += m1.m12;
        this.m13 += m1.m13;
        this.m20 += m1.m20;
        this.m21 += m1.m21;
        this.m22 += m1.m22;
        this.m23 += m1.m23;
        this.m30 += m1.m30;
        this.m31 += m1.m31;
        this.m32 += m1.m32;
        this.m33 += m1.m33;
    }
    
    public final void sub(final Matrix4f m1, final Matrix4f m2) {
        this.set(m1.m00 - m2.m00, m1.m01 - m2.m01, m1.m02 - m2.m02, m1.m03 - m2.m03, m1.m10 - m2.m10, m1.m11 - m2.m11, m1.m12 - m2.m12, m1.m13 - m2.m13, m1.m20 - m2.m20, m1.m21 - m2.m21, m1.m22 - m2.m22, m1.m23 - m2.m23, m1.m30 - m2.m30, m1.m31 - m2.m31, m1.m32 - m2.m32, m1.m33 - m2.m33);
    }
    
    public final void sub(final Matrix4f m1) {
        this.m00 -= m1.m00;
        this.m01 -= m1.m01;
        this.m02 -= m1.m02;
        this.m03 -= m1.m03;
        this.m10 -= m1.m10;
        this.m11 -= m1.m11;
        this.m12 -= m1.m12;
        this.m13 -= m1.m13;
        this.m20 -= m1.m20;
        this.m21 -= m1.m21;
        this.m22 -= m1.m22;
        this.m23 -= m1.m23;
        this.m30 -= m1.m30;
        this.m31 -= m1.m31;
        this.m32 -= m1.m32;
        this.m33 -= m1.m33;
    }
    
    public final void transpose() {
        float tmp = this.m01;
        this.m01 = this.m10;
        this.m10 = tmp;
        tmp = this.m02;
        this.m02 = this.m20;
        this.m20 = tmp;
        tmp = this.m03;
        this.m03 = this.m30;
        this.m30 = tmp;
        tmp = this.m12;
        this.m12 = this.m21;
        this.m21 = tmp;
        tmp = this.m13;
        this.m13 = this.m31;
        this.m31 = tmp;
        tmp = this.m23;
        this.m23 = this.m32;
        this.m32 = tmp;
    }
    
    public final void transpose(final Matrix4f m1) {
        this.set(m1);
        this.transpose();
    }
    
    public final void set(final Quat4f q1) {
        this.setFromQuat(q1.x, q1.y, q1.z, q1.w);
    }
    
    public final void set(final AxisAngle4f a1) {
        this.setFromAxisAngle(a1.x, a1.y, a1.z, a1.angle);
    }
    
    public final void set(final Quat4d q1) {
        this.setFromQuat(q1.x, q1.y, q1.z, q1.w);
    }
    
    public final void set(final AxisAngle4d a1) {
        this.setFromAxisAngle(a1.x, a1.y, a1.z, a1.angle);
    }
    
    public final void set(final Quat4d q1, final Vector3d t1, final double s) {
        this.set(q1);
        this.mulRotationScale((float)s);
        this.m03 = (float)t1.x;
        this.m13 = (float)t1.y;
        this.m23 = (float)t1.z;
    }
    
    public final void set(final Quat4f q1, final Vector3f t1, final float s) {
        this.set(q1);
        this.mulRotationScale(s);
        this.m03 = t1.x;
        this.m13 = t1.y;
        this.m23 = t1.z;
    }
    
    public final void set(final Matrix4d m1) {
        this.m00 = (float)m1.m00;
        this.m01 = (float)m1.m01;
        this.m02 = (float)m1.m02;
        this.m03 = (float)m1.m03;
        this.m10 = (float)m1.m10;
        this.m11 = (float)m1.m11;
        this.m12 = (float)m1.m12;
        this.m13 = (float)m1.m13;
        this.m20 = (float)m1.m20;
        this.m21 = (float)m1.m21;
        this.m22 = (float)m1.m22;
        this.m23 = (float)m1.m23;
        this.m30 = (float)m1.m30;
        this.m31 = (float)m1.m31;
        this.m32 = (float)m1.m32;
        this.m33 = (float)m1.m33;
    }
    
    public final void set(final Matrix4f m1) {
        this.m00 = m1.m00;
        this.m01 = m1.m01;
        this.m02 = m1.m02;
        this.m03 = m1.m03;
        this.m10 = m1.m10;
        this.m11 = m1.m11;
        this.m12 = m1.m12;
        this.m13 = m1.m13;
        this.m20 = m1.m20;
        this.m21 = m1.m21;
        this.m22 = m1.m22;
        this.m23 = m1.m23;
        this.m30 = m1.m30;
        this.m31 = m1.m31;
        this.m32 = m1.m32;
        this.m33 = m1.m33;
    }
    
    public final void invert(final Matrix4f m1) {
        this.set(m1);
        this.invert();
    }
    
    public final void invert() {
        float s = this.determinant();
        if (s == 0.0) {
            return;
        }
        s = 1.0f / s;
        this.set(this.m11 * (this.m22 * this.m33 - this.m23 * this.m32) + this.m12 * (this.m23 * this.m31 - this.m21 * this.m33) + this.m13 * (this.m21 * this.m32 - this.m22 * this.m31), this.m21 * (this.m02 * this.m33 - this.m03 * this.m32) + this.m22 * (this.m03 * this.m31 - this.m01 * this.m33) + this.m23 * (this.m01 * this.m32 - this.m02 * this.m31), this.m31 * (this.m02 * this.m13 - this.m03 * this.m12) + this.m32 * (this.m03 * this.m11 - this.m01 * this.m13) + this.m33 * (this.m01 * this.m12 - this.m02 * this.m11), this.m01 * (this.m13 * this.m22 - this.m12 * this.m23) + this.m02 * (this.m11 * this.m23 - this.m13 * this.m21) + this.m03 * (this.m12 * this.m21 - this.m11 * this.m22), this.m12 * (this.m20 * this.m33 - this.m23 * this.m30) + this.m13 * (this.m22 * this.m30 - this.m20 * this.m32) + this.m10 * (this.m23 * this.m32 - this.m22 * this.m33), this.m22 * (this.m00 * this.m33 - this.m03 * this.m30) + this.m23 * (this.m02 * this.m30 - this.m00 * this.m32) + this.m20 * (this.m03 * this.m32 - this.m02 * this.m33), this.m32 * (this.m00 * this.m13 - this.m03 * this.m10) + this.m33 * (this.m02 * this.m10 - this.m00 * this.m12) + this.m30 * (this.m03 * this.m12 - this.m02 * this.m13), this.m02 * (this.m13 * this.m20 - this.m10 * this.m23) + this.m03 * (this.m10 * this.m22 - this.m12 * this.m20) + this.m00 * (this.m12 * this.m23 - this.m13 * this.m22), this.m13 * (this.m20 * this.m31 - this.m21 * this.m30) + this.m10 * (this.m21 * this.m33 - this.m23 * this.m31) + this.m11 * (this.m23 * this.m30 - this.m20 * this.m33), this.m23 * (this.m00 * this.m31 - this.m01 * this.m30) + this.m20 * (this.m01 * this.m33 - this.m03 * this.m31) + this.m21 * (this.m03 * this.m30 - this.m00 * this.m33), this.m33 * (this.m00 * this.m11 - this.m01 * this.m10) + this.m30 * (this.m01 * this.m13 - this.m03 * this.m11) + this.m31 * (this.m03 * this.m10 - this.m00 * this.m13), this.m03 * (this.m11 * this.m20 - this.m10 * this.m21) + this.m00 * (this.m13 * this.m21 - this.m11 * this.m23) + this.m01 * (this.m10 * this.m23 - this.m13 * this.m20), this.m10 * (this.m22 * this.m31 - this.m21 * this.m32) + this.m11 * (this.m20 * this.m32 - this.m22 * this.m30) + this.m12 * (this.m21 * this.m30 - this.m20 * this.m31), this.m20 * (this.m02 * this.m31 - this.m01 * this.m32) + this.m21 * (this.m00 * this.m32 - this.m02 * this.m30) + this.m22 * (this.m01 * this.m30 - this.m00 * this.m31), this.m30 * (this.m02 * this.m11 - this.m01 * this.m12) + this.m31 * (this.m00 * this.m12 - this.m02 * this.m10) + this.m32 * (this.m01 * this.m10 - this.m00 * this.m11), this.m00 * (this.m11 * this.m22 - this.m12 * this.m21) + this.m01 * (this.m12 * this.m20 - this.m10 * this.m22) + this.m02 * (this.m10 * this.m21 - this.m11 * this.m20));
        this.mul(s);
    }
    
    public final float determinant() {
        return (this.m00 * this.m11 - this.m01 * this.m10) * (this.m22 * this.m33 - this.m23 * this.m32) - (this.m00 * this.m12 - this.m02 * this.m10) * (this.m21 * this.m33 - this.m23 * this.m31) + (this.m00 * this.m13 - this.m03 * this.m10) * (this.m21 * this.m32 - this.m22 * this.m31) + (this.m01 * this.m12 - this.m02 * this.m11) * (this.m20 * this.m33 - this.m23 * this.m30) - (this.m01 * this.m13 - this.m03 * this.m11) * (this.m20 * this.m32 - this.m22 * this.m30) + (this.m02 * this.m13 - this.m03 * this.m12) * (this.m20 * this.m31 - this.m21 * this.m30);
    }
    
    public final void set(final Matrix3f m1) {
        this.m00 = m1.m00;
        this.m01 = m1.m01;
        this.m02 = m1.m02;
        this.m03 = 0.0f;
        this.m10 = m1.m10;
        this.m11 = m1.m11;
        this.m12 = m1.m12;
        this.m13 = 0.0f;
        this.m20 = m1.m20;
        this.m21 = m1.m21;
        this.m22 = m1.m22;
        this.m23 = 0.0f;
        this.m30 = 0.0f;
        this.m31 = 0.0f;
        this.m32 = 0.0f;
        this.m33 = 1.0f;
    }
    
    public final void set(final Matrix3d m1) {
        this.m00 = (float)m1.m00;
        this.m01 = (float)m1.m01;
        this.m02 = (float)m1.m02;
        this.m03 = 0.0f;
        this.m10 = (float)m1.m10;
        this.m11 = (float)m1.m11;
        this.m12 = (float)m1.m12;
        this.m13 = 0.0f;
        this.m20 = (float)m1.m20;
        this.m21 = (float)m1.m21;
        this.m22 = (float)m1.m22;
        this.m23 = 0.0f;
        this.m30 = 0.0f;
        this.m31 = 0.0f;
        this.m32 = 0.0f;
        this.m33 = 1.0f;
    }
    
    public final void set(final float scale) {
        this.m00 = scale;
        this.m01 = 0.0f;
        this.m02 = 0.0f;
        this.m03 = 0.0f;
        this.m10 = 0.0f;
        this.m11 = scale;
        this.m12 = 0.0f;
        this.m13 = 0.0f;
        this.m20 = 0.0f;
        this.m21 = 0.0f;
        this.m22 = scale;
        this.m23 = 0.0f;
        this.m30 = 0.0f;
        this.m31 = 0.0f;
        this.m32 = 0.0f;
        this.m33 = 1.0f;
    }
    
    public final void set(final float[] m) {
        this.m00 = m[0];
        this.m01 = m[1];
        this.m02 = m[2];
        this.m03 = m[3];
        this.m10 = m[4];
        this.m11 = m[5];
        this.m12 = m[6];
        this.m13 = m[7];
        this.m20 = m[8];
        this.m21 = m[9];
        this.m22 = m[10];
        this.m23 = m[11];
        this.m30 = m[12];
        this.m31 = m[13];
        this.m32 = m[14];
        this.m33 = m[15];
    }
    
    public final void set(final Vector3f v1) {
        this.setIdentity();
        this.setTranslation(v1);
    }
    
    public final void set(final float scale, final Vector3f v1) {
        this.set(scale);
        this.setTranslation(v1);
    }
    
    public final void set(final Vector3f v1, final float scale) {
        this.m00 = scale;
        this.m01 = 0.0f;
        this.m02 = 0.0f;
        this.m03 = scale * v1.x;
        this.m10 = 0.0f;
        this.m11 = scale;
        this.m12 = 0.0f;
        this.m13 = scale * v1.y;
        this.m20 = 0.0f;
        this.m21 = 0.0f;
        this.m22 = scale;
        this.m23 = scale * v1.z;
        this.m30 = 0.0f;
        this.m31 = 0.0f;
        this.m32 = 0.0f;
        this.m33 = 1.0f;
    }
    
    public final void set(final Matrix3f m1, final Vector3f t1, final float scale) {
        this.setRotationScale(m1);
        this.mulRotationScale(scale);
        this.setTranslation(t1);
        this.m33 = 1.0f;
    }
    
    public final void set(final Matrix3d m1, final Vector3d t1, final double scale) {
        this.setRotationScale(m1);
        this.mulRotationScale((float)scale);
        this.setTranslation(t1);
        this.m33 = 1.0f;
    }
    
    public void setTranslation(final Vector3f trans) {
        this.m03 = trans.x;
        this.m13 = trans.y;
        this.m23 = trans.z;
    }
    
    public final void rotX(final float angle) {
        final float c = (float)Math.cos(angle);
        final float s = (float)Math.sin(angle);
        this.m00 = 1.0f;
        this.m01 = 0.0f;
        this.m02 = 0.0f;
        this.m03 = 0.0f;
        this.m10 = 0.0f;
        this.m11 = c;
        this.m12 = -s;
        this.m13 = 0.0f;
        this.m20 = 0.0f;
        this.m21 = s;
        this.m22 = c;
        this.m23 = 0.0f;
        this.m30 = 0.0f;
        this.m31 = 0.0f;
        this.m32 = 0.0f;
        this.m33 = 1.0f;
    }
    
    public final void rotY(final float angle) {
        final float c = (float)Math.cos(angle);
        final float s = (float)Math.sin(angle);
        this.m00 = c;
        this.m01 = 0.0f;
        this.m02 = s;
        this.m03 = 0.0f;
        this.m10 = 0.0f;
        this.m11 = 1.0f;
        this.m12 = 0.0f;
        this.m13 = 0.0f;
        this.m20 = -s;
        this.m21 = 0.0f;
        this.m22 = c;
        this.m23 = 0.0f;
        this.m30 = 0.0f;
        this.m31 = 0.0f;
        this.m32 = 0.0f;
        this.m33 = 1.0f;
    }
    
    public final void rotZ(final float angle) {
        final float c = (float)Math.cos(angle);
        final float s = (float)Math.sin(angle);
        this.m00 = c;
        this.m01 = -s;
        this.m02 = 0.0f;
        this.m03 = 0.0f;
        this.m10 = s;
        this.m11 = c;
        this.m12 = 0.0f;
        this.m13 = 0.0f;
        this.m20 = 0.0f;
        this.m21 = 0.0f;
        this.m22 = 1.0f;
        this.m23 = 0.0f;
        this.m30 = 0.0f;
        this.m31 = 0.0f;
        this.m32 = 0.0f;
        this.m33 = 1.0f;
    }
    
    public final void mul(final float scalar) {
        this.m00 *= scalar;
        this.m01 *= scalar;
        this.m02 *= scalar;
        this.m03 *= scalar;
        this.m10 *= scalar;
        this.m11 *= scalar;
        this.m12 *= scalar;
        this.m13 *= scalar;
        this.m20 *= scalar;
        this.m21 *= scalar;
        this.m22 *= scalar;
        this.m23 *= scalar;
        this.m30 *= scalar;
        this.m31 *= scalar;
        this.m32 *= scalar;
        this.m33 *= scalar;
    }
    
    public final void mul(final float scalar, final Matrix4f m1) {
        this.set(m1);
        this.mul(scalar);
    }
    
    public final void mul(final Matrix4f m1) {
        this.mul(this, m1);
    }
    
    public final void mul(final Matrix4f m1, final Matrix4f m2) {
        this.set(m1.m00 * m2.m00 + m1.m01 * m2.m10 + m1.m02 * m2.m20 + m1.m03 * m2.m30, m1.m00 * m2.m01 + m1.m01 * m2.m11 + m1.m02 * m2.m21 + m1.m03 * m2.m31, m1.m00 * m2.m02 + m1.m01 * m2.m12 + m1.m02 * m2.m22 + m1.m03 * m2.m32, m1.m00 * m2.m03 + m1.m01 * m2.m13 + m1.m02 * m2.m23 + m1.m03 * m2.m33, m1.m10 * m2.m00 + m1.m11 * m2.m10 + m1.m12 * m2.m20 + m1.m13 * m2.m30, m1.m10 * m2.m01 + m1.m11 * m2.m11 + m1.m12 * m2.m21 + m1.m13 * m2.m31, m1.m10 * m2.m02 + m1.m11 * m2.m12 + m1.m12 * m2.m22 + m1.m13 * m2.m32, m1.m10 * m2.m03 + m1.m11 * m2.m13 + m1.m12 * m2.m23 + m1.m13 * m2.m33, m1.m20 * m2.m00 + m1.m21 * m2.m10 + m1.m22 * m2.m20 + m1.m23 * m2.m30, m1.m20 * m2.m01 + m1.m21 * m2.m11 + m1.m22 * m2.m21 + m1.m23 * m2.m31, m1.m20 * m2.m02 + m1.m21 * m2.m12 + m1.m22 * m2.m22 + m1.m23 * m2.m32, m1.m20 * m2.m03 + m1.m21 * m2.m13 + m1.m22 * m2.m23 + m1.m23 * m2.m33, m1.m30 * m2.m00 + m1.m31 * m2.m10 + m1.m32 * m2.m20 + m1.m33 * m2.m30, m1.m30 * m2.m01 + m1.m31 * m2.m11 + m1.m32 * m2.m21 + m1.m33 * m2.m31, m1.m30 * m2.m02 + m1.m31 * m2.m12 + m1.m32 * m2.m22 + m1.m33 * m2.m32, m1.m30 * m2.m03 + m1.m31 * m2.m13 + m1.m32 * m2.m23 + m1.m33 * m2.m33);
    }
    
    public final void mulTransposeBoth(final Matrix4f m1, final Matrix4f m2) {
        this.mul(m2, m1);
        this.transpose();
    }
    
    public final void mulTransposeRight(final Matrix4f m1, final Matrix4f m2) {
        this.set(m1.m00 * m2.m00 + m1.m01 * m2.m01 + m1.m02 * m2.m02 + m1.m03 * m2.m03, m1.m00 * m2.m10 + m1.m01 * m2.m11 + m1.m02 * m2.m12 + m1.m03 * m2.m13, m1.m00 * m2.m20 + m1.m01 * m2.m21 + m1.m02 * m2.m22 + m1.m03 * m2.m23, m1.m00 * m2.m30 + m1.m01 * m2.m31 + m1.m02 * m2.m32 + m1.m03 * m2.m33, m1.m10 * m2.m00 + m1.m11 * m2.m01 + m1.m12 * m2.m02 + m1.m13 * m2.m03, m1.m10 * m2.m10 + m1.m11 * m2.m11 + m1.m12 * m2.m12 + m1.m13 * m2.m13, m1.m10 * m2.m20 + m1.m11 * m2.m21 + m1.m12 * m2.m22 + m1.m13 * m2.m23, m1.m10 * m2.m30 + m1.m11 * m2.m31 + m1.m12 * m2.m32 + m1.m13 * m2.m33, m1.m20 * m2.m00 + m1.m21 * m2.m01 + m1.m22 * m2.m02 + m1.m23 * m2.m03, m1.m20 * m2.m10 + m1.m21 * m2.m11 + m1.m22 * m2.m12 + m1.m23 * m2.m13, m1.m20 * m2.m20 + m1.m21 * m2.m21 + m1.m22 * m2.m22 + m1.m23 * m2.m23, m1.m20 * m2.m30 + m1.m21 * m2.m31 + m1.m22 * m2.m32 + m1.m23 * m2.m33, m1.m30 * m2.m00 + m1.m31 * m2.m01 + m1.m32 * m2.m02 + m1.m33 * m2.m03, m1.m30 * m2.m10 + m1.m31 * m2.m11 + m1.m32 * m2.m12 + m1.m33 * m2.m13, m1.m30 * m2.m20 + m1.m31 * m2.m21 + m1.m32 * m2.m22 + m1.m33 * m2.m23, m1.m30 * m2.m30 + m1.m31 * m2.m31 + m1.m32 * m2.m32 + m1.m33 * m2.m33);
    }
    
    public final void mulTransposeLeft(final Matrix4f m1, final Matrix4f m2) {
        this.set(m1.m00 * m2.m00 + m1.m10 * m2.m10 + m1.m20 * m2.m20 + m1.m30 * m2.m30, m1.m00 * m2.m01 + m1.m10 * m2.m11 + m1.m20 * m2.m21 + m1.m30 * m2.m31, m1.m00 * m2.m02 + m1.m10 * m2.m12 + m1.m20 * m2.m22 + m1.m30 * m2.m32, m1.m00 * m2.m03 + m1.m10 * m2.m13 + m1.m20 * m2.m23 + m1.m30 * m2.m33, m1.m01 * m2.m00 + m1.m11 * m2.m10 + m1.m21 * m2.m20 + m1.m31 * m2.m30, m1.m01 * m2.m01 + m1.m11 * m2.m11 + m1.m21 * m2.m21 + m1.m31 * m2.m31, m1.m01 * m2.m02 + m1.m11 * m2.m12 + m1.m21 * m2.m22 + m1.m31 * m2.m32, m1.m01 * m2.m03 + m1.m11 * m2.m13 + m1.m21 * m2.m23 + m1.m31 * m2.m33, m1.m02 * m2.m00 + m1.m12 * m2.m10 + m1.m22 * m2.m20 + m1.m32 * m2.m30, m1.m02 * m2.m01 + m1.m12 * m2.m11 + m1.m22 * m2.m21 + m1.m32 * m2.m31, m1.m02 * m2.m02 + m1.m12 * m2.m12 + m1.m22 * m2.m22 + m1.m32 * m2.m32, m1.m02 * m2.m03 + m1.m12 * m2.m13 + m1.m22 * m2.m23 + m1.m32 * m2.m33, m1.m03 * m2.m00 + m1.m13 * m2.m10 + m1.m23 * m2.m20 + m1.m33 * m2.m30, m1.m03 * m2.m01 + m1.m13 * m2.m11 + m1.m23 * m2.m21 + m1.m33 * m2.m31, m1.m03 * m2.m02 + m1.m13 * m2.m12 + m1.m23 * m2.m22 + m1.m33 * m2.m32, m1.m03 * m2.m03 + m1.m13 * m2.m13 + m1.m23 * m2.m23 + m1.m33 * m2.m33);
    }
    
    public boolean equals(final Matrix4f m1) {
        return m1 != null && this.m00 == m1.m00 && this.m01 == m1.m01 && this.m02 == m1.m02 && this.m03 == m1.m03 && this.m10 == m1.m10 && this.m11 == m1.m11 && this.m12 == m1.m12 && this.m13 == m1.m13 && this.m20 == m1.m20 && this.m21 == m1.m21 && this.m22 == m1.m22 && this.m23 == m1.m23 && this.m30 == m1.m30 && this.m31 == m1.m31 && this.m32 == m1.m32 && this.m33 == m1.m33;
    }
    
    public boolean equals(final Object o1) {
        return o1 != null && o1 instanceof Matrix4f && this.equals((Matrix4f)o1);
    }
    
    public boolean epsilonEquals(final Matrix4f m1, final float epsilon) {
        return Math.abs(this.m00 - m1.m00) <= epsilon && Math.abs(this.m01 - m1.m01) <= epsilon && Math.abs(this.m02 - m1.m02) <= epsilon && Math.abs(this.m03 - m1.m03) <= epsilon && Math.abs(this.m10 - m1.m10) <= epsilon && Math.abs(this.m11 - m1.m11) <= epsilon && Math.abs(this.m12 - m1.m12) <= epsilon && Math.abs(this.m13 - m1.m13) <= epsilon && Math.abs(this.m20 - m1.m20) <= epsilon && Math.abs(this.m21 - m1.m21) <= epsilon && Math.abs(this.m22 - m1.m22) <= epsilon && Math.abs(this.m23 - m1.m23) <= epsilon && Math.abs(this.m30 - m1.m30) <= epsilon && Math.abs(this.m31 - m1.m31) <= epsilon && Math.abs(this.m32 - m1.m32) <= epsilon && Math.abs(this.m33 - m1.m33) <= epsilon;
    }
    
    public int hashCode() {
        return Float.floatToIntBits(this.m00) ^ Float.floatToIntBits(this.m01) ^ Float.floatToIntBits(this.m02) ^ Float.floatToIntBits(this.m03) ^ Float.floatToIntBits(this.m10) ^ Float.floatToIntBits(this.m11) ^ Float.floatToIntBits(this.m12) ^ Float.floatToIntBits(this.m13) ^ Float.floatToIntBits(this.m20) ^ Float.floatToIntBits(this.m21) ^ Float.floatToIntBits(this.m22) ^ Float.floatToIntBits(this.m23) ^ Float.floatToIntBits(this.m30) ^ Float.floatToIntBits(this.m31) ^ Float.floatToIntBits(this.m32) ^ Float.floatToIntBits(this.m33);
    }
    
    public final void transform(final Tuple4f vec, final Tuple4f vecOut) {
        vecOut.set(this.m00 * vec.x + this.m01 * vec.y + this.m02 * vec.z + this.m03 * vec.w, this.m10 * vec.x + this.m11 * vec.y + this.m12 * vec.z + this.m13 * vec.w, this.m20 * vec.x + this.m21 * vec.y + this.m22 * vec.z + this.m23 * vec.w, this.m30 * vec.x + this.m31 * vec.y + this.m32 * vec.z + this.m33 * vec.w);
    }
    
    public final void transform(final Tuple4f vec) {
        this.transform(vec, vec);
    }
    
    public final void transform(final Point3f point, final Point3f pointOut) {
        pointOut.set(this.m00 * point.x + this.m01 * point.y + this.m02 * point.z + this.m03, this.m10 * point.x + this.m11 * point.y + this.m12 * point.z + this.m13, this.m20 * point.x + this.m21 * point.y + this.m22 * point.z + this.m23);
    }
    
    public final void transform(final Point3f point) {
        this.transform(point, point);
    }
    
    public final void transform(final Vector3f normal, final Vector3f normalOut) {
        normalOut.set(this.m00 * normal.x + this.m01 * normal.y + this.m02 * normal.z, this.m10 * normal.x + this.m11 * normal.y + this.m12 * normal.z, this.m20 * normal.x + this.m21 * normal.y + this.m22 * normal.z);
    }
    
    public final void transform(final Vector3f normal) {
        this.transform(normal, normal);
    }
    
    public final void setRotation(final Matrix3d m1) {
        final float scale = this.SVD(null);
        this.setRotationScale(m1);
        this.mulRotationScale(scale);
    }
    
    public final void setRotation(final Matrix3f m1) {
        final float scale = this.SVD(null);
        this.setRotationScale(m1);
        this.mulRotationScale(scale);
    }
    
    public final void setRotation(final Quat4f q1) {
        final float scale = this.SVD(null, null);
        final float tx = this.m03;
        final float ty = this.m13;
        final float tz = this.m23;
        final float w0 = this.m30;
        final float w2 = this.m31;
        final float w3 = this.m32;
        final float w4 = this.m33;
        this.set(q1);
        this.mulRotationScale(scale);
        this.m03 = tx;
        this.m13 = ty;
        this.m23 = tz;
        this.m30 = w0;
        this.m31 = w2;
        this.m32 = w3;
        this.m33 = w4;
    }
    
    public final void setRotation(final Quat4d q1) {
        final float scale = this.SVD(null, null);
        final float tx = this.m03;
        final float ty = this.m13;
        final float tz = this.m23;
        final float w0 = this.m30;
        final float w2 = this.m31;
        final float w3 = this.m32;
        final float w4 = this.m33;
        this.set(q1);
        this.mulRotationScale(scale);
        this.m03 = tx;
        this.m13 = ty;
        this.m23 = tz;
        this.m30 = w0;
        this.m31 = w2;
        this.m32 = w3;
        this.m33 = w4;
    }
    
    public final void setRotation(final AxisAngle4f a1) {
        final float scale = this.SVD(null, null);
        final float tx = this.m03;
        final float ty = this.m13;
        final float tz = this.m23;
        final float w0 = this.m30;
        final float w2 = this.m31;
        final float w3 = this.m32;
        final float w4 = this.m33;
        this.set(a1);
        this.mulRotationScale(scale);
        this.m03 = tx;
        this.m13 = ty;
        this.m23 = tz;
        this.m30 = w0;
        this.m31 = w2;
        this.m32 = w3;
        this.m33 = w4;
    }
    
    public final void setZero() {
        this.m00 = 0.0f;
        this.m01 = 0.0f;
        this.m02 = 0.0f;
        this.m03 = 0.0f;
        this.m10 = 0.0f;
        this.m11 = 0.0f;
        this.m12 = 0.0f;
        this.m13 = 0.0f;
        this.m20 = 0.0f;
        this.m21 = 0.0f;
        this.m22 = 0.0f;
        this.m23 = 0.0f;
        this.m30 = 0.0f;
        this.m31 = 0.0f;
        this.m32 = 0.0f;
        this.m33 = 0.0f;
    }
    
    public final void negate() {
        this.m00 = -this.m00;
        this.m01 = -this.m01;
        this.m02 = -this.m02;
        this.m03 = -this.m03;
        this.m10 = -this.m10;
        this.m11 = -this.m11;
        this.m12 = -this.m12;
        this.m13 = -this.m13;
        this.m20 = -this.m20;
        this.m21 = -this.m21;
        this.m22 = -this.m22;
        this.m23 = -this.m23;
        this.m30 = -this.m30;
        this.m31 = -this.m31;
        this.m32 = -this.m32;
        this.m33 = -this.m33;
    }
    
    public final void negate(final Matrix4f m1) {
        this.set(m1);
        this.negate();
    }
    
    private void set(final float m00, final float m01, final float m02, final float m03, final float m10, final float m11, final float m12, final float m13, final float m20, final float m21, final float m22, final float m23, final float m30, final float m31, final float m32, final float m33) {
        this.m00 = m00;
        this.m01 = m01;
        this.m02 = m02;
        this.m03 = m03;
        this.m10 = m10;
        this.m11 = m11;
        this.m12 = m12;
        this.m13 = m13;
        this.m20 = m20;
        this.m21 = m21;
        this.m22 = m22;
        this.m23 = m23;
        this.m30 = m30;
        this.m31 = m31;
        this.m32 = m32;
        this.m33 = m33;
    }
    
    private float SVD(final Matrix3f rot3, final Matrix4f rot4) {
        final float s = (float)Math.sqrt((this.m00 * this.m00 + this.m10 * this.m10 + this.m20 * this.m20 + this.m01 * this.m01 + this.m11 * this.m11 + this.m21 * this.m21 + this.m02 * this.m02 + this.m12 * this.m12 + this.m22 * this.m22) / 3.0);
        final float t = (s == 0.0f) ? 0.0f : (1.0f / s);
        if (rot3 != null) {
            this.getRotationScale(rot3);
            rot3.mul(t);
        }
        if (rot4 != null) {
            if (rot4 != this) {
                rot4.setRotationScale(this);
            }
            rot4.mulRotationScale(t);
        }
        return s;
    }
    
    private float SVD(final Matrix3d rot) {
        final float s = (float)Math.sqrt((this.m00 * this.m00 + this.m10 * this.m10 + this.m20 * this.m20 + this.m01 * this.m01 + this.m11 * this.m11 + this.m21 * this.m21 + this.m02 * this.m02 + this.m12 * this.m12 + this.m22 * this.m22) / 3.0);
        final float t = (s == 0.0f) ? 0.0f : (1.0f / s);
        if (rot != null) {
            this.getRotationScale(rot);
            rot.mul(t);
        }
        return s;
    }
    
    private void mulRotationScale(final float scale) {
        this.m00 *= scale;
        this.m01 *= scale;
        this.m02 *= scale;
        this.m10 *= scale;
        this.m11 *= scale;
        this.m12 *= scale;
        this.m20 *= scale;
        this.m21 *= scale;
        this.m22 *= scale;
    }
    
    private void setRotationScale(final Matrix4f m1) {
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
    
    private void setRotationScale(final Matrix3d m1) {
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
    
    private void setTranslation(final Vector3d trans) {
        this.m03 = (float)trans.x;
        this.m13 = (float)trans.y;
        this.m23 = (float)trans.z;
    }
    
    private final void getRotationScale(final Matrix3d m1) {
        m1.m00 = this.m00;
        m1.m01 = this.m01;
        m1.m02 = this.m02;
        m1.m10 = this.m10;
        m1.m11 = this.m11;
        m1.m12 = this.m12;
        m1.m20 = this.m20;
        m1.m21 = this.m21;
        m1.m22 = this.m22;
    }
    
    private void setFromQuat(final double x, final double y, final double z, final double w) {
        final double n = x * x + y * y + z * z + w * w;
        final double s = (n > 0.0) ? (2.0 / n) : 0.0;
        final double xs = x * s;
        final double ys = y * s;
        final double zs = z * s;
        final double wx = w * xs;
        final double wy = w * ys;
        final double wz = w * zs;
        final double xx = x * xs;
        final double xy = x * ys;
        final double xz = x * zs;
        final double yy = y * ys;
        final double yz = y * zs;
        final double zz = z * zs;
        this.setIdentity();
        this.m00 = (float)(1.0 - (yy + zz));
        this.m01 = (float)(xy - wz);
        this.m02 = (float)(xz + wy);
        this.m10 = (float)(xy + wz);
        this.m11 = (float)(1.0 - (xx + zz));
        this.m12 = (float)(yz - wx);
        this.m20 = (float)(xz - wy);
        this.m21 = (float)(yz + wx);
        this.m22 = (float)(1.0 - (xx + yy));
    }
    
    private void setFromAxisAngle(double x, double y, double z, final double angle) {
        double n = Math.sqrt(x * x + y * y + z * z);
        n = 1.0 / n;
        x *= n;
        y *= n;
        z *= n;
        final double c = Math.cos(angle);
        final double s = Math.sin(angle);
        final double omc = 1.0 - c;
        this.m00 = (float)(c + x * x * omc);
        this.m11 = (float)(c + y * y * omc);
        this.m22 = (float)(c + z * z * omc);
        double tmp1 = x * y * omc;
        double tmp2 = z * s;
        this.m01 = (float)(tmp1 - tmp2);
        this.m10 = (float)(tmp1 + tmp2);
        tmp1 = x * z * omc;
        tmp2 = y * s;
        this.m02 = (float)(tmp1 + tmp2);
        this.m20 = (float)(tmp1 - tmp2);
        tmp1 = y * z * omc;
        tmp2 = x * s;
        this.m12 = (float)(tmp1 - tmp2);
        this.m21 = (float)(tmp1 + tmp2);
    }
}
