/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  vip.astroline.client.storage.utils.other.DelayTimer
 */
package vip.astroline.client.layout.dropdown.utils;

import vip.astroline.client.storage.utils.other.DelayTimer;

public class AnimationTimer {
    private final int delay;
    private int bottom;
    private int top;
    private int timer;
    private boolean wasRising;
    private DelayTimer helpertimer = new DelayTimer();

    public AnimationTimer(int delay) {
        this.delay = delay;
        this.top = delay;
        this.bottom = 0;
    }

    public void update(boolean increment) {
        if (!this.helpertimer.hasPassed(10.0)) return;
        if (increment) {
            if (this.timer < this.delay) {
                if (!this.wasRising) {
                    this.bottom = this.timer;
                }
                ++this.timer;
            }
            this.wasRising = true;
        } else {
            if (this.timer > 0) {
                if (this.wasRising) {
                    this.top = this.timer;
                }
                --this.timer;
            }
            this.wasRising = false;
        }
        this.helpertimer.reset();
    }

    public void reset() {
        this.timer = 0;
        this.wasRising = false;
        this.helpertimer.reset();
        this.top = this.delay;
        this.bottom = 0;
    }

    public double getValue() {
        return this.wasRising ? Math.sin((double)(this.timer - this.bottom) / (double)(this.delay - this.bottom) * Math.PI / 2.0) : 1.0 - Math.cos((double)this.timer / (double)this.top * Math.PI / 2.0);
    }
}
