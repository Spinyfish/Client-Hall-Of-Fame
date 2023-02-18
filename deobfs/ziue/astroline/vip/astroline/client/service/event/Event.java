/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  vip.astroline.client.service.event.Data
 *  vip.astroline.client.service.event.EventManager
 *  vip.astroline.client.service.event.types.ArrayHelper
 */
package vip.astroline.client.service.event;

import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import vip.astroline.client.service.event.Data;
import vip.astroline.client.service.event.EventManager;
import vip.astroline.client.service.event.types.ArrayHelper;

public abstract class Event {
    private boolean cancelled;

    public Event call() {
        this.cancelled = false;
        Event.call(this);
        return this;
    }

    public boolean isCancelled() {
        return this.cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    private static void call(Event event) {
        ArrayHelper dataList = EventManager.get(event.getClass());
        if (dataList == null) return;
        Iterator iterator = dataList.iterator();
        while (iterator.hasNext()) {
            Data data = (Data)iterator.next();
            try {
                data.target.invoke(data.source, event);
            }
            catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }
}
