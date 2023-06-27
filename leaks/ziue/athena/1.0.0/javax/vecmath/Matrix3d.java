package javax.vecmath;

import java.io.*;

public class Matrix3d implements Serializable, Cloneable
{
    static final long serialVersionUID = 6837536777072402710L;
    public double m00;
    public double m01;
    public double m02;
    public double m10;
    public double m11;
    public double m12;
    public double m20;
    public double m21;
    public double m22;
    private static final double EPS = 1.110223024E-16;
    private static final double ERR_EPS = 1.0E-8;
    private static double xin;
    private static double yin;
    private static double zin;
    private static double xout;
    private static double yout;
    private static double zout;
    
    public Matrix3d(final double m00, final double m01, final double m02, final double m10, final double m11, final double m12, final double m20, final double m21, final double m22) {
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
    
    public Matrix3d(final double[] v) {
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
    
    public Matrix3d(final Matrix3d m1) {
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
    
    public Matrix3d(final Matrix3f m1) {
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
    
    public Matrix3d() {
        this.m00 = 0.0;
        this.m01 = 0.0;
        this.m02 = 0.0;
        this.m10 = 0.0;
        this.m11 = 0.0;
        this.m12 = 0.0;
        this.m20 = 0.0;
        this.m21 = 0.0;
        this.m22 = 0.0;
    }
    
    @Override
    public String toString() {
        return this.m00 + ", " + this.m01 + ", " + this.m02 + "\n" + this.m10 + ", " + this.m11 + ", " + this.m12 + "\n" + this.m20 + ", " + this.m21 + ", " + this.m22 + "\n";
    }
    
    public final void setIdentity() {
        this.m00 = 1.0;
        this.m01 = 0.0;
        this.m02 = 0.0;
        this.m10 = 0.0;
        this.m11 = 1.0;
        this.m12 = 0.0;
        this.m20 = 0.0;
        this.m21 = 0.0;
        this.m22 = 1.0;
    }
    
    public final void setScale(final double scale) {
        final double[] tmp_rot = new double[9];
        final double[] tmp_scale = new double[3];
        this.getScaleRotate(tmp_scale, tmp_rot);
        this.m00 = tmp_rot[0] * scale;
        this.m01 = tmp_rot[1] * scale;
        this.m02 = tmp_rot[2] * scale;
        this.m10 = tmp_rot[3] * scale;
        this.m11 = tmp_rot[4] * scale;
        this.m12 = tmp_rot[5] * scale;
        this.m20 = tmp_rot[6] * scale;
        this.m21 = tmp_rot[7] * scale;
        this.m22 = tmp_rot[8] * scale;
    }
    
    public final void setElement(final int row, final int column, final double value) {
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
                            throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix3d0"));
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
                            throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix3d0"));
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
                            throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix3d0"));
                        }
                    }
                    break;
                }
                default: {
                    throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix3d0"));
                }
            }
        }
    }
    
    public final double getElement(final int row, final int column) {
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
        throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix3d1"));
    }
    
    public final void getRow(final int row, final Vector3d v) {
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
                throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix3d2"));
            }
            v.x = this.m20;
            v.y = this.m21;
            v.z = this.m22;
        }
    }
    
    public final void getRow(final int row, final double[] v) {
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
                throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix3d2"));
            }
            v[0] = this.m20;
            v[1] = this.m21;
            v[2] = this.m22;
        }
    }
    
    public final void getColumn(final int column, final Vector3d v) {
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
                throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix3d4"));
            }
            v.x = this.m02;
            v.y = this.m12;
            v.z = this.m22;
        }
    }
    
    public final void getColumn(final int column, final double[] v) {
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
                throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix3d4"));
            }
            v[0] = this.m02;
            v[1] = this.m12;
            v[2] = this.m22;
        }
    }
    
    public final void setRow(final int row, final double x, final double y, final double z) {
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
                throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix3d6"));
            }
        }
    }
    
    public final void setRow(final int row, final Vector3d v) {
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
                throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix3d6"));
            }
        }
    }
    
    public final void setRow(final int row, final double[] v) {
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
                throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix3d6"));
            }
        }
    }
    
    public final void setColumn(final int column, final double x, final double y, final double z) {
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
                throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix3d9"));
            }
        }
    }
    
    public final void setColumn(final int column, final Vector3d v) {
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
                throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix3d9"));
            }
        }
    }
    
    public final void setColumn(final int column, final double[] v) {
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
                throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix3d9"));
            }
        }
    }
    
    public final double getScale() {
        final double[] tmp_scale = new double[3];
        final double[] tmp_rot = new double[9];
        this.getScaleRotate(tmp_scale, tmp_rot);
        return max3(tmp_scale);
    }
    
    public final void add(final double scalar) {
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
    
    public final void add(final double scalar, final Matrix3d m1) {
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
    
    public final void add(final Matrix3d m1, final Matrix3d m2) {
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
    
    public final void add(final Matrix3d m1) {
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
    
    public final void sub(final Matrix3d m1, final Matrix3d m2) {
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
    
    public final void sub(final Matrix3d m1) {
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
        double temp = this.m10;
        this.m10 = this.m01;
        this.m01 = temp;
        temp = this.m20;
        this.m20 = this.m02;
        this.m02 = temp;
        temp = this.m21;
        this.m21 = this.m12;
        this.m12 = temp;
    }
    
    public final void transpose(final Matrix3d m1) {
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
    
    public final void set(final Quat4d q1) {
        this.m00 = 1.0 - 2.0 * q1.y * q1.y - 2.0 * q1.z * q1.z;
        this.m10 = 2.0 * (q1.x * q1.y + q1.w * q1.z);
        this.m20 = 2.0 * (q1.x * q1.z - q1.w * q1.y);
        this.m01 = 2.0 * (q1.x * q1.y - q1.w * q1.z);
        this.m11 = 1.0 - 2.0 * q1.x * q1.x - 2.0 * q1.z * q1.z;
        this.m21 = 2.0 * (q1.y * q1.z + q1.w * q1.x);
        this.m02 = 2.0 * (q1.x * q1.z + q1.w * q1.y);
        this.m12 = 2.0 * (q1.y * q1.z - q1.w * q1.x);
        this.m22 = 1.0 - 2.0 * q1.x * q1.x - 2.0 * q1.y * q1.y;
    }
    
    public final void set(final AxisAngle4d a1) {
        double mag = Math.sqrt(a1.x * a1.x + a1.y * a1.y + a1.z * a1.z);
        if (mag < 1.110223024E-16) {
            this.m00 = 1.0;
            this.m01 = 0.0;
            this.m02 = 0.0;
            this.m10 = 0.0;
            this.m11 = 1.0;
            this.m12 = 0.0;
            this.m20 = 0.0;
            this.m21 = 0.0;
            this.m22 = 1.0;
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
    
    public final void set(final Quat4f q1) {
        this.m00 = 1.0 - 2.0 * q1.y * q1.y - 2.0 * q1.z * q1.z;
        this.m10 = 2.0 * (q1.x * q1.y + q1.w * q1.z);
        this.m20 = 2.0 * (q1.x * q1.z - q1.w * q1.y);
        this.m01 = 2.0 * (q1.x * q1.y - q1.w * q1.z);
        this.m11 = 1.0 - 2.0 * q1.x * q1.x - 2.0 * q1.z * q1.z;
        this.m21 = 2.0 * (q1.y * q1.z + q1.w * q1.x);
        this.m02 = 2.0 * (q1.x * q1.z + q1.w * q1.y);
        this.m12 = 2.0 * (q1.y * q1.z - q1.w * q1.x);
        this.m22 = 1.0 - 2.0 * q1.x * q1.x - 2.0 * q1.y * q1.y;
    }
    
    public final void set(final AxisAngle4f a1) {
        double mag = Math.sqrt(a1.x * a1.x + a1.y * a1.y + a1.z * a1.z);
        if (mag < 1.110223024E-16) {
            this.m00 = 1.0;
            this.m01 = 0.0;
            this.m02 = 0.0;
            this.m10 = 0.0;
            this.m11 = 1.0;
            this.m12 = 0.0;
            this.m20 = 0.0;
            this.m21 = 0.0;
            this.m22 = 1.0;
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
    
    public final void set(final double[] m) {
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
    
    public final void invert(final Matrix3d m1) {
        this.invertGeneral(m1);
    }
    
    public final void invert() {
        this.invertGeneral(this);
    }
    
    private final void invertGeneral(final Matrix3d m1) {
        final double[] result = new double[9];
        final int[] row_perm = new int[3];
        final double[] tmp = { m1.m00, m1.m01, m1.m02, m1.m10, m1.m11, m1.m12, m1.m20, m1.m21, m1.m22 };
        if (!luDecomposition(tmp, row_perm)) {
            throw new SingularMatrixException(VecMathI18N.getString("Matrix3d12"));
        }
        for (int i = 0; i < 9; ++i) {
            result[i] = 0.0;
        }
        result[0] = 1.0;
        result[8] = (result[4] = 1.0);
        luBacksubstitution(tmp, row_perm, result);
        this.m00 = result[0];
        this.m01 = result[1];
        this.m02 = result[2];
        this.m10 = result[3];
        this.m11 = result[4];
        this.m12 = result[5];
        this.m20 = result[6];
        this.m21 = result[7];
        this.m22 = result[8];
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
                throw new RuntimeException(VecMathI18N.getString("Matrix3d13"));
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
    
    public final double determinant() {
        final double total = this.m00 * (this.m11 * this.m22 - this.m12 * this.m21) + this.m01 * (this.m12 * this.m20 - this.m10 * this.m22) + this.m02 * (this.m10 * this.m21 - this.m11 * this.m20);
        return total;
    }
    
    public final void set(final double scale) {
        this.m00 = scale;
        this.m01 = 0.0;
        this.m02 = 0.0;
        this.m10 = 0.0;
        this.m11 = scale;
        this.m12 = 0.0;
        this.m20 = 0.0;
        this.m21 = 0.0;
        this.m22 = scale;
    }
    
    public final void rotX(final double angle) {
        final double sinAngle = Math.sin(angle);
        final double cosAngle = Math.cos(angle);
        this.m00 = 1.0;
        this.m01 = 0.0;
        this.m02 = 0.0;
        this.m10 = 0.0;
        this.m11 = cosAngle;
        this.m12 = -sinAngle;
        this.m20 = 0.0;
        this.m21 = sinAngle;
        this.m22 = cosAngle;
    }
    
    public final void rotY(final double angle) {
        final double sinAngle = Math.sin(angle);
        final double cosAngle = Math.cos(angle);
        this.m00 = cosAngle;
        this.m01 = 0.0;
        this.m02 = sinAngle;
        this.m10 = 0.0;
        this.m11 = 1.0;
        this.m12 = 0.0;
        this.m20 = -sinAngle;
        this.m21 = 0.0;
        this.m22 = cosAngle;
    }
    
    public final void rotZ(final double angle) {
        final double sinAngle = Math.sin(angle);
        final double cosAngle = Math.cos(angle);
        this.m00 = cosAngle;
        this.m01 = -sinAngle;
        this.m02 = 0.0;
        this.m10 = sinAngle;
        this.m11 = cosAngle;
        this.m12 = 0.0;
        this.m20 = 0.0;
        this.m21 = 0.0;
        this.m22 = 1.0;
    }
    
    public final void mul(final double scalar) {
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
    
    public final void mul(final double scalar, final Matrix3d m1) {
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
    
    public final void mul(final Matrix3d m1) {
        final double m2 = this.m00 * m1.m00 + this.m01 * m1.m10 + this.m02 * m1.m20;
        final double m3 = this.m00 * m1.m01 + this.m01 * m1.m11 + this.m02 * m1.m21;
        final double m4 = this.m00 * m1.m02 + this.m01 * m1.m12 + this.m02 * m1.m22;
        final double m5 = this.m10 * m1.m00 + this.m11 * m1.m10 + this.m12 * m1.m20;
        final double m6 = this.m10 * m1.m01 + this.m11 * m1.m11 + this.m12 * m1.m21;
        final double m7 = this.m10 * m1.m02 + this.m11 * m1.m12 + this.m12 * m1.m22;
        final double m8 = this.m20 * m1.m00 + this.m21 * m1.m10 + this.m22 * m1.m20;
        final double m9 = this.m20 * m1.m01 + this.m21 * m1.m11 + this.m22 * m1.m21;
        final double m10 = this.m20 * m1.m02 + this.m21 * m1.m12 + this.m22 * m1.m22;
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
    
    public final void mul(final Matrix3d m1, final Matrix3d m2) {
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
            final double m3 = m1.m00 * m2.m00 + m1.m01 * m2.m10 + m1.m02 * m2.m20;
            final double m4 = m1.m00 * m2.m01 + m1.m01 * m2.m11 + m1.m02 * m2.m21;
            final double m5 = m1.m00 * m2.m02 + m1.m01 * m2.m12 + m1.m02 * m2.m22;
            final double m6 = m1.m10 * m2.m00 + m1.m11 * m2.m10 + m1.m12 * m2.m20;
            final double m7 = m1.m10 * m2.m01 + m1.m11 * m2.m11 + m1.m12 * m2.m21;
            final double m8 = m1.m10 * m2.m02 + m1.m11 * m2.m12 + m1.m12 * m2.m22;
            final double m9 = m1.m20 * m2.m00 + m1.m21 * m2.m10 + m1.m22 * m2.m20;
            final double m10 = m1.m20 * m2.m01 + m1.m21 * m2.m11 + m1.m22 * m2.m21;
            final double m11 = m1.m20 * m2.m02 + m1.m21 * m2.m12 + m1.m22 * m2.m22;
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
    
    public final void mulNormalize(final Matrix3d m1) {
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
        compute_svd(tmp, tmp_scale, tmp_rot);
        this.m00 = tmp_rot[0];
        this.m01 = tmp_rot[1];
        this.m02 = tmp_rot[2];
        this.m10 = tmp_rot[3];
        this.m11 = tmp_rot[4];
        this.m12 = tmp_rot[5];
        this.m20 = tmp_rot[6];
        this.m21 = tmp_rot[7];
        this.m22 = tmp_rot[8];
    }
    
    public final void mulNormalize(final Matrix3d m1, final Matrix3d m2) {
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
        compute_svd(tmp, tmp_scale, tmp_rot);
        this.m00 = tmp_rot[0];
        this.m01 = tmp_rot[1];
        this.m02 = tmp_rot[2];
        this.m10 = tmp_rot[3];
        this.m11 = tmp_rot[4];
        this.m12 = tmp_rot[5];
        this.m20 = tmp_rot[6];
        this.m21 = tmp_rot[7];
        this.m22 = tmp_rot[8];
    }
    
    public final void mulTransposeBoth(final Matrix3d m1, final Matrix3d m2) {
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
            final double m3 = m1.m00 * m2.m00 + m1.m10 * m2.m01 + m1.m20 * m2.m02;
            final double m4 = m1.m00 * m2.m10 + m1.m10 * m2.m11 + m1.m20 * m2.m12;
            final double m5 = m1.m00 * m2.m20 + m1.m10 * m2.m21 + m1.m20 * m2.m22;
            final double m6 = m1.m01 * m2.m00 + m1.m11 * m2.m01 + m1.m21 * m2.m02;
            final double m7 = m1.m01 * m2.m10 + m1.m11 * m2.m11 + m1.m21 * m2.m12;
            final double m8 = m1.m01 * m2.m20 + m1.m11 * m2.m21 + m1.m21 * m2.m22;
            final double m9 = m1.m02 * m2.m00 + m1.m12 * m2.m01 + m1.m22 * m2.m02;
            final double m10 = m1.m02 * m2.m10 + m1.m12 * m2.m11 + m1.m22 * m2.m12;
            final double m11 = m1.m02 * m2.m20 + m1.m12 * m2.m21 + m1.m22 * m2.m22;
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
    
    public final void mulTransposeRight(final Matrix3d m1, final Matrix3d m2) {
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
            final double m3 = m1.m00 * m2.m00 + m1.m01 * m2.m01 + m1.m02 * m2.m02;
            final double m4 = m1.m00 * m2.m10 + m1.m01 * m2.m11 + m1.m02 * m2.m12;
            final double m5 = m1.m00 * m2.m20 + m1.m01 * m2.m21 + m1.m02 * m2.m22;
            final double m6 = m1.m10 * m2.m00 + m1.m11 * m2.m01 + m1.m12 * m2.m02;
            final double m7 = m1.m10 * m2.m10 + m1.m11 * m2.m11 + m1.m12 * m2.m12;
            final double m8 = m1.m10 * m2.m20 + m1.m11 * m2.m21 + m1.m12 * m2.m22;
            final double m9 = m1.m20 * m2.m00 + m1.m21 * m2.m01 + m1.m22 * m2.m02;
            final double m10 = m1.m20 * m2.m10 + m1.m21 * m2.m11 + m1.m22 * m2.m12;
            final double m11 = m1.m20 * m2.m20 + m1.m21 * m2.m21 + m1.m22 * m2.m22;
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
    
    public final void mulTransposeLeft(final Matrix3d m1, final Matrix3d m2) {
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
            final double m3 = m1.m00 * m2.m00 + m1.m10 * m2.m10 + m1.m20 * m2.m20;
            final double m4 = m1.m00 * m2.m01 + m1.m10 * m2.m11 + m1.m20 * m2.m21;
            final double m5 = m1.m00 * m2.m02 + m1.m10 * m2.m12 + m1.m20 * m2.m22;
            final double m6 = m1.m01 * m2.m00 + m1.m11 * m2.m10 + m1.m21 * m2.m20;
            final double m7 = m1.m01 * m2.m01 + m1.m11 * m2.m11 + m1.m21 * m2.m21;
            final double m8 = m1.m01 * m2.m02 + m1.m11 * m2.m12 + m1.m21 * m2.m22;
            final double m9 = m1.m02 * m2.m00 + m1.m12 * m2.m10 + m1.m22 * m2.m20;
            final double m10 = m1.m02 * m2.m01 + m1.m12 * m2.m11 + m1.m22 * m2.m21;
            final double m11 = m1.m02 * m2.m02 + m1.m12 * m2.m12 + m1.m22 * m2.m22;
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
        this.m00 = tmp_rot[0];
        this.m01 = tmp_rot[1];
        this.m02 = tmp_rot[2];
        this.m10 = tmp_rot[3];
        this.m11 = tmp_rot[4];
        this.m12 = tmp_rot[5];
        this.m20 = tmp_rot[6];
        this.m21 = tmp_rot[7];
        this.m22 = tmp_rot[8];
    }
    
    public final void normalize(final Matrix3d m1) {
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
        compute_svd(tmp, tmp_scale, tmp_rot);
        this.m00 = tmp_rot[0];
        this.m01 = tmp_rot[1];
        this.m02 = tmp_rot[2];
        this.m10 = tmp_rot[3];
        this.m11 = tmp_rot[4];
        this.m12 = tmp_rot[5];
        this.m20 = tmp_rot[6];
        this.m21 = tmp_rot[7];
        this.m22 = tmp_rot[8];
    }
    
    public final void normalizeCP() {
        double mag = 1.0 / Math.sqrt(this.m00 * this.m00 + this.m10 * this.m10 + this.m20 * this.m20);
        this.m00 *= mag;
        this.m10 *= mag;
        this.m20 *= mag;
        mag = 1.0 / Math.sqrt(this.m01 * this.m01 + this.m11 * this.m11 + this.m21 * this.m21);
        this.m01 *= mag;
        this.m11 *= mag;
        this.m21 *= mag;
        this.m02 = this.m10 * this.m21 - this.m11 * this.m20;
        this.m12 = this.m01 * this.m20 - this.m00 * this.m21;
        this.m22 = this.m00 * this.m11 - this.m01 * this.m10;
    }
    
    public final void normalizeCP(final Matrix3d m1) {
        double mag = 1.0 / Math.sqrt(m1.m00 * m1.m00 + m1.m10 * m1.m10 + m1.m20 * m1.m20);
        this.m00 = m1.m00 * mag;
        this.m10 = m1.m10 * mag;
        this.m20 = m1.m20 * mag;
        mag = 1.0 / Math.sqrt(m1.m01 * m1.m01 + m1.m11 * m1.m11 + m1.m21 * m1.m21);
        this.m01 = m1.m01 * mag;
        this.m11 = m1.m11 * mag;
        this.m21 = m1.m21 * mag;
        this.m02 = this.m10 * this.m21 - this.m11 * this.m20;
        this.m12 = this.m01 * this.m20 - this.m00 * this.m21;
        this.m22 = this.m00 * this.m11 - this.m01 * this.m10;
    }
    
    public boolean equals(final Matrix3d m1) {
        try {
            return this.m00 == m1.m00 && this.m01 == m1.m01 && this.m02 == m1.m02 && this.m10 == m1.m10 && this.m11 == m1.m11 && this.m12 == m1.m12 && this.m20 == m1.m20 && this.m21 == m1.m21 && this.m22 == m1.m22;
        }
        catch (NullPointerException e2) {
            return false;
        }
    }
    
    @Override
    public boolean equals(final Object t1) {
        try {
            final Matrix3d m2 = (Matrix3d)t1;
            return this.m00 == m2.m00 && this.m01 == m2.m01 && this.m02 == m2.m02 && this.m10 == m2.m10 && this.m11 == m2.m11 && this.m12 == m2.m12 && this.m20 == m2.m20 && this.m21 == m2.m21 && this.m22 == m2.m22;
        }
        catch (ClassCastException e1) {
            return false;
        }
        catch (NullPointerException e2) {
            return false;
        }
    }
    
    public boolean epsilonEquals(final Matrix3d m1, final double epsilon) {
        double diff = this.m00 - m1.m00;
        if (((diff < 0.0) ? (-diff) : diff) > epsilon) {
            return false;
        }
        diff = this.m01 - m1.m01;
        if (((diff < 0.0) ? (-diff) : diff) > epsilon) {
            return false;
        }
        diff = this.m02 - m1.m02;
        if (((diff < 0.0) ? (-diff) : diff) > epsilon) {
            return false;
        }
        diff = this.m10 - m1.m10;
        if (((diff < 0.0) ? (-diff) : diff) > epsilon) {
            return false;
        }
        diff = this.m11 - m1.m11;
        if (((diff < 0.0) ? (-diff) : diff) > epsilon) {
            return false;
        }
        diff = this.m12 - m1.m12;
        if (((diff < 0.0) ? (-diff) : diff) > epsilon) {
            return false;
        }
        diff = this.m20 - m1.m20;
        if (((diff < 0.0) ? (-diff) : diff) > epsilon) {
            return false;
        }
        diff = this.m21 - m1.m21;
        if (((diff < 0.0) ? (-diff) : diff) > epsilon) {
            return false;
        }
        diff = this.m22 - m1.m22;
        return ((diff < 0.0) ? (-diff) : diff) <= epsilon;
    }
    
    @Override
    public int hashCode() {
        long bits = 1L;
        bits = 31L * bits + VecMathUtil.doubleToLongBits(this.m00);
        bits = 31L * bits + VecMathUtil.doubleToLongBits(this.m01);
        bits = 31L * bits + VecMathUtil.doubleToLongBits(this.m02);
        bits = 31L * bits + VecMathUtil.doubleToLongBits(this.m10);
        bits = 31L * bits + VecMathUtil.doubleToLongBits(this.m11);
        bits = 31L * bits + VecMathUtil.doubleToLongBits(this.m12);
        bits = 31L * bits + VecMathUtil.doubleToLongBits(this.m20);
        bits = 31L * bits + VecMathUtil.doubleToLongBits(this.m21);
        bits = 31L * bits + VecMathUtil.doubleToLongBits(this.m22);
        return (int)(bits ^ bits >> 32);
    }
    
    public final void setZero() {
        this.m00 = 0.0;
        this.m01 = 0.0;
        this.m02 = 0.0;
        this.m10 = 0.0;
        this.m11 = 0.0;
        this.m12 = 0.0;
        this.m20 = 0.0;
        this.m21 = 0.0;
        this.m22 = 0.0;
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
    
    public final void negate(final Matrix3d m1) {
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
    
    public final void transform(final Tuple3d t) {
        final double x = this.m00 * t.x + this.m01 * t.y + this.m02 * t.z;
        final double y = this.m10 * t.x + this.m11 * t.y + this.m12 * t.z;
        final double z = this.m20 * t.x + this.m21 * t.y + this.m22 * t.z;
        t.set(x, y, z);
    }
    
    public final void transform(final Tuple3d t, final Tuple3d result) {
        final double x = this.m00 * t.x + this.m01 * t.y + this.m02 * t.z;
        final double y = this.m10 * t.x + this.m11 * t.y + this.m12 * t.z;
        result.z = this.m20 * t.x + this.m21 * t.y + this.m22 * t.z;
        result.x = x;
        result.y = y;
    }
    
    final void getScaleRotate(final double[] scales, final double[] rots) {
        final double[] tmp = { this.m00, this.m01, this.m02, this.m10, this.m11, this.m12, this.m20, this.m21, this.m22 };
        compute_svd(tmp, scales, rots);
    }
    
    static void compute_svd(final double[] m, final double[] outScale, final double[] outRot) {
        final double[] u1 = new double[9];
        final double[] v1 = new double[9];
        final double[] t1 = new double[9];
        final double[] t2 = new double[9];
        final double[] tmp = t1;
        final double[] single_values = t2;
        final double[] rot = new double[9];
        final double[] e = new double[3];
        final double[] scales = new double[3];
        int negCnt = 0;
        for (int i = 0; i < 9; ++i) {
            rot[i] = m[i];
        }
        if (m[3] * m[3] < 1.110223024E-16) {
            u1[0] = 1.0;
            u1[1] = 0.0;
            u1[3] = (u1[2] = 0.0);
            u1[4] = 1.0;
            u1[5] = 0.0;
            u1[7] = (u1[6] = 0.0);
            u1[8] = 1.0;
        }
        else if (m[0] * m[0] < 1.110223024E-16) {
            tmp[0] = m[0];
            tmp[1] = m[1];
            tmp[2] = m[2];
            m[0] = m[3];
            m[1] = m[4];
            m[2] = m[5];
            m[3] = -tmp[0];
            m[4] = -tmp[1];
            m[5] = -tmp[2];
            u1[0] = 0.0;
            u1[1] = 1.0;
            u1[2] = 0.0;
            u1[3] = -1.0;
            u1[5] = (u1[4] = 0.0);
            u1[7] = (u1[6] = 0.0);
            u1[8] = 1.0;
        }
        else {
            final double g = 1.0 / Math.sqrt(m[0] * m[0] + m[3] * m[3]);
            final double c1 = m[0] * g;
            final double s1 = m[3] * g;
            tmp[0] = c1 * m[0] + s1 * m[3];
            tmp[1] = c1 * m[1] + s1 * m[4];
            tmp[2] = c1 * m[2] + s1 * m[5];
            m[3] = -s1 * m[0] + c1 * m[3];
            m[4] = -s1 * m[1] + c1 * m[4];
            m[5] = -s1 * m[2] + c1 * m[5];
            m[0] = tmp[0];
            m[1] = tmp[1];
            m[2] = tmp[2];
            u1[0] = c1;
            u1[1] = s1;
            u1[2] = 0.0;
            u1[3] = -s1;
            u1[4] = c1;
            u1[5] = 0.0;
            u1[7] = (u1[6] = 0.0);
            u1[8] = 1.0;
        }
        if (m[6] * m[6] >= 1.110223024E-16) {
            if (m[0] * m[0] < 1.110223024E-16) {
                tmp[0] = m[0];
                tmp[1] = m[1];
                tmp[2] = m[2];
                m[0] = m[6];
                m[1] = m[7];
                m[2] = m[8];
                m[6] = -tmp[0];
                m[7] = -tmp[1];
                m[8] = -tmp[2];
                tmp[0] = u1[0];
                tmp[1] = u1[1];
                tmp[2] = u1[2];
                u1[0] = u1[6];
                u1[1] = u1[7];
                u1[2] = u1[8];
                u1[6] = -tmp[0];
                u1[7] = -tmp[1];
                u1[8] = -tmp[2];
            }
            else {
                final double g = 1.0 / Math.sqrt(m[0] * m[0] + m[6] * m[6]);
                final double c2 = m[0] * g;
                final double s2 = m[6] * g;
                tmp[0] = c2 * m[0] + s2 * m[6];
                tmp[1] = c2 * m[1] + s2 * m[7];
                tmp[2] = c2 * m[2] + s2 * m[8];
                m[6] = -s2 * m[0] + c2 * m[6];
                m[7] = -s2 * m[1] + c2 * m[7];
                m[8] = -s2 * m[2] + c2 * m[8];
                m[0] = tmp[0];
                m[1] = tmp[1];
                m[2] = tmp[2];
                tmp[0] = c2 * u1[0];
                tmp[1] = c2 * u1[1];
                u1[2] = s2;
                tmp[6] = -u1[0] * s2;
                tmp[7] = -u1[1] * s2;
                u1[8] = c2;
                u1[0] = tmp[0];
                u1[1] = tmp[1];
                u1[6] = tmp[6];
                u1[7] = tmp[7];
            }
        }
        if (m[2] * m[2] < 1.110223024E-16) {
            v1[0] = 1.0;
            v1[1] = 0.0;
            v1[3] = (v1[2] = 0.0);
            v1[4] = 1.0;
            v1[5] = 0.0;
            v1[7] = (v1[6] = 0.0);
            v1[8] = 1.0;
        }
        else if (m[1] * m[1] < 1.110223024E-16) {
            tmp[2] = m[2];
            tmp[5] = m[5];
            tmp[8] = m[8];
            m[2] = -m[1];
            m[5] = -m[4];
            m[8] = -m[7];
            m[1] = tmp[2];
            m[4] = tmp[5];
            m[7] = tmp[8];
            v1[0] = 1.0;
            v1[2] = (v1[1] = 0.0);
            v1[4] = (v1[3] = 0.0);
            v1[5] = -1.0;
            v1[6] = 0.0;
            v1[7] = 1.0;
            v1[8] = 0.0;
        }
        else {
            final double g = 1.0 / Math.sqrt(m[1] * m[1] + m[2] * m[2]);
            final double c3 = m[1] * g;
            final double s3 = m[2] * g;
            tmp[1] = c3 * m[1] + s3 * m[2];
            m[2] = -s3 * m[1] + c3 * m[2];
            m[1] = tmp[1];
            tmp[4] = c3 * m[4] + s3 * m[5];
            m[5] = -s3 * m[4] + c3 * m[5];
            m[4] = tmp[4];
            tmp[7] = c3 * m[7] + s3 * m[8];
            m[8] = -s3 * m[7] + c3 * m[8];
            m[7] = tmp[7];
            v1[0] = 1.0;
            v1[1] = 0.0;
            v1[3] = (v1[2] = 0.0);
            v1[4] = c3;
            v1[5] = -s3;
            v1[6] = 0.0;
            v1[7] = s3;
            v1[8] = c3;
        }
        if (m[7] * m[7] >= 1.110223024E-16) {
            if (m[4] * m[4] < 1.110223024E-16) {
                tmp[3] = m[3];
                tmp[4] = m[4];
                tmp[5] = m[5];
                m[3] = m[6];
                m[4] = m[7];
                m[5] = m[8];
                m[6] = -tmp[3];
                m[7] = -tmp[4];
                m[8] = -tmp[5];
                tmp[3] = u1[3];
                tmp[4] = u1[4];
                tmp[5] = u1[5];
                u1[3] = u1[6];
                u1[4] = u1[7];
                u1[5] = u1[8];
                u1[6] = -tmp[3];
                u1[7] = -tmp[4];
                u1[8] = -tmp[5];
            }
            else {
                final double g = 1.0 / Math.sqrt(m[4] * m[4] + m[7] * m[7]);
                final double c4 = m[4] * g;
                final double s4 = m[7] * g;
                tmp[3] = c4 * m[3] + s4 * m[6];
                m[6] = -s4 * m[3] + c4 * m[6];
                m[3] = tmp[3];
                tmp[4] = c4 * m[4] + s4 * m[7];
                m[7] = -s4 * m[4] + c4 * m[7];
                m[4] = tmp[4];
                tmp[5] = c4 * m[5] + s4 * m[8];
                m[8] = -s4 * m[5] + c4 * m[8];
                m[5] = tmp[5];
                tmp[3] = c4 * u1[3] + s4 * u1[6];
                u1[6] = -s4 * u1[3] + c4 * u1[6];
                u1[3] = tmp[3];
                tmp[4] = c4 * u1[4] + s4 * u1[7];
                u1[7] = -s4 * u1[4] + c4 * u1[7];
                u1[4] = tmp[4];
                tmp[5] = c4 * u1[5] + s4 * u1[8];
                u1[8] = -s4 * u1[5] + c4 * u1[8];
                u1[5] = tmp[5];
            }
        }
        single_values[0] = m[0];
        single_values[1] = m[4];
        single_values[2] = m[8];
        e[0] = m[1];
        e[1] = m[5];
        if (e[0] * e[0] >= 1.110223024E-16 || e[1] * e[1] >= 1.110223024E-16) {
            compute_qr(single_values, e, u1, v1);
        }
        scales[0] = single_values[0];
        scales[1] = single_values[1];
        scales[2] = single_values[2];
        if (almostEqual(Math.abs(scales[0]), 1.0) && almostEqual(Math.abs(scales[1]), 1.0) && almostEqual(Math.abs(scales[2]), 1.0)) {
            for (int i = 0; i < 3; ++i) {
                if (scales[i] < 0.0) {
                    ++negCnt;
                }
            }
            if (negCnt == 0 || negCnt == 2) {
                final int n = 0;
                final int n2 = 1;
                final int n3 = 2;
                final double n4 = 1.0;
                outScale[n3] = n4;
                outScale[n] = (outScale[n2] = n4);
                for (int i = 0; i < 9; ++i) {
                    outRot[i] = rot[i];
                }
                return;
            }
        }
        transpose_mat(u1, t1);
        transpose_mat(v1, t2);
        svdReorder(m, t1, t2, scales, outRot, outScale);
    }
    
    static void svdReorder(final double[] m, final double[] t1, final double[] t2, final double[] scales, final double[] outRot, final double[] outScale) {
        final int[] out = new int[3];
        final int[] in = new int[3];
        final double[] mag = new double[3];
        final double[] rot = new double[9];
        if (scales[0] < 0.0) {
            scales[0] = -scales[0];
            t2[0] = -t2[0];
            t2[1] = -t2[1];
            t2[2] = -t2[2];
        }
        if (scales[1] < 0.0) {
            scales[1] = -scales[1];
            t2[3] = -t2[3];
            t2[4] = -t2[4];
            t2[5] = -t2[5];
        }
        if (scales[2] < 0.0) {
            scales[2] = -scales[2];
            t2[6] = -t2[6];
            t2[7] = -t2[7];
            t2[8] = -t2[8];
        }
        mat_mul(t1, t2, rot);
        if (almostEqual(Math.abs(scales[0]), Math.abs(scales[1])) && almostEqual(Math.abs(scales[1]), Math.abs(scales[2]))) {
            for (int i = 0; i < 9; ++i) {
                outRot[i] = rot[i];
            }
            for (int i = 0; i < 3; ++i) {
                outScale[i] = scales[i];
            }
        }
        else {
            if (scales[0] > scales[1]) {
                if (scales[0] > scales[2]) {
                    if (scales[2] > scales[1]) {
                        out[0] = 0;
                        out[out[1] = 2] = 1;
                    }
                    else {
                        out[0] = 0;
                        out[1] = 1;
                        out[2] = 2;
                    }
                }
                else {
                    out[0] = 2;
                    out[1] = 0;
                    out[2] = 1;
                }
            }
            else if (scales[1] > scales[2]) {
                if (scales[2] > scales[0]) {
                    out[0] = 1;
                    out[out[1] = 2] = 0;
                }
                else {
                    out[out[0] = 1] = 0;
                    out[2] = 2;
                }
            }
            else {
                out[0] = 2;
                out[1] = 1;
                out[2] = 0;
            }
            mag[0] = m[0] * m[0] + m[1] * m[1] + m[2] * m[2];
            mag[1] = m[3] * m[3] + m[4] * m[4] + m[5] * m[5];
            mag[2] = m[6] * m[6] + m[7] * m[7] + m[8] * m[8];
            int in2;
            int in3;
            int in4;
            if (mag[0] > mag[1]) {
                if (mag[0] > mag[2]) {
                    if (mag[2] > mag[1]) {
                        in2 = 0;
                        in3 = 1;
                        in4 = 2;
                    }
                    else {
                        in2 = 0;
                        in4 = 1;
                        in3 = 2;
                    }
                }
                else {
                    in3 = 0;
                    in2 = 1;
                    in4 = 2;
                }
            }
            else if (mag[1] > mag[2]) {
                if (mag[2] > mag[0]) {
                    in4 = 0;
                    in3 = 1;
                    in2 = 2;
                }
                else {
                    in4 = 0;
                    in2 = 1;
                    in3 = 2;
                }
            }
            else {
                in3 = 0;
                in4 = 1;
                in2 = 2;
            }
            int index = out[in2];
            outScale[0] = scales[index];
            index = out[in4];
            outScale[1] = scales[index];
            index = out[in3];
            outScale[2] = scales[index];
            index = out[in2];
            outRot[0] = rot[index];
            index = out[in2] + 3;
            outRot[3] = rot[index];
            index = out[in2] + 6;
            outRot[6] = rot[index];
            index = out[in4];
            outRot[1] = rot[index];
            index = out[in4] + 3;
            outRot[4] = rot[index];
            index = out[in4] + 6;
            outRot[7] = rot[index];
            index = out[in3];
            outRot[2] = rot[index];
            index = out[in3] + 3;
            outRot[5] = rot[index];
            index = out[in3] + 6;
            outRot[8] = rot[index];
        }
    }
    
    static int compute_qr(final double[] s, final double[] e, final double[] u, final double[] v) {
        final double[] cosl = new double[2];
        final double[] cosr = new double[2];
        final double[] sinl = new double[2];
        final double[] sinr = new double[2];
        final double[] m = new double[9];
        final int MAX_INTERATIONS = 10;
        final double CONVERGE_TOL = 4.89E-15;
        final double c_b48 = 1.0;
        final double c_b49 = -1.0;
        boolean converged = false;
        int first = 1;
        if (Math.abs(e[1]) < 4.89E-15 || Math.abs(e[0]) < 4.89E-15) {
            converged = true;
        }
        for (int k = 0; k < 10 && !converged; ++k) {
            final double shift = compute_shift(s[1], e[1], s[2]);
            double f = (Math.abs(s[0]) - shift) * (d_sign(c_b48, s[0]) + shift / s[0]);
            double g = e[0];
            double r = compute_rot(f, g, sinr, cosr, 0, first);
            f = cosr[0] * s[0] + sinr[0] * e[0];
            e[0] = cosr[0] * e[0] - sinr[0] * s[0];
            g = sinr[0] * s[1];
            s[1] *= cosr[0];
            r = compute_rot(f, g, sinl, cosl, 0, first);
            first = 0;
            s[0] = r;
            f = cosl[0] * e[0] + sinl[0] * s[1];
            s[1] = cosl[0] * s[1] - sinl[0] * e[0];
            g = sinl[0] * e[1];
            e[1] *= cosl[0];
            r = compute_rot(f, g, sinr, cosr, 1, first);
            e[0] = r;
            f = cosr[1] * s[1] + sinr[1] * e[1];
            e[1] = cosr[1] * e[1] - sinr[1] * s[1];
            g = sinr[1] * s[2];
            s[2] *= cosr[1];
            r = compute_rot(f, g, sinl, cosl, 1, first);
            s[1] = r;
            f = cosl[1] * e[1] + sinl[1] * s[2];
            s[2] = cosl[1] * s[2] - sinl[1] * e[1];
            e[1] = f;
            double utemp = u[0];
            u[0] = cosl[0] * utemp + sinl[0] * u[3];
            u[3] = -sinl[0] * utemp + cosl[0] * u[3];
            utemp = u[1];
            u[1] = cosl[0] * utemp + sinl[0] * u[4];
            u[4] = -sinl[0] * utemp + cosl[0] * u[4];
            utemp = u[2];
            u[2] = cosl[0] * utemp + sinl[0] * u[5];
            u[5] = -sinl[0] * utemp + cosl[0] * u[5];
            utemp = u[3];
            u[3] = cosl[1] * utemp + sinl[1] * u[6];
            u[6] = -sinl[1] * utemp + cosl[1] * u[6];
            utemp = u[4];
            u[4] = cosl[1] * utemp + sinl[1] * u[7];
            u[7] = -sinl[1] * utemp + cosl[1] * u[7];
            utemp = u[5];
            u[5] = cosl[1] * utemp + sinl[1] * u[8];
            u[8] = -sinl[1] * utemp + cosl[1] * u[8];
            double vtemp = v[0];
            v[0] = cosr[0] * vtemp + sinr[0] * v[1];
            v[1] = -sinr[0] * vtemp + cosr[0] * v[1];
            vtemp = v[3];
            v[3] = cosr[0] * vtemp + sinr[0] * v[4];
            v[4] = -sinr[0] * vtemp + cosr[0] * v[4];
            vtemp = v[6];
            v[6] = cosr[0] * vtemp + sinr[0] * v[7];
            v[7] = -sinr[0] * vtemp + cosr[0] * v[7];
            vtemp = v[1];
            v[1] = cosr[1] * vtemp + sinr[1] * v[2];
            v[2] = -sinr[1] * vtemp + cosr[1] * v[2];
            vtemp = v[4];
            v[4] = cosr[1] * vtemp + sinr[1] * v[5];
            v[5] = -sinr[1] * vtemp + cosr[1] * v[5];
            vtemp = v[7];
            v[7] = cosr[1] * vtemp + sinr[1] * v[8];
            v[8] = -sinr[1] * vtemp + cosr[1] * v[8];
            m[0] = s[0];
            m[1] = e[0];
            m[3] = (m[2] = 0.0);
            m[4] = s[1];
            m[5] = e[1];
            m[7] = (m[6] = 0.0);
            m[8] = s[2];
            if (Math.abs(e[1]) < 4.89E-15 || Math.abs(e[0]) < 4.89E-15) {
                converged = true;
            }
        }
        if (Math.abs(e[1]) < 4.89E-15) {
            compute_2X2(s[0], e[0], s[1], s, sinl, cosl, sinr, cosr, 0);
            double utemp = u[0];
            u[0] = cosl[0] * utemp + sinl[0] * u[3];
            u[3] = -sinl[0] * utemp + cosl[0] * u[3];
            utemp = u[1];
            u[1] = cosl[0] * utemp + sinl[0] * u[4];
            u[4] = -sinl[0] * utemp + cosl[0] * u[4];
            utemp = u[2];
            u[2] = cosl[0] * utemp + sinl[0] * u[5];
            u[5] = -sinl[0] * utemp + cosl[0] * u[5];
            double vtemp = v[0];
            v[0] = cosr[0] * vtemp + sinr[0] * v[1];
            v[1] = -sinr[0] * vtemp + cosr[0] * v[1];
            vtemp = v[3];
            v[3] = cosr[0] * vtemp + sinr[0] * v[4];
            v[4] = -sinr[0] * vtemp + cosr[0] * v[4];
            vtemp = v[6];
            v[6] = cosr[0] * vtemp + sinr[0] * v[7];
            v[7] = -sinr[0] * vtemp + cosr[0] * v[7];
        }
        else {
            compute_2X2(s[1], e[1], s[2], s, sinl, cosl, sinr, cosr, 1);
            double utemp = u[3];
            u[3] = cosl[0] * utemp + sinl[0] * u[6];
            u[6] = -sinl[0] * utemp + cosl[0] * u[6];
            utemp = u[4];
            u[4] = cosl[0] * utemp + sinl[0] * u[7];
            u[7] = -sinl[0] * utemp + cosl[0] * u[7];
            utemp = u[5];
            u[5] = cosl[0] * utemp + sinl[0] * u[8];
            u[8] = -sinl[0] * utemp + cosl[0] * u[8];
            double vtemp = v[1];
            v[1] = cosr[0] * vtemp + sinr[0] * v[2];
            v[2] = -sinr[0] * vtemp + cosr[0] * v[2];
            vtemp = v[4];
            v[4] = cosr[0] * vtemp + sinr[0] * v[5];
            v[5] = -sinr[0] * vtemp + cosr[0] * v[5];
            vtemp = v[7];
            v[7] = cosr[0] * vtemp + sinr[0] * v[8];
            v[8] = -sinr[0] * vtemp + cosr[0] * v[8];
        }
        return 0;
    }
    
    static double max(final double a, final double b) {
        if (a > b) {
            return a;
        }
        return b;
    }
    
    static double min(final double a, final double b) {
        if (a < b) {
            return a;
        }
        return b;
    }
    
    static double d_sign(final double a, final double b) {
        final double x = (a >= 0.0) ? a : (-a);
        return (b >= 0.0) ? x : (-x);
    }
    
    static double compute_shift(final double f, final double g, final double h) {
        final double fa = Math.abs(f);
        final double ga = Math.abs(g);
        final double ha = Math.abs(h);
        final double fhmn = min(fa, ha);
        final double fhmx = max(fa, ha);
        double ssmin;
        if (fhmn == 0.0) {
            ssmin = 0.0;
            if (fhmx != 0.0) {
                final double n = min(fhmx, ga) / max(fhmx, ga);
            }
        }
        else if (ga < fhmx) {
            final double as = fhmn / fhmx + 1.0;
            final double at = (fhmx - fhmn) / fhmx;
            final double d__1 = ga / fhmx;
            final double au = d__1 * d__1;
            final double c = 2.0 / (Math.sqrt(as * as + au) + Math.sqrt(at * at + au));
            ssmin = fhmn * c;
        }
        else {
            final double au = fhmx / ga;
            if (au == 0.0) {
                ssmin = fhmn * fhmx / ga;
            }
            else {
                final double as = fhmn / fhmx + 1.0;
                final double at = (fhmx - fhmn) / fhmx;
                final double d__1 = as * au;
                final double d__2 = at * au;
                final double c = 1.0 / (Math.sqrt(d__1 * d__1 + 1.0) + Math.sqrt(d__2 * d__2 + 1.0));
                ssmin = fhmn * c * au;
                ssmin += ssmin;
            }
        }
        return ssmin;
    }
    
    static int compute_2X2(final double f, final double g, final double h, final double[] single_values, final double[] snl, final double[] csl, final double[] snr, final double[] csr, final int index) {
        final double c_b3 = 2.0;
        final double c_b4 = 1.0;
        double ssmax = single_values[0];
        double ssmin = single_values[1];
        double clt = 0.0;
        double crt = 0.0;
        double slt = 0.0;
        double srt = 0.0;
        double tsign = 0.0;
        double ft = f;
        double fa = Math.abs(ft);
        double ht = h;
        double ha = Math.abs(h);
        int pmax = 1;
        final boolean swap = ha > fa;
        if (swap) {
            pmax = 3;
            double temp = ft;
            ft = ht;
            ht = temp;
            temp = fa;
            fa = ha;
            ha = temp;
        }
        final double gt = g;
        final double ga = Math.abs(gt);
        if (ga == 0.0) {
            single_values[1] = ha;
            single_values[0] = fa;
            clt = 1.0;
            crt = 1.0;
            slt = 0.0;
            srt = 0.0;
        }
        else {
            boolean gasmal = true;
            if (ga > fa) {
                pmax = 2;
                if (fa / ga < 1.110223024E-16) {
                    gasmal = false;
                    ssmax = ga;
                    if (ha > 1.0) {
                        ssmin = fa / (ga / ha);
                    }
                    else {
                        ssmin = fa / ga * ha;
                    }
                    clt = 1.0;
                    slt = ht / gt;
                    srt = 1.0;
                    crt = ft / gt;
                }
            }
            if (gasmal) {
                double d = fa - ha;
                double l;
                if (d == fa) {
                    l = 1.0;
                }
                else {
                    l = d / fa;
                }
                double m = gt / ft;
                double t = 2.0 - l;
                double mm = m * m;
                double tt = t * t;
                double s = Math.sqrt(tt + mm);
                double r;
                if (l == 0.0) {
                    r = Math.abs(m);
                }
                else {
                    r = Math.sqrt(l * l + mm);
                }
                double a = (s + r) * 0.5;
                if (ga > fa) {
                    pmax = 2;
                    if (fa / ga < 1.110223024E-16) {
                        gasmal = false;
                        ssmax = ga;
                        if (ha > 1.0) {
                            ssmin = fa / (ga / ha);
                        }
                        else {
                            ssmin = fa / ga * ha;
                        }
                        clt = 1.0;
                        slt = ht / gt;
                        srt = 1.0;
                        crt = ft / gt;
                    }
                }
                if (gasmal) {
                    d = fa - ha;
                    if (d == fa) {
                        l = 1.0;
                    }
                    else {
                        l = d / fa;
                    }
                    m = gt / ft;
                    t = 2.0 - l;
                    mm = m * m;
                    tt = t * t;
                    s = Math.sqrt(tt + mm);
                    if (l == 0.0) {
                        r = Math.abs(m);
                    }
                    else {
                        r = Math.sqrt(l * l + mm);
                    }
                    a = (s + r) * 0.5;
                    ssmin = ha / a;
                    ssmax = fa * a;
                    if (mm == 0.0) {
                        if (l == 0.0) {
                            t = d_sign(c_b3, ft) * d_sign(c_b4, gt);
                        }
                        else {
                            t = gt / d_sign(d, ft) + m / t;
                        }
                    }
                    else {
                        t = (m / (s + t) + m / (r + l)) * (a + 1.0);
                    }
                    l = Math.sqrt(t * t + 4.0);
                    crt = 2.0 / l;
                    srt = t / l;
                    clt = (crt + srt * m) / a;
                    slt = ht / ft * srt / a;
                }
            }
            if (swap) {
                csl[0] = srt;
                snl[0] = crt;
                csr[0] = slt;
                snr[0] = clt;
            }
            else {
                csl[0] = clt;
                snl[0] = slt;
                csr[0] = crt;
                snr[0] = srt;
            }
            if (pmax == 1) {
                tsign = d_sign(c_b4, csr[0]) * d_sign(c_b4, csl[0]) * d_sign(c_b4, f);
            }
            if (pmax == 2) {
                tsign = d_sign(c_b4, snr[0]) * d_sign(c_b4, csl[0]) * d_sign(c_b4, g);
            }
            if (pmax == 3) {
                tsign = d_sign(c_b4, snr[0]) * d_sign(c_b4, snl[0]) * d_sign(c_b4, h);
            }
            single_values[index] = d_sign(ssmax, tsign);
            final double d__1 = tsign * d_sign(c_b4, f) * d_sign(c_b4, h);
            single_values[index + 1] = d_sign(ssmin, d__1);
        }
        return 0;
    }
    
    static double compute_rot(final double f, final double g, final double[] sin, final double[] cos, final int index, final int first) {
        final double safmn2 = 2.002083095183101E-146;
        final double safmx2 = 4.9947976805055876E145;
        double cs;
        double sn;
        double r;
        if (g == 0.0) {
            cs = 1.0;
            sn = 0.0;
            r = f;
        }
        else if (f == 0.0) {
            cs = 0.0;
            sn = 1.0;
            r = g;
        }
        else {
            double f2 = f;
            double g2 = g;
            double scale = max(Math.abs(f2), Math.abs(g2));
            if (scale >= 4.9947976805055876E145) {
                int count = 0;
                while (scale >= 4.9947976805055876E145) {
                    ++count;
                    f2 *= 2.002083095183101E-146;
                    g2 *= 2.002083095183101E-146;
                    scale = max(Math.abs(f2), Math.abs(g2));
                }
                r = Math.sqrt(f2 * f2 + g2 * g2);
                cs = f2 / r;
                sn = g2 / r;
                final int i__1 = count;
                for (int i = 1; i <= count; ++i) {
                    r *= 4.9947976805055876E145;
                }
            }
            else if (scale <= 2.002083095183101E-146) {
                int count = 0;
                while (scale <= 2.002083095183101E-146) {
                    ++count;
                    f2 *= 4.9947976805055876E145;
                    g2 *= 4.9947976805055876E145;
                    scale = max(Math.abs(f2), Math.abs(g2));
                }
                r = Math.sqrt(f2 * f2 + g2 * g2);
                cs = f2 / r;
                sn = g2 / r;
                final int i__1 = count;
                for (int i = 1; i <= count; ++i) {
                    r *= 2.002083095183101E-146;
                }
            }
            else {
                r = Math.sqrt(f2 * f2 + g2 * g2);
                cs = f2 / r;
                sn = g2 / r;
            }
            if (Math.abs(f) > Math.abs(g) && cs < 0.0) {
                cs = -cs;
                sn = -sn;
                r = -r;
            }
        }
        sin[index] = sn;
        cos[index] = cs;
        return r;
    }
    
    static void print_mat(final double[] mat) {
        for (int i = 0; i < 3; ++i) {
            System.out.println(mat[i * 3 + 0] + " " + mat[i * 3 + 1] + " " + mat[i * 3 + 2] + "\n");
        }
    }
    
    static void print_det(final double[] mat) {
        final double det = mat[0] * mat[4] * mat[8] + mat[1] * mat[5] * mat[6] + mat[2] * mat[3] * mat[7] - mat[2] * mat[4] * mat[6] - mat[0] * mat[5] * mat[7] - mat[1] * mat[3] * mat[8];
        System.out.println("det= " + det);
    }
    
    static void mat_mul(final double[] m1, final double[] m2, final double[] m3) {
        final double[] tmp = { m1[0] * m2[0] + m1[1] * m2[3] + m1[2] * m2[6], m1[0] * m2[1] + m1[1] * m2[4] + m1[2] * m2[7], m1[0] * m2[2] + m1[1] * m2[5] + m1[2] * m2[8], m1[3] * m2[0] + m1[4] * m2[3] + m1[5] * m2[6], m1[3] * m2[1] + m1[4] * m2[4] + m1[5] * m2[7], m1[3] * m2[2] + m1[4] * m2[5] + m1[5] * m2[8], m1[6] * m2[0] + m1[7] * m2[3] + m1[8] * m2[6], m1[6] * m2[1] + m1[7] * m2[4] + m1[8] * m2[7], m1[6] * m2[2] + m1[7] * m2[5] + m1[8] * m2[8] };
        for (int i = 0; i < 9; ++i) {
            m3[i] = tmp[i];
        }
    }
    
    static void transpose_mat(final double[] in, final double[] out) {
        out[0] = in[0];
        out[1] = in[3];
        out[2] = in[6];
        out[3] = in[1];
        out[4] = in[4];
        out[5] = in[7];
        out[6] = in[2];
        out[7] = in[5];
        out[8] = in[8];
    }
    
    static double max3(final double[] values) {
        if (values[0] > values[1]) {
            if (values[0] > values[2]) {
                return values[0];
            }
            return values[2];
        }
        else {
            if (values[1] > values[2]) {
                return values[1];
            }
            return values[2];
        }
    }
    
    private static final boolean almostEqual(final double a, final double b) {
        if (a == b) {
            return true;
        }
        final double EPSILON_ABSOLUTE = 1.0E-6;
        final double EPSILON_RELATIVE = 1.0E-4;
        final double diff = Math.abs(a - b);
        final double absA = Math.abs(a);
        final double absB = Math.abs(b);
        final double max = (absA >= absB) ? absA : absB;
        return diff < 1.0E-6 || diff / max < 1.0E-4;
    }
    
    public Object clone() {
        Matrix3d m1 = null;
        try {
            m1 = (Matrix3d)super.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new InternalError();
        }
        return m1;
    }
    
    public final double getM00() {
        return this.m00;
    }
    
    public final void setM00(final double m00) {
        this.m00 = m00;
    }
    
    public final double getM01() {
        return this.m01;
    }
    
    public final void setM01(final double m01) {
        this.m01 = m01;
    }
    
    public final double getM02() {
        return this.m02;
    }
    
    public final void setM02(final double m02) {
        this.m02 = m02;
    }
    
    public final double getM10() {
        return this.m10;
    }
    
    public final void setM10(final double m10) {
        this.m10 = m10;
    }
    
    public final double getM11() {
        return this.m11;
    }
    
    public final void setM11(final double m11) {
        this.m11 = m11;
    }
    
    public final double getM12() {
        return this.m12;
    }
    
    public final void setM12(final double m12) {
        this.m12 = m12;
    }
    
    public final double getM20() {
        return this.m20;
    }
    
    public final void setM20(final double m20) {
        this.m20 = m20;
    }
    
    public final double getM21() {
        return this.m21;
    }
    
    public final void setM21(final double m21) {
        this.m21 = m21;
    }
    
    public final double getM22() {
        return this.m22;
    }
    
    public final void setM22(final double m22) {
        this.m22 = m22;
    }
}
