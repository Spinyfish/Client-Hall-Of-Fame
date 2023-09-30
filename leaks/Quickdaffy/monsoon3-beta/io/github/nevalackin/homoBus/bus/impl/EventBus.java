/*
 * Decompiled with CFR 0.152.
 */
package io.github.nevalackin.homoBus.bus.impl;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import io.github.nevalackin.homoBus.bus.Bus;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.Minecraft;
import wtf.monsoon.Wrapper;
import wtf.monsoon.impl.module.combat.TargetStrafe;
import wtf.monsoon.impl.module.movement.Speed;

public final class EventBus<Event>
implements Bus<Event> {
    private final Map<Type, List<CallSite<Event>>> callSiteMap = new HashMap<Type, List<CallSite<Event>>>();
    private final Map<Type, List<Listener<Event>>> listenerCache = new HashMap<Type, List<Listener<Event>>>();
    private static final MethodHandles.Lookup LOOKUP = MethodHandles.lookup();

    @Override
    public void subscribe(Object subscriber) {
        for (Field field : subscriber.getClass().getDeclaredFields()) {
            EventLink annotation = field.getAnnotation(EventLink.class);
            if (annotation == null) continue;
            Type eventType = ((ParameterizedType)field.getGenericType()).getActualTypeArguments()[0];
            if (!field.isAccessible()) {
                field.setAccessible(true);
            }
            try {
                List<Object> callSites;
                Listener listener = (Listener)LOOKUP.unreflectGetter(field).invokeWithArguments(subscriber);
                byte priority = annotation.value();
                CallSite callSite = new CallSite(subscriber, listener, priority);
                if (this.callSiteMap.containsKey(eventType)) {
                    callSites = this.callSiteMap.get(eventType);
                    callSites.add(callSite);
                    callSites.sort((o1, o2) -> ((CallSite)o2).priority - ((CallSite)o1).priority);
                    continue;
                }
                callSites = new ArrayList(1);
                callSites.add(callSite);
                this.callSiteMap.put(eventType, callSites);
            }
            catch (Throwable throwable) {
                // empty catch block
            }
        }
        this.populateListenerCache();
        if (subscriber instanceof Speed && Wrapper.getModule(TargetStrafe.class).isEnabled()) {
            this.unsubscribe(Wrapper.getModule(TargetStrafe.class));
            this.subscribe(Wrapper.getModule(TargetStrafe.class));
        }
    }

    private void populateListenerCache() {
        Map<Type, List<CallSite<Event>>> callSiteMap = this.callSiteMap;
        Map<Type, List<Listener<Event>>> listenerCache = this.listenerCache;
        for (Type type : callSiteMap.keySet()) {
            List<CallSite<Event>> callSites = callSiteMap.get(type);
            int size = callSites.size();
            ArrayList<Listener> listeners = new ArrayList<Listener>(size);
            for (int i = 0; i < size; ++i) {
                listeners.add(((CallSite)callSites.get(i)).listener);
            }
            listenerCache.put(type, listeners);
        }
    }

    @Override
    public void unsubscribe(Object subscriber) {
        for (List<CallSite<Event>> callSites : this.callSiteMap.values()) {
            callSites.removeIf(eventCallSite -> ((CallSite)eventCallSite).owner == subscriber);
        }
        this.populateListenerCache();
    }

    @Override
    public void post(Event event) {
        List listeners = this.listenerCache.getOrDefault(event.getClass(), Collections.emptyList());
        int i = 0;
        int listenersSize = listeners.size();
        while (i < listenersSize) {
            if (Minecraft.getMinecraft().thePlayer == null || Minecraft.getMinecraft().theWorld == null || !Minecraft.getMinecraft().getNetHandler().doneLoadingTerrain) {
                return;
            }
            ((Listener)listeners.get(i++)).call(event);
        }
    }

    private static class CallSite<Event> {
        private final Object owner;
        private final Listener<Event> listener;
        private final byte priority;

        public CallSite(Object owner, Listener<Event> listener, byte priority) {
            this.owner = owner;
            this.listener = listener;
            this.priority = priority;
        }
    }
}

