package rip.athena.client.events;

import java.lang.annotation.*;
import java.lang.reflect.*;
import java.util.*;
import rip.athena.client.*;

public class EventBus
{
    public static final boolean DEBUG = false;
    private final Map<Object, List<Method>> registry;
    
    public EventBus() {
        this.registry = new LinkedHashMap<Object, List<Method>>();
    }
    
    public void register(final Object clazz) {
        final Method[] methods = clazz.getClass().getDeclaredMethods();
        final List<Method> registered = new ArrayList<Method>();
        for (final Method method : methods) {
            if (method.isAnnotationPresent(SubscribeEvent.class) && method.getParameterCount() == 1) {
                if (!method.isAccessible()) {
                    method.setAccessible(true);
                }
                final Class<?>[] parameters = method.getParameterTypes();
                if (Event.class.isAssignableFrom(parameters[0])) {
                    registered.add(method);
                }
            }
        }
        if (!registered.isEmpty()) {
            this.registry.put(clazz, registered);
        }
    }
    
    public void unregister(final Object clazz) {
        this.registry.remove(clazz);
    }
    
    public boolean post(final Event event) {
        synchronized (this.registry) {
            final Map<Object, List<Method>> snapshot = new LinkedHashMap<Object, List<Method>>(this.registry);
            for (final Object key : snapshot.keySet()) {
                final List<Method> value = snapshot.get(key);
                for (final Method method : value) {
                    final Class<?>[] parameters = method.getParameterTypes();
                    if (parameters[0] == event.getClass()) {
                        try {
                            method.invoke(key, event);
                        }
                        catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex2) {
                            final Exception ex;
                            final Exception e = ex;
                            e.printStackTrace();
                            this.debug("Failed supplying subscribed event " + event.getClass().getName() + " to " + key.getClass().getName() + "." + method.getName() + ".");
                        }
                    }
                }
            }
            return !event.isCancelled();
        }
    }
    
    private void debug(final String string) {
        Athena.INSTANCE.getLog().info("EventBus - " + string);
    }
    
    public boolean isRegistered(final Object clazz) {
        return this.registry.containsKey(clazz);
    }
}
