/*
 * Decompiled with CFR 0.152.
 */
package net.optifine.util;

import net.optifine.util.NumUtils;

public class SmoothFloat {
    private float valueLast;
    private float timeFadeUpSec;
    private float timeFadeDownSec;
    private long timeLastMs;

    public SmoothFloat(float valueLast, float timeFadeSec) {
        this(valueLast, timeFadeSec, timeFadeSec);
    }

    public SmoothFloat(float valueLast, float timeFadeUpSec, float timeFadeDownSec) {
        this.valueLast = valueLast;
        this.timeFadeUpSec = timeFadeUpSec;
        this.timeFadeDownSec = timeFadeDownSec;
        this.timeLastMs = System.currentTimeMillis();
    }

    public float getValueLast() {
        return this.valueLast;
    }

    public float getTimeFadeUpSec() {
        return this.timeFadeUpSec;
    }

    public float getTimeFadeDownSec() {
        return this.timeFadeDownSec;
    }

    public long getTimeLastMs() {
        return this.timeLastMs;
    }

    public float getSmoothValue(float value, float timeFadeUpSec, float timeFadeDownSec) {
        this.timeFadeUpSec = timeFadeUpSec;
        this.timeFadeDownSec = timeFadeDownSec;
        return this.getSmoothValue(value);
    }

    public float getSmoothValue(float value) {
        float f3;
        long i = System.currentTimeMillis();
        float f = this.valueLast;
        long j = this.timeLastMs;
        float f1 = (float)(i - j) / 1000.0f;
        float f2 = value >= f ? this.timeFadeUpSec : this.timeFadeDownSec;
        this.valueLast = f3 = SmoothFloat.getSmoothValue(f, value, f1, f2);
        this.timeLastMs = i;
        return f3;
    }

    public static float getSmoothValue(float valPrev, float value, float timeDeltaSec, float timeFadeSec) {
        float f1;
        if (timeDeltaSec <= 0.0f) {
            return valPrev;
        }
        float f = value - valPrev;
        if (timeFadeSec > 0.0f && timeDeltaSec < timeFadeSec && Math.abs(f) > 1.0E-6f) {
            float f2 = timeFadeSec / timeDeltaSec;
            float f3 = 4.61f;
            float f4 = 0.13f;
            float f5 = 10.0f;
            float f6 = f3 - 1.0f / (f4 + f2 / f5);
            float f7 = timeDeltaSec / timeFadeSec * f6;
            f7 = NumUtils.limit(f7, 0.0f, 1.0f);
            f1 = valPrev + f * f7;
        } else {
            f1 = value;
        }
        return f1;
    }
}

