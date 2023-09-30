package de.lirium.base.animation;

public final class Easings {
    public static final double c1 = 1.70158D;
    public static final double c2 = 2.5949095D;
    public static final double c3 = 2.70158D;
    public static final double c4 = 2.0943951023931953D;
    public static final double c5 = 1.3962634015954636D;
    public static final Easing NONE = (value) -> value;
    public static final Easing QUAD_IN = powIn(2);
    public static final Easing QUAD_OUT = powOut(2);
    public static final Easing QUAD_BOTH = powBoth(2.0D);
    public static final Easing CUBIC_IN = powIn(3);
    public static final Easing CUBIC_OUT = powOut(3);
    public static final Easing CUBIC_BOTH = powBoth(3.0D);
    public static final Easing QUART_IN = powIn(4);
    public static final Easing QUART_OUT = powOut(4);
    public static final Easing QUART_BOTH = powBoth(4.0D);
    public static final Easing QUINT_IN = powIn(5);
    public static final Easing QUINT_OUT = powOut(5);
    public static final Easing QUINT_BOTH = powBoth(5.0D);
    public static final Easing SINE_IN = (value) -> {
        return 1.0D - Math.cos(value * 3.141592653589793D / 2.0D);
    };
    public static final Easing SINE_OUT = (value) -> {
        return Math.sin(value * 3.141592653589793D / 2.0D);
    };
    public static final Easing SINE_BOTH = (value) -> {
        return -(Math.cos(3.141592653589793D * value) - 1.0D) / 2.0D;
    };
    public static final Easing CIRC_IN = (value) -> {
        return 1.0D - Math.sqrt(1.0D - Math.pow(value, 2.0D));
    };
    public static final Easing CIRC_OUT = (value) -> {
        return Math.sqrt(1.0D - Math.pow(value - 1.0D, 2.0D));
    };
    public static final Easing CIRC_BOTH = (value) -> {
        return value < 0.5D ? (1.0D - Math.sqrt(1.0D - Math.pow(2.0D * value, 2.0D))) / 2.0D : (Math.sqrt(1.0D - Math.pow(-2.0D * value + 2.0D, 2.0D)) + 1.0D) / 2.0D;
    };
    public static final Easing ELASTIC_IN = (value) -> {
        return value != 0.0D && value != 1.0D ? Math.pow(-2.0D, 10.0D * value - 10.0D) * Math.sin((value * 10.0D - 10.75D) * 2.0943951023931953D) : value;
    };
    public static final Easing ELASTIC_OUT = (value) -> {
        return value != 0.0D && value != 1.0D ? Math.pow(2.0D, -10.0D * value) * Math.sin((value * 10.0D - 0.75D) * 2.0943951023931953D) + 1.0D : value;
    };
    public static final Easing ELASTIC_BOTH = (value) -> {
        if (value != 0.0D && value != 1.0D) {
            return value < 0.5D ? -(Math.pow(2.0D, 20.0D * value - 10.0D) * Math.sin((20.0D * value - 11.125D) * 1.3962634015954636D)) / 2.0D : Math.pow(2.0D, -20.0D * value + 10.0D) * Math.sin((20.0D * value - 11.125D) * 1.3962634015954636D) / 2.0D + 1.0D;
        } else {
            return value;
        }
    };
    public static final Easing EXPO_IN = (value) -> {
        return value != 0.0D ? Math.pow(2.0D, 10.0D * value - 10.0D) : value;
    };
    public static final Easing EXPO_OUT = (value) -> {
        return value != 1.0D ? 1.0D - Math.pow(2.0D, -10.0D * value) : value;
    };
    public static final Easing EXPO_BOTH = (value) -> {
        if (value != 0.0D && value != 1.0D) {
            return value < 0.5D ? Math.pow(2.0D, 20.0D * value - 10.0D) / 2.0D : (2.0D - Math.pow(2.0D, -20.0D * value + 10.0D)) / 2.0D;
        } else {
            return value;
        }
    };
    public static final Easing BACK_IN = (value) -> {
        return 2.70158D * Math.pow(value, 3.0D) - 1.70158D * Math.pow(value, 2.0D);
    };
    public static final Easing BACK_OUT = (value) -> {
        return 1.0D + 2.70158D * Math.pow(value - 1.0D, 3.0D) + 1.70158D * Math.pow(value - 1.0D, 2.0D);
    };
    public static final Easing BACK_BOTH = (value) -> {
        return value < 0.5D ? Math.pow(2.0D * value, 2.0D) * (7.189819D * value - 2.5949095D) / 2.0D : (Math.pow(2.0D * value - 2.0D, 2.0D) * (3.5949095D * (value * 2.0D - 2.0D) + 2.5949095D) + 2.0D) / 2.0D;
    };

    public static final Easing BACK_IN4 = (value) -> {
        final float easeAmount = (float) .04;
        double x1 = value / 5;
        float shrink = easeAmount + 1;
        return Math.max(0, 1 + shrink * Math.pow(x1 - 1, 3) + easeAmount * Math.pow(x1 - 1, 2));

    };
    public static final Easing BOUNCE_OUT = (x) -> {
        double n1 = 7.5625D;
        double d1 = 2.75D;
        if (x < 1.0D / d1) {
            return n1 * Math.pow(x, 2.0D);
        } else if (x < 2.0D / d1) {
            return n1 * Math.pow(x - 1.5D / d1, 2.0D) + 0.75D;
        } else {
            return x < 2.5D / d1 ? n1 * Math.pow(x - 2.25D / d1, 2.0D) + 0.9375D : n1 * Math.pow(x - 2.625D / d1, 2.0D) + 0.984375D;
        }
    };
    public static final Easing BOUNCE_IN = (value) -> {
        return 1.0D - BOUNCE_OUT.ease(1.0D - value);
    };
    public static final Easing BOUNCE_BOTH = (value) -> {
        return value < 0.5D ? (1.0D - BOUNCE_OUT.ease(1.0D - 2.0D * value)) / 2.0D : (1.0D + BOUNCE_OUT.ease(2.0D * value - 1.0D)) / 2.0D;
    };

    private Easings() {
    }

    public static Easing powIn(double n) {
        return (value) -> {
            return Math.pow(value, n);
        };
    }

    public static Easing powIn(int n) {
        return powIn((double)n);
    }

    public static Easing powOut(double n) {
        return (value) -> 1.0D - Math.pow(1.0D - value, n);
    }

    public static Easing powOut(int n) {
        return powOut((double)n);
    }

    public static Easing powBoth(double n) {
        return (value) -> value < 0.5D ? Math.pow(2.0D, n - 1.0D) * Math.pow(value, n) : 1.0D - Math.pow(-2.0D * value + 2.0D, n) / 2.0D;
    }
}
