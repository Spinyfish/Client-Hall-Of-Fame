/*
 * Decompiled with CFR 0.152.
 */
package lodomir.dev.utils.render.animations;

public interface Easing {
    public static final Easing LINEAR = (t, b, c, d) -> c * t / d + b;
    public static final Easing QUAD_IN = (t, b, c, d) -> c * (t /= d) * t + b;
    public static final Easing QUAD_OUT = (t, b, c, d) -> -c * (t /= d) * (t - 2.0f) + b;
    public static final Easing QUAD_IN_OUT = (t, b, c, d) -> {
        float f;
        t /= d / 2.0f;
        if (f < 1.0f) {
            return c / 2.0f * t * t + b;
        }
        return -c / 2.0f * ((t -= 1.0f) * (t - 2.0f) - 1.0f) + b;
    };
    public static final Easing CUBIC_IN = (t, b, c, d) -> c * (t /= d) * t * t + b;
    public static final Easing CUBIC_OUT = (t, b, c, d) -> {
        t = t / d - 1.0f;
        return c * (t * t * t + 1.0f) + b;
    };
    public static final Easing CUBIC_IN_OUT = (t, b, c, d) -> {
        float f;
        t /= d / 2.0f;
        if (f < 1.0f) {
            return c / 2.0f * t * t * t + b;
        }
        return c / 2.0f * ((t -= 2.0f) * t * t + 2.0f) + b;
    };
    public static final Easing QUARTIC_IN = (t, b, c, d) -> c * (t /= d) * t * t * t + b;
    public static final Easing QUARTIC_OUT = (t, b, c, d) -> {
        t = t / d - 1.0f;
        return -c * (t * t * t * t - 1.0f) + b;
    };
    public static final Easing QUARTIC_IN_OUT = (t, b, c, d) -> {
        float f;
        t /= d / 2.0f;
        if (f < 1.0f) {
            return c / 2.0f * t * t * t * t + b;
        }
        return -c / 2.0f * ((t -= 2.0f) * t * t * t - 2.0f) + b;
    };
    public static final Easing QUINTIC_IN = (t, b, c, d) -> c * (t /= d) * t * t * t * t + b;
    public static final Easing QUINTIC_OUT = (t, b, c, d) -> {
        t = t / d - 1.0f;
        return c * (t * t * t * t * t + 1.0f) + b;
    };
    public static final Easing QUINTIC_IN_OUT = (t, b, c, d) -> {
        float f;
        t /= d / 2.0f;
        if (f < 1.0f) {
            return c / 2.0f * t * t * t * t * t + b;
        }
        return c / 2.0f * ((t -= 2.0f) * t * t * t * t + 2.0f) + b;
    };
    public static final Easing SINE_IN = (t, b, c, d) -> -c * (float)Math.cos((double)(t / d) * 1.5707963267948966) + c + b;
    public static final Easing SINE_OUT = (t, b, c, d) -> c * (float)Math.sin((double)(t / d) * 1.5707963267948966) + b;
    public static final Easing SINE_IN_OUT = (t, b, c, d) -> -c / 2.0f * ((float)Math.cos(Math.PI * (double)t / (double)d) - 1.0f) + b;
    public static final Easing EXPO_IN = (t, b, c, d) -> t == 0.0f ? b : c * (float)Math.pow(2.0, 10.0f * (t / d - 1.0f)) + b;
    public static final Easing EXPO_OUT = (t, b, c, d) -> t == d ? b + c : c * (-((float)Math.pow(2.0, -10.0f * t / d)) + 1.0f) + b;
    public static final Easing EXPO_IN_OUT = (t, b, c, d) -> {
        float f;
        if (t == 0.0f) {
            return b;
        }
        if (t == d) {
            return b + c;
        }
        t /= d / 2.0f;
        if (f < 1.0f) {
            return c / 2.0f * (float)Math.pow(2.0, 10.0f * (t - 1.0f)) + b;
        }
        return c / 2.0f * (-((float)Math.pow(2.0, -10.0f * (t -= 1.0f))) + 2.0f) + b;
    };
    public static final Easing CIRC_IN = (t, b, c, d) -> -c * ((float)Math.sqrt(1.0f - (t /= d) * t) - 1.0f) + b;
    public static final Easing CIRC_OUT = (t, b, c, d) -> {
        t = t / d - 1.0f;
        return c * (float)Math.sqrt(1.0f - t * t) + b;
    };
    public static final Easing CIRC_IN_OUT = (t, b, c, d) -> {
        float f;
        t /= d / 2.0f;
        if (f < 1.0f) {
            return -c / 2.0f * ((float)Math.sqrt(1.0f - t * t) - 1.0f) + b;
        }
        return c / 2.0f * ((float)Math.sqrt(1.0f - (t -= 2.0f) * t) + 1.0f) + b;
    };
    public static final Elastic ELASTIC_IN = new ElasticIn();
    public static final Elastic ELASTIC_OUT = new ElasticOut();
    public static final Elastic ELASTIC_IN_OUT = new ElasticInOut();
    public static final Back BACK_IN = new BackIn();
    public static final Back BACK_OUT = new BackOut();
    public static final Back BACK_IN_OUT = new BackInOut();
    public static final Easing BOUNCE_OUT = (t, b, c, d) -> {
        float f;
        t /= d;
        if (f < 0.36363637f) {
            return c * (7.5625f * t * t) + b;
        }
        if (t < 0.72727275f) {
            return c * (7.5625f * (t -= 0.54545456f) * t + 0.75f) + b;
        }
        if (t < 0.90909094f) {
            return c * (7.5625f * (t -= 0.8181818f) * t + 0.9375f) + b;
        }
        return c * (7.5625f * (t -= 0.95454544f) * t + 0.984375f) + b;
    };
    public static final Easing BOUNCE_IN = (t, b, c, d) -> c - BOUNCE_OUT.ease(d - t, 0.0f, c, d) + b;
    public static final Easing BOUNCE_IN_OUT = (t, b, c, d) -> {
        if (t < d / 2.0f) {
            return BOUNCE_IN.ease(t * 2.0f, 0.0f, c, d) * 0.5f + b;
        }
        return BOUNCE_OUT.ease(t * 2.0f - d, 0.0f, c, d) * 0.5f + c * 0.5f + b;
    };

    public float ease(float var1, float var2, float var3, float var4);

    public static class BackInOut
    extends Back {
        public BackInOut() {
        }

        public BackInOut(float overshoot) {
            super(overshoot);
        }

        @Override
        public float ease(float t, float b, float c, float d) {
            float f;
            float s = this.getOvershoot();
            t /= d / 2.0f;
            if (f < 1.0f) {
                s = (float)((double)s * 1.525);
                return c / 2.0f * (t * t * ((s + 1.0f) * t - s)) + b;
            }
            s = (float)((double)s * 1.525);
            return c / 2.0f * ((t -= 2.0f) * t * ((s + 1.0f) * t + s) + 2.0f) + b;
        }
    }

    public static class BackOut
    extends Back {
        public BackOut() {
        }

        public BackOut(float overshoot) {
            super(overshoot);
        }

        @Override
        public float ease(float t, float b, float c, float d) {
            float s = this.getOvershoot();
            t = t / d - 1.0f;
            return c * (t * t * ((s + 1.0f) * t + s) + 1.0f) + b;
        }
    }

    public static class BackIn
    extends Back {
        public BackIn() {
        }

        public BackIn(float overshoot) {
            super(overshoot);
        }

        @Override
        public float ease(float t, float b, float c, float d) {
            float s = this.getOvershoot();
            return c * (t /= d) * t * ((s + 1.0f) * t - s) + b;
        }
    }

    public static abstract class Back
    implements Easing {
        public static final float DEFAULT_OVERSHOOT = 1.70158f;
        private float overshoot;

        public Back() {
            this(1.70158f);
        }

        public Back(float overshoot) {
            this.overshoot = overshoot;
        }

        public float getOvershoot() {
            return this.overshoot;
        }

        public void setOvershoot(float overshoot) {
            this.overshoot = overshoot;
        }
    }

    public static class ElasticInOut
    extends Elastic {
        public ElasticInOut(float amplitude, float period) {
            super(amplitude, period);
        }

        public ElasticInOut() {
        }

        @Override
        public float ease(float t, float b, float c, float d) {
            float a = this.getAmplitude();
            float p = this.getPeriod();
            if (t == 0.0f) {
                return b;
            }
            if ((t /= d / 2.0f) == 2.0f) {
                return b + c;
            }
            if (p == 0.0f) {
                p = d * 0.45000002f;
            }
            float s = 0.0f;
            if (a < Math.abs(c)) {
                a = c;
                s = p / 4.0f;
            } else {
                s = p / ((float)Math.PI * 2) * (float)Math.asin(c / a);
            }
            if (t < 1.0f) {
                return -0.5f * (a * (float)Math.pow(2.0, 10.0f * (t -= 1.0f)) * (float)Math.sin((double)(t * d - s) * (Math.PI * 2) / (double)p)) + b;
            }
            return a * (float)Math.pow(2.0, -10.0f * (t -= 1.0f)) * (float)Math.sin((double)(t * d - s) * (Math.PI * 2) / (double)p) * 0.5f + c + b;
        }
    }

    public static class ElasticOut
    extends Elastic {
        public ElasticOut(float amplitude, float period) {
            super(amplitude, period);
        }

        public ElasticOut() {
        }

        @Override
        public float ease(float t, float b, float c, float d) {
            float a = this.getAmplitude();
            float p = this.getPeriod();
            if (t == 0.0f) {
                return b;
            }
            if ((t /= d) == 1.0f) {
                return b + c;
            }
            if (p == 0.0f) {
                p = d * 0.3f;
            }
            float s = 0.0f;
            if (a < Math.abs(c)) {
                a = c;
                s = p / 4.0f;
            } else {
                s = p / ((float)Math.PI * 2) * (float)Math.asin(c / a);
            }
            return a * (float)Math.pow(2.0, -10.0f * t) * (float)Math.sin((double)(t * d - s) * (Math.PI * 2) / (double)p) + c + b;
        }
    }

    public static class ElasticIn
    extends Elastic {
        public ElasticIn(float amplitude, float period) {
            super(amplitude, period);
        }

        public ElasticIn() {
        }

        @Override
        public float ease(float t, float b, float c, float d) {
            float a = this.getAmplitude();
            float p = this.getPeriod();
            if (t == 0.0f) {
                return b;
            }
            if ((t /= d) == 1.0f) {
                return b + c;
            }
            if (p == 0.0f) {
                p = d * 0.3f;
            }
            float s = 0.0f;
            if (a < Math.abs(c)) {
                a = c;
                s = p / 4.0f;
            } else {
                s = p / ((float)Math.PI * 2) * (float)Math.asin(c / a);
            }
            return -(a * (float)Math.pow(2.0, 10.0f * (t -= 1.0f)) * (float)Math.sin((double)(t * d - s) * (Math.PI * 2) / (double)p)) + b;
        }
    }

    public static abstract class Elastic
    implements Easing {
        private float amplitude;
        private float period;

        public Elastic(float amplitude, float period) {
            this.amplitude = amplitude;
            this.period = period;
        }

        public Elastic() {
            this(-1.0f, 0.0f);
        }

        public float getPeriod() {
            return this.period;
        }

        public void setPeriod(float period) {
            this.period = period;
        }

        public float getAmplitude() {
            return this.amplitude;
        }

        public void setAmplitude(float amplitude) {
            this.amplitude = amplitude;
        }
    }
}

