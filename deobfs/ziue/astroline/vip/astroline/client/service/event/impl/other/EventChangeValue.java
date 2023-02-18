/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  vip.astroline.client.service.event.Event
 */
package vip.astroline.client.service.event.impl.other;

import vip.astroline.client.service.event.Event;

public class EventChangeValue
extends Event {
    public String valKey;
    public String valName;
    public Object oldVal;
    public Object val;

    public EventChangeValue(String valKey, String valName, Object oldVal, Object val) {
        this.valKey = valKey;
        this.valName = valName;
        this.oldVal = oldVal;
        this.val = val;
    }
}
