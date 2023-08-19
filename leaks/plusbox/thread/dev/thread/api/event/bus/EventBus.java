package dev.thread.api.event.bus;

import dev.thread.api.event.Event;
import dev.thread.api.event.bus.annotation.Subscribe;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class EventBus extends ConcurrentHashMap<Class<?>, Object> {
    public void subscribe(Object object) {
        if (!containsKey(object.getClass())) {
            put(object.getClass(), object);
        }
    }

    public void unsubscribe(Object object) {
        if (containsKey(object.getClass())) {
            remove(object.getClass());
        }
    }

    public void post(Event event) {
        keySet().forEach((clazz) -> {
            List<Method> declaredMethods = Arrays.stream(clazz.getDeclaredMethods()).filter((method) -> method.isAnnotationPresent(Subscribe.class) && method.getParameterTypes()[0].isAssignableFrom(event.getClass())).collect(Collectors.toList());
            declaredMethods.forEach((method) -> {
                boolean accessible = method.isAccessible();
                if (!accessible) {
                    method.setAccessible(true);
                }

                try {
                    method.invoke(get(clazz), event);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }

                if (!accessible) {
                    method.setAccessible(false);
                }
            });
        });
    }
}
