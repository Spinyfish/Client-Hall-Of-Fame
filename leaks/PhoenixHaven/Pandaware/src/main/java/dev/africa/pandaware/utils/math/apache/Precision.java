package dev.africa.pandaware.utils.math.apache;

public class Precision {
    /**
     * <p>
     * Largest double-precision floating-point number such that
     * {@code 1 + EPSILON} is numerically equal to 1. This value is an upper
     * bound on the relative error due to rounding real numbers to double
     * precision floating-point numbers.
     * </p>
     * <p>
     * In IEEE 754 arithmetic, this is 2<sup>-53</sup>.
     * </p>
     *
     * @see <a href="http://en.wikipedia.org/wiki/Machine_epsilon">Machine epsilon</a>
     */
    public static final double EPSILON;

    /**
     * Safe minimum, such that {@code 1 / SAFE_MIN} does not overflow.
     * In IEEE 754 arithmetic, this is also the smallest normalized
     * number 2<sup>-1022</sup>.
     */
    public static final double SAFE_MIN;

    /**
     * Exponent offset in IEEE754 representation.
     */
    private static final long EXPONENT_OFFSET = 1023L;

    /**
     * Offset to order signed double numbers lexicographically.
     */
    private static final long SGN_MASK = 0x8000000000000000L;
    /**
     * Offset to order signed double numbers lexicographically.
     */
    private static final int SGN_MASK_FLOAT = 0x80000000;
    /**
     * Positive zero.
     */
    private static final double POSITIVE_ZERO = 0d;
    /**
     * Positive zero bits.
     */
    private static final long POSITIVE_ZERO_DOUBLE_BITS = Double.doubleToRawLongBits(+0.0);
    /**
     * Negative zero bits.
     */
    private static final long NEGATIVE_ZERO_DOUBLE_BITS = Double.doubleToRawLongBits(-0.0);
    /**
     * Positive zero bits.
     */
    private static final int POSITIVE_ZERO_FLOAT_BITS = Float.floatToRawIntBits(+0.0f);
    /**
     * Negative zero bits.
     */
    private static final int NEGATIVE_ZERO_FLOAT_BITS = Float.floatToRawIntBits(-0.0f);

    static {
        /*
         *  This was previously expressed as = 0x1.0p-53
         *  However, OpenJDK (Sparc Solaris) cannot handle such small
         *  constants: MATH-721
         */
        EPSILON = Double.longBitsToDouble((EXPONENT_OFFSET - 53L) << 52);

        /*
         * This was previously expressed as = 0x1.0p-1022
         * However, OpenJDK (Sparc Solaris) cannot handle such small
         * constants: MATH-721
         */
        SAFE_MIN = Double.longBitsToDouble((EXPONENT_OFFSET - 1022L) << 52);
    }

    /**
     * Private constructor.
     */
    private Precision() {
    }

    /**
     * Compares two numbers given some amount of allowed error.
     * The returned value is
     * <ul>
     *  <li>
     *   0 if  {@link #equals(double, double, double) equals(x, y, eps)},
     *  </li>
     *  <li>
     *   negative if !{@link #equals(double, double, double) equals(x, y, eps)} and {@code x < y},
     *  </li>
     *  <li>
     *   positive if !{@link #equals(double, double, double) equals(x, y, eps)} and {@code x > y} or
     *   either argument is {@code NaN}.
     *  </li>
     * </ul>
     *
     * @param x   First value.
     * @param y   Second value.
     * @param eps Allowed error when checking for equality.
     * @return 0 if the value are considered equal, -1 if the first is smaller than
     * the second, 1 is the first is larger than the second.
     */
    public static int compareTo(double x, double y, double eps) {
        if (equals(x, y, eps)) {
            return 0;
        } else if (x < y) {
            return -1;
        } else if (x > y) {
            return 1;
        }
        // NaN input.
        return Double.compare(x, y);
    }

    /**
     * Compares two numbers given some amount of allowed error.
     * Two float numbers are considered equal if there are {@code (maxUlps - 1)}
     * (or fewer) floating point numbers between them, i.e. two adjacent floating
     * point numbers are considered equal.
     * Adapted from
     * <a href="http://randomascii.wordpress.com/2012/02/25/comparing-floating-point-numbers-2012-edition/">
     * Bruce Dawson</a>. Returns {@code false} if either of the arguments is NaN.
     * The returned value is
     * <ul>
     *  <li>
     *   zero if {@link #equals(double, double, int) equals(x, y, maxUlps)},
     *  </li>
     *  <li>
     *   negative if !{@link #equals(double, double, int) equals(x, y, maxUlps)} and {@code x < y},
     *  </li>
     *  <li>
     *   positive if !{@link #equals(double, double, int) equals(x, y, maxUlps)} and {@code x > y}
     *       or either argument is {@code NaN}.
     *  </li>
     * </ul>
     *
     * @param x       First value.
     * @param y       Second value.
     * @param maxUlps {@code (maxUlps - 1)} is the number of floating point
     *                values between {@code x} and {@code y}.
     * @return 0 if the value are considered equal, -1 if the first is smaller than
     * the second, 1 is the first is larger than the second.
     */
    public static int compareTo(final double x, final double y, final int maxUlps) {
        if (equals(x, y, maxUlps)) {
            return 0;
        } else if (x < y) {
            return -1;
        }
        return 1;
    }

    /**
     * Returns true iff they are equal as defined by
     * {@link #equals(float, float, int) equals(x, y, 1)}.
     *
     * @param x first value
     * @param y second value
     * @return {@code true} if the values are equal.
     */
    public static boolean equals(float x, float y) {
        return equals(x, y, 1);
    }

    /**
     * Returns true if both arguments are NaN or they are
     * equal as defined by {@link #equals(float, float) equals(x, y, 1)}.
     *
     * @param x first value
     * @param y second value
     * @return {@code true} if the values are equal or both are NaN.
     */
    public static boolean equalsIncludingNaN(float x, float y) {
        final boolean xIsNan = Float.isNaN(x);
        final boolean yIsNan = Float.isNaN(y);
        // Combine the booleans with bitwise OR
        return (xIsNan | yIsNan) ?
                !(xIsNan ^ yIsNan) :
                equals(x, y, 1);
    }

    /**
     * Returns {@code true} if there is no float value strictly between the
     * arguments or the difference between them is within the range of allowed
     * error (inclusive). Returns {@code false} if either of the arguments
     * is NaN.
     *
     * @param x   first value
     * @param y   second value
     * @param eps the amount of absolute error to allow.
     * @return {@code true} if the values are equal or within range of each other.
     */
    public static boolean equals(float x, float y, float eps) {
        return equals(x, y, 1) || Math.abs(y - x) <= eps;
    }

    /**
     * Returns true if the arguments are both NaN, there are no float value strictly
     * between the arguments or the difference between them is within the range of allowed
     * error (inclusive).
     *
     * @param x   first value
     * @param y   second value
     * @param eps the amount of absolute error to allow.
     * @return {@code true} if the values are equal or within range of each other,
     * or both are NaN.
     */
    public static boolean equalsIncludingNaN(float x, float y, float eps) {
        return equalsIncludingNaN(x, y, 1) || (Math.abs(y - x) <= eps);
    }

    /**
     * Returns true if the arguments are equal or within the range of allowed
     * error (inclusive). Returns {@code false} if either of the arguments is NaN.
     * <p>
     * Two double numbers are considered equal if there are {@code (maxUlps - 1)}
     * (or fewer) floating point numbers between them, i.e. two adjacent
     * floating point numbers are considered equal.
     * </p>
     * <p>
     * Adapted from <a
     * href="http://randomascii.wordpress.com/2012/02/25/comparing-floating-point-numbers-2012-edition/">
     * Bruce Dawson</a>.
     * </p>
     *
     * @param x       first value
     * @param y       second value
     * @param maxUlps {@code (maxUlps - 1)} is the number of floating point
     *                values between {@code x} and {@code y}.
     * @return {@code true} if there are fewer than {@code maxUlps} floating
     * point values between {@code x} and {@code y}.
     */
    public static boolean equals(final float x, final float y, final int maxUlps) {

        final int xInt = Float.floatToRawIntBits(x);
        final int yInt = Float.floatToRawIntBits(y);

        final boolean isEqual;
        if (((xInt ^ yInt) & SGN_MASK_FLOAT) == 0) {
            // number have same sign, there is no risk of overflow
            isEqual = Math.abs(xInt - yInt) <= maxUlps;
        } else {
            // number have opposite signs, take care of overflow
            final int deltaPlus;
            final int deltaMinus;
            if (xInt < yInt) {
                deltaPlus = yInt - POSITIVE_ZERO_FLOAT_BITS;
                deltaMinus = xInt - NEGATIVE_ZERO_FLOAT_BITS;
            } else {
                deltaPlus = xInt - POSITIVE_ZERO_FLOAT_BITS;
                deltaMinus = yInt - NEGATIVE_ZERO_FLOAT_BITS;
            }

            if (deltaPlus > maxUlps) {
                isEqual = false;
            } else {
                isEqual = deltaMinus <= (maxUlps - deltaPlus);
            }

        }

        return isEqual && !Float.isNaN(x) && !Float.isNaN(y);
    }

    /**
     * Returns true if both arguments are NaN or if they are equal as defined
     * by {@link #equals(float, float, int) equals(x, y, maxUlps)}.
     *
     * @param x       first value
     * @param y       second value
     * @param maxUlps {@code (maxUlps - 1)} is the number of floating point
     *                values between {@code x} and {@code y}.
     * @return {@code true} if both arguments are NaN or if there are less than
     * {@code maxUlps} floating point values between {@code x} and {@code y}.
     */
    public static boolean equalsIncludingNaN(float x, float y, int maxUlps) {
        final boolean xIsNan = Float.isNaN(x);
        final boolean yIsNan = Float.isNaN(y);
        // Combine the booleans with bitwise OR
        return (xIsNan | yIsNan) ?
                !(xIsNan ^ yIsNan) :
                equals(x, y, maxUlps);
    }

    /**
     * Returns true iff they are equal as defined by
     * {@link #equals(double, double, int) equals(x, y, 1)}.
     *
     * @param x first value
     * @param y second value
     * @return {@code true} if the values are equal.
     */
    public static boolean equals(double x, double y) {
        return equals(x, y, 1);
    }

    /**
     * Returns true if the arguments are both NaN or they are
     * equal as defined by {@link #equals(double, double) equals(x, y, 1)}.
     *
     * @param x first value
     * @param y second value
     * @return {@code true} if the values are equal or both are NaN.
     */
    public static boolean equalsIncludingNaN(double x, double y) {
        final boolean xIsNan = Double.isNaN(x);
        final boolean yIsNan = Double.isNaN(y);
        // Combine the booleans with bitwise OR
        return (xIsNan | yIsNan) ?
                !(xIsNan ^ yIsNan) :
                equals(x, y, 1);
    }

    /**
     * Returns {@code true} if there is no double value strictly between the
     * arguments or the difference between them is within the range of allowed
     * error (inclusive). Returns {@code false} if either of the arguments
     * is NaN.
     *
     * @param x   First value.
     * @param y   Second value.
     * @param eps Amount of allowed absolute error.
     * @return {@code true} if the values are equal or within range of each other.
     */
    public static boolean equals(double x, double y, double eps) {
        return equals(x, y, 1) || Math.abs(y - x) <= eps;
    }

    /**
     * Returns {@code true} if there is no double value strictly between the
     * arguments or the relative difference between them is less than or equal
     * to the given tolerance. Returns {@code false} if either of the arguments
     * is NaN.
     *
     * @param x   First value.
     * @param y   Second value.
     * @param eps Amount of allowed relative error.
     * @return {@code true} if the values are two adjacent floating point
     * numbers or they are within range of each other.
     */
    public static boolean equalsWithRelativeTolerance(double x, double y, double eps) {
        if (equals(x, y, 1)) {
            return true;
        }

        final double absoluteMax = Math.max(Math.abs(x), Math.abs(y));
        final double relativeDifference = Math.abs((x - y) / absoluteMax);

        return relativeDifference <= eps;
    }

    /**
     * Returns true if the arguments are both NaN, there are no double value strictly
     * between the arguments or the difference between them is within the range of allowed
     * error (inclusive).
     *
     * @param x   first value
     * @param y   second value
     * @param eps the amount of absolute error to allow.
     * @return {@code true} if the values are equal or within range of each other,
     * or both are NaN.
     */
    public static boolean equalsIncludingNaN(double x, double y, double eps) {
        return equalsIncludingNaN(x, y) || (Math.abs(y - x) <= eps);
    }

    /**
     * Returns true if the arguments are equal or within the range of allowed
     * error (inclusive). Returns {@code false} if either of the arguments is NaN.
     * <p>
     * Two float numbers are considered equal if there are {@code (maxUlps - 1)}
     * (or fewer) floating point numbers between them, i.e. two adjacent
     * floating point numbers are considered equal.
     * </p>
     * <p>
     * Adapted from <a
     * href="http://randomascii.wordpress.com/2012/02/25/comparing-floating-point-numbers-2012-edition/">
     * Bruce Dawson</a>.
     * </p>
     *
     * @param x       first value
     * @param y       second value
     * @param maxUlps {@code (maxUlps - 1)} is the number of floating point
     *                values between {@code x} and {@code y}.
     * @return {@code true} if there are fewer than {@code maxUlps} floating
     * point values between {@code x} and {@code y}.
     */
    public static boolean equals(final double x, final double y, final int maxUlps) {

        final long xInt = Double.doubleToRawLongBits(x);
        final long yInt = Double.doubleToRawLongBits(y);

        final boolean isEqual;
        if (((xInt ^ yInt) & SGN_MASK) == 0L) {
            // number have same sign, there is no risk of overflow
            isEqual = Math.abs(xInt - yInt) <= maxUlps;
        } else {
            // number have opposite signs, take care of overflow
            final long deltaPlus;
            final long deltaMinus;
            if (xInt < yInt) {
                deltaPlus = yInt - POSITIVE_ZERO_DOUBLE_BITS;
                deltaMinus = xInt - NEGATIVE_ZERO_DOUBLE_BITS;
            } else {
                deltaPlus = xInt - POSITIVE_ZERO_DOUBLE_BITS;
                deltaMinus = yInt - NEGATIVE_ZERO_DOUBLE_BITS;
            }

            if (deltaPlus > maxUlps) {
                isEqual = false;
            } else {
                isEqual = deltaMinus <= (maxUlps - deltaPlus);
            }

        }

        return isEqual && !Double.isNaN(x) && !Double.isNaN(y);
    }

    /**
     * Computes a number close to {@code delta} with the property that
     * {@code (x + delta - x)} is exactly machine-representable.
     * This is useful when computing numerical derivatives, in order to
     * reduce roundoff errors.
     *
     * @param x     Value.
     * @param delta Offset value.
     * @return the machine-representable floating number closest to the
     * difference between {@code x + delta} and {@code x}.
     */
    public static double representableDelta(double x,
                                            double delta) {
        return x + delta - x;
    }

    /**
     * Creates a {@link DoubleEquivalence} instance that uses the given epsilon
     * value for determining equality.
     *
     * @param eps Value to use for determining equality.
     * @return a new instance.
     */
    public static DoubleEquivalence doubleEquivalenceOfEpsilon(final double eps) {
        if (!Double.isFinite(eps) ||
                eps < 0d) {
            throw new IllegalArgumentException("Invalid epsilon value: " + eps);
        }

        return new DoubleEquivalence() {
            /** Epsilon value. */
            private final double epsilon = eps;

            /** {@inheritDoc} */
            @Override
            public int compare(double a,
                               double b) {
                return Precision.compareTo(a, b, epsilon);
            }
        };
    }

    /**
     * Interface containing comparison operations for doubles that allow values
     * to be <em>considered</em> equal even if they are not exactly equal.
     * It is intended for comparing outputs of a computation where floating
     * point errors may have occurred.
     */
    public interface DoubleEquivalence {
        /**
         * Indicates whether given values are considered equal to each other.
         *
         * @param a Value.
         * @param b Value.
         * @return true if the given values are considered equal.
         */
        default boolean eq(double a, double b) {
            return compare(a, b) == 0;
        }

        /**
         * Indicates whether the given value is considered equal to zero.
         * It is a shortcut for {@code eq(a, 0.0)}.
         *
         * @param a Value.
         * @return true if the argument is considered equal to zero.
         */
        default boolean eqZero(double a) {
            return eq(a, 0d);
        }

        /**
         * Indicates whether the first argument is strictly smaller than the second.
         *
         * @param a Value.
         * @param b Value.
         * @return true if {@code a < b}
         */
        default boolean lt(double a, double b) {
            return compare(a, b) < 0;
        }

        /**
         * Indicates whether the first argument is smaller or considered equal to the second.
         *
         * @param a Value.
         * @param b Value.
         * @return true if {@code a <= b}
         */
        default boolean lte(double a, double b) {
            return compare(a, b) <= 0;
        }

        /**
         * Indicates whether the first argument is strictly greater than the second.
         *
         * @param a Value.
         * @param b Value.
         * @return true if {@code a > b}
         */
        default boolean gt(double a, double b) {
            return compare(a, b) > 0;
        }

        /**
         * Indicates whether the first argument is greater than or considered equal to the second.
         *
         * @param a Value.
         * @param b Value.
         * @return true if {@code a >= b}
         */
        default boolean gte(double a, double b) {
            return compare(a, b) >= 0;
        }

        /**
         * Returns the {@link Math#signum(double) sign} of the argument.
         *
         * @param a Value.
         * @return the sign (or {@code a} if {@code eqZero(a)} is true or
         * {@code a} is NaN).
         */
        default double signum(double a) {
            return a == 0d ||
                    Double.isNaN(a) ?
                    a :
                    eqZero(a) ?
                            Math.copySign(0d, a) :
                            Math.copySign(1d, a);
        }

        /**
         * Compares two values.
         * The returned value is
         * <ul>
         *  <li>{@code 0} if the arguments are considered equal,</li>
         *  <li>{@code -1} if {@code a < b},</li>
         *  <li>{@code +1} if {@code a > b} or if either value is NaN.</li>
         * </ul>
         *
         * @param a Value.
         * @param b Value.
         * @return {@code 0} if the values are considered equal, {@code -1}
         * if the first is smaller than the second, {@code 1} is the first
         * is larger than the second or either value is NaN.
         */
        int compare(double a, double b);
    }
}