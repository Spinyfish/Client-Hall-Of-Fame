// 
// Decompiled by Procyon v0.5.36
// 

package javax.vecmath;

import java.io.Serializable;

public class GVector implements Serializable
{
    private int elementCount;
    private double[] elementData;
    
    public GVector(final int length) {
        this.elementCount = length;
        this.elementData = new double[length];
    }
    
    public GVector(final double[] vector) {
        this(vector.length);
        System.arraycopy(vector, 0, this.elementData, 0, this.elementCount);
    }
    
    public GVector(final GVector vector) {
        this(vector.elementCount);
        System.arraycopy(vector.elementData, 0, this.elementData, 0, this.elementCount);
    }
    
    public GVector(final Tuple2f tuple) {
        this(2);
        this.set(tuple);
    }
    
    public GVector(final Tuple3f tuple) {
        this(3);
        this.set(tuple);
    }
    
    public GVector(final Tuple3d tuple) {
        this(3);
        this.set(tuple);
    }
    
    public GVector(final Tuple4f tuple) {
        this(4);
        this.set(tuple);
    }
    
    public GVector(final Tuple4d tuple) {
        this(4);
        this.set(tuple);
    }
    
    public GVector(final double[] vector, final int length) {
        this(length);
        System.arraycopy(vector, 0, this.elementData, 0, this.elementCount);
    }
    
    public final double norm() {
        return Math.sqrt(this.normSquared());
    }
    
    public final double normSquared() {
        double s = 0.0;
        for (int i = 0; i < this.elementCount; ++i) {
            s += this.elementData[i] * this.elementData[i];
        }
        return s;
    }
    
    public final void normalize(final GVector v1) {
        this.set(v1);
        this.normalize();
    }
    
    public final void normalize() {
        final double len = this.norm();
        for (int i = 0; i < this.elementCount; ++i) {
            final double[] elementData = this.elementData;
            final int n = i;
            elementData[n] /= len;
        }
    }
    
    public final void scale(final double s, final GVector v1) {
        this.set(v1);
        this.scale(s);
    }
    
    public final void scale(final double s) {
        for (int i = 0; i < this.elementCount; ++i) {
            final double[] elementData = this.elementData;
            final int n = i;
            elementData[n] *= s;
        }
    }
    
    public final void scaleAdd(final double s, final GVector v1, final GVector v2) {
        final double[] v1data = v1.elementData;
        final double[] v2data = v2.elementData;
        if (this.elementCount != v1.elementCount) {
            throw new ArrayIndexOutOfBoundsException("this.size:" + this.elementCount + " != v1's size:" + v1.elementCount);
        }
        if (this.elementCount != v2.elementCount) {
            throw new ArrayIndexOutOfBoundsException("this.size:" + this.elementCount + " != v2's size:" + v2.elementCount);
        }
        for (int i = 0; i < this.elementCount; ++i) {
            this.elementData[i] = s * v1data[i] + v2data[i];
        }
    }
    
    public final void add(final GVector vector) {
        final double[] v1data = vector.elementData;
        if (this.elementCount != vector.elementCount) {
            throw new ArrayIndexOutOfBoundsException("this.size:" + this.elementCount + " != v2's size:" + vector.elementCount);
        }
        for (int i = 0; i < this.elementCount; ++i) {
            final double[] elementData = this.elementData;
            final int n = i;
            elementData[n] += v1data[i];
        }
    }
    
    public final void add(final GVector vector1, final GVector vector2) {
        this.set(vector1);
        this.add(vector2);
    }
    
    public final void sub(final GVector vector) {
        final double[] v1data = vector.elementData;
        if (this.elementCount != vector.elementCount) {
            throw new ArrayIndexOutOfBoundsException("this.size:" + this.elementCount + " != vector's size:" + vector.elementCount);
        }
        for (int i = 0; i < this.elementCount; ++i) {
            final double[] elementData = this.elementData;
            final int n = i;
            elementData[n] -= v1data[i];
        }
    }
    
    public final void sub(final GVector vector1, final GVector vector2) {
        this.set(vector1);
        this.sub(vector2);
    }
    
    public final void mul(final GMatrix m1, final GVector v1) {
        final double[] v1data = v1.elementData;
        final int v1size = v1.elementCount;
        final int nCol = m1.getNumCol();
        final int nRow = m1.getNumRow();
        if (v1size != nCol) {
            throw new IllegalArgumentException("v1.size:" + v1size + " != m1.nCol:" + nCol);
        }
        if (this.elementCount != nRow) {
            throw new IllegalArgumentException("this.size:" + this.elementCount + " != m1.nRow:" + nRow);
        }
        for (int i = 0; i < this.elementCount; ++i) {
            double sum = 0.0;
            for (int j = 0; j < nCol; ++j) {
                sum += m1.getElement(i, j) * v1data[j];
            }
            this.elementData[i] = sum;
        }
    }
    
    public final void mul(final GVector v1, final GMatrix m1) {
        final double[] v1data = v1.elementData;
        final int v1size = v1.elementCount;
        final int nCol = m1.getNumCol();
        final int nRow = m1.getNumRow();
        if (v1size != nRow) {
            throw new IllegalArgumentException("v1.size:" + v1size + " != m1.nRow:" + nRow);
        }
        if (this.elementCount != nCol) {
            throw new IllegalArgumentException("this.size:" + this.elementCount + " != m1.nCol:" + nCol);
        }
        for (int i = 0; i < this.elementCount; ++i) {
            double sum = 0.0;
            for (int j = 0; j < nRow; ++j) {
                sum += m1.getElement(j, i) * v1data[j];
            }
            this.elementData[i] = sum;
        }
    }
    
    public final void negate() {
        for (int i = 0; i < this.elementCount; ++i) {
            this.elementData[i] = -this.elementData[i];
        }
    }
    
    public final void zero() {
        for (int i = 0; i < this.elementCount; ++i) {
            this.elementData[i] = 0.0;
        }
    }
    
    public final void setSize(final int newSize) {
        if (newSize < 0) {
            throw new NegativeArraySizeException("newSize:" + newSize + " < 0");
        }
        if (this.elementCount < newSize) {
            final double[] oldData = this.elementData;
            System.arraycopy(oldData, 0, this.elementData = new double[newSize], 0, this.elementCount);
        }
        this.elementCount = newSize;
    }
    
    public final void set(final double[] vector) {
        System.arraycopy(vector, 0, this.elementData, 0, this.elementCount);
    }
    
    public final void set(final GVector vector) {
        System.arraycopy(vector.elementData, 0, this.elementData, 0, this.elementCount);
    }
    
    public final void set(final Tuple2f tuple) {
        this.elementData[0] = tuple.x;
        this.elementData[1] = tuple.y;
    }
    
    public final void set(final Tuple3f tuple) {
        this.elementData[0] = tuple.x;
        this.elementData[1] = tuple.y;
        this.elementData[2] = tuple.z;
    }
    
    public final void set(final Tuple3d tuple) {
        this.elementData[0] = tuple.x;
        this.elementData[1] = tuple.y;
        this.elementData[2] = tuple.z;
    }
    
    public final void set(final Tuple4f tuple) {
        this.elementData[0] = tuple.x;
        this.elementData[1] = tuple.y;
        this.elementData[2] = tuple.z;
        this.elementData[3] = tuple.w;
    }
    
    public final void set(final Tuple4d tuple) {
        this.elementData[0] = tuple.x;
        this.elementData[1] = tuple.y;
        this.elementData[2] = tuple.z;
        this.elementData[3] = tuple.w;
    }
    
    public final int getSize() {
        return this.elementCount;
    }
    
    public final double getElement(final int index) {
        try {
            return this.elementData[index];
        }
        catch (ArrayIndexOutOfBoundsException e) {
            throw new ArrayIndexOutOfBoundsException("index:" + index + "must be in [0, " + (this.elementCount - 1) + "]");
        }
    }
    
    public final void setElement(final int index, final double value) {
        try {
            this.elementData[index] = value;
        }
        catch (ArrayIndexOutOfBoundsException e) {
            throw new ArrayIndexOutOfBoundsException("index:" + index + " must be in [0, " + (this.elementCount - 1) + "]");
        }
    }
    
    public String toString() {
        final StringBuffer buf = new StringBuffer();
        buf.append("(");
        for (int i = 0; i < this.elementCount - 1; ++i) {
            buf.append(this.elementData[i]);
            buf.append(",");
        }
        buf.append(this.elementData[this.elementCount - 1]);
        buf.append(")");
        return buf.toString();
    }
    
    public int hashCode() {
        int hash = 0;
        for (int i = 0; i < this.elementCount; ++i) {
            final long bits = Double.doubleToLongBits(this.elementData[i]);
            hash ^= (int)(bits ^ bits >> 32);
        }
        return hash;
    }
    
    public boolean equals(final GVector vector1) {
        if (vector1 == null) {
            return false;
        }
        if (this.elementCount != vector1.elementCount) {
            return false;
        }
        final double[] v1data = vector1.elementData;
        for (int i = 0; i < this.elementCount; ++i) {
            if (this.elementData[i] != v1data[i]) {
                return false;
            }
        }
        return true;
    }
    
    public boolean equals(final Object o1) {
        return o1 != null && o1 instanceof GVector && this.equals((GVector)o1);
    }
    
    public boolean epsilonEquals(final GVector v1, final double epsilon) {
        if (this.elementCount != v1.elementCount) {
            return false;
        }
        final double[] v1data = v1.elementData;
        for (int i = 0; i < this.elementCount; ++i) {
            if (Math.abs(this.elementData[i] - v1data[i]) > epsilon) {
                return false;
            }
        }
        return true;
    }
    
    public final double dot(final GVector v1) {
        final double[] v1data = v1.elementData;
        if (this.elementCount != v1.elementCount) {
            throw new IllegalArgumentException("this.size:" + this.elementCount + " != v1.size:" + v1.elementCount);
        }
        double sum = 0.0;
        for (int i = 0; i < this.elementCount; ++i) {
            sum += this.elementData[i] * v1data[i];
        }
        return sum;
    }
    
    public final void SVDBackSolve(final GMatrix U, final GMatrix W, final GMatrix V, final GVector b) {
        if (this.elementCount != U.getNumRow() || this.elementCount != U.getNumCol()) {
            throw new ArrayIndexOutOfBoundsException("this.size:" + this.elementCount + " != U.nRow,nCol:" + U.getNumRow() + "," + U.getNumCol());
        }
        if (this.elementCount != W.getNumRow()) {
            throw new ArrayIndexOutOfBoundsException("this.size:" + this.elementCount + " != W.nRow:" + W.getNumRow());
        }
        if (b.elementCount != W.getNumCol()) {
            throw new ArrayIndexOutOfBoundsException("b.size:" + b.elementCount + " != W.nCol:" + W.getNumCol());
        }
        if (b.elementCount != V.getNumRow() || b.elementCount != V.getNumCol()) {
            throw new ArrayIndexOutOfBoundsException("b.size:" + this.elementCount + " != V.nRow,nCol:" + V.getNumRow() + "," + V.getNumCol());
        }
        final int m = U.getNumRow();
        final int n = V.getNumRow();
        final double[] tmp = new double[n];
        for (int j = 0; j < n; ++j) {
            double s = 0.0;
            final double wj = W.getElement(j, j);
            if (wj != 0.0) {
                for (int i = 0; i < m; ++i) {
                    s += U.getElement(i, j) * b.elementData[i];
                }
                s /= wj;
            }
            tmp[j] = s;
        }
        for (int k = 0; k < n; ++k) {
            double s2 = 0.0;
            for (int jj = 0; jj < n; ++jj) {
                s2 += V.getElement(k, jj) * tmp[jj];
            }
            this.elementData[k] = s2;
        }
    }
    
    public final void LUDBackSolve(final GMatrix LU, final GVector b, final GVector permutation) {
        if (this.elementCount != b.elementCount) {
            throw new ArrayIndexOutOfBoundsException("this.size:" + this.elementCount + " != b.size:" + b.elementCount);
        }
        if (this.elementCount != LU.getNumRow()) {
            throw new ArrayIndexOutOfBoundsException("this.size:" + this.elementCount + " != LU.nRow:" + LU.getNumRow());
        }
        if (this.elementCount != LU.getNumCol()) {
            throw new ArrayIndexOutOfBoundsException("this.size:" + this.elementCount + " != LU.nCol:" + LU.getNumCol());
        }
        final int n = this.elementCount;
        final double[] indx = permutation.elementData;
        final double[] x = this.elementData;
        final double[] bdata = b.elementData;
        for (int i = 0; i < n; ++i) {
            x[i] = bdata[(int)indx[i]];
        }
        int ii = -1;
        for (int j = 0; j < n; ++j) {
            double sum = x[j];
            if (0 <= ii) {
                for (int k = ii; k <= j - 1; ++k) {
                    sum -= LU.getElement(j, k) * x[k];
                }
            }
            else if (sum != 0.0) {
                ii = j;
            }
            x[j] = sum;
        }
        for (int l = n - 1; l >= 0; --l) {
            double sum2 = x[l];
            for (int m = l + 1; m < n; ++m) {
                sum2 -= LU.getElement(l, m) * x[m];
            }
            x[l] = sum2 / LU.getElement(l, l);
        }
    }
    
    public final double angle(final GVector v1) {
        return Math.acos(this.dot(v1) / this.norm() / v1.norm());
    }
    
    public final void interpolate(final GVector v1, final GVector v2, final float alpha) {
        this.interpolate(v1, v2, (double)alpha);
    }
    
    public final void interpolate(final GVector v1, final float alpha) {
        this.interpolate(v1, (double)alpha);
    }
    
    public final void interpolate(final GVector v1, final GVector v2, final double alpha) {
        this.set(v1);
        this.interpolate(v2, alpha);
    }
    
    public final void interpolate(final GVector v1, final double alpha) {
        final double[] v1data = v1.elementData;
        if (this.elementCount != v1.elementCount) {
            throw new IllegalArgumentException("this.size:" + this.elementCount + " != v1.size:" + v1.elementCount);
        }
        final double beta = 1.0 - alpha;
        for (int i = 0; i < this.elementCount; ++i) {
            this.elementData[i] = beta * this.elementData[i] + alpha * v1data[i];
        }
    }
}
