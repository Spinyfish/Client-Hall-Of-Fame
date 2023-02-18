/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  vip.astroline.client.service.event.impl.other.EventChangeValue
 *  vip.astroline.client.service.module.value.Value
 *  vip.astroline.client.service.module.value.ValueManager
 *  vip.astroline.client.storage.utils.gui.clickgui.SmoothAnimationTimer
 */
package vip.astroline.client.service.module.value;

import vip.astroline.client.service.event.impl.other.EventChangeValue;
import vip.astroline.client.service.module.value.Value;
import vip.astroline.client.service.module.value.ValueManager;
import vip.astroline.client.storage.utils.gui.clickgui.SmoothAnimationTimer;

public class FloatValue
extends Value {
    SmoothAnimationTimer animationTimer = new SmoothAnimationTimer(1.0f);
    private float min;
    private float max;
    private float increment;
    private String unit;
    public boolean anotherShit;

    public FloatValue(String group, String key, float value, float min, float max, float increment, boolean fromAPI) {
        this.group = group;
        this.key = key;
        this.value = Float.valueOf(value);
        this.min = min;
        this.max = max;
        this.increment = increment;
        this.anotherShit = false;
        if (fromAPI) return;
        ValueManager.addValue((Value)this);
    }

    public FloatValue(String group, String key, float value, float min, float max, float increment) {
        this(group, key, value, min, max, increment, false);
    }

    public FloatValue(String group, String key, float value, float min, float max, float increment, String unit) {
        this(group, key, value, min, max, increment);
        this.unit = unit;
    }

    public Float getValue() {
        return (Float)this.value;
    }

    public void setValue(float value) {
        if (value < this.min) {
            value = this.min;
        }
        if (value > this.max) {
            value = this.max;
        }
        EventChangeValue eventChangeValue = new EventChangeValue(this.group, this.key, this.value, (Object)Float.valueOf(value));
        eventChangeValue.call();
        this.value = Float.valueOf(value);
    }

    public float getValueState() {
        return this.getValue().floatValue();
    }

    public void setValueState(float val) {
        this.setValue(val);
    }

    public float getDMin() {
        return this.min;
    }

    public float getDMax() {
        return this.max;
    }

    public float getDIncrement() {
        return this.increment;
    }

    public SmoothAnimationTimer getAnimationTimer() {
        return this.animationTimer;
    }

    public float getMin() {
        return this.min;
    }

    public float getMax() {
        return this.max;
    }

    public float getIncrement() {
        return this.increment;
    }

    public String getUnit() {
        return this.unit;
    }
}
