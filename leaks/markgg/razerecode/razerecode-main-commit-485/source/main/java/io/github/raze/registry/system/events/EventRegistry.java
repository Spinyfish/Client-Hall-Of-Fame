package io.github.raze.registry.system.events;

import io.github.raze.events.system.BaseEvent;
import io.github.raze.events.system.SubscribeEvent;
import io.github.raze.utilities.collection.arrays.ArrayUtil;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class EventRegistry {

    public static Map<Class<? extends BaseEvent>, ArrayUtil<BaseEvent.Data>> REGISTRY_MAP = new HashMap<>();

    public void register(Object o) {
        for (Method method : o.getClass().getDeclaredMethods()) {
            if (!isMethodBad(method)) {
                register(method, o);
            }
        }
    }

    public void register(Object o, Class<? extends BaseEvent> input) {
        for (Method method : o.getClass().getDeclaredMethods()) {
            if (!isMethodBad(method, input)) {
                register(method, o);
            }
        }
    }

    private void register(Method method, Object object) {
        Class<?> clazz = method.getParameterTypes()[0];
        BaseEvent.Data methodData = new BaseEvent.Data(object, method, method.getAnnotation(SubscribeEvent.class).value());

        if (!methodData.target.isAccessible()) {
            methodData.target.setAccessible(true);
        }

        if (REGISTRY_MAP.containsKey(clazz)) {
            if (!REGISTRY_MAP.get(clazz).contains(methodData)) {
                REGISTRY_MAP.get(clazz).add(methodData);
                sortListValue((Class<? extends BaseEvent>) clazz);
            }
        } else {
            REGISTRY_MAP.put((Class<? extends BaseEvent>) clazz, new ArrayUtil<BaseEvent.Data>() {{
                    add(methodData);
            }});
        }
    }

    public void unregister(Object object) {
        for (ArrayUtil<BaseEvent.Data> flexibalArray : REGISTRY_MAP.values()) {
            for (BaseEvent.Data methodData : flexibalArray) {
                if (methodData.source.equals(object)) {
                    flexibalArray.remove(methodData);
                }
            }
        }

        clean(true);
    }

    public void unregister(Object object, Class<? extends BaseEvent> input) {
        if (REGISTRY_MAP.containsKey(input)) {
            for (BaseEvent.Data methodData : REGISTRY_MAP.get(input)) {
                if (methodData.source.equals(object)) {
                    REGISTRY_MAP.get(input).remove(methodData);
                }
            }

            clean(true);
        }
    }

    public ArrayUtil<BaseEvent.Data> get(Class<? extends BaseEvent> input) {
        return REGISTRY_MAP.get(input);
    }

    public void clean(boolean input) {
        Iterator<Map.Entry<Class<? extends BaseEvent>, ArrayUtil<BaseEvent.Data>>> iterator = REGISTRY_MAP.entrySet().iterator();

        while (iterator.hasNext()) {
            if (!input || iterator.next().getValue().isEmpty()) {
                iterator.remove();
            }
        }
    }

    public void remove(Class<? extends BaseEvent> input) {
        Iterator<Map.Entry<Class<? extends BaseEvent>, ArrayUtil<BaseEvent.Data>>> iterator = REGISTRY_MAP.entrySet().iterator();

        while (iterator.hasNext()) {
            if (iterator.next().getKey().equals(input)) {
                iterator.remove();
                break;
            }
        }
    }

    private void sortListValue(Class<? extends BaseEvent> input) {
        ArrayUtil<BaseEvent.Data> flexibleArray = new ArrayUtil<>();

        for (byte b : BaseEvent.Priority.VALUE_ARRAY) {
            for (BaseEvent.Data methodData : REGISTRY_MAP.get(input)) {
                if (methodData.priority == b) {
                    flexibleArray.add(methodData);
                }
            }
        }

        REGISTRY_MAP.put(input, flexibleArray);
    }

    private boolean isMethodBad(Method method) {
        return method.getParameterTypes().length != 1 || !method.isAnnotationPresent(SubscribeEvent.class);
    }

    private boolean isMethodBad(Method method, Class<? extends BaseEvent> input) {
        return isMethodBad(method) || method.getParameterTypes()[0].equals(input);
    }

    public void shutdown() {
        REGISTRY_MAP.clear();
    }
    
}
