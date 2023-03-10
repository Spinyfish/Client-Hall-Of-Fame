/*
 * Decompiled with CFR 0.152.
 */
package lodomir.dev.utils.math.apache;

public class Precision {
    public static final double EPSILON;
    public static final double SAFE_MIN;
    private static final long EXPONENT_OFFSET = 1023L;
    private static final long SGN_MASK = Long.MIN_VALUE;
    private static final int SGN_MASK_FLOAT = Integer.MIN_VALUE;
    private static final double POSITIVE_ZERO = 0.0;
    private static final long POSITIVE_ZERO_DOUBLE_BITS;
    private static final long NEGATIVE_ZERO_DOUBLE_BITS;
    private static final int POSITIVE_ZERO_FLOAT_BITS;
    private static final int NEGATIVE_ZERO_FLOAT_BITS;

    private Precision() {
    }

    public static int compareTo(double x, double y, double eps) {
        if (Precision.equals(x, y, eps)) {
            return 0;
        }
        if (x < y) {
            return -1;
        }
        if (x > y) {
            return 1;
        }
        return Double.compare(x, y);
    }

    public static int compareTo(double x, double y, int maxUlps) {
        if (Precision.equals(x, y, maxUlps)) {
            return 0;
        }
        if (x < y) {
            return -1;
        }
        return 1;
    }

    public static boolean equals(float x, float y) {
        return Precision.equals(x, y, 1);
    }

    public static boolean equalsIncludingNaN(float x, float y) {
        boolean yIsNan;
        boolean xIsNan = Float.isNaN(x);
        return xIsNan | (yIsNan = Float.isNaN(y)) ? !(xIsNan ^ yIsNan) : Precision.equals(x, y, 1);
    }

    public static boolean equals(float x, float y, float eps) {
        return Precision.equals(x, y, 1) || Math.abs(y - x) <= eps;
    }

    public static boolean equalsIncludingNaN(float x, float y, float eps) {
        return Precision.equalsIncludingNaN(x, y, 1) || Math.abs(y - x) <= eps;
    }

    public static boolean equals(float x, float y, int maxUlps) {
        boolean isEqual;
        int yInt;
        int xInt = Float.floatToRawIntBits(x);
        if (((xInt ^ (yInt = Float.floatToRawIntBits(y))) & Integer.MIN_VALUE) == 0) {
            isEqual = Math.abs(xInt - yInt) <= maxUlps;
        } else {
            int deltaMinus;
            int deltaPlus;
            if (xInt < yInt) {
                deltaPlus = yInt - POSITIVE_ZERO_FLOAT_BITS;
                deltaMinus = xInt - NEGATIVE_ZERO_FLOAT_BITS;
            } else {
                deltaPlus = xInt - POSITIVE_ZERO_FLOAT_BITS;
                deltaMinus = yInt - NEGATIVE_ZERO_FLOAT_BITS;
            }
            isEqual = deltaPlus > maxUlps ? false : deltaMinus <= maxUlps - deltaPlus;
        }
        return isEqual && !Float.isNaN(x) && !Float.isNaN(y);
    }

    public static boolean equalsIncludingNaN(float x, float y, int maxUlps) {
        boolean yIsNan;
        boolean xIsNan = Float.isNaN(x);
        return xIsNan | (yIsNan = Float.isNaN(y)) ? !(xIsNan ^ yIsNan) : Precision.equals(x, y, maxUlps);
    }

    public static boolean equals(double x, double y) {
        return Precision.equals(x, y, 1);
    }

    public static boolean equalsIncludingNaN(double x, double y) {
        boolean yIsNan;
        boolean xIsNan = Double.isNaN(x);
        return xIsNan | (yIsNan = Double.isNaN(y)) ? !(xIsNan ^ yIsNan) : Precision.equals(x, y, 1);
    }

    public static boolean equals(double x, double y, double eps) {
        return Precision.equals(x, y, 1) || Math.abs(y - x) <= eps;
    }

    public static boolean equalsWithRelativeTolerance(double x, double y, double eps) {
        if (Precision.equals(x, y, 1)) {
            return true;
        }
        double absoluteMax = Math.max(Math.abs(x), Math.abs(y));
        double relativeDifference = Math.abs((x - y) / absoluteMax);
        return relativeDifference <= eps;
    }

    public static boolean equalsIncludingNaN(double x, double y, double eps) {
        return Precision.equalsIncludingNaN(x, y) || Math.abs(y - x) <= eps;
    }

    public static boolean equals(double x, double y, int maxUlps) {
        boolean isEqual;
        long yInt;
        long xInt = Double.doubleToRawLongBits(x);
        if (((xInt ^ (yInt = Double.doubleToRawLongBits(y))) & Long.MIN_VALUE) == 0L) {
            isEqual = Math.abs(xInt - yInt) <= (long)maxUlps;
        } else {
            long deltaMinus;
            long deltaPlus;
            if (xInt < yInt) {
                deltaPlus = yInt - POSITIVE_ZERO_DOUBLE_BITS;
                deltaMinus = xInt - NEGATIVE_ZERO_DOUBLE_BITS;
            } else {
                deltaPlus = xInt - POSITIVE_ZERO_DOUBLE_BITS;
                deltaMinus = yInt - NEGATIVE_ZERO_DOUBLE_BITS;
            }
            isEqual = deltaPlus > (long)maxUlps ? false : deltaMinus <= (long)maxUlps - deltaPlus;
        }
        return isEqual && !Double.isNaN(x) && !Double.isNaN(y);
    }

    public static double representableDelta(double x, double delta) {
        return x + delta - x;
    }

    public static DoubleEquivalence doubleEquivalenceOfEpsilon(final double eps) {
        if (!Double.isFinite(eps) || eps < 0.0) {
            throw new IllegalArgumentException("Invalid epsilon value: " + eps);
        }
        return new DoubleEquivalence(){
            private final double epsilon;
            {
                this.epsilon = eps;
            }

            @Override
            public int compare(double a, double b) {
                return Precision.compareTo(a, b, this.epsilon);
            }
        };
    }

    static {
        POSITIVE_ZERO_DOUBLE_BITS = Double.doubleToRawLongBits(0.0);
        NEGATIVE_ZERO_DOUBLE_BITS = Double.doubleToRawLongBits(-0.0);
        POSITIVE_ZERO_FLOAT_BITS = Float.floatToRawIntBits(0.0f);
        NEGATIVE_ZERO_FLOAT_BITS = Float.floatToRawIntBits(-0.0f);
        EPSILON = Double.longBitsToDouble(4368491638549381120L);
        SAFE_MIN = Double.longBitsToDouble(0x10000000000000L);
    }

    public static interface DoubleEquivalence {
        default public boolean eq(double a, double b) {
            return this.compare(a, b) == 0;
        }

        default public boolean eqZero(double a) {
            return this.eq(a, 0.0);
        }

        default public boolean lt(double a, double b) {
            return this.compare(a, b) < 0;
        }

        default public boolean lte(double a, double b) {
            return this.compare(a, b) <= 0;
        }

        default public boolean gt(double a, double b) {
            return this.compare(a, b) > 0;
        }

        default public boolean gte(double a, double b) {
            return this.compare(a, b) >= 0;
        }

        default public double signum(double a) {
            return a == 0.0 || Double.isNaN(a) ? a : (this.eqZero(a) ? Math.copySign(0.0, a) : Math.copySign(1.0, a));
        }

        public int compare(double var1, double var3);
    }
}

