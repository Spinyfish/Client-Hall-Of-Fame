/*
 * Decompiled with CFR 0.152.
 */
package vip.astroline.client.service.event;

import java.lang.reflect.Method;

public class Data {
    public final Object source;
    public Method target;
    public final byte priority;

    Data(Object source, Method target, byte priority) {
        this.source = source;
        this.target = target;
        this.priority = priority;
    }
}
