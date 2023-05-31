// 
// Decompiled by Procyon v0.5.36
// 

package javax.vecmath;

import java.io.Serializable;

public class Matrix3f implements Serializable
{
    public float m00;
    public float m01;
    public float m02;
    public float m10;
    public float m11;
    public float m12;
    public float m20;
    public float m21;
    public float m22;
    
    public Matrix3f(final float m00, final float m01, final float m02, final float m10, final float m11, final float m12, final float m20, final float m21, final float m22) {
        this.set(m00, m01, m02, m10, m11, m12, m20, m21, m22);
    }
    
    public Matrix3f(final float[] v) {
        this.set(v);
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
        this.setZero();
    }
    
    public String toString() {
        final String nl = System.getProperty("line.separator");
        return "[" + nl + "  [" + this.m00 + "\t" + this.m01 + "\t" + this.m02 + "]" + nl + "  [" + this.m10 + "\t" + this.m11 + "\t" + this.m12 + "]" + nl + "  [" + this.m20 + "\t" + this.m21 + "\t" + this.m22 + "] ]";
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
        this.SVD(this);
        this.mul(scale);
    }
    
    public final void setElement(final int row, final int column, final float value) {
        if (row == 0) {
            if (column == 0) {
                this.m00 = value;
            }
            else if (column == 1) {
                this.m01 = value;
            }
            else {
                if (column != 2) {
                    throw new ArrayIndexOutOfBoundsException("column must be 0 to 2 and is " + column);
                }
                this.m02 = value;
            }
        }
        else if (row == 1) {
            if (column == 0) {
                this.m10 = value;
            }
            else if (column == 1) {
                this.m11 = value;
            }
            else {
                if (column != 2) {
                    throw new ArrayIndexOutOfBoundsException("column must be 0 to 2 and is " + column);
                }
                this.m12 = value;
            }
        }
        else {
            if (row != 2) {
                throw new ArrayIndexOutOfBoundsException("row must be 0 to 2 and is " + row);
            }
            if (column == 0) {
                this.m20 = value;
            }
            else if (column == 1) {
                this.m21 = value;
            }
            else {
                if (column != 2) {
                    throw new ArrayIndexOutOfBoundsException("column must be 0 to 2 and is " + column);
                }
                this.m22 = value;
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
            throw new ArrayIndexOutOfBoundsException("column must be 0 to 2 and is " + column);
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
            throw new ArrayIndexOutOfBoundsException("column must be 0 to 2 and is " + column);
        }
        else {
            if (row != 2) {
                throw new ArrayIndexOutOfBoundsException("row must be 0 to 2 and is " + row);
            }
            if (column == 0) {
                return this.m20;
            }
            if (column == 1) {
                return this.m21;
            }
            if (column == 2) {
                return this.m22;
            }
            throw new ArrayIndexOutOfBoundsException("column must be 0 to 2 and is " + column);
        }
    }
    
    public final void setRow(final int row, final float x, final float y, final float z) {
        if (row == 0) {
            this.m00 = x;
            this.m01 = y;
            this.m02 = z;
        }
        else if (row == 1) {
            this.m10 = x;
            this.m11 = y;
            this.m12 = z;
        }
        else {
            if (row != 2) {
                throw new ArrayIndexOutOfBoundsException("row must be 0 to 2 and is " + row);
            }
            this.m20 = x;
            this.m21 = y;
            this.m22 = z;
        }
    }
    
    public final void setRow(final int row, final Vector3f v) {
        if (row == 0) {
            this.m00 = v.x;
            this.m01 = v.y;
            this.m02 = v.z;
        }
        else if (row == 1) {
            this.m10 = v.x;
            this.m11 = v.y;
            this.m12 = v.z;
        }
        else {
            if (row != 2) {
                throw new ArrayIndexOutOfBoundsException("row must be 0 to 2 and is " + row);
            }
            this.m20 = v.x;
            this.m21 = v.y;
            this.m22 = v.z;
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
                throw new ArrayIndexOutOfBoundsException("row must be 0 to 2 and is " + row);
            }
            v[0] = this.m20;
            v[1] = this.m21;
            v[2] = this.m22;
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
                throw new ArrayIndexOutOfBoundsException("row must be 0 to 2 and is " + row);
            }
            v.x = this.m20;
            v.y = this.m21;
            v.z = this.m22;
        }
    }
    
    public final void setRow(final int row, final float[] v) {
        if (row == 0) {
            this.m00 = v[0];
            this.m01 = v[1];
            this.m02 = v[2];
        }
        else if (row == 1) {
            this.m10 = v[0];
            this.m11 = v[1];
            this.m12 = v[2];
        }
        else {
            if (row != 2) {
                throw new ArrayIndexOutOfBoundsException("row must be 0 to 2 and is " + row);
            }
            this.m20 = v[0];
            this.m21 = v[1];
            this.m22 = v[2];
        }
    }
    
    public final void setColumn(final int column, final float x, final float y, final float z) {
        if (column == 0) {
            this.m00 = x;
            this.m10 = y;
            this.m20 = z;
        }
        else if (column == 1) {
            this.m01 = x;
            this.m11 = y;
            this.m21 = z;
        }
        else {
            if (column != 2) {
                throw new ArrayIndexOutOfBoundsException("column must be 0 to 2 and is " + column);
            }
            this.m02 = x;
            this.m12 = y;
            this.m22 = z;
        }
    }
    
    public final void setColumn(final int column, final Vector3f v) {
        if (column == 0) {
            this.m00 = v.x;
            this.m10 = v.y;
            this.m20 = v.z;
        }
        else if (column == 1) {
            this.m01 = v.x;
            this.m11 = v.y;
            this.m21 = v.z;
        }
        else {
            if (column != 2) {
                throw new ArrayIndexOutOfBoundsException("column must be 0 to 2 and is " + column);
            }
            this.m02 = v.x;
            this.m12 = v.y;
            this.m22 = v.z;
        }
    }
    
    public final void setColumn(final int column, final float[] v) {
        if (column == 0) {
            this.m00 = v[0];
            this.m10 = v[1];
            this.m20 = v[2];
        }
        else if (column == 1) {
            this.m01 = v[0];
            this.m11 = v[1];
            this.m21 = v[2];
        }
        else {
            if (column != 2) {
                throw new ArrayIndexOutOfBoundsException("column must be 0 to 2 and is " + column);
            }
            this.m02 = v[0];
            this.m12 = v[1];
            this.m22 = v[2];
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
                throw new ArrayIndexOutOfBoundsException("column must be 0 to 2 and is " + column);
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
                throw new ArrayIndexOutOfBoundsException("column must be 0 to 2 and is " + column);
            }
            v[0] = this.m02;
            v[1] = this.m12;
            v[2] = this.m22;
        }
    }
    
    public final float getScale() {
        return this.SVD(null);
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
        this.set(m1);
        this.add(scalar);
    }
    
    public final void add(final Matrix3f m1, final Matrix3f m2) {
        this.set(m1.m00 + m2.m00, m1.m01 + m2.m01, m1.m02 + m2.m02, m1.m10 + m2.m10, m1.m11 + m2.m11, m1.m12 + m2.m12, m1.m20 + m2.m20, m1.m21 + m2.m21, m1.m22 + m2.m22);
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
        this.set(m1.m00 - m2.m00, m1.m01 - m2.m01, m1.m02 - m2.m02, m1.m10 - m2.m10, m1.m11 - m2.m11, m1.m12 - m2.m12, m1.m20 - m2.m20, m1.m21 - m2.m21, m1.m22 - m2.m22);
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
        float tmp = this.m01;
        this.m01 = this.m10;
        this.m10 = tmp;
        tmp = this.m02;
        this.m02 = this.m20;
        this.m20 = tmp;
        tmp = this.m12;
        this.m12 = this.m21;
        this.m21 = tmp;
    }
    
    public final void transpose(final Matrix3f m1) {
        this.set(m1);
        this.transpose();
    }
    
    public final void set(final Quat4f q1) {
        this.setFromQuat(q1.x, q1.y, q1.z, q1.w);
    }
    
    public final void set(final AxisAngle4f a1) {
        this.setFromAxisAngle(a1.x, a1.y, a1.z, a1.angle);
    }
    
    public final void set(final AxisAngle4d a1) {
        this.setFromAxisAngle(a1.x, a1.y, a1.z, a1.angle);
    }
    
    public final void set(final Quat4d q1) {
        this.setFromQuat(q1.x, q1.y, q1.z, q1.w);
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
    
    public final void invert(final Matrix3f m1) {
        this.set(m1);
        this.invert();
    }
    
    public final void invert() {
        double s = this.determinant();
        if (s == 0.0) {
            return;
        }
        s = 1.0 / s;
        this.set(this.m11 * this.m22 - this.m12 * this.m21, this.m02 * this.m21 - this.m01 * this.m22, this.m01 * this.m12 - this.m02 * this.m11, this.m12 * this.m20 - this.m10 * this.m22, this.m00 * this.m22 - this.m02 * this.m20, this.m02 * this.m10 - this.m00 * this.m12, this.m10 * this.m21 - this.m11 * this.m20, this.m01 * this.m20 - this.m00 * this.m21, this.m00 * this.m11 - this.m01 * this.m10);
        this.mul((float)s);
    }
    
    public final float determinant() {
        return this.m00 * (this.m11 * this.m22 - this.m21 * this.m12) - this.m01 * (this.m10 * this.m22 - this.m20 * this.m12) + this.m02 * (this.m10 * this.m21 - this.m20 * this.m11);
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
        final double c = Math.cos(angle);
        final double s = Math.sin(angle);
        this.m00 = 1.0f;
        this.m01 = 0.0f;
        this.m02 = 0.0f;
        this.m10 = 0.0f;
        this.m11 = (float)c;
        this.m12 = (float)(-s);
        this.m20 = 0.0f;
        this.m21 = (float)s;
        this.m22 = (float)c;
    }
    
    public final void rotY(final float angle) {
        final double c = Math.cos(angle);
        final double s = Math.sin(angle);
        this.m00 = (float)c;
        this.m01 = 0.0f;
        this.m02 = (float)s;
        this.m10 = 0.0f;
        this.m11 = 1.0f;
        this.m12 = 0.0f;
        this.m20 = (float)(-s);
        this.m21 = 0.0f;
        this.m22 = (float)c;
    }
    
    public final void rotZ(final float angle) {
        final double c = Math.cos(angle);
        final double s = Math.sin(angle);
        this.m00 = (float)c;
        this.m01 = (float)(-s);
        this.m02 = 0.0f;
        this.m10 = (float)s;
        this.m11 = (float)c;
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
        this.set(m1);
        this.mul(scalar);
    }
    
    public final void mul(final Matrix3f m1) {
        this.mul(this, m1);
    }
    
    public final void mul(final Matrix3f m1, final Matrix3f m2) {
        this.set(m1.m00 * m2.m00 + m1.m01 * m2.m10 + m1.m02 * m2.m20, m1.m00 * m2.m01 + m1.m01 * m2.m11 + m1.m02 * m2.m21, m1.m00 * m2.m02 + m1.m01 * m2.m12 + m1.m02 * m2.m22, m1.m10 * m2.m00 + m1.m11 * m2.m10 + m1.m12 * m2.m20, m1.m10 * m2.m01 + m1.m11 * m2.m11 + m1.m12 * m2.m21, m1.m10 * m2.m02 + m1.m11 * m2.m12 + m1.m12 * m2.m22, m1.m20 * m2.m00 + m1.m21 * m2.m10 + m1.m22 * m2.m20, m1.m20 * m2.m01 + m1.m21 * m2.m11 + m1.m22 * m2.m21, m1.m20 * m2.m02 + m1.m21 * m2.m12 + m1.m22 * m2.m22);
    }
    
    public final void mulNormalize(final Matrix3f m1) {
        this.mul(m1);
        this.SVD(this);
    }
    
    public final void mulNormalize(final Matrix3f m1, final Matrix3f m2) {
        this.mul(m1, m2);
        this.SVD(this);
    }
    
    public final void mulTransposeBoth(final Matrix3f m1, final Matrix3f m2) {
        this.mul(m2, m1);
        this.transpose();
    }
    
    public final void mulTransposeRight(final Matrix3f m1, final Matrix3f m2) {
        this.set(m1.m00 * m2.m00 + m1.m01 * m2.m01 + m1.m02 * m2.m02, m1.m00 * m2.m10 + m1.m01 * m2.m11 + m1.m02 * m2.m12, m1.m00 * m2.m20 + m1.m01 * m2.m21 + m1.m02 * m2.m22, m1.m10 * m2.m00 + m1.m11 * m2.m01 + m1.m12 * m2.m02, m1.m10 * m2.m10 + m1.m11 * m2.m11 + m1.m12 * m2.m12, m1.m10 * m2.m20 + m1.m11 * m2.m21 + m1.m12 * m2.m22, m1.m20 * m2.m00 + m1.m21 * m2.m01 + m1.m22 * m2.m02, m1.m20 * m2.m10 + m1.m21 * m2.m11 + m1.m22 * m2.m12, m1.m20 * m2.m20 + m1.m21 * m2.m21 + m1.m22 * m2.m22);
    }
    
    public final void mulTransposeLeft(final Matrix3f m1, final Matrix3f m2) {
        this.set(m1.m00 * m2.m00 + m1.m10 * m2.m10 + m1.m20 * m2.m20, m1.m00 * m2.m01 + m1.m10 * m2.m11 + m1.m20 * m2.m21, m1.m00 * m2.m02 + m1.m10 * m2.m12 + m1.m20 * m2.m22, m1.m01 * m2.m00 + m1.m11 * m2.m10 + m1.m21 * m2.m20, m1.m01 * m2.m01 + m1.m11 * m2.m11 + m1.m21 * m2.m21, m1.m01 * m2.m02 + m1.m11 * m2.m12 + m1.m21 * m2.m22, m1.m02 * m2.m00 + m1.m12 * m2.m10 + m1.m22 * m2.m20, m1.m02 * m2.m01 + m1.m12 * m2.m11 + m1.m22 * m2.m21, m1.m02 * m2.m02 + m1.m12 * m2.m12 + m1.m22 * m2.m22);
    }
    
    public final void normalize() {
        this.SVD(this);
    }
    
    public final void normalize(final Matrix3f m1) {
        this.set(m1);
        this.SVD(this);
    }
    
    public final void normalizeCP() {
        final double s = Math.pow(this.determinant(), -0.3333333333333333);
        this.mul((float)s);
    }
    
    public final void normalizeCP(final Matrix3f m1) {
        this.set(m1);
        this.normalizeCP();
    }
    
    public boolean equals(final Matrix3f m1) {
        return m1 != null && this.m00 == m1.m00 && this.m01 == m1.m01 && this.m02 == m1.m02 && this.m10 == m1.m10 && this.m11 == m1.m11 && this.m12 == m1.m12 && this.m20 == m1.m20 && this.m21 == m1.m21 && this.m22 == m1.m22;
    }
    
    public boolean equals(final Object o1) {
        return o1 != null && o1 instanceof Matrix3f && this.equals((Matrix3f)o1);
    }
    
    public boolean epsilonEquals(final Matrix3f m1, final double epsilon) {
        return Math.abs(this.m00 - m1.m00) <= epsilon && Math.abs(this.m01 - m1.m01) <= epsilon && Math.abs(this.m02 - m1.m02) <= epsilon && Math.abs(this.m10 - m1.m10) <= epsilon && Math.abs(this.m11 - m1.m11) <= epsilon && Math.abs(this.m12 - m1.m12) <= epsilon && Math.abs(this.m20 - m1.m20) <= epsilon && Math.abs(this.m21 - m1.m21) <= epsilon && Math.abs(this.m22 - m1.m22) <= epsilon;
    }
    
    public int hashCode() {
        return Float.floatToIntBits(this.m00) ^ Float.floatToIntBits(this.m01) ^ Float.floatToIntBits(this.m02) ^ Float.floatToIntBits(this.m10) ^ Float.floatToIntBits(this.m11) ^ Float.floatToIntBits(this.m12) ^ Float.floatToIntBits(this.m20) ^ Float.floatToIntBits(this.m21) ^ Float.floatToIntBits(this.m22);
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
        this.set(m1);
        this.negate();
    }
    
    public final void transform(final Tuple3f t) {
        this.transform(t, t);
    }
    
    public final void transform(final Tuple3f t, final Tuple3f result) {
        result.set(this.m00 * t.x + this.m01 * t.y + this.m02 * t.z, this.m10 * t.x + this.m11 * t.y + this.m12 * t.z, this.m20 * t.x + this.m21 * t.y + this.m22 * t.z);
    }
    
    private void set(final float m00, final float m01, final float m02, final float m10, final float m11, final float m12, final float m20, final float m21, final float m22) {
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
    
    private float SVD(final Matrix3f rot) {
        final float s = (float)Math.sqrt((this.m00 * this.m00 + this.m10 * this.m10 + this.m20 * this.m20 + this.m01 * this.m01 + this.m11 * this.m11 + this.m21 * this.m21 + this.m02 * this.m02 + this.m12 * this.m12 + this.m22 * this.m22) / 3.0);
        final float t = (s == 0.0f) ? 0.0f : (1.0f / s);
        if (rot != null) {
            if (rot != this) {
                rot.set(this);
            }
            rot.mul(t);
        }
        return s;
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
