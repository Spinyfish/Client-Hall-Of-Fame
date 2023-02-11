/*
 * Decompiled with CFR 0_132.
 */
package Reality.Realii.event;

import Reality.Realii.event.events.world.EventPostUpdate;
import Reality.Realii.event.events.world.EventPreUpdate;
import Reality.Realii.event.events.world.EventTick;
import Reality.Realii.mods.Module;
import Reality.Realii.utils.cheats.player.Helper;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class EventBus {
    private ConcurrentHashMap<Class<? extends Event>, List<Handler>> registry = new ConcurrentHashMap();
    private final Comparator<Handler> comparator = (h, h1) -> Byte.compare(h.priority, h1.priority);
    private final MethodHandles.Lookup lookup = MethodHandles.lookup();
    private static final EventBus instance = new EventBus();

    public static EventBus getInstance() {
        return instance;
    }

    public /* varargs */ void register(Object ... objs) {
        Object[] arrobject = objs;
        int n = arrobject.length;
        int n2 = 0;
        while (n2 < n) {
            Object obj = arrobject[n2];
            Method[] arrmethod = obj.getClass().getDeclaredMethods();
            int n3 = arrmethod.length;
            int n4 = 0;
            while (n4 < n3) {
                Method m = arrmethod[n4];
                if (m.getParameterCount() == 1 && m.isAnnotationPresent(EventHandler.class)) {
                    Class<?> eventClass = m.getParameterTypes()[0];
                    if (!this.registry.containsKey(eventClass)) {
                        this.registry.put((Class<? extends Event>) eventClass, new CopyOnWriteArrayList());
                    }
                    this.registry.get(eventClass).add(new Handler(m, obj, m.getDeclaredAnnotation(EventHandler.class).priority()));
                    this.registry.get(eventClass).sort(this.comparator);
                }
                ++n4;
            }
            ++n2;
        }
    }

    public /* varargs */ void unregister(Object ... objs) {
        Object[] arrobject = objs;
        int n = arrobject.length;
        int n2 = 0;
        while (n2 < n) {
            Object obj = arrobject[n2];
            for (List<Handler> list : this.registry.values()) {
                for (Handler data : list) {
                    if (data.parent != obj) continue;
                    list.remove(data);
                }
            }
            ++n2;
        }
    }

    public <E extends Event> E call(E event) {
        boolean whiteListedEvents = event instanceof EventTick || event instanceof EventPreUpdate || event instanceof EventPostUpdate;
        List<Handler> list = this.registry.get(event.getClass());
        if (list != null && !list.isEmpty()) {
            for (Handler data : list) {
                try {
                    if (list instanceof Module) {
                        if (((Module)((Object)list)).isEnabled()) {
                            if (whiteListedEvents) {
                                Helper.mc.mcProfiler.startSection(((Module)((Object)list)).getName());
                            }
                            if (whiteListedEvents) {
                                Helper.mc.mcProfiler.endSection();
                            }
                        }
                    } else {
                        if (whiteListedEvents) {
                            Helper.mc.mcProfiler.startSection("non module");
                        }
                        if (whiteListedEvents) {
                            Helper.mc.mcProfiler.endSection();
                        }
                    }
                    data.handler.invokeExact(data.parent, event);
                }
                catch (Throwable e1) {
                    e1.printStackTrace();
                }
            }
        }
        return event;
    }

    private class Handler {
        private MethodHandle handler;
        private Object parent;
        private byte priority;

        public Handler(Method method, Object parent, byte priority) {
            if (!method.isAccessible()) {
                method.setAccessible(true);
            }
            MethodHandle m = null;
            try {
                m = EventBus.this.lookup.unreflect(method);
            }
            catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            if (m != null) {
                this.handler = m.asType(m.type().changeParameterType(0, Object.class).changeParameterType(1, Event.class));
            }
            this.parent = parent;
            this.priority = priority;
        }
    }

}

