/*
 * Decompiled with CFR 0.152.
 */
package vip.astroline.client.service.module.value;

public class Value {
    protected String group;
    protected String key;
    protected Object value;

    public Object getValue() {
        return this.value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public String getGroup() {
        return this.group;
    }

    public String getKey() {
        return this.key;
    }
}
