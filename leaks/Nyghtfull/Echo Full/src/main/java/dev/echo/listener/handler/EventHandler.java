package dev.echo.listener.handler;

import dev.echo.listener.Link;
import dev.echo.listener.Listener;
import dev.echo.listener.event.Event;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

public class EventHandler {

    private final Map<Type, List<CallSite<Event>>> callSiteMap;
    private final Map<Type, List<Listener<Event>>> listenerCache;
    private final MethodHandles.Lookup LOOKUP = MethodHandles.lookup();

    public EventHandler() {
        callSiteMap = new HashMap<>();
        listenerCache = new HashMap<>();
    }

    @SuppressWarnings("unchecked")
    public void subscribe(Object subscriber) {
        for (Field field : subscriber.getClass().getDeclaredFields()) {
            Link annotation = field.getAnnotation(Link.class);
            
            if (annotation != null) {
                Type eventType = ((ParameterizedType) (field.getGenericType())).getActualTypeArguments()[0];

                if (!field.isAccessible())
                    field.setAccessible(true);

                try {
                    Listener<Event> listener = (Listener<Event>) LOOKUP.unreflectGetter(field).invokeWithArguments(subscriber);
                    byte priority = annotation.value();

                    List<CallSite<Event>> callSites;
                    CallSite<Event> callSite = new CallSite<Event>(listener, subscriber, priority);

                    if (this.callSiteMap.containsKey(eventType)) {
                        callSites = this.callSiteMap.get(eventType);
                        callSites.add(callSite);
                        callSites.sort((o1, o2) -> o2.priority - o1.priority);
                    } else {
                        callSites = new ArrayList<>(1);
                        callSites.add(callSite);
                        this.callSiteMap.put(eventType, callSites);
                    }
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            }
        }

        this.populateListenerCache();
    }

    public void unsubscribe(Object subscriber) {
        for (List<CallSite<Event>> callSites : this.callSiteMap.values()) {
            callSites.removeIf(eventCallSite -> eventCallSite.owner == subscriber);
        }

        this.populateListenerCache();
    }

    public void handleEvent(Event event) {
        List<Listener<Event>> listeners = listenerCache.getOrDefault(event.getClass(), Collections.emptyList());

        int i = 0;
        int listenersSize = listeners.size();

        while (i < listenersSize) {
            listeners.get(i++).call(event);
        }
    }

    private void populateListenerCache() {
        Map<Type, List<CallSite<Event>>> callSiteMap = this.callSiteMap;
        Map<Type, List<Listener<Event>>> listenerCache = this.listenerCache;

        for (Type type : callSiteMap.keySet()) {
            List<CallSite<Event>> callSites = callSiteMap.get(type);
            int size = callSites.size();
            List<Listener<Event>> listeners = new ArrayList<>(size);

            for (int i = 0; i < size; i++)
                listeners.add(callSites.get(i).listener);

            listenerCache.put(type, listeners);
        }
    }

    private static class CallSite<Event> {
        private final Listener<Event> listener;
        private final Object owner;
        private final byte priority;

        public CallSite(Listener<Event> listener, Object owner, byte priority) {
            this.owner = owner;
            this.listener = listener;
            this.priority = priority;
        }
    }
}