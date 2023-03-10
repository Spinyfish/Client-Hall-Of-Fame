/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.libs.fastutil.ints;

import com.viaversion.viaversion.libs.fastutil.ints.IntComparator;
import java.io.Serializable;
import java.util.Comparator;

public final class IntComparators {
    public static final IntComparator NATURAL_COMPARATOR = new NaturalImplicitComparator();
    public static final IntComparator OPPOSITE_COMPARATOR = new OppositeImplicitComparator();

    private IntComparators() {
    }

    public static IntComparator oppositeComparator(IntComparator c) {
        if (c instanceof OppositeComparator) {
            return ((OppositeComparator)c).comparator;
        }
        return new OppositeComparator(c);
    }

    public static IntComparator asIntComparator(final Comparator<? super Integer> c) {
        if (c == null || c instanceof IntComparator) {
            return (IntComparator)c;
        }
        return new IntComparator(){

            @Override
            public int compare(int x, int y) {
                return c.compare(x, y);
            }

            @Override
            public int compare(Integer x, Integer y) {
                return c.compare(x, y);
            }
        };
    }

    protected static class OppositeComparator
    implements IntComparator,
    Serializable {
        private static final long serialVersionUID = 1L;
        final IntComparator comparator;

        protected OppositeComparator(IntComparator c) {
            this.comparator = c;
        }

        @Override
        public final int compare(int a, int b) {
            return this.comparator.compare(b, a);
        }

        @Override
        public final IntComparator reversed() {
            return this.comparator;
        }
    }

    protected static class NaturalImplicitComparator
    implements IntComparator,
    Serializable {
        private static final long serialVersionUID = 1L;

        protected NaturalImplicitComparator() {
        }

        @Override
        public final int compare(int a, int b) {
            return Integer.compare(a, b);
        }

        @Override
        public IntComparator reversed() {
            return OPPOSITE_COMPARATOR;
        }

        private Object readResolve() {
            return NATURAL_COMPARATOR;
        }
    }

    protected static class OppositeImplicitComparator
    implements IntComparator,
    Serializable {
        private static final long serialVersionUID = 1L;

        protected OppositeImplicitComparator() {
        }

        @Override
        public final int compare(int a, int b) {
            return -Integer.compare(a, b);
        }

        @Override
        public IntComparator reversed() {
            return NATURAL_COMPARATOR;
        }

        private Object readResolve() {
            return OPPOSITE_COMPARATOR;
        }
    }
}

