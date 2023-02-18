/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  vip.astroline.client.service.module.value.Value
 *  vip.astroline.client.service.module.value.ValueManager
 */
package vip.astroline.client.service.module.value;

import vip.astroline.client.service.module.value.Value;
import vip.astroline.client.service.module.value.ValueManager;

public class BooleanValue
extends Value {
    public BooleanValue(String group, String key, Boolean value, boolean fromAPI) {
        this.group = group;
        this.key = key;
        this.value = value;
        if (fromAPI) return;
        ValueManager.addValue((Value)this);
    }

    public BooleanValue(String group, String key, Boolean value) {
        this(group, key, value, false);
    }

    public Boolean getValue() {
        return (Boolean)this.value;
    }

    public void setValue(Object value) {
        super.setValue(value);
    }

    public boolean getValueState() {
        return this.getValue();
    }
}
