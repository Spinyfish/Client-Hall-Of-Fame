/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.libs.fastutil.objects;

import java.io.Serializable;
import java.util.Comparator;

public final class ObjectComparators {
    public static final Comparator NATURAL_COMPARATOR = new NaturalImplicitComparator();
    public static final Comparator OPPOSITE_COMPARATOR = new OppositeImplicitComparator();

    private ObjectComparators() {
    }

    public static <K> Comparator<K> oppositeComparator(Comparator<K> c) {
        if (c instanceof OppositeComparator) {
            return ((OppositeComparator)c).comparator;
        }
        return new OppositeComparator<K>(c);
    }

    public static <K> Comparator<K> asObjectComparator(Comparator<K> c) {
        return c;
    }

    protected static class OppositeComparator<K>
    implements Comparator<K>,
    Serializable {
        private static final long serialVersionUID = 1L;
        final Comparator<K> comparator;

        protected OppositeComparator(Comparator<K> c) {
            this.comparator = c;
        }

        @Override
        public final int compare(K a, K b) {
            return this.comparator.compare(b, a);
        }

        @Override
        public final Comparator<K> reversed() {
            return this.comparator;
        }
    }

    protected static class NaturalImplicitComparator
    implements Comparator,
    Serializable {
        private static final long serialVersionUID = 1L;

        protected NaturalImplicitComparator() {
        }

        public final int compare(Object a, Object b) {
            return ((Comparable)a).compareTo(b);
        }

        public Comparator reversed() {
            return OPPOSITE_COMPARATOR;
        }

        private Object readResolve() {
            return NATURAL_COMPARATOR;
        }
    }

    protected static class OppositeImplicitComparator
    implements Comparator,
    Serializable {
        private static final long serialVersionUID = 1L;

        protected OppositeImplicitComparator() {
        }

        public final int compare(Object a, Object b) {
            return ((Comparable)b).compareTo(a);
        }

        public Comparator reversed() {
            return NATURAL_COMPARATOR;
        }

        private Object readResolve() {
            return OPPOSITE_COMPARATOR;
        }
    }
}

