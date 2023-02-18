/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  vip.astroline.client.storage.utils.gui.clickgui.AnimationUtils
 */
package vip.astroline.client.storage.utils.gui.clickgui;

import vip.astroline.client.storage.utils.gui.clickgui.AnimationUtils;

public class SmoothAnimationTimer {
    public float target;
    public float speed = 0.3f;
    private float value = 0.0f;

    public SmoothAnimationTimer(float target) {
        this.target = target;
    }

    public SmoothAnimationTimer(float target, float speed) {
        this.target = target;
        this.speed = speed;
    }

    public boolean update(boolean increment) {
        this.value = AnimationUtils.getAnimationState((float)this.value, (float)(increment ? this.target : 0.0f), (float)(Math.max(10.0f, Math.abs(this.value - (increment ? this.target : 0.0f)) * 40.0f) * this.speed));
        return this.value == this.target;
    }

    public float getTarget() {
        return this.target;
    }

    public void setTarget(float target) {
        this.target = target;
    }

    public float getSpeed() {
        return this.speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public float getValue() {
        return this.value;
    }

    public void setValue(float value) {
        this.value = value;
    }
}
