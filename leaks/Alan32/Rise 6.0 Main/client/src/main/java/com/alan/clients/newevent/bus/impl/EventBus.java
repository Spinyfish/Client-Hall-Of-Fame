package com.alan.clients.newevent.bus.impl;

import com.alan.clients.Client;
import com.alan.clients.newevent.Listener;
import com.alan.clients.newevent.annotations.EventLink;
import com.alan.clients.newevent.bus.Bus;
import com.alan.clients.newevent.impl.other.*;
import com.alan.clients.util.chat.ChatUtil;
import com.alan.clients.util.interfaces.InstanceAccess;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

public final class EventBus<Event> implements Bus<Event>, InstanceAccess {

    private final Map<Type, List<CallSite<Event>>> callSiteMap;
    private final Map<Type, List<Listener<Event>>> listenerCache;

    private static final MethodHandles.Lookup LOOKUP = MethodHandles.lookup();

    public EventBus() {
        callSiteMap = new HashMap<>();
        listenerCache = new HashMap<>();
    }

    @Override
    public void register(final Object subscriber) {
        try {
            for (final Field field : subscriber.getClass().getDeclaredFields()) {
                final EventLink annotation = field.getAnnotation(EventLink.class);
                if (annotation != null) {
                    final Type eventType = ((ParameterizedType) (field.getGenericType())).getActualTypeArguments()[0];

                    if (!field.isAccessible())
                        field.setAccessible(true);
                    try {
                        final Listener<Event> listener =
                                (Listener<Event>) LOOKUP.unreflectGetter(field)
                                        .invokeWithArguments(subscriber);

                        final byte priority = annotation.value();

                        final List<CallSite<Event>> callSites;
                        final CallSite<Event> callSite = new CallSite<>(subscriber, listener, priority);

                        if (this.callSiteMap.containsKey(eventType)) {
                            callSites = this.callSiteMap.get(eventType);
                            callSites.add(callSite);
                            callSites.sort((o1, o2) -> o2.priority - o1.priority);
                        } else {
                            callSites = new ArrayList<>(1);
                            callSites.add(callSite);
                            this.callSiteMap.put(eventType, callSites);
                        }
                    } catch (Throwable exception) {
                        if (!Client.DEVELOPMENT_SWITCH) return;
                        ChatUtil.display("Exception in console");
                        exception.printStackTrace();
                    }
                }
            }

            this.populateListenerCache();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private void populateListenerCache() {
        final Map<Type, List<CallSite<Event>>> callSiteMap = this.callSiteMap;
        final Map<Type, List<Listener<Event>>> listenerCache = this.listenerCache;

        for (final Type type : callSiteMap.keySet()) {
            final List<CallSite<Event>> callSites = callSiteMap.get(type);
            final int size = callSites.size();
            final List<Listener<Event>> listeners = new ArrayList<>(size);

            for (int i = 0; i < size; i++)
                listeners.add(callSites.get(i).listener);

            listenerCache.put(type, listeners);
        }
    }

    @Override
    public void unregister(final Object subscriber) {
        for (List<CallSite<Event>> callSites : this.callSiteMap.values()) {
            callSites.removeIf(eventCallSite -> eventCallSite.owner == subscriber);
        }

        this.populateListenerCache();
    }

    @Override
    public void handle(final Event event) {
        if ((mc.theWorld == null) && !(event instanceof BackendPacketEvent || event instanceof ServerKickEvent || event instanceof GameEvent || event instanceof WorldChangeEvent || event instanceof ServerJoinEvent)) return;

        final List<Listener<Event>> listeners = listenerCache.getOrDefault(event.getClass(), Collections.emptyList());

        int i = 0;
        final int listenersSize = listeners.size();

        while (i < listenersSize) {
            listeners.get(i++).call(event);
        }
    }

    public void handle(final Event event, final Object... listeners) {
        int i = 0;
        final int listenersSize = listeners.length;
        final List<Object> list = Arrays.asList(listeners);
        while (i < listenersSize) {
            ((Listener)list.get(i++)).call(event);
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