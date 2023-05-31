// 
// Decompiled by Procyon v0.5.36
// 

package javax.vecmath;

import java.io.Serializable;

public class GMatrix implements Serializable
{
    private double[] elementData;
    private int nRow;
    private int nCol;
    
    public GMatrix(final int nRow, final int nCol) {
        if (nRow < 0) {
            throw new NegativeArraySizeException(nRow + " < 0");
        }
        if (nCol < 0) {
            throw new NegativeArraySizeException(nCol + " < 0");
        }
        this.nRow = nRow;
        this.nCol = nCol;
        this.elementData = new double[nRow * nCol];
        this.setIdentity();
    }
    
    public GMatrix(final int nRow, final int nCol, final double[] matrix) {
        if (nRow < 0) {
            throw new NegativeArraySizeException(nRow + " < 0");
        }
        if (nCol < 0) {
            throw new NegativeArraySizeException(nCol + " < 0");
        }
        this.nRow = nRow;
        this.nCol = nCol;
        this.elementData = new double[nRow * nCol];
        this.set(matrix);
    }
    
    public GMatrix(final GMatrix matrix) {
        this.nRow = matrix.nRow;
        this.nCol = matrix.nCol;
        final int newSize = this.nRow * this.nCol;
        this.elementData = new double[newSize];
        System.arraycopy(matrix.elementData, 0, this.elementData, 0, newSize);
    }
    
    public final void mul(final GMatrix m1) {
        this.mul(this, m1);
    }
    
    public final void mul(final GMatrix m1, final GMatrix m2) {
        if (this.nRow != m1.nRow) {
            throw new ArrayIndexOutOfBoundsException("nRow:" + this.nRow + " != m1.nRow:" + m1.nRow);
        }
        if (this.nCol != m2.nCol) {
            throw new ArrayIndexOutOfBoundsException("nCol:" + this.nCol + " != m2.nCol:" + m2.nCol);
        }
        if (m1.nCol != m2.nRow) {
            throw new ArrayIndexOutOfBoundsException("m1.nCol:" + m1.nCol + " != m2.nRow:" + m2.nRow);
        }
        final double[] newData = new double[this.nCol * this.nRow];
        for (int i = 0; i < this.nRow; ++i) {
            for (int j = 0; j < this.nCol; ++j) {
                double sum = 0.0;
                for (int k = 0; k < m1.nCol; ++k) {
                    sum += m1.elementData[i * m1.nCol + k] * m2.elementData[k * m2.nCol + j];
                }
                newData[i * this.nCol + j] = sum;
            }
        }
        this.elementData = newData;
    }
    
    public final void mul(final GVector v1, final GVector v2) {
        if (this.nRow < v1.getSize()) {
            throw new IllegalArgumentException("nRow:" + this.nRow + " < v1.getSize():" + v1.getSize());
        }
        if (this.nCol < v2.getSize()) {
            throw new IllegalArgumentException("nCol:" + this.nCol + " < v2.getSize():" + v2.getSize());
        }
        for (int i = 0; i < this.nRow; ++i) {
            for (int j = 0; j < this.nCol; ++j) {
                this.elementData[i * this.nCol + j] = v1.getElement(i) * v2.getElement(j);
            }
        }
    }
    
    public final void add(final GMatrix m1) {
        if (this.nRow != m1.nRow || this.nCol != m1.nCol) {
            throw new IllegalArgumentException("this:(" + this.nRow + "x" + this.nCol + ") != m1:(" + m1.nRow + "x" + m1.nCol + ").");
        }
        for (int i = 0; i < this.nRow * this.nCol; ++i) {
            final double[] elementData = this.elementData;
            final int n = i;
            elementData[n] += m1.elementData[i];
        }
    }
    
    public final void add(final GMatrix m1, final GMatrix m2) {
        if (this.nRow != m1.nRow || this.nCol != m1.nCol) {
            throw new IllegalArgumentException("this:(" + this.nRow + "x" + this.nCol + ") != m1:(" + m1.nRow + "x" + m1.nCol + ").");
        }
        if (this.nRow != m2.nRow || this.nCol != m2.nCol) {
            throw new IllegalArgumentException("this:(" + this.nRow + "x" + this.nCol + ") != m2:(" + m2.nRow + "x" + m2.nCol + ").");
        }
        for (int i = 0; i < this.nRow * this.nCol; ++i) {
            this.elementData[i] = m1.elementData[i] + m2.elementData[i];
        }
    }
    
    public final void sub(final GMatrix m1) {
        if (this.nRow != m1.nRow || this.nCol != m1.nCol) {
            throw new IllegalArgumentException("this:(" + this.nRow + "x" + this.nCol + ") != m1:(" + m1.nRow + "x" + m1.nCol + ").");
        }
        for (int i = 0; i < this.nRow * this.nCol; ++i) {
            final double[] elementData = this.elementData;
            final int n = i;
            elementData[n] -= m1.elementData[i];
        }
    }
    
    public final void sub(final GMatrix m1, final GMatrix m2) {
        if (this.nRow != m1.nRow || this.nCol != m1.nCol) {
            throw new IllegalArgumentException("this:(" + this.nRow + "x" + this.nCol + ") != m1:(" + m1.nRow + "x" + m1.nCol + ").");
        }
        if (this.nRow != m2.nRow || this.nCol != m2.nCol) {
            throw new IllegalArgumentException("this:(" + this.nRow + "x" + this.nCol + ") != m2:(" + m2.nRow + "x" + m2.nCol + ").");
        }
        for (int i = 0; i < this.nRow * this.nCol; ++i) {
            this.elementData[i] = m1.elementData[i] - m2.elementData[i];
        }
    }
    
    public final void negate() {
        for (int i = 0; i < this.nRow * this.nCol; ++i) {
            this.elementData[i] = -this.elementData[i];
        }
    }
    
    public final void negate(final GMatrix m1) {
        this.set(m1);
        this.negate();
    }
    
    public final void setIdentity() {
        this.setZero();
        for (int min = (this.nRow < this.nCol) ? this.nRow : this.nCol, i = 0; i < min; ++i) {
            this.elementData[i * this.nCol + i] = 1.0;
        }
    }
    
    public final void setZero() {
        for (int i = 0; i < this.nRow * this.nCol; ++i) {
            this.elementData[i] = 0.0;
        }
    }
    
    public final void identityMinus() {
        this.negate();
        for (int min = (this.nRow < this.nCol) ? this.nRow : this.nCol, i = 0; i < min; ++i) {
            final double[] elementData = this.elementData;
            final int n = i * this.nCol + i;
            ++elementData[n];
        }
    }
    
    public final void invert() {
        if (this.nRow != this.nCol) {
            throw new ArrayIndexOutOfBoundsException("not a square matrix");
        }
        final int n = this.nRow;
        final GMatrix LU = new GMatrix(n, n);
        final GVector permutation = new GVector(n);
        final GVector column = new GVector(n);
        final GVector unit = new GVector(n);
        this.LUD(LU, permutation);
        for (int j = 0; j < n; ++j) {
            unit.zero();
            unit.setElement(j, 1.0);
            column.LUDBackSolve(LU, unit, permutation);
            this.setColumn(j, column);
        }
    }
    
    public final void invert(final GMatrix m1) {
        this.set(m1);
        this.invert();
    }
    
    public final void copySubMatrix(final int rowSource, final int colSource, final int numRow, final int numCol, final int rowDest, final int colDest, final GMatrix target) {
        if (rowSource < 0 || colSource < 0 || rowDest < 0 || colDest < 0) {
            throw new ArrayIndexOutOfBoundsException("rowSource,colSource,rowDest,colDest < 0.");
        }
        if (this.nRow < numRow + rowSource || this.nCol < numCol + colSource) {
            throw new ArrayIndexOutOfBoundsException("Source GMatrix too small.");
        }
        if (target.nRow < numRow + rowDest || target.nCol < numCol + colDest) {
            throw new ArrayIndexOutOfBoundsException("Target GMatrix too small.");
        }
        for (int i = 0; i < numRow; ++i) {
            for (int j = 0; j < numCol; ++j) {
                target.elementData[(i + rowDest) * this.nCol + (j + colDest)] = this.elementData[(i + rowSource) * this.nCol + (j + colSource)];
            }
        }
    }
    
    public final void setSize(final int nRow, final int nCol) {
        if (nRow < 0 || nCol < 0) {
            throw new NegativeArraySizeException("nRow or nCol < 0");
        }
        final int oldnRow = this.nRow;
        final int oldnCol = this.nCol;
        final int oldSize = this.nRow * this.nCol;
        this.nRow = nRow;
        this.nCol = nCol;
        final int newSize = nRow * nCol;
        final double[] oldData = this.elementData;
        if (oldnCol == nCol) {
            if (nRow <= oldnRow) {
                return;
            }
            System.arraycopy(oldData, 0, this.elementData = new double[newSize], 0, oldSize);
        }
        else {
            this.elementData = new double[newSize];
            this.setZero();
            for (int i = 0; i < oldnRow; ++i) {
                System.arraycopy(oldData, i * oldnCol, this.elementData, i * nCol, oldnCol);
            }
        }
    }
    
    public final void set(final double[] matrix) {
        final int size = this.nRow * this.nCol;
        System.arraycopy(matrix, 0, this.elementData, 0, size);
    }
    
    public final void set(final Matrix3f m1) {
        this.elementData[0] = m1.m00;
        this.elementData[1] = m1.m01;
        this.elementData[2] = m1.m02;
        this.elementData[this.nCol] = m1.m10;
        this.elementData[this.nCol + 1] = m1.m11;
        this.elementData[this.nCol + 2] = m1.m12;
        this.elementData[2 * this.nCol] = m1.m20;
        this.elementData[2 * this.nCol + 1] = m1.m21;
        this.elementData[2 * this.nCol + 2] = m1.m22;
    }
    
    public final void set(final Matrix3d m1) {
        this.elementData[0] = m1.m00;
        this.elementData[1] = m1.m01;
        this.elementData[2] = m1.m02;
        this.elementData[this.nCol] = m1.m10;
        this.elementData[this.nCol + 1] = m1.m11;
        this.elementData[this.nCol + 2] = m1.m12;
        this.elementData[2 * this.nCol] = m1.m20;
        this.elementData[2 * this.nCol + 1] = m1.m21;
        this.elementData[2 * this.nCol + 2] = m1.m22;
    }
    
    public final void set(final Matrix4f m1) {
        this.elementData[0] = m1.m00;
        this.elementData[1] = m1.m01;
        this.elementData[2] = m1.m02;
        this.elementData[3] = m1.m03;
        this.elementData[this.nCol] = m1.m10;
        this.elementData[this.nCol + 1] = m1.m11;
        this.elementData[this.nCol + 2] = m1.m12;
        this.elementData[this.nCol + 3] = m1.m13;
        this.elementData[2 * this.nCol] = m1.m20;
        this.elementData[2 * this.nCol + 1] = m1.m21;
        this.elementData[2 * this.nCol + 2] = m1.m22;
        this.elementData[2 * this.nCol + 3] = m1.m23;
        this.elementData[3 * this.nCol] = m1.m30;
        this.elementData[3 * this.nCol + 1] = m1.m31;
        this.elementData[3 * this.nCol + 2] = m1.m32;
        this.elementData[3 * this.nCol + 3] = m1.m33;
    }
    
    public final void set(final Matrix4d m1) {
        this.elementData[0] = m1.m00;
        this.elementData[1] = m1.m01;
        this.elementData[2] = m1.m02;
        this.elementData[3] = m1.m03;
        this.elementData[this.nCol] = m1.m10;
        this.elementData[this.nCol + 1] = m1.m11;
        this.elementData[this.nCol + 2] = m1.m12;
        this.elementData[this.nCol + 3] = m1.m13;
        this.elementData[2 * this.nCol] = m1.m20;
        this.elementData[2 * this.nCol + 1] = m1.m21;
        this.elementData[2 * this.nCol + 2] = m1.m22;
        this.elementData[2 * this.nCol + 3] = m1.m23;
        this.elementData[3 * this.nCol] = m1.m30;
        this.elementData[3 * this.nCol + 1] = m1.m31;
        this.elementData[3 * this.nCol + 2] = m1.m32;
        this.elementData[3 * this.nCol + 3] = m1.m33;
    }
    
    public final void set(final GMatrix m1) {
        if (m1.nRow < this.nRow || m1.nCol < this.nCol) {
            throw new ArrayIndexOutOfBoundsException("m1 smaller than this matrix");
        }
        System.arraycopy(m1.elementData, 0, this.elementData, 0, this.nRow * this.nCol);
    }
    
    public final int getNumRow() {
        return this.nRow;
    }
    
    public final int getNumCol() {
        return this.nCol;
    }
    
    public final double getElement(final int row, final int column) {
        if (this.nRow <= row) {
            throw new ArrayIndexOutOfBoundsException("row:" + row + " > matrix's nRow:" + this.nRow);
        }
        if (row < 0) {
            throw new ArrayIndexOutOfBoundsException("row:" + row + " < 0");
        }
        if (this.nCol <= column) {
            throw new ArrayIndexOutOfBoundsException("column:" + column + " > matrix's nCol:" + this.nCol);
        }
        if (column < 0) {
            throw new ArrayIndexOutOfBoundsException("column:" + column + " < 0");
        }
        return this.elementData[row * this.nCol + column];
    }
    
    public final void setElement(final int row, final int column, final double value) {
        if (this.nRow <= row) {
            throw new ArrayIndexOutOfBoundsException("row:" + row + " > matrix's nRow:" + this.nRow);
        }
        if (row < 0) {
            throw new ArrayIndexOutOfBoundsException("row:" + row + " < 0");
        }
        if (this.nCol <= column) {
            throw new ArrayIndexOutOfBoundsException("column:" + column + " > matrix's nCol:" + this.nCol);
        }
        if (column < 0) {
            throw new ArrayIndexOutOfBoundsException("column:" + column + " < 0");
        }
        this.elementData[row * this.nCol + column] = value;
    }
    
    public final void getRow(final int row, final double[] array) {
        if (this.nRow <= row) {
            throw new ArrayIndexOutOfBoundsException("row:" + row + " > matrix's nRow:" + this.nRow);
        }
        if (row < 0) {
            throw new ArrayIndexOutOfBoundsException("row:" + row + " < 0");
        }
        if (array.length < this.nCol) {
            throw new ArrayIndexOutOfBoundsException("array length:" + array.length + " smaller than matrix's nCol:" + this.nCol);
        }
        System.arraycopy(this.elementData, row * this.nCol, array, 0, this.nCol);
    }
    
    public final void getRow(final int row, final GVector vector) {
        if (this.nRow <= row) {
            throw new ArrayIndexOutOfBoundsException("row:" + row + " > matrix's nRow:" + this.nRow);
        }
        if (row < 0) {
            throw new ArrayIndexOutOfBoundsException("row:" + row + " < 0");
        }
        if (vector.getSize() < this.nCol) {
            throw new ArrayIndexOutOfBoundsException("vector size:" + vector.getSize() + " smaller than matrix's nCol:" + this.nCol);
        }
        for (int i = 0; i < this.nCol; ++i) {
            vector.setElement(i, this.elementData[row * this.nCol + i]);
        }
    }
    
    public final void getColumn(final int col, final double[] array) {
        if (this.nCol <= col) {
            throw new ArrayIndexOutOfBoundsException("col:" + col + " > matrix's nCol:" + this.nCol);
        }
        if (col < 0) {
            throw new ArrayIndexOutOfBoundsException("col:" + col + " < 0");
        }
        if (array.length < this.nRow) {
            throw new ArrayIndexOutOfBoundsException("array.length:" + array.length + " < matrix's nRow=" + this.nRow);
        }
        for (int i = 0; i < this.nRow; ++i) {
            array[i] = this.elementData[i * this.nCol + col];
        }
    }
    
    public final void getColumn(final int col, final GVector vector) {
        if (this.nCol <= col) {
            throw new ArrayIndexOutOfBoundsException("col:" + col + " > matrix's nCol:" + this.nCol);
        }
        if (col < 0) {
            throw new ArrayIndexOutOfBoundsException("col:" + col + " < 0");
        }
        if (vector.getSize() < this.nRow) {
            throw new ArrayIndexOutOfBoundsException("vector size:" + vector.getSize() + " < matrix's nRow:" + this.nRow);
        }
        for (int i = 0; i < this.nRow; ++i) {
            vector.setElement(i, this.elementData[i * this.nCol + col]);
        }
    }
    
    public final void get(final Matrix3d m1) {
        m1.m00 = this.elementData[0];
        m1.m01 = this.elementData[1];
        m1.m02 = this.elementData[2];
        m1.m10 = this.elementData[this.nCol];
        m1.m11 = this.elementData[this.nCol + 1];
        m1.m12 = this.elementData[this.nCol + 2];
        m1.m20 = this.elementData[2 * this.nCol];
        m1.m21 = this.elementData[2 * this.nCol + 1];
        m1.m22 = this.elementData[2 * this.nCol + 2];
    }
    
    public final void get(final Matrix3f m1) {
        m1.m00 = (float)this.elementData[0];
        m1.m01 = (float)this.elementData[1];
        m1.m02 = (float)this.elementData[2];
        m1.m10 = (float)this.elementData[this.nCol];
        m1.m11 = (float)this.elementData[this.nCol + 1];
        m1.m12 = (float)this.elementData[this.nCol + 2];
        m1.m20 = (float)this.elementData[2 * this.nCol];
        m1.m21 = (float)this.elementData[2 * this.nCol + 1];
        m1.m22 = (float)this.elementData[2 * this.nCol + 2];
    }
    
    public final void get(final Matrix4d m1) {
        m1.m00 = this.elementData[0];
        m1.m01 = this.elementData[1];
        m1.m02 = this.elementData[2];
        m1.m03 = this.elementData[3];
        m1.m10 = this.elementData[this.nCol];
        m1.m11 = this.elementData[this.nCol + 1];
        m1.m12 = this.elementData[this.nCol + 2];
        m1.m13 = this.elementData[this.nCol + 3];
        m1.m20 = this.elementData[2 * this.nCol];
        m1.m21 = this.elementData[2 * this.nCol + 1];
        m1.m22 = this.elementData[2 * this.nCol + 2];
        m1.m23 = this.elementData[2 * this.nCol + 3];
        m1.m30 = this.elementData[3 * this.nCol];
        m1.m31 = this.elementData[3 * this.nCol + 1];
        m1.m32 = this.elementData[3 * this.nCol + 2];
        m1.m33 = this.elementData[3 * this.nCol + 3];
    }
    
    public final void get(final Matrix4f m1) {
        m1.m00 = (float)this.elementData[0];
        m1.m01 = (float)this.elementData[1];
        m1.m02 = (float)this.elementData[2];
        m1.m03 = (float)this.elementData[3];
        m1.m10 = (float)this.elementData[this.nCol];
        m1.m11 = (float)this.elementData[this.nCol + 1];
        m1.m12 = (float)this.elementData[this.nCol + 2];
        m1.m13 = (float)this.elementData[this.nCol + 3];
        m1.m20 = (float)this.elementData[2 * this.nCol];
        m1.m21 = (float)this.elementData[2 * this.nCol + 1];
        m1.m22 = (float)this.elementData[2 * this.nCol + 2];
        m1.m23 = (float)this.elementData[2 * this.nCol + 3];
        m1.m30 = (float)this.elementData[3 * this.nCol];
        m1.m31 = (float)this.elementData[3 * this.nCol + 1];
        m1.m32 = (float)this.elementData[3 * this.nCol + 2];
        m1.m33 = (float)this.elementData[3 * this.nCol + 3];
    }
    
    public final void get(final GMatrix m1) {
        if (m1.nRow < this.nRow || m1.nCol < this.nCol) {
            throw new IllegalArgumentException("m1 matrix is smaller than this matrix.");
        }
        if (m1.nCol == this.nCol) {
            System.arraycopy(this.elementData, 0, m1.elementData, 0, this.nRow * this.nCol);
        }
        else {
            for (int i = 0; i < this.nRow; ++i) {
                System.arraycopy(this.elementData, i * this.nCol, m1.elementData, i * m1.nCol, this.nCol);
            }
        }
    }
    
    public final void setRow(final int row, final double[] array) {
        if (this.nRow <= row) {
            throw new ArrayIndexOutOfBoundsException("row:" + row + " > matrix's nRow:" + this.nRow);
        }
        if (row < 0) {
            throw new ArrayIndexOutOfBoundsException("row:" + row + " < 0");
        }
        if (array.length < this.nCol) {
            throw new ArrayIndexOutOfBoundsException("array length:" + array.length + " < matrix's nCol=" + this.nCol);
        }
        System.arraycopy(array, 0, this.elementData, row * this.nCol, this.nCol);
    }
    
    public final void setRow(final int row, final GVector vector) {
        if (this.nRow <= row) {
            throw new ArrayIndexOutOfBoundsException("row:" + row + " > matrix's nRow:" + this.nRow);
        }
        if (row < 0) {
            throw new ArrayIndexOutOfBoundsException("row:" + row + " < 0");
        }
        final int vecSize = vector.getSize();
        if (vecSize < this.nCol) {
            throw new ArrayIndexOutOfBoundsException("vector's size:" + vecSize + " < matrix's nCol=" + this.nCol);
        }
        for (int i = 0; i < this.nCol; ++i) {
            this.elementData[row * this.nCol + i] = vector.getElement(i);
        }
    }
    
    public final void setColumn(final int col, final double[] array) {
        if (this.nCol <= col) {
            throw new ArrayIndexOutOfBoundsException("col:" + col + " > matrix's nCol=" + this.nCol);
        }
        if (col < 0) {
            throw new ArrayIndexOutOfBoundsException("col:" + col + " < 0");
        }
        if (array.length < this.nRow) {
            throw new ArrayIndexOutOfBoundsException("array length:" + array.length + " < matrix's nRow:" + this.nRow);
        }
        for (int i = 0; i < this.nRow; ++i) {
            this.elementData[i * this.nCol + col] = array[i];
        }
    }
    
    public final void setColumn(final int col, final GVector vector) {
        if (this.nCol <= col) {
            throw new ArrayIndexOutOfBoundsException("col:" + col + " > matrix's nCol=" + this.nCol);
        }
        if (col < 0) {
            throw new ArrayIndexOutOfBoundsException("col:" + col + " < 0");
        }
        final int vecSize = vector.getSize();
        if (vecSize < this.nRow) {
            throw new ArrayIndexOutOfBoundsException("vector size:" + vecSize + " < matrix's nRow=" + this.nRow);
        }
        for (int i = 0; i < this.nRow; ++i) {
            this.elementData[i * this.nCol + col] = vector.getElement(i);
        }
    }
    
    public final void mulTransposeBoth(final GMatrix m1, final GMatrix m2) {
        this.mul(m2, m1);
        this.transpose();
    }
    
    public final void mulTransposeRight(final GMatrix m1, final GMatrix m2) {
        if (m1.nCol != m2.nCol || this.nRow != m1.nRow || this.nCol != m2.nRow) {
            throw new ArrayIndexOutOfBoundsException("matrices mismatch");
        }
        for (int i = 0; i < this.nRow; ++i) {
            for (int j = 0; j < this.nCol; ++j) {
                double sum = 0.0;
                for (int k = 0; k < m1.nCol; ++k) {
                    sum += m1.elementData[i * m1.nCol + k] * m2.elementData[j * m2.nCol + k];
                }
                this.elementData[i * this.nCol + j] = sum;
            }
        }
    }
    
    public final void mulTransposeLeft(final GMatrix m1, final GMatrix m2) {
        this.transpose(m1);
        this.mul(m2);
    }
    
    public final void transpose() {
        for (int i = 0; i < this.nRow; ++i) {
            for (int j = i + 1; j < this.nCol; ++j) {
                final double tmp = this.elementData[i * this.nCol + j];
                this.elementData[i * this.nCol + j] = this.elementData[j * this.nCol + i];
                this.elementData[j * this.nCol + i] = tmp;
            }
        }
    }
    
    public final void transpose(final GMatrix m1) {
        this.set(m1);
        this.transpose();
    }
    
    public String toString() {
        final String nl = System.getProperty("line.separator");
        final StringBuffer out = new StringBuffer("[");
        out.append(nl);
        for (int i = 0; i < this.nRow; ++i) {
            out.append("  [");
            for (int j = 0; j < this.nCol; ++j) {
                if (0 < j) {
                    out.append("\t");
                }
                out.append(this.elementData[i * this.nCol + j]);
            }
            if (i + 1 < this.nRow) {
                out.append("]");
                out.append(nl);
            }
            else {
                out.append("] ]");
            }
        }
        return out.toString();
    }
    
    public int hashCode() {
        int hash = 0;
        for (int i = 0; i < this.nRow * this.nCol; ++i) {
            final long bits = Double.doubleToLongBits(this.elementData[i]);
            hash ^= (int)(bits ^ bits >> 32);
        }
        return hash;
    }
    
    public boolean equals(final GMatrix m1) {
        if (m1 == null) {
            return false;
        }
        if (m1.nRow != this.nRow) {
            return false;
        }
        if (m1.nCol != this.nCol) {
            return false;
        }
        for (int i = 0; i < this.nRow; ++i) {
            for (int j = 0; j < this.nCol; ++j) {
                if (this.elementData[i * this.nCol + j] != m1.elementData[i * this.nCol + j]) {
                    return false;
                }
            }
        }
        return true;
    }
    
    public boolean equals(final Object o1) {
        return o1 != null && o1 instanceof GMatrix && this.equals((GMatrix)o1);
    }
    
    public boolean epsilonEquals(final GMatrix m1, final float epsilon) {
        if (m1.nRow != this.nRow) {
            return false;
        }
        if (m1.nCol != this.nCol) {
            return false;
        }
        for (int i = 0; i < this.nRow; ++i) {
            for (int j = 0; j < this.nCol; ++j) {
                if (epsilon < Math.abs(this.elementData[i * this.nCol + j] - m1.elementData[i * this.nCol + j])) {
                    return false;
                }
            }
        }
        return true;
    }
    
    public boolean epsilonEquals(final GMatrix m1, final double epsilon) {
        if (m1.nRow != this.nRow) {
            return false;
        }
        if (m1.nCol != this.nCol) {
            return false;
        }
        for (int i = 0; i < this.nRow; ++i) {
            for (int j = 0; j < this.nCol; ++j) {
                if (epsilon < Math.abs(this.elementData[i * this.nCol + j] - m1.elementData[i * this.nCol + j])) {
                    return false;
                }
            }
        }
        return true;
    }
    
    public final double trace() {
        final int min = (this.nRow < this.nCol) ? this.nRow : this.nCol;
        double trace = 0.0;
        for (int i = 0; i < min; ++i) {
            trace += this.elementData[i * this.nCol + i];
        }
        return trace;
    }
    
    public final void setScale(final double scale) {
        this.setZero();
        for (int min = (this.nRow < this.nCol) ? this.nRow : this.nCol, i = 0; i < min; ++i) {
            this.elementData[i * this.nCol + i] = scale;
        }
    }
    
    private void setDiag(final int i, final double value) {
        this.elementData[i * this.nCol + i] = value;
    }
    
    private double getDiag(final int i) {
        return this.elementData[i * this.nCol + i];
    }
    
    private double dpythag(final double a, final double b) {
        final double absa = Math.abs(a);
        final double absb = Math.abs(b);
        if (absa > absb) {
            if (absa == 0.0) {
                return 0.0;
            }
            final double term = absb / absa;
            if (Math.abs(term) <= Double.MIN_VALUE) {
                return absa;
            }
            return absa * Math.sqrt(1.0 + term * term);
        }
        else {
            if (absb == 0.0) {
                return 0.0;
            }
            final double term = absa / absb;
            if (Math.abs(term) <= Double.MIN_VALUE) {
                return absb;
            }
            return absb * Math.sqrt(1.0 + term * term);
        }
    }
    
    public final int SVD(final GMatrix u, final GMatrix w, final GMatrix v) {
        if (u.nRow != this.nRow || u.nCol != this.nRow) {
            throw new ArrayIndexOutOfBoundsException("The U Matrix invalid size");
        }
        if (v.nRow != this.nCol || v.nCol != this.nCol) {
            throw new ArrayIndexOutOfBoundsException("The V Matrix invalid size");
        }
        if (w.nCol != this.nCol || w.nRow != this.nRow) {
            throw new ArrayIndexOutOfBoundsException("The W Matrix invalid size");
        }
        final int m = this.nRow;
        final int n = this.nCol;
        final int imax = (m > n) ? m : n;
        final double[] A = u.elementData;
        final double[] V = v.elementData;
        int l = 0;
        int nm = 0;
        final double[] rv1 = new double[n];
        this.get(u);
        for (int i = m; i < imax; ++i) {
            for (int j = 0; j < imax; ++j) {
                A[i * m + j] = 0.0;
            }
        }
        for (int j = n; j < imax; ++j) {
            for (int i = 0; i < imax; ++i) {
                A[i * m + j] = 0.0;
            }
        }
        w.setZero();
        double anorm;
        double g;
        double scale = g = (anorm = 0.0);
        for (int i = 0; i < n; ++i) {
            l = i + 1;
            rv1[i] = scale * g;
            double s = g = (scale = 0.0);
            if (i < m) {
                for (int k = i; k < m; ++k) {
                    scale += Math.abs(A[k * m + i]);
                }
                if (scale != 0.0) {
                    for (int k = i; k < m; ++k) {
                        final double[] array = A;
                        final int n2 = k * m + i;
                        array[n2] /= scale;
                        s += A[k * m + i] * A[k * m + i];
                    }
                    double f = A[i * m + i];
                    g = ((f < 0.0) ? Math.sqrt(s) : (-Math.sqrt(s)));
                    final double h = f * g - s;
                    A[i * m + i] = f - g;
                    for (int j = l; j < n; ++j) {
                        s = 0.0;
                        for (int k = i; k < m; ++k) {
                            s += A[k * m + i] * A[k * m + j];
                        }
                        f = s / h;
                        for (int k = i; k < m; ++k) {
                            final double[] array2 = A;
                            final int n3 = k * m + j;
                            array2[n3] += f * A[k * m + i];
                        }
                    }
                    for (int k = i; k < m; ++k) {
                        final double[] array3 = A;
                        final int n4 = k * m + i;
                        array3[n4] *= scale;
                    }
                }
            }
            w.setDiag(i, scale * g);
            s = (g = (scale = 0.0));
            if (i < m && i != n - 1) {
                for (int k = l; k < n; ++k) {
                    scale += Math.abs(A[i * m + k]);
                }
                if (scale != 0.0) {
                    for (int k = l; k < n; ++k) {
                        final double[] array4 = A;
                        final int n5 = i * m + k;
                        array4[n5] /= scale;
                        s += A[i * m + k] * A[i * m + k];
                    }
                    final double f = A[i * m + l];
                    g = ((f < 0.0) ? Math.sqrt(s) : (-Math.sqrt(s)));
                    final double h = f * g - s;
                    A[i * m + l] = f - g;
                    for (int k = l; k < n; ++k) {
                        rv1[k] = A[i * m + k] / h;
                    }
                    for (int j = l; j < m; ++j) {
                        s = 0.0;
                        for (int k = l; k < n; ++k) {
                            s += A[j * m + k] * A[i * m + k];
                        }
                        for (int k = l; k < n; ++k) {
                            final double[] array5 = A;
                            final int n6 = j * m + k;
                            array5[n6] += s * rv1[k];
                        }
                    }
                    for (int k = l; k < n; ++k) {
                        final double[] array6 = A;
                        final int n7 = i * m + k;
                        array6[n7] *= scale;
                    }
                }
            }
            final double a1 = Math.abs(w.getDiag(i)) + Math.abs(rv1[i]);
            if (a1 > anorm) {
                anorm = a1;
            }
        }
        for (int i = n - 1; i >= 0; --i) {
            if (i < n - 1) {
                if (g != 0.0) {
                    for (int j = l; j < n; ++j) {
                        V[j * n + i] = A[i * m + j] / A[i * m + l] / g;
                    }
                    for (int j = l; j < n; ++j) {
                        double s = 0.0;
                        for (int k = l; k < n; ++k) {
                            s += A[i * m + k] * V[k * n + j];
                        }
                        for (int k = l; k < n; ++k) {
                            final double[] array7 = V;
                            final int n8 = k * n + j;
                            array7[n8] += s * V[k * n + i];
                        }
                    }
                }
                for (int j = l; j < n; ++j) {
                    V[i * n + j] = (V[j * n + i] = 0.0);
                }
            }
            V[i * n + i] = 1.0;
            g = rv1[i];
            l = i;
        }
        final int imin = (m < n) ? m : n;
        for (int i = imin - 1; i >= 0; --i) {
            l = i + 1;
            g = w.getDiag(i);
            for (int j = l; j < n; ++j) {
                A[i * m + j] = 0.0;
            }
            if (g != 0.0) {
                g = 1.0 / g;
                for (int j = l; j < n; ++j) {
                    double s = 0.0;
                    for (int k = l; k < m; ++k) {
                        s += A[k * m + i] * A[k * m + j];
                    }
                    final double f = s / A[i * m + i] * g;
                    for (int k = i; k < m; ++k) {
                        final double[] array8 = A;
                        final int n9 = k * m + j;
                        array8[n9] += f * A[k * m + i];
                    }
                }
                for (int j = i; j < m; ++j) {
                    final double[] array9 = A;
                    final int n10 = j * m + i;
                    array9[n10] *= g;
                }
            }
            else {
                for (int j = i; j < m; ++j) {
                    A[j * m + i] = 0.0;
                }
            }
            final double[] array10 = A;
            final int n11 = i * m + i;
            ++array10[n11];
        }
        for (int k = n - 1; k >= 0; --k) {
            int its = 1;
            while (its <= 30) {
                boolean flag = true;
                for (l = k; l >= 0; --l) {
                    nm = l - 1;
                    if (Math.abs(rv1[l]) + anorm == anorm) {
                        flag = false;
                        break;
                    }
                    if (Math.abs(w.getDiag(nm)) + anorm == anorm) {
                        break;
                    }
                }
                if (flag) {
                    double c = 0.0;
                    double s = 1.0;
                    for (int i = l; i <= k; ++i) {
                        final double f = s * rv1[i];
                        rv1[i] *= c;
                        if (Math.abs(f) + anorm == anorm) {
                            break;
                        }
                        g = w.getDiag(i);
                        double h = this.dpythag(f, g);
                        w.setDiag(i, h);
                        h = 1.0 / h;
                        c = g * h;
                        s = -f * h;
                        for (int j = 0; j < m; ++j) {
                            final double y = A[j * m + nm];
                            final double z = A[j * m + i];
                            A[j * m + nm] = y * c + z * s;
                            A[j * m + i] = z * c - y * s;
                        }
                    }
                }
                double z = w.getDiag(k);
                if (l == k) {
                    if (z < 0.0) {
                        w.setDiag(k, -z);
                        for (int j = 0; j < n; ++j) {
                            V[j * n + k] = -V[j * n + k];
                        }
                        break;
                    }
                    break;
                }
                else {
                    if (its == 30) {
                        return 0;
                    }
                    double x = w.getDiag(l);
                    nm = k - 1;
                    double y = w.getDiag(nm);
                    g = rv1[nm];
                    double h = rv1[k];
                    double f = ((y - z) * (y + z) + (g - h) * (g + h)) / (2.0 * h * y);
                    g = this.dpythag(f, 1.0);
                    f = ((x - z) * (x + z) + h * (y / (f + ((f >= 0.0) ? Math.abs(g) : (-Math.abs(g)))) - h)) / x;
                    double c;
                    double s = c = 1.0;
                    for (int j = l; j <= nm; ++j) {
                        final int i = j + 1;
                        g = rv1[i];
                        y = w.getDiag(i);
                        h = s * g;
                        g *= c;
                        z = this.dpythag(f, h);
                        rv1[j] = z;
                        c = f / z;
                        s = h / z;
                        f = x * c + g * s;
                        g = g * c - x * s;
                        h = y * s;
                        y *= c;
                        for (int jj = 0; jj < n; ++jj) {
                            x = V[jj * n + j];
                            z = V[jj * n + i];
                            V[jj * n + j] = x * c + z * s;
                            V[jj * n + i] = z * c - x * s;
                        }
                        z = this.dpythag(f, h);
                        w.setDiag(j, z);
                        if (z != 0.0) {
                            z = 1.0 / z;
                            c = f * z;
                            s = h * z;
                        }
                        f = c * g + s * y;
                        x = c * y - s * g;
                        for (int jj = 0; jj < m; ++jj) {
                            y = A[jj * m + j];
                            z = A[jj * m + i];
                            A[jj * m + j] = y * c + z * s;
                            A[jj * m + i] = z * c - y * s;
                        }
                    }
                    rv1[l] = 0.0;
                    rv1[k] = f;
                    w.setDiag(k, x);
                    ++its;
                }
            }
        }
        int rank = 0;
        for (int i = 0; i < n; ++i) {
            if (w.getDiag(i) > 0.0) {
                ++rank;
            }
        }
        return rank;
    }
    
    private void swapRows(final int i, final int j) {
        for (int k = 0; k < this.nCol; ++k) {
            final double tmp = this.elementData[i * this.nCol + k];
            this.elementData[i * this.nCol + k] = this.elementData[j * this.nCol + k];
            this.elementData[j * this.nCol + k] = tmp;
        }
    }
    
    public final int LUD(final GMatrix LU, final GVector permutation) {
        if (this.nRow != this.nCol) {
            throw new ArrayIndexOutOfBoundsException("not a square matrix");
        }
        final int n = this.nRow;
        if (n != LU.nRow) {
            throw new ArrayIndexOutOfBoundsException("this.nRow:" + n + " != LU.nRow:" + LU.nRow);
        }
        if (n != LU.nCol) {
            throw new ArrayIndexOutOfBoundsException("this.nCol:" + n + " != LU.nCol:" + LU.nCol);
        }
        if (permutation.getSize() < n) {
            throw new ArrayIndexOutOfBoundsException("permutation.size:" + permutation.getSize() + " < this.nCol:" + n);
        }
        if (this != LU) {
            LU.set(this);
        }
        int even = 1;
        final double[] a = LU.elementData;
        for (int i = 0; i < n; ++i) {
            permutation.setElement(i, i);
        }
        for (int j = 0; j < n; ++j) {
            for (int k = 0; k < j; ++k) {
                double sum = a[k * n + j];
                for (int l = 0; l < k; ++l) {
                    if (a[k * n + l] != 0.0 && a[l * n + j] != 0.0) {
                        sum -= a[k * n + l] * a[l * n + j];
                    }
                }
                a[k * n + j] = sum;
            }
            double big = 0.0;
            int imax = j;
            for (int m = j; m < n; ++m) {
                double sum = a[m * n + j];
                for (int k2 = 0; k2 < j; ++k2) {
                    if (a[m * n + k2] != 0.0 && a[k2 * n + j] != 0.0) {
                        sum -= a[m * n + k2] * a[k2 * n + j];
                    }
                }
                a[m * n + j] = sum;
                final double dum = Math.abs(sum);
                if (dum >= big) {
                    big = dum;
                    imax = m;
                }
            }
            if (j != imax) {
                LU.swapRows(imax, j);
                final double tmp = permutation.getElement(imax);
                permutation.setElement(imax, permutation.getElement(j));
                permutation.setElement(j, tmp);
                even = -even;
            }
            if (j != n - 1) {
                final double dum = 1.0 / a[j * n + j];
                for (int i2 = j + 1; i2 < n; ++i2) {
                    final double[] array = a;
                    final int n2 = i2 * n + j;
                    array[n2] *= dum;
                }
            }
        }
        return even;
    }
}
