/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  vip.astroline.client.service.module.value.Value
 *  vip.astroline.client.service.module.value.ValueManager
 *  vip.astroline.client.storage.utils.gui.clickgui.SmoothAnimationTimer
 */
package vip.astroline.client.service.module.value;

import java.util.ArrayList;
import java.util.Arrays;
import vip.astroline.client.service.module.value.Value;
import vip.astroline.client.service.module.value.ValueManager;
import vip.astroline.client.storage.utils.gui.clickgui.SmoothAnimationTimer;

public class ModeValue
extends Value {
    SmoothAnimationTimer alphaTimer = new SmoothAnimationTimer(1.0f, 0.05f);
    private String[] modes;

    public ModeValue(String group, String key, String mode, String ... otherModes) {
        this(group, key, mode, false, otherModes);
    }

    public ModeValue(String group, String key, String mode, boolean fromAPI, String ... otherModes) {
        this.group = group;
        this.key = key;
        this.value = mode;
        ArrayList<String> modes = new ArrayList<String>(Arrays.asList(otherModes));
        if (!modes.contains(mode)) {
            modes.add(mode);
        }
        this.modes = modes.toArray(new String[0]);
        if (fromAPI) return;
        ValueManager.addValue((Value)this);
    }

    public String getValue() {
        return (String)this.value;
    }

    public void setValue(String value) {
        if (value == null) {
            return;
        }
        if (!Arrays.asList(this.modes).contains(value)) return;
        this.value = value;
    }

    public String[] getValues() {
        return this.modes;
    }

    public String getCurVal() {
        return (String)this.value;
    }

    public void setCurVal(String val) {
        this.setValue(val);
    }

    public boolean isCurrentMode(String string) {
        return this.getValue().equals(string);
    }

    public SmoothAnimationTimer getAlphaTimer() {
        return this.alphaTimer;
    }

    public String[] getModes() {
        return this.modes;
    }
}
