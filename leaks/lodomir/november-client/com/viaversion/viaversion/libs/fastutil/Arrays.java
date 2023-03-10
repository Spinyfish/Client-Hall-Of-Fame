/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.libs.fastutil;

import com.viaversion.viaversion.libs.fastutil.Swapper;
import com.viaversion.viaversion.libs.fastutil.ints.IntComparator;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveAction;

public class Arrays {
    public static final int MAX_ARRAY_SIZE = 0x7FFFFFF7;
    private static final int MERGESORT_NO_REC = 16;
    private static final int QUICKSORT_NO_REC = 16;
    private static final int PARALLEL_QUICKSORT_NO_FORK = 8192;
    private static final int QUICKSORT_MEDIAN_OF_9 = 128;

    private Arrays() {
    }

    public static void ensureFromTo(int arrayLength, int from, int to) {
        if (from < 0) {
            throw new ArrayIndexOutOfBoundsException("Start index (" + from + ") is negative");
        }
        if (from > to) {
            throw new IllegalArgumentException("Start index (" + from + ") is greater than end index (" + to + ")");
        }
        if (to > arrayLength) {
            throw new ArrayIndexOutOfBoundsException("End index (" + to + ") is greater than array length (" + arrayLength + ")");
        }
    }

    public static void ensureOffsetLength(int arrayLength, int offset, int length) {
        if (offset < 0) {
            throw new ArrayIndexOutOfBoundsException("Offset (" + offset + ") is negative");
        }
        if (length < 0) {
            throw new IllegalArgumentException("Length (" + length + ") is negative");
        }
        if (offset + length > arrayLength) {
            throw new ArrayIndexOutOfBoundsException("Last index (" + (offset + length) + ") is greater than array length (" + arrayLength + ")");
        }
    }

    private static void inPlaceMerge(int from, int mid, int to, IntComparator comp, Swapper swapper) {
        int secondCut;
        int firstCut;
        if (from >= mid || mid >= to) {
            return;
        }
        if (to - from == 2) {
            if (comp.compare(mid, from) < 0) {
                swapper.swap(from, mid);
            }
            return;
        }
        if (mid - from > to - mid) {
            firstCut = from + (mid - from) / 2;
            secondCut = Arrays.lowerBound(mid, to, firstCut, comp);
        } else {
            secondCut = mid + (to - mid) / 2;
            firstCut = Arrays.upperBound(from, mid, secondCut, comp);
        }
        int first2 = firstCut;
        int middle2 = mid;
        int last2 = secondCut;
        if (middle2 != first2 && middle2 != last2) {
            int first1 = first2;
            int last1 = middle2;
            while (first1 < --last1) {
                swapper.swap(first1++, last1);
            }
            first1 = middle2;
            last1 = last2;
            while (first1 < --last1) {
                swapper.swap(first1++, last1);
            }
            first1 = first2;
            last1 = last2;
            while (first1 < --last1) {
                swapper.swap(first1++, last1);
            }
        }
        mid = firstCut + (secondCut - mid);
        Arrays.inPlaceMerge(from, firstCut, mid, comp, swapper);
        Arrays.inPlaceMerge(mid, secondCut, to, comp, swapper);
    }

    private static int lowerBound(int from, int to, int pos, IntComparator comp) {
        int len = to - from;
        while (len > 0) {
            int half = len / 2;
            int middle = from + half;
            if (comp.compare(middle, pos) < 0) {
                from = middle + 1;
                len -= half + 1;
                continue;
            }
            len = half;
        }
        return from;
    }

    private static int upperBound(int from, int mid, int pos, IntComparator comp) {
        int len = mid - from;
        while (len > 0) {
            int half = len / 2;
            int middle = from + half;
            if (comp.compare(pos, middle) < 0) {
                len = half;
                continue;
            }
            from = middle + 1;
            len -= half + 1;
        }
        return from;
    }

    private static int med3(int a, int b, int c, IntComparator comp) {
        int ab = comp.compare(a, b);
        int ac = comp.compare(a, c);
        int bc = comp.compare(b, c);
        return ab < 0 ? (bc < 0 ? b : (ac < 0 ? c : a)) : (bc > 0 ? b : (ac > 0 ? c : a));
    }

    private static ForkJoinPool getPool() {
        ForkJoinPool current = ForkJoinTask.getPool();
        return current == null ? ForkJoinPool.commonPool() : current;
    }

    public static void mergeSort(int from, int to, IntComparator c, Swapper swapper) {
        int length = to - from;
        if (length < 16) {
            for (int i = from; i < to; ++i) {
                for (int j = i; j > from && c.compare(j - 1, j) > 0; --j) {
                    swapper.swap(j, j - 1);
                }
            }
            return;
        }
        int mid = from + to >>> 1;
        Arrays.mergeSort(from, mid, c, swapper);
        Arrays.mergeSort(mid, to, c, swapper);
        if (c.compare(mid - 1, mid) <= 0) {
            return;
        }
        Arrays.inPlaceMerge(from, mid, to, c, swapper);
    }

    protected static void swap(Swapper swapper, int a, int b, int n) {
        int i = 0;
        while (i < n) {
            swapper.swap(a, b);
            ++i;
            ++a;
            ++b;
        }
    }

    public static void parallelQuickSort(int from, int to, IntComparator comp, Swapper swapper) {
        ForkJoinPool pool = Arrays.getPool();
        if (to - from < 8192 || pool.getParallelism() == 1) {
            Arrays.quickSort(from, to, comp, swapper);
        } else {
            pool.invoke(new ForkJoinGenericQuickSort(from, to, comp, swapper));
        }
    }

    public static void quickSort(int from, int to, IntComparator comp, Swapper swapper) {
        int c;
        int a;
        int len = to - from;
        if (len < 16) {
            for (int i = from; i < to; ++i) {
                for (int j = i; j > from && comp.compare(j - 1, j) > 0; --j) {
                    swapper.swap(j, j - 1);
                }
            }
            return;
        }
        int m = from + len / 2;
        int l = from;
        int n = to - 1;
        if (len > 128) {
            int s = len / 8;
            l = Arrays.med3(l, l + s, l + 2 * s, comp);
            m = Arrays.med3(m - s, m, m + s, comp);
            n = Arrays.med3(n - 2 * s, n - s, n, comp);
        }
        m = Arrays.med3(l, m, n, comp);
        int b = a = from;
        int d = c = to - 1;
        while (true) {
            int comparison;
            if (b <= c && (comparison = comp.compare(b, m)) <= 0) {
                if (comparison == 0) {
                    if (a == m) {
                        m = b;
                    } else if (b == m) {
                        m = a;
                    }
                    swapper.swap(a++, b);
                }
                ++b;
                continue;
            }
            while (c >= b && (comparison = comp.compare(c, m)) >= 0) {
                if (comparison == 0) {
                    if (c == m) {
                        m = d;
                    } else if (d == m) {
                        m = c;
                    }
                    swapper.swap(c, d--);
                }
                --c;
            }
            if (b > c) break;
            if (b == m) {
                m = d;
            } else if (c == m) {
                m = c;
            }
            swapper.swap(b++, c--);
        }
        int s = Math.min(a - from, b - a);
        Arrays.swap(swapper, from, b - s, s);
        s = Math.min(d - c, to - d - 1);
        Arrays.swap(swapper, b, to - s, s);
        s = b - a;
        if (s > 1) {
            Arrays.quickSort(from, from + s, comp, swapper);
        }
        if ((s = d - c) > 1) {
            Arrays.quickSort(to - s, to, comp, swapper);
        }
    }

    protected static class ForkJoinGenericQuickSort
    extends RecursiveAction {
        private static final long serialVersionUID = 1L;
        private final int from;
        private final int to;
        private final IntComparator comp;
        private final Swapper swapper;

        public ForkJoinGenericQuickSort(int from, int to, IntComparator comp, Swapper swapper) {
            this.from = from;
            this.to = to;
            this.comp = comp;
            this.swapper = swapper;
        }

        @Override
        protected void compute() {
            int c;
            int a;
            int len = this.to - this.from;
            if (len < 8192) {
                Arrays.quickSort(this.from, this.to, this.comp, this.swapper);
                return;
            }
            int m = this.from + len / 2;
            int l = this.from;
            int n = this.to - 1;
            int s = len / 8;
            l = Arrays.med3(l, l + s, l + 2 * s, this.comp);
            m = Arrays.med3(m - s, m, m + s, this.comp);
            n = Arrays.med3(n - 2 * s, n - s, n, this.comp);
            m = Arrays.med3(l, m, n, this.comp);
            int b = a = this.from;
            int d = c = this.to - 1;
            while (true) {
                int comparison;
                if (b <= c && (comparison = this.comp.compare(b, m)) <= 0) {
                    if (comparison == 0) {
                        if (a == m) {
                            m = b;
                        } else if (b == m) {
                            m = a;
                        }
                        this.swapper.swap(a++, b);
                    }
                    ++b;
                    continue;
                }
                while (c >= b && (comparison = this.comp.compare(c, m)) >= 0) {
                    if (comparison == 0) {
                        if (c == m) {
                            m = d;
                        } else if (d == m) {
                            m = c;
                        }
                        this.swapper.swap(c, d--);
                    }
                    --c;
                }
                if (b > c) break;
                if (b == m) {
                    m = d;
                } else if (c == m) {
                    m = c;
                }
                this.swapper.swap(b++, c--);
            }
            s = Math.min(a - this.from, b - a);
            Arrays.swap(this.swapper, this.from, b - s, s);
            s = Math.min(d - c, this.to - d - 1);
            Arrays.swap(this.swapper, b, this.to - s, s);
            s = b - a;
            int t = d - c;
            if (s > 1 && t > 1) {
                ForkJoinGenericQuickSort.invokeAll(new ForkJoinGenericQuickSort(this.from, this.from + s, this.comp, this.swapper), new ForkJoinGenericQuickSort(this.to - t, this.to, this.comp, this.swapper));
            } else if (s > 1) {
                ForkJoinGenericQuickSort.invokeAll(new ForkJoinGenericQuickSort(this.from, this.from + s, this.comp, this.swapper));
            } else {
                ForkJoinGenericQuickSort.invokeAll(new ForkJoinGenericQuickSort(this.to - t, this.to, this.comp, this.swapper));
            }
        }
    }
}

