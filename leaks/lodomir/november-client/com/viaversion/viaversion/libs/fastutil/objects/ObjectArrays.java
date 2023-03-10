/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.libs.fastutil.objects;

import com.viaversion.viaversion.libs.fastutil.Arrays;
import com.viaversion.viaversion.libs.fastutil.Hash;
import com.viaversion.viaversion.libs.fastutil.ints.IntArrays;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.Comparator;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveAction;

public final class ObjectArrays {
    public static final Object[] EMPTY_ARRAY = new Object[0];
    public static final Object[] DEFAULT_EMPTY_ARRAY = new Object[0];
    private static final int QUICKSORT_NO_REC = 16;
    private static final int PARALLEL_QUICKSORT_NO_FORK = 8192;
    private static final int QUICKSORT_MEDIAN_OF_9 = 128;
    private static final int MERGESORT_NO_REC = 16;
    public static final Hash.Strategy HASH_STRATEGY = new ArrayHashStrategy();

    private ObjectArrays() {
    }

    private static <K> K[] newArray(K[] prototype, int length) {
        Class<?> klass = prototype.getClass();
        if (klass == Object[].class) {
            return length == 0 ? EMPTY_ARRAY : new Object[length];
        }
        return (Object[])Array.newInstance(klass.getComponentType(), length);
    }

    public static <K> K[] forceCapacity(K[] array, int length, int preserve) {
        K[] t = ObjectArrays.newArray(array, length);
        System.arraycopy(array, 0, t, 0, preserve);
        return t;
    }

    public static <K> K[] ensureCapacity(K[] array, int length) {
        return ObjectArrays.ensureCapacity(array, length, array.length);
    }

    public static <K> K[] ensureCapacity(K[] array, int length, int preserve) {
        return length > array.length ? ObjectArrays.forceCapacity(array, length, preserve) : array;
    }

    public static <K> K[] grow(K[] array, int length) {
        return ObjectArrays.grow(array, length, array.length);
    }

    public static <K> K[] grow(K[] array, int length, int preserve) {
        if (length > array.length) {
            int newLength = (int)Math.max(Math.min((long)array.length + (long)(array.length >> 1), 0x7FFFFFF7L), (long)length);
            K[] t = ObjectArrays.newArray(array, newLength);
            System.arraycopy(array, 0, t, 0, preserve);
            return t;
        }
        return array;
    }

    public static <K> K[] trim(K[] array, int length) {
        if (length >= array.length) {
            return array;
        }
        K[] t = ObjectArrays.newArray(array, length);
        System.arraycopy(array, 0, t, 0, length);
        return t;
    }

    public static <K> K[] setLength(K[] array, int length) {
        if (length == array.length) {
            return array;
        }
        if (length < array.length) {
            return ObjectArrays.trim(array, length);
        }
        return ObjectArrays.ensureCapacity(array, length);
    }

    public static <K> K[] copy(K[] array, int offset, int length) {
        ObjectArrays.ensureOffsetLength(array, offset, length);
        K[] a = ObjectArrays.newArray(array, length);
        System.arraycopy(array, offset, a, 0, length);
        return a;
    }

    public static <K> K[] copy(K[] array) {
        return (Object[])array.clone();
    }

    @Deprecated
    public static <K> void fill(K[] array, K value) {
        int i = array.length;
        while (i-- != 0) {
            array[i] = value;
        }
    }

    @Deprecated
    public static <K> void fill(K[] array, int from, int to, K value) {
        ObjectArrays.ensureFromTo(array, from, to);
        if (from == 0) {
            while (to-- != 0) {
                array[to] = value;
            }
        } else {
            for (int i = from; i < to; ++i) {
                array[i] = value;
            }
        }
    }

    @Deprecated
    public static <K> boolean equals(K[] a1, K[] a2) {
        int i = a1.length;
        if (i != a2.length) {
            return false;
        }
        while (i-- != 0) {
            if (Objects.equals(a1[i], a2[i])) continue;
            return false;
        }
        return true;
    }

    public static <K> void ensureFromTo(K[] a, int from, int to) {
        Arrays.ensureFromTo(a.length, from, to);
    }

    public static <K> void ensureOffsetLength(K[] a, int offset, int length) {
        Arrays.ensureOffsetLength(a.length, offset, length);
    }

    public static <K> void ensureSameLength(K[] a, K[] b) {
        if (a.length != b.length) {
            throw new IllegalArgumentException("Array size mismatch: " + a.length + " != " + b.length);
        }
    }

    private static ForkJoinPool getPool() {
        ForkJoinPool current = ForkJoinTask.getPool();
        return current == null ? ForkJoinPool.commonPool() : current;
    }

    public static <K> void swap(K[] x, int a, int b) {
        K t = x[a];
        x[a] = x[b];
        x[b] = t;
    }

    public static <K> void swap(K[] x, int a, int b, int n) {
        int i = 0;
        while (i < n) {
            ObjectArrays.swap(x, a, b);
            ++i;
            ++a;
            ++b;
        }
    }

    private static <K> int med3(K[] x, int a, int b, int c, Comparator<K> comp) {
        int ab = comp.compare(x[a], x[b]);
        int ac = comp.compare(x[a], x[c]);
        int bc = comp.compare(x[b], x[c]);
        return ab < 0 ? (bc < 0 ? b : (ac < 0 ? c : a)) : (bc > 0 ? b : (ac > 0 ? c : a));
    }

    private static <K> void selectionSort(K[] a, int from, int to, Comparator<K> comp) {
        for (int i = from; i < to - 1; ++i) {
            int m = i;
            for (int j = i + 1; j < to; ++j) {
                if (comp.compare(a[j], a[m]) >= 0) continue;
                m = j;
            }
            if (m == i) continue;
            K u = a[i];
            a[i] = a[m];
            a[m] = u;
        }
    }

    private static <K> void insertionSort(K[] a, int from, int to, Comparator<K> comp) {
        int i = from;
        while (++i < to) {
            K t = a[i];
            int j = i;
            K u = a[j - 1];
            while (comp.compare(t, u) < 0) {
                a[j] = u;
                if (from == j - 1) {
                    --j;
                    break;
                }
                u = a[--j - 1];
            }
            a[j] = t;
        }
    }

    public static <K> void quickSort(K[] x, int from, int to, Comparator<K> comp) {
        int c;
        int a;
        int len = to - from;
        if (len < 16) {
            ObjectArrays.selectionSort(x, from, to, comp);
            return;
        }
        int m = from + len / 2;
        int l = from;
        int n = to - 1;
        if (len > 128) {
            int s = len / 8;
            l = ObjectArrays.med3(x, l, l + s, l + 2 * s, comp);
            m = ObjectArrays.med3(x, m - s, m, m + s, comp);
            n = ObjectArrays.med3(x, n - 2 * s, n - s, n, comp);
        }
        m = ObjectArrays.med3(x, l, m, n, comp);
        K v = x[m];
        int b = a = from;
        int d = c = to - 1;
        while (true) {
            int comparison;
            if (b <= c && (comparison = comp.compare(x[b], v)) <= 0) {
                if (comparison == 0) {
                    ObjectArrays.swap(x, a++, b);
                }
                ++b;
                continue;
            }
            while (c >= b && (comparison = comp.compare(x[c], v)) >= 0) {
                if (comparison == 0) {
                    ObjectArrays.swap(x, c, d--);
                }
                --c;
            }
            if (b > c) break;
            ObjectArrays.swap(x, b++, c--);
        }
        int s = Math.min(a - from, b - a);
        ObjectArrays.swap(x, from, b - s, s);
        s = Math.min(d - c, to - d - 1);
        ObjectArrays.swap(x, b, to - s, s);
        s = b - a;
        if (s > 1) {
            ObjectArrays.quickSort(x, from, from + s, comp);
        }
        if ((s = d - c) > 1) {
            ObjectArrays.quickSort(x, to - s, to, comp);
        }
    }

    public static <K> void quickSort(K[] x, Comparator<K> comp) {
        ObjectArrays.quickSort(x, 0, x.length, comp);
    }

    public static <K> void parallelQuickSort(K[] x, int from, int to, Comparator<K> comp) {
        ForkJoinPool pool = ObjectArrays.getPool();
        if (to - from < 8192 || pool.getParallelism() == 1) {
            ObjectArrays.quickSort(x, from, to, comp);
        } else {
            pool.invoke(new ForkJoinQuickSortComp<K>(x, from, to, comp));
        }
    }

    public static <K> void parallelQuickSort(K[] x, Comparator<K> comp) {
        ObjectArrays.parallelQuickSort(x, 0, x.length, comp);
    }

    private static <K> int med3(K[] x, int a, int b, int c) {
        int ab = ((Comparable)x[a]).compareTo(x[b]);
        int ac = ((Comparable)x[a]).compareTo(x[c]);
        int bc = ((Comparable)x[b]).compareTo(x[c]);
        return ab < 0 ? (bc < 0 ? b : (ac < 0 ? c : a)) : (bc > 0 ? b : (ac > 0 ? c : a));
    }

    private static <K> void selectionSort(K[] a, int from, int to) {
        for (int i = from; i < to - 1; ++i) {
            int m = i;
            for (int j = i + 1; j < to; ++j) {
                if (((Comparable)a[j]).compareTo(a[m]) >= 0) continue;
                m = j;
            }
            if (m == i) continue;
            K u = a[i];
            a[i] = a[m];
            a[m] = u;
        }
    }

    private static <K> void insertionSort(K[] a, int from, int to) {
        int i = from;
        while (++i < to) {
            K t = a[i];
            int j = i;
            K u = a[j - 1];
            while (((Comparable)t).compareTo(u) < 0) {
                a[j] = u;
                if (from == j - 1) {
                    --j;
                    break;
                }
                u = a[--j - 1];
            }
            a[j] = t;
        }
    }

    public static <K> void quickSort(K[] x, int from, int to) {
        int c;
        int a;
        int len = to - from;
        if (len < 16) {
            ObjectArrays.selectionSort(x, from, to);
            return;
        }
        int m = from + len / 2;
        int l = from;
        int n = to - 1;
        if (len > 128) {
            int s = len / 8;
            l = ObjectArrays.med3(x, l, l + s, l + 2 * s);
            m = ObjectArrays.med3(x, m - s, m, m + s);
            n = ObjectArrays.med3(x, n - 2 * s, n - s, n);
        }
        m = ObjectArrays.med3(x, l, m, n);
        K v = x[m];
        int b = a = from;
        int d = c = to - 1;
        while (true) {
            int comparison;
            if (b <= c && (comparison = ((Comparable)x[b]).compareTo(v)) <= 0) {
                if (comparison == 0) {
                    ObjectArrays.swap(x, a++, b);
                }
                ++b;
                continue;
            }
            while (c >= b && (comparison = ((Comparable)x[c]).compareTo(v)) >= 0) {
                if (comparison == 0) {
                    ObjectArrays.swap(x, c, d--);
                }
                --c;
            }
            if (b > c) break;
            ObjectArrays.swap(x, b++, c--);
        }
        int s = Math.min(a - from, b - a);
        ObjectArrays.swap(x, from, b - s, s);
        s = Math.min(d - c, to - d - 1);
        ObjectArrays.swap(x, b, to - s, s);
        s = b - a;
        if (s > 1) {
            ObjectArrays.quickSort(x, from, from + s);
        }
        if ((s = d - c) > 1) {
            ObjectArrays.quickSort(x, to - s, to);
        }
    }

    public static <K> void quickSort(K[] x) {
        ObjectArrays.quickSort(x, 0, x.length);
    }

    public static <K> void parallelQuickSort(K[] x, int from, int to) {
        ForkJoinPool pool = ObjectArrays.getPool();
        if (to - from < 8192 || pool.getParallelism() == 1) {
            ObjectArrays.quickSort(x, from, to);
        } else {
            pool.invoke(new ForkJoinQuickSort<K>(x, from, to));
        }
    }

    public static <K> void parallelQuickSort(K[] x) {
        ObjectArrays.parallelQuickSort(x, 0, x.length);
    }

    private static <K> int med3Indirect(int[] perm, K[] x, int a, int b, int c) {
        K aa = x[perm[a]];
        K bb = x[perm[b]];
        K cc = x[perm[c]];
        int ab = ((Comparable)aa).compareTo(bb);
        int ac = ((Comparable)aa).compareTo(cc);
        int bc = ((Comparable)bb).compareTo(cc);
        return ab < 0 ? (bc < 0 ? b : (ac < 0 ? c : a)) : (bc > 0 ? b : (ac > 0 ? c : a));
    }

    private static <K> void insertionSortIndirect(int[] perm, K[] a, int from, int to) {
        int i = from;
        while (++i < to) {
            int t = perm[i];
            int j = i;
            int u = perm[j - 1];
            while (((Comparable)a[t]).compareTo(a[u]) < 0) {
                perm[j] = u;
                if (from == j - 1) {
                    --j;
                    break;
                }
                u = perm[--j - 1];
            }
            perm[j] = t;
        }
    }

    public static <K> void quickSortIndirect(int[] perm, K[] x, int from, int to) {
        int c;
        int a;
        int len = to - from;
        if (len < 16) {
            ObjectArrays.insertionSortIndirect(perm, x, from, to);
            return;
        }
        int m = from + len / 2;
        int l = from;
        int n = to - 1;
        if (len > 128) {
            int s = len / 8;
            l = ObjectArrays.med3Indirect(perm, x, l, l + s, l + 2 * s);
            m = ObjectArrays.med3Indirect(perm, x, m - s, m, m + s);
            n = ObjectArrays.med3Indirect(perm, x, n - 2 * s, n - s, n);
        }
        m = ObjectArrays.med3Indirect(perm, x, l, m, n);
        K v = x[perm[m]];
        int b = a = from;
        int d = c = to - 1;
        while (true) {
            int comparison;
            if (b <= c && (comparison = ((Comparable)x[perm[b]]).compareTo(v)) <= 0) {
                if (comparison == 0) {
                    IntArrays.swap(perm, a++, b);
                }
                ++b;
                continue;
            }
            while (c >= b && (comparison = ((Comparable)x[perm[c]]).compareTo(v)) >= 0) {
                if (comparison == 0) {
                    IntArrays.swap(perm, c, d--);
                }
                --c;
            }
            if (b > c) break;
            IntArrays.swap(perm, b++, c--);
        }
        int s = Math.min(a - from, b - a);
        IntArrays.swap(perm, from, b - s, s);
        s = Math.min(d - c, to - d - 1);
        IntArrays.swap(perm, b, to - s, s);
        s = b - a;
        if (s > 1) {
            ObjectArrays.quickSortIndirect(perm, x, from, from + s);
        }
        if ((s = d - c) > 1) {
            ObjectArrays.quickSortIndirect(perm, x, to - s, to);
        }
    }

    public static <K> void quickSortIndirect(int[] perm, K[] x) {
        ObjectArrays.quickSortIndirect(perm, x, 0, x.length);
    }

    public static <K> void parallelQuickSortIndirect(int[] perm, K[] x, int from, int to) {
        ForkJoinPool pool = ObjectArrays.getPool();
        if (to - from < 8192 || pool.getParallelism() == 1) {
            ObjectArrays.quickSortIndirect(perm, x, from, to);
        } else {
            pool.invoke(new ForkJoinQuickSortIndirect<K>(perm, x, from, to));
        }
    }

    public static <K> void parallelQuickSortIndirect(int[] perm, K[] x) {
        ObjectArrays.parallelQuickSortIndirect(perm, x, 0, x.length);
    }

    public static <K> void stabilize(int[] perm, K[] x, int from, int to) {
        int curr = from;
        for (int i = from + 1; i < to; ++i) {
            if (x[perm[i]] == x[perm[curr]]) continue;
            if (i - curr > 1) {
                IntArrays.parallelQuickSort(perm, curr, i);
            }
            curr = i;
        }
        if (to - curr > 1) {
            IntArrays.parallelQuickSort(perm, curr, to);
        }
    }

    public static <K> void stabilize(int[] perm, K[] x) {
        ObjectArrays.stabilize(perm, x, 0, perm.length);
    }

    private static <K> int med3(K[] x, K[] y, int a, int b, int c) {
        int bc;
        int t = ((Comparable)x[a]).compareTo(x[b]);
        int ab = t == 0 ? ((Comparable)y[a]).compareTo(y[b]) : t;
        t = ((Comparable)x[a]).compareTo(x[c]);
        int ac = t == 0 ? ((Comparable)y[a]).compareTo(y[c]) : t;
        t = ((Comparable)x[b]).compareTo(x[c]);
        int n = bc = t == 0 ? ((Comparable)y[b]).compareTo(y[c]) : t;
        return ab < 0 ? (bc < 0 ? b : (ac < 0 ? c : a)) : (bc > 0 ? b : (ac > 0 ? c : a));
    }

    private static <K> void swap(K[] x, K[] y, int a, int b) {
        K t = x[a];
        K u = y[a];
        x[a] = x[b];
        y[a] = y[b];
        x[b] = t;
        y[b] = u;
    }

    private static <K> void swap(K[] x, K[] y, int a, int b, int n) {
        int i = 0;
        while (i < n) {
            ObjectArrays.swap(x, y, a, b);
            ++i;
            ++a;
            ++b;
        }
    }

    private static <K> void selectionSort(K[] a, K[] b, int from, int to) {
        for (int i = from; i < to - 1; ++i) {
            int m = i;
            for (int j = i + 1; j < to; ++j) {
                int u = ((Comparable)a[j]).compareTo(a[m]);
                if (u >= 0 && (u != 0 || ((Comparable)b[j]).compareTo(b[m]) >= 0)) continue;
                m = j;
            }
            if (m == i) continue;
            K t = a[i];
            a[i] = a[m];
            a[m] = t;
            t = b[i];
            b[i] = b[m];
            b[m] = t;
        }
    }

    public static <K> void quickSort(K[] x, K[] y, int from, int to) {
        int c;
        int a;
        int len = to - from;
        if (len < 16) {
            ObjectArrays.selectionSort(x, y, from, to);
            return;
        }
        int m = from + len / 2;
        int l = from;
        int n = to - 1;
        if (len > 128) {
            int s = len / 8;
            l = ObjectArrays.med3(x, y, l, l + s, l + 2 * s);
            m = ObjectArrays.med3(x, y, m - s, m, m + s);
            n = ObjectArrays.med3(x, y, n - 2 * s, n - s, n);
        }
        m = ObjectArrays.med3(x, y, l, m, n);
        K v = x[m];
        K w = y[m];
        int b = a = from;
        int d = c = to - 1;
        while (true) {
            int t;
            int comparison;
            if (b <= c && (comparison = (t = ((Comparable)x[b]).compareTo(v)) == 0 ? ((Comparable)y[b]).compareTo(w) : t) <= 0) {
                if (comparison == 0) {
                    ObjectArrays.swap(x, y, a++, b);
                }
                ++b;
                continue;
            }
            while (c >= b && (comparison = (t = ((Comparable)x[c]).compareTo(v)) == 0 ? ((Comparable)y[c]).compareTo(w) : t) >= 0) {
                if (comparison == 0) {
                    ObjectArrays.swap(x, y, c, d--);
                }
                --c;
            }
            if (b > c) break;
            ObjectArrays.swap(x, y, b++, c--);
        }
        int s = Math.min(a - from, b - a);
        ObjectArrays.swap(x, y, from, b - s, s);
        s = Math.min(d - c, to - d - 1);
        ObjectArrays.swap(x, y, b, to - s, s);
        s = b - a;
        if (s > 1) {
            ObjectArrays.quickSort(x, y, from, from + s);
        }
        if ((s = d - c) > 1) {
            ObjectArrays.quickSort(x, y, to - s, to);
        }
    }

    public static <K> void quickSort(K[] x, K[] y) {
        ObjectArrays.ensureSameLength(x, y);
        ObjectArrays.quickSort(x, y, 0, x.length);
    }

    public static <K> void parallelQuickSort(K[] x, K[] y, int from, int to) {
        ForkJoinPool pool = ObjectArrays.getPool();
        if (to - from < 8192 || pool.getParallelism() == 1) {
            ObjectArrays.quickSort(x, y, from, to);
        } else {
            pool.invoke(new ForkJoinQuickSort2<K>(x, y, from, to));
        }
    }

    public static <K> void parallelQuickSort(K[] x, K[] y) {
        ObjectArrays.ensureSameLength(x, y);
        ObjectArrays.parallelQuickSort(x, y, 0, x.length);
    }

    public static <K> void unstableSort(K[] a, int from, int to) {
        ObjectArrays.quickSort(a, from, to);
    }

    public static <K> void unstableSort(K[] a) {
        ObjectArrays.unstableSort(a, 0, a.length);
    }

    public static <K> void unstableSort(K[] a, int from, int to, Comparator<K> comp) {
        ObjectArrays.quickSort(a, from, to, comp);
    }

    public static <K> void unstableSort(K[] a, Comparator<K> comp) {
        ObjectArrays.unstableSort(a, 0, a.length, comp);
    }

    public static <K> void mergeSort(K[] a, int from, int to, K[] supp) {
        int len = to - from;
        if (len < 16) {
            ObjectArrays.insertionSort(a, from, to);
            return;
        }
        if (supp == null) {
            supp = java.util.Arrays.copyOf(a, to);
        }
        int mid = from + to >>> 1;
        ObjectArrays.mergeSort(supp, from, mid, a);
        ObjectArrays.mergeSort(supp, mid, to, a);
        if (((Comparable)supp[mid - 1]).compareTo(supp[mid]) <= 0) {
            System.arraycopy(supp, from, a, from, len);
            return;
        }
        int p = from;
        int q = mid;
        for (int i = from; i < to; ++i) {
            a[i] = q >= to || p < mid && ((Comparable)supp[p]).compareTo(supp[q]) <= 0 ? supp[p++] : supp[q++];
        }
    }

    public static <K> void mergeSort(K[] a, int from, int to) {
        ObjectArrays.mergeSort(a, from, to, (Object[])null);
    }

    public static <K> void mergeSort(K[] a) {
        ObjectArrays.mergeSort(a, 0, a.length);
    }

    public static <K> void mergeSort(K[] a, int from, int to, Comparator<K> comp, K[] supp) {
        int len = to - from;
        if (len < 16) {
            ObjectArrays.insertionSort(a, from, to, comp);
            return;
        }
        if (supp == null) {
            supp = java.util.Arrays.copyOf(a, to);
        }
        int mid = from + to >>> 1;
        ObjectArrays.mergeSort(supp, from, mid, comp, a);
        ObjectArrays.mergeSort(supp, mid, to, comp, a);
        if (comp.compare(supp[mid - 1], supp[mid]) <= 0) {
            System.arraycopy(supp, from, a, from, len);
            return;
        }
        int p = from;
        int q = mid;
        for (int i = from; i < to; ++i) {
            a[i] = q >= to || p < mid && comp.compare(supp[p], supp[q]) <= 0 ? supp[p++] : supp[q++];
        }
    }

    public static <K> void mergeSort(K[] a, int from, int to, Comparator<K> comp) {
        ObjectArrays.mergeSort(a, from, to, comp, null);
    }

    public static <K> void mergeSort(K[] a, Comparator<K> comp) {
        ObjectArrays.mergeSort(a, 0, a.length, comp);
    }

    public static <K> void stableSort(K[] a, int from, int to) {
        java.util.Arrays.sort(a, from, to);
    }

    public static <K> void stableSort(K[] a) {
        ObjectArrays.stableSort(a, 0, a.length);
    }

    public static <K> void stableSort(K[] a, int from, int to, Comparator<K> comp) {
        java.util.Arrays.sort(a, from, to, comp);
    }

    public static <K> void stableSort(K[] a, Comparator<K> comp) {
        ObjectArrays.stableSort(a, 0, a.length, comp);
    }

    public static <K> int binarySearch(K[] a, int from, int to, K key) {
        --to;
        while (from <= to) {
            int mid = from + to >>> 1;
            K midVal = a[mid];
            int cmp = ((Comparable)midVal).compareTo(key);
            if (cmp < 0) {
                from = mid + 1;
                continue;
            }
            if (cmp > 0) {
                to = mid - 1;
                continue;
            }
            return mid;
        }
        return -(from + 1);
    }

    public static <K> int binarySearch(K[] a, K key) {
        return ObjectArrays.binarySearch(a, 0, a.length, key);
    }

    public static <K> int binarySearch(K[] a, int from, int to, K key, Comparator<K> c) {
        --to;
        while (from <= to) {
            int mid = from + to >>> 1;
            K midVal = a[mid];
            int cmp = c.compare(midVal, key);
            if (cmp < 0) {
                from = mid + 1;
                continue;
            }
            if (cmp > 0) {
                to = mid - 1;
                continue;
            }
            return mid;
        }
        return -(from + 1);
    }

    public static <K> int binarySearch(K[] a, K key, Comparator<K> c) {
        return ObjectArrays.binarySearch(a, 0, a.length, key, c);
    }

    public static <K> K[] shuffle(K[] a, int from, int to, Random random) {
        int i = to - from;
        while (i-- != 0) {
            int p = random.nextInt(i + 1);
            K t = a[from + i];
            a[from + i] = a[from + p];
            a[from + p] = t;
        }
        return a;
    }

    public static <K> K[] shuffle(K[] a, Random random) {
        int i = a.length;
        while (i-- != 0) {
            int p = random.nextInt(i + 1);
            K t = a[i];
            a[i] = a[p];
            a[p] = t;
        }
        return a;
    }

    public static <K> K[] reverse(K[] a) {
        int length = a.length;
        int i = length / 2;
        while (i-- != 0) {
            K t = a[length - i - 1];
            a[length - i - 1] = a[i];
            a[i] = t;
        }
        return a;
    }

    public static <K> K[] reverse(K[] a, int from, int to) {
        int length = to - from;
        int i = length / 2;
        while (i-- != 0) {
            K t = a[from + length - i - 1];
            a[from + length - i - 1] = a[from + i];
            a[from + i] = t;
        }
        return a;
    }

    protected static class ForkJoinQuickSortComp<K>
    extends RecursiveAction {
        private static final long serialVersionUID = 1L;
        private final int from;
        private final int to;
        private final K[] x;
        private final Comparator<K> comp;

        public ForkJoinQuickSortComp(K[] x, int from, int to, Comparator<K> comp) {
            this.from = from;
            this.to = to;
            this.x = x;
            this.comp = comp;
        }

        @Override
        protected void compute() {
            int c;
            int a;
            Object[] x = this.x;
            int len = this.to - this.from;
            if (len < 8192) {
                ObjectArrays.quickSort(x, this.from, this.to, this.comp);
                return;
            }
            int m = this.from + len / 2;
            int l = this.from;
            int n = this.to - 1;
            int s = len / 8;
            l = ObjectArrays.med3(x, l, l + s, l + 2 * s, this.comp);
            m = ObjectArrays.med3(x, m - s, m, m + s, this.comp);
            n = ObjectArrays.med3(x, n - 2 * s, n - s, n, this.comp);
            m = ObjectArrays.med3(x, l, m, n, this.comp);
            Object v = x[m];
            int b = a = this.from;
            int d = c = this.to - 1;
            while (true) {
                int comparison;
                if (b <= c && (comparison = this.comp.compare(x[b], v)) <= 0) {
                    if (comparison == 0) {
                        ObjectArrays.swap(x, a++, b);
                    }
                    ++b;
                    continue;
                }
                while (c >= b && (comparison = this.comp.compare(x[c], v)) >= 0) {
                    if (comparison == 0) {
                        ObjectArrays.swap(x, c, d--);
                    }
                    --c;
                }
                if (b > c) break;
                ObjectArrays.swap(x, b++, c--);
            }
            s = Math.min(a - this.from, b - a);
            ObjectArrays.swap(x, this.from, b - s, s);
            s = Math.min(d - c, this.to - d - 1);
            ObjectArrays.swap(x, b, this.to - s, s);
            s = b - a;
            int t = d - c;
            if (s > 1 && t > 1) {
                ForkJoinQuickSortComp.invokeAll(new ForkJoinQuickSortComp<Object>(x, this.from, this.from + s, this.comp), new ForkJoinQuickSortComp<Object>(x, this.to - t, this.to, this.comp));
            } else if (s > 1) {
                ForkJoinQuickSortComp.invokeAll(new ForkJoinQuickSortComp<Object>(x, this.from, this.from + s, this.comp));
            } else {
                ForkJoinQuickSortComp.invokeAll(new ForkJoinQuickSortComp<Object>(x, this.to - t, this.to, this.comp));
            }
        }
    }

    protected static class ForkJoinQuickSort<K>
    extends RecursiveAction {
        private static final long serialVersionUID = 1L;
        private final int from;
        private final int to;
        private final K[] x;

        public ForkJoinQuickSort(K[] x, int from, int to) {
            this.from = from;
            this.to = to;
            this.x = x;
        }

        @Override
        protected void compute() {
            int c;
            int a;
            Object[] x = this.x;
            int len = this.to - this.from;
            if (len < 8192) {
                ObjectArrays.quickSort(x, this.from, this.to);
                return;
            }
            int m = this.from + len / 2;
            int l = this.from;
            int n = this.to - 1;
            int s = len / 8;
            l = ObjectArrays.med3(x, l, l + s, l + 2 * s);
            m = ObjectArrays.med3(x, m - s, m, m + s);
            n = ObjectArrays.med3(x, n - 2 * s, n - s, n);
            m = ObjectArrays.med3(x, l, m, n);
            Object v = x[m];
            int b = a = this.from;
            int d = c = this.to - 1;
            while (true) {
                int comparison;
                if (b <= c && (comparison = ((Comparable)x[b]).compareTo(v)) <= 0) {
                    if (comparison == 0) {
                        ObjectArrays.swap(x, a++, b);
                    }
                    ++b;
                    continue;
                }
                while (c >= b && (comparison = ((Comparable)x[c]).compareTo(v)) >= 0) {
                    if (comparison == 0) {
                        ObjectArrays.swap(x, c, d--);
                    }
                    --c;
                }
                if (b > c) break;
                ObjectArrays.swap(x, b++, c--);
            }
            s = Math.min(a - this.from, b - a);
            ObjectArrays.swap(x, this.from, b - s, s);
            s = Math.min(d - c, this.to - d - 1);
            ObjectArrays.swap(x, b, this.to - s, s);
            s = b - a;
            int t = d - c;
            if (s > 1 && t > 1) {
                ForkJoinQuickSort.invokeAll(new ForkJoinQuickSort<Object>(x, this.from, this.from + s), new ForkJoinQuickSort<Object>(x, this.to - t, this.to));
            } else if (s > 1) {
                ForkJoinQuickSort.invokeAll(new ForkJoinQuickSort<Object>(x, this.from, this.from + s));
            } else {
                ForkJoinQuickSort.invokeAll(new ForkJoinQuickSort<Object>(x, this.to - t, this.to));
            }
        }
    }

    protected static class ForkJoinQuickSortIndirect<K>
    extends RecursiveAction {
        private static final long serialVersionUID = 1L;
        private final int from;
        private final int to;
        private final int[] perm;
        private final K[] x;

        public ForkJoinQuickSortIndirect(int[] perm, K[] x, int from, int to) {
            this.from = from;
            this.to = to;
            this.x = x;
            this.perm = perm;
        }

        @Override
        protected void compute() {
            int c;
            int a;
            Object[] x = this.x;
            int len = this.to - this.from;
            if (len < 8192) {
                ObjectArrays.quickSortIndirect(this.perm, x, this.from, this.to);
                return;
            }
            int m = this.from + len / 2;
            int l = this.from;
            int n = this.to - 1;
            int s = len / 8;
            l = ObjectArrays.med3Indirect(this.perm, x, l, l + s, l + 2 * s);
            m = ObjectArrays.med3Indirect(this.perm, x, m - s, m, m + s);
            n = ObjectArrays.med3Indirect(this.perm, x, n - 2 * s, n - s, n);
            m = ObjectArrays.med3Indirect(this.perm, x, l, m, n);
            Object v = x[this.perm[m]];
            int b = a = this.from;
            int d = c = this.to - 1;
            while (true) {
                int comparison;
                if (b <= c && (comparison = ((Comparable)x[this.perm[b]]).compareTo(v)) <= 0) {
                    if (comparison == 0) {
                        IntArrays.swap(this.perm, a++, b);
                    }
                    ++b;
                    continue;
                }
                while (c >= b && (comparison = ((Comparable)x[this.perm[c]]).compareTo(v)) >= 0) {
                    if (comparison == 0) {
                        IntArrays.swap(this.perm, c, d--);
                    }
                    --c;
                }
                if (b > c) break;
                IntArrays.swap(this.perm, b++, c--);
            }
            s = Math.min(a - this.from, b - a);
            IntArrays.swap(this.perm, this.from, b - s, s);
            s = Math.min(d - c, this.to - d - 1);
            IntArrays.swap(this.perm, b, this.to - s, s);
            s = b - a;
            int t = d - c;
            if (s > 1 && t > 1) {
                ForkJoinQuickSortIndirect.invokeAll(new ForkJoinQuickSortIndirect<Object>(this.perm, x, this.from, this.from + s), new ForkJoinQuickSortIndirect<Object>(this.perm, x, this.to - t, this.to));
            } else if (s > 1) {
                ForkJoinQuickSortIndirect.invokeAll(new ForkJoinQuickSortIndirect<Object>(this.perm, x, this.from, this.from + s));
            } else {
                ForkJoinQuickSortIndirect.invokeAll(new ForkJoinQuickSortIndirect<Object>(this.perm, x, this.to - t, this.to));
            }
        }
    }

    protected static class ForkJoinQuickSort2<K>
    extends RecursiveAction {
        private static final long serialVersionUID = 1L;
        private final int from;
        private final int to;
        private final K[] x;
        private final K[] y;

        public ForkJoinQuickSort2(K[] x, K[] y, int from, int to) {
            this.from = from;
            this.to = to;
            this.x = x;
            this.y = y;
        }

        @Override
        protected void compute() {
            int c;
            int a;
            Object[] x = this.x;
            Object[] y = this.y;
            int len = this.to - this.from;
            if (len < 8192) {
                ObjectArrays.quickSort(x, y, this.from, this.to);
                return;
            }
            int m = this.from + len / 2;
            int l = this.from;
            int n = this.to - 1;
            int s = len / 8;
            l = ObjectArrays.med3(x, y, l, l + s, l + 2 * s);
            m = ObjectArrays.med3(x, y, m - s, m, m + s);
            n = ObjectArrays.med3(x, y, n - 2 * s, n - s, n);
            m = ObjectArrays.med3(x, y, l, m, n);
            Object v = x[m];
            Object w = y[m];
            int b = a = this.from;
            int d = c = this.to - 1;
            while (true) {
                int t;
                int comparison;
                if (b <= c && (comparison = (t = ((Comparable)x[b]).compareTo(v)) == 0 ? ((Comparable)y[b]).compareTo(w) : t) <= 0) {
                    if (comparison == 0) {
                        ObjectArrays.swap(x, y, a++, b);
                    }
                    ++b;
                    continue;
                }
                while (c >= b && (comparison = (t = ((Comparable)x[c]).compareTo(v)) == 0 ? ((Comparable)y[c]).compareTo(w) : t) >= 0) {
                    if (comparison == 0) {
                        ObjectArrays.swap(x, y, c, d--);
                    }
                    --c;
                }
                if (b > c) break;
                ObjectArrays.swap(x, y, b++, c--);
            }
            s = Math.min(a - this.from, b - a);
            ObjectArrays.swap(x, y, this.from, b - s, s);
            s = Math.min(d - c, this.to - d - 1);
            ObjectArrays.swap(x, y, b, this.to - s, s);
            s = b - a;
            int t = d - c;
            if (s > 1 && t > 1) {
                ForkJoinQuickSort2.invokeAll(new ForkJoinQuickSort2<Object>(x, y, this.from, this.from + s), new ForkJoinQuickSort2<Object>(x, y, this.to - t, this.to));
            } else if (s > 1) {
                ForkJoinQuickSort2.invokeAll(new ForkJoinQuickSort2<Object>(x, y, this.from, this.from + s));
            } else {
                ForkJoinQuickSort2.invokeAll(new ForkJoinQuickSort2<Object>(x, y, this.to - t, this.to));
            }
        }
    }

    private static final class ArrayHashStrategy<K>
    implements Hash.Strategy<K[]>,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;

        private ArrayHashStrategy() {
        }

        @Override
        public int hashCode(K[] o) {
            return java.util.Arrays.hashCode(o);
        }

        @Override
        public boolean equals(K[] a, K[] b) {
            return java.util.Arrays.equals(a, b);
        }
    }
}

