package club.pulsive.api.event.eventBus.core;


import club.pulsive.api.event.Event;
import club.pulsive.api.event.eventBus.handler.EventExecutable;
import club.pulsive.api.event.eventBus.handler.EventHandler;
import club.pulsive.api.event.eventBus.handler.EventObject;
import club.pulsive.api.event.eventBus.handler.Listener;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Comparator;

public class EventBus {

    //List of the objects registered in the event system
    private final ArrayList<EventObject> objects = new ArrayList<>();

    /**
     * Register an object in the event system with priority
     *
     * @param object        the object to register
     * @param eventPriority priority in the event system
     */
    public void register(final Object object, final EventPriority eventPriority) {
        if (isRegistered(object)) return;
        final EventObject eventObject = new EventObject(object, eventPriority);
        for (final Method method : object.getClass().getDeclaredMethods()) {
            if (!method.isAnnotationPresent(EventHandler.class)) continue;
            if (method.getParameterCount() <= 0) continue;
            eventObject.getEventExecutables().add(new EventExecutable(method));
        }
        for (final Field field : object.getClass().getDeclaredFields()) {
            if (!field.isAnnotationPresent(EventHandler.class)) continue;
            try {
                field.setAccessible(true);
                if (!(field.get(object) instanceof Listener)) continue;
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            eventObject.getEventExecutables().add(new EventExecutable(field));
        }
        objects.add(eventObject);
        objects.sort(Comparator.comparingInt(o1 -> o1.getEventPriority().getPriority()));
    }

    /**
     * Method used for calling events
     *
     * @param event the event that should be called
     */
    @SuppressWarnings("unchecked")
    public void call(final Event event) {
        try {
            for (final EventObject eventObject : objects)
                for (final EventExecutable eventExecutable : eventObject.getEventExecutables()) {
                    if (eventExecutable.getField() != null) {
                        try {
                            eventExecutable.getField().setAccessible(true);
                            if (eventExecutable.getField().getGenericType() instanceof ParameterizedType) {
                                ParameterizedType type = (ParameterizedType) eventExecutable.getField().getGenericType();
                                if (type.getActualTypeArguments().length > 0) {
                                    if (type.getActualTypeArguments()[0] == event.getClass() || type.getActualTypeArguments()[0] == Event.class)
                                        ((Listener<Event>) eventExecutable.getField().get(eventObject.getObject())).call(event);
                                }
                            }
                        } catch (Exception ignored) {}
                    }
                    if (eventExecutable.getMethod() != null) {
                        if (isValidMethod(eventExecutable.getMethod(), event.getClass()) ||
                                isValidMethod(eventExecutable.getMethod(), Event.class)) {
                            try {
                                eventExecutable.getMethod().setAccessible(true);
                                eventExecutable.getMethod().invoke(eventObject.getObject(), event);
                            } catch (Exception ignored) {
                            }
                        }
                    }
                }
        } catch (Exception ignored) {
        }
    }

    /**
     * Register an object in the event system without priority
     *
     * @param object the object to register
     */
    public void register(final Object object) {
        register(object, EventPriority.DEFAULT);
    }

    /**
     * Unregister an object from the event system
     *
     * @param object the object to unregister
     */
    public void unregister(final Object object) {
        if (!isRegistered(object)) return;
        objects.removeIf(o -> o.getObject().equals(object));
    }

    /**
     * Method used to check whether an object is registered in the event system
     *
     * @param object the object to check
     */
    public boolean isRegistered(final Object object) {
        return objects.stream().anyMatch(o -> o.getObject().equals(object));
    }

    /**
     * Method used to check whether a method is valid for the event system
     *
     * @param method the method that should be checked
     */
    public boolean isValidMethod(final Method method, final Class<?> clazz) {
        for (final Class<?> parameter : method.getParameterTypes())
            if (clazz == parameter && method.getParameterCount() == 1) return true;
        return false;
    }

}