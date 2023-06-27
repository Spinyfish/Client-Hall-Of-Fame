package javax.vecmath;

import java.io.*;

public class GVector implements Serializable, Cloneable
{
    private int length;
    double[] values;
    static final long serialVersionUID = 1398850036893875112L;
    
    public GVector(final int length) {
        this.length = length;
        this.values = new double[length];
        for (int i = 0; i < length; ++i) {
            this.values[i] = 0.0;
        }
    }
    
    public GVector(final double[] vector) {
        this.length = vector.length;
        this.values = new double[vector.length];
        for (int i = 0; i < this.length; ++i) {
            this.values[i] = vector[i];
        }
    }
    
    public GVector(final GVector vector) {
        this.values = new double[vector.length];
        this.length = vector.length;
        for (int i = 0; i < this.length; ++i) {
            this.values[i] = vector.values[i];
        }
    }
    
    public GVector(final Tuple2f tuple) {
        (this.values = new double[2])[0] = tuple.x;
        this.values[1] = tuple.y;
        this.length = 2;
    }
    
    public GVector(final Tuple3f tuple) {
        (this.values = new double[3])[0] = tuple.x;
        this.values[1] = tuple.y;
        this.values[2] = tuple.z;
        this.length = 3;
    }
    
    public GVector(final Tuple3d tuple) {
        (this.values = new double[3])[0] = tuple.x;
        this.values[1] = tuple.y;
        this.values[2] = tuple.z;
        this.length = 3;
    }
    
    public GVector(final Tuple4f tuple) {
        (this.values = new double[4])[0] = tuple.x;
        this.values[1] = tuple.y;
        this.values[2] = tuple.z;
        this.values[3] = tuple.w;
        this.length = 4;
    }
    
    public GVector(final Tuple4d tuple) {
        (this.values = new double[4])[0] = tuple.x;
        this.values[1] = tuple.y;
        this.values[2] = tuple.z;
        this.values[3] = tuple.w;
        this.length = 4;
    }
    
    public GVector(final double[] vector, final int length) {
        this.length = length;
        this.values = new double[length];
        for (int i = 0; i < length; ++i) {
            this.values[i] = vector[i];
        }
    }
    
    public final double norm() {
        double sq = 0.0;
        for (int i = 0; i < this.length; ++i) {
            sq += this.values[i] * this.values[i];
        }
        return Math.sqrt(sq);
    }
    
    public final double normSquared() {
        double sq = 0.0;
        for (int i = 0; i < this.length; ++i) {
            sq += this.values[i] * this.values[i];
        }
        return sq;
    }
    
    public final void normalize(final GVector v1) {
        double sq = 0.0;
        if (this.length != v1.length) {
            throw new MismatchedSizeException(VecMathI18N.getString("GVector0"));
        }
        for (int i = 0; i < this.length; ++i) {
            sq += v1.values[i] * v1.values[i];
        }
        final double invMag = 1.0 / Math.sqrt(sq);
        for (int i = 0; i < this.length; ++i) {
            this.values[i] = v1.values[i] * invMag;
        }
    }
    
    public final void normalize() {
        double sq = 0.0;
        for (int i = 0; i < this.length; ++i) {
            sq += this.values[i] * this.values[i];
        }
        final double invMag = 1.0 / Math.sqrt(sq);
        for (int i = 0; i < this.length; ++i) {
            this.values[i] *= invMag;
        }
    }
    
    public final void scale(final double s, final GVector v1) {
        if (this.length != v1.length) {
            throw new MismatchedSizeException(VecMathI18N.getString("GVector1"));
        }
        for (int i = 0; i < this.length; ++i) {
            this.values[i] = v1.values[i] * s;
        }
    }
    
    public final void scale(final double s) {
        for (int i = 0; i < this.length; ++i) {
            this.values[i] *= s;
        }
    }
    
    public final void scaleAdd(final double s, final GVector v1, final GVector v2) {
        if (v2.length != v1.length) {
            throw new MismatchedSizeException(VecMathI18N.getString("GVector2"));
        }
        if (this.length != v1.length) {
            throw new MismatchedSizeException(VecMathI18N.getString("GVector3"));
        }
        for (int i = 0; i < this.length; ++i) {
            this.values[i] = v1.values[i] * s + v2.values[i];
        }
    }
    
    public final void add(final GVector vector) {
        if (this.length != vector.length) {
            throw new MismatchedSizeException(VecMathI18N.getString("GVector4"));
        }
        for (int i = 0; i < this.length; ++i) {
            final double[] values = this.values;
            final int n = i;
            values[n] += vector.values[i];
        }
    }
    
    public final void add(final GVector vector1, final GVector vector2) {
        if (vector1.length != vector2.length) {
            throw new MismatchedSizeException(VecMathI18N.getString("GVector5"));
        }
        if (this.length != vector1.length) {
            throw new MismatchedSizeException(VecMathI18N.getString("GVector6"));
        }
        for (int i = 0; i < this.length; ++i) {
            this.values[i] = vector1.values[i] + vector2.values[i];
        }
    }
    
    public final void sub(final GVector vector) {
        if (this.length != vector.length) {
            throw new MismatchedSizeException(VecMathI18N.getString("GVector7"));
        }
        for (int i = 0; i < this.length; ++i) {
            final double[] values = this.values;
            final int n = i;
            values[n] -= vector.values[i];
        }
    }
    
    public final void sub(final GVector vector1, final GVector vector2) {
        if (vector1.length != vector2.length) {
            throw new MismatchedSizeException(VecMathI18N.getString("GVector8"));
        }
        if (this.length != vector1.length) {
            throw new MismatchedSizeException(VecMathI18N.getString("GVector9"));
        }
        for (int i = 0; i < this.length; ++i) {
            this.values[i] = vector1.values[i] - vector2.values[i];
        }
    }
    
    public final void mul(final GMatrix m1, final GVector v1) {
        if (m1.getNumCol() != v1.length) {
            throw new MismatchedSizeException(VecMathI18N.getString("GVector10"));
        }
        if (this.length != m1.getNumRow()) {
            throw new MismatchedSizeException(VecMathI18N.getString("GVector11"));
        }
        double[] v2;
        if (v1 != this) {
            v2 = v1.values;
        }
        else {
            v2 = this.values.clone();
        }
        for (int j = this.length - 1; j >= 0; --j) {
            this.values[j] = 0.0;
            for (int i = v1.length - 1; i >= 0; --i) {
                final double[] values = this.values;
                final int n = j;
                values[n] += m1.values[j][i] * v2[i];
            }
        }
    }
    
    public final void mul(final GVector v1, final GMatrix m1) {
        if (m1.getNumRow() != v1.length) {
            throw new MismatchedSizeException(VecMathI18N.getString("GVector12"));
        }
        if (this.length != m1.getNumCol()) {
            throw new MismatchedSizeException(VecMathI18N.getString("GVector13"));
        }
        double[] v2;
        if (v1 != this) {
            v2 = v1.values;
        }
        else {
            v2 = this.values.clone();
        }
        for (int j = this.length - 1; j >= 0; --j) {
            this.values[j] = 0.0;
            for (int i = v1.length - 1; i >= 0; --i) {
                final double[] values = this.values;
                final int n = j;
                values[n] += m1.values[i][j] * v2[i];
            }
        }
    }
    
    public final void negate() {
        for (int i = this.length - 1; i >= 0; --i) {
            final double[] values = this.values;
            final int n = i;
            values[n] *= -1.0;
        }
    }
    
    public final void zero() {
        for (int i = 0; i < this.length; ++i) {
            this.values[i] = 0.0;
        }
    }
    
    public final void setSize(final int length) {
        final double[] tmp = new double[length];
        int max;
        if (this.length < length) {
            max = this.length;
        }
        else {
            max = length;
        }
        for (int i = 0; i < max; ++i) {
            tmp[i] = this.values[i];
        }
        this.length = length;
        this.values = tmp;
    }
    
    public final void set(final double[] vector) {
        for (int i = this.length - 1; i >= 0; --i) {
            this.values[i] = vector[i];
        }
    }
    
    public final void set(final GVector vector) {
        if (this.length < vector.length) {
            this.length = vector.length;
            this.values = new double[this.length];
            for (int i = 0; i < this.length; ++i) {
                this.values[i] = vector.values[i];
            }
        }
        else {
            for (int i = 0; i < vector.length; ++i) {
                this.values[i] = vector.values[i];
            }
            for (int i = vector.length; i < this.length; ++i) {
                this.values[i] = 0.0;
            }
        }
    }
    
    public final void set(final Tuple2f tuple) {
        if (this.length < 2) {
            this.length = 2;
            this.values = new double[2];
        }
        this.values[0] = tuple.x;
        this.values[1] = tuple.y;
        for (int i = 2; i < this.length; ++i) {
            this.values[i] = 0.0;
        }
    }
    
    public final void set(final Tuple3f tuple) {
        if (this.length < 3) {
            this.length = 3;
            this.values = new double[3];
        }
        this.values[0] = tuple.x;
        this.values[1] = tuple.y;
        this.values[2] = tuple.z;
        for (int i = 3; i < this.length; ++i) {
            this.values[i] = 0.0;
        }
    }
    
    public final void set(final Tuple3d tuple) {
        if (this.length < 3) {
            this.length = 3;
            this.values = new double[3];
        }
        this.values[0] = tuple.x;
        this.values[1] = tuple.y;
        this.values[2] = tuple.z;
        for (int i = 3; i < this.length; ++i) {
            this.values[i] = 0.0;
        }
    }
    
    public final void set(final Tuple4f tuple) {
        if (this.length < 4) {
            this.length = 4;
            this.values = new double[4];
        }
        this.values[0] = tuple.x;
        this.values[1] = tuple.y;
        this.values[2] = tuple.z;
        this.values[3] = tuple.w;
        for (int i = 4; i < this.length; ++i) {
            this.values[i] = 0.0;
        }
    }
    
    public final void set(final Tuple4d tuple) {
        if (this.length < 4) {
            this.length = 4;
            this.values = new double[4];
        }
        this.values[0] = tuple.x;
        this.values[1] = tuple.y;
        this.values[2] = tuple.z;
        this.values[3] = tuple.w;
        for (int i = 4; i < this.length; ++i) {
            this.values[i] = 0.0;
        }
    }
    
    public final int getSize() {
        return this.values.length;
    }
    
    public final double getElement(final int index) {
        return this.values[index];
    }
    
    public final void setElement(final int index, final double value) {
        this.values[index] = value;
    }
    
    @Override
    public String toString() {
        final StringBuffer buffer = new StringBuffer(this.length * 8);
        for (int i = 0; i < this.length; ++i) {
            buffer.append(this.values[i]).append(" ");
        }
        return buffer.toString();
    }
    
    @Override
    public int hashCode() {
        long bits = 1L;
        for (int i = 0; i < this.length; ++i) {
            bits = 31L * bits + VecMathUtil.doubleToLongBits(this.values[i]);
        }
        return (int)(bits ^ bits >> 32);
    }
    
    public boolean equals(final GVector vector1) {
        try {
            if (this.length != vector1.length) {
                return false;
            }
            for (int i = 0; i < this.length; ++i) {
                if (this.values[i] != vector1.values[i]) {
                    return false;
                }
            }
            return true;
        }
        catch (NullPointerException e2) {
            return false;
        }
    }
    
    @Override
    public boolean equals(final Object o1) {
        try {
            final GVector v2 = (GVector)o1;
            if (this.length != v2.length) {
                return false;
            }
            for (int i = 0; i < this.length; ++i) {
                if (this.values[i] != v2.values[i]) {
                    return false;
                }
            }
            return true;
        }
        catch (ClassCastException e1) {
            return false;
        }
        catch (NullPointerException e2) {
            return false;
        }
    }
    
    public boolean epsilonEquals(final GVector v1, final double epsilon) {
        if (this.length != v1.length) {
            return false;
        }
        for (int i = 0; i < this.length; ++i) {
            final double diff = this.values[i] - v1.values[i];
            if (((diff < 0.0) ? (-diff) : diff) > epsilon) {
                return false;
            }
        }
        return true;
    }
    
    public final double dot(final GVector v1) {
        if (this.length != v1.length) {
            throw new MismatchedSizeException(VecMathI18N.getString("GVector14"));
        }
        double result = 0.0;
        for (int i = 0; i < this.length; ++i) {
            result += this.values[i] * v1.values[i];
        }
        return result;
    }
    
    public final void SVDBackSolve(final GMatrix U, final GMatrix W, final GMatrix V, final GVector b) {
        if (U.nRow != b.getSize() || U.nRow != U.nCol || U.nRow != W.nRow) {
            throw new MismatchedSizeException(VecMathI18N.getString("GVector15"));
        }
        if (W.nCol != this.values.length || W.nCol != V.nCol || W.nCol != V.nRow) {
            throw new MismatchedSizeException(VecMathI18N.getString("GVector23"));
        }
        final GMatrix tmp = new GMatrix(U.nRow, W.nCol);
        tmp.mul(U, V);
        tmp.mulTransposeRight(U, W);
        tmp.invert();
        this.mul(tmp, b);
    }
    
    public final void LUDBackSolve(final GMatrix LU, final GVector b, final GVector permutation) {
        final int size = LU.nRow * LU.nCol;
        final double[] temp = new double[size];
        final double[] result = new double[size];
        final int[] row_perm = new int[b.getSize()];
        if (LU.nRow != b.getSize()) {
            throw new MismatchedSizeException(VecMathI18N.getString("GVector16"));
        }
        if (LU.nRow != permutation.getSize()) {
            throw new MismatchedSizeException(VecMathI18N.getString("GVector24"));
        }
        if (LU.nRow != LU.nCol) {
            throw new MismatchedSizeException(VecMathI18N.getString("GVector25"));
        }
        for (int i = 0; i < LU.nRow; ++i) {
            for (int j = 0; j < LU.nCol; ++j) {
                temp[i * LU.nCol + j] = LU.values[i][j];
            }
        }
        for (int i = 0; i < size; ++i) {
            result[i] = 0.0;
        }
        for (int i = 0; i < LU.nRow; ++i) {
            result[i * LU.nCol] = b.values[i];
        }
        for (int i = 0; i < LU.nCol; ++i) {
            row_perm[i] = (int)permutation.values[i];
        }
        GMatrix.luBacksubstitution(LU.nRow, temp, row_perm, result);
        for (int i = 0; i < LU.nRow; ++i) {
            this.values[i] = result[i * LU.nCol];
        }
    }
    
    public final double angle(final GVector v1) {
        return Math.acos(this.dot(v1) / (this.norm() * v1.norm()));
    }
    
    @Deprecated
    public final void interpolate(final GVector v1, final GVector v2, final float alpha) {
        this.interpolate(v1, v2, (double)alpha);
    }
    
    @Deprecated
    public final void interpolate(final GVector v1, final float alpha) {
        this.interpolate(v1, (double)alpha);
    }
    
    public final void interpolate(final GVector v1, final GVector v2, final double alpha) {
        if (v2.length != v1.length) {
            throw new MismatchedSizeException(VecMathI18N.getString("GVector20"));
        }
        if (this.length != v1.length) {
            throw new MismatchedSizeException(VecMathI18N.getString("GVector21"));
        }
        for (int i = 0; i < this.length; ++i) {
            this.values[i] = (1.0 - alpha) * v1.values[i] + alpha * v2.values[i];
        }
    }
    
    public final void interpolate(final GVector v1, final double alpha) {
        if (v1.length != this.length) {
            throw new MismatchedSizeException(VecMathI18N.getString("GVector22"));
        }
        for (int i = 0; i < this.length; ++i) {
            this.values[i] = (1.0 - alpha) * this.values[i] + alpha * v1.values[i];
        }
    }
    
    public Object clone() {
        GVector v1 = null;
        try {
            v1 = (GVector)super.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new InternalError();
        }
        v1.values = new double[this.length];
        for (int i = 0; i < this.length; ++i) {
            v1.values[i] = this.values[i];
        }
        return v1;
    }
}
