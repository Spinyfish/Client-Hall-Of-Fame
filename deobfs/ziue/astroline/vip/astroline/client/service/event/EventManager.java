/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  vip.astroline.client.service.event.Data
 *  vip.astroline.client.service.event.Event
 *  vip.astroline.client.service.event.types.ArrayHelper
 *  vip.astroline.client.service.event.types.EventTarget
 *  vip.astroline.client.service.event.types.Priority
 */
package vip.astroline.client.service.event;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import vip.astroline.client.service.event.Data;
import vip.astroline.client.service.event.Event;
import vip.astroline.client.service.event.types.ArrayHelper;
import vip.astroline.client.service.event.types.EventTarget;
import vip.astroline.client.service.event.types.Priority;

public class EventManager {
    private static final Map<Class<? extends Event>, ArrayHelper<Data>> REGISTRY_MAP = new HashMap<Class<? extends Event>, ArrayHelper<Data>>();

    public static void register(Object o) {
        Method[] methodArray = o.getClass().getDeclaredMethods();
        int n = methodArray.length;
        int n2 = 0;
        while (n2 < n) {
            Method method = methodArray[n2];
            if (!EventManager.isMethodBad(method)) {
                EventManager.register(method, o);
            }
            ++n2;
        }
    }

    public static void register(Object o, Class<? extends Event> clazz) {
        Method[] methodArray = o.getClass().getDeclaredMethods();
        int n = methodArray.length;
        int n2 = 0;
        while (n2 < n) {
            Method method = methodArray[n2];
            if (!EventManager.isMethodBad(method, clazz)) {
                EventManager.register(method, o);
            }
            ++n2;
        }
    }

    private static void register(Method method, Object o) {
        Class<?> clazz = method.getParameterTypes()[0];
        Data methodData = new Data(o, method, method.getAnnotation(EventTarget.class).value());
        if (!methodData.target.isAccessible()) {
            methodData.target.setAccessible(true);
        }
        if (REGISTRY_MAP.containsKey(clazz)) {
            if (REGISTRY_MAP.get(clazz).contains((Object)methodData)) return;
            REGISTRY_MAP.get(clazz).add((Object)methodData);
            EventManager.sortListValue(clazz);
        } else {
            REGISTRY_MAP.put(clazz, (ArrayHelper<Data>)new /* Unavailable Anonymous Inner Class!! */);
        }
    }

    public static void unregister(Object o) {
        Iterator<ArrayHelper<Data>> iterator = REGISTRY_MAP.values().iterator();
        block0: while (true) {
            if (!iterator.hasNext()) {
                EventManager.cleanMap(true);
                return;
            }
            ArrayHelper<Data> flexibalArray = iterator.next();
            Iterator iterator2 = flexibalArray.iterator();
            while (true) {
                if (!iterator2.hasNext()) continue block0;
                Data methodData = (Data)iterator2.next();
                if (!methodData.source.equals(o)) continue;
                flexibalArray.remove((Object)methodData);
            }
            break;
        }
    }

    public static void unregister(Object o, Class<? extends Event> clazz) {
        if (!REGISTRY_MAP.containsKey(clazz)) return;
        Iterator iterator = REGISTRY_MAP.get(clazz).iterator();
        while (true) {
            if (!iterator.hasNext()) {
                EventManager.cleanMap(true);
                return;
            }
            Data methodData = (Data)iterator.next();
            if (!methodData.source.equals(o)) continue;
            REGISTRY_MAP.get(clazz).remove((Object)methodData);
        }
    }

    public static void cleanMap(boolean b) {
        Iterator<Map.Entry<Class<? extends Event>, ArrayHelper<Data>>> iterator = REGISTRY_MAP.entrySet().iterator();
        while (iterator.hasNext()) {
            if (b && !iterator.next().getValue().isEmpty()) continue;
            iterator.remove();
        }
    }

    public static void removeEnty(Class<? extends Event> clazz) {
        Iterator<Map.Entry<Class<? extends Event>, ArrayHelper<Data>>> iterator = REGISTRY_MAP.entrySet().iterator();
        do {
            if (!iterator.hasNext()) return;
        } while (!iterator.next().getKey().equals(clazz));
        iterator.remove();
    }

    private static void sortListValue(Class<? extends Event> clazz) {
        ArrayHelper flexibleArray = new ArrayHelper();
        byte[] byArray = Priority.VALUE_ARRAY;
        int n = byArray.length;
        int n2 = 0;
        while (true) {
            if (n2 >= n) {
                REGISTRY_MAP.put(clazz, (ArrayHelper<Data>)flexibleArray);
                return;
            }
            byte b = byArray[n2];
            for (Data methodData : REGISTRY_MAP.get(clazz)) {
                if (methodData.priority != b) continue;
                flexibleArray.add((Object)methodData);
            }
            ++n2;
        }
    }

    private static boolean isMethodBad(Method method) {
        return method.getParameterTypes().length != 1 || !method.isAnnotationPresent(EventTarget.class);
    }

    private static boolean isMethodBad(Method method, Class<? extends Event> clazz) {
        return EventManager.isMethodBad(method) || method.getParameterTypes()[0].equals(clazz);
    }

    public static ArrayHelper<Data> get(Class<? extends Event> clazz) {
        return REGISTRY_MAP.get(clazz);
    }

    public static void shutdown() {
        REGISTRY_MAP.clear();
    }
}
