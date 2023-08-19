package dev.lemon.api.event.bus;

import dev.lemon.api.event.Event;
import dev.lemon.api.event.bus.annotation.Subscribe;
import dev.lemon.api.event.bus.listener.IListener;

import java.lang.reflect.Field;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class EventBus implements IEventBus {
    private final Map<Class<?>, List<IListener<Event>>> listeners = new ConcurrentHashMap<>();
    private final Map<IListener<Event>, Type> types = new HashMap<>();

    @Override
    public void post(Event e) {
        for (Iterator<Map.Entry<Class<?>, List<IListener<Event>>>> it = listeners.entrySet().iterator();
             it.hasNext();){
                List<IListener<Event>> value = it.next().getValue();
                for (IListener<Event> iListener : value) {
                    try {
                        Type type = types.get(iListener);

                        if (type.getTypeName().equals(e.getClass().getTypeName())) {
                            iListener.post(e);
                        }
                    } catch (ClassCastException exception) {
                        exception.printStackTrace();
                    }
                }
        }
    }

    @Override
    public void subscribe(Object o) {
        Class<?> clazz = o.getClass();
        List<IListener<Event>> clazzListeners = new ArrayList<>();
        for (Field f : clazz.getDeclaredFields()){
            try {
                f.setAccessible(true);

                if (f.isAnnotationPresent(Subscribe.class) && f.getType().isAssignableFrom(IListener.class) && f.isAccessible()) {
                    IListener<Event> listener = (IListener<Event>) (MethodHandles.lookup().unreflectGetter(f).invokeWithArguments(o));

                    types.put(listener, ((ParameterizedType)f.getGenericType()).getActualTypeArguments()[0]);

                    clazzListeners.add(listener);

                }
            } catch (Throwable e){
                e.printStackTrace();
            }
        }
        if (!clazzListeners.isEmpty()){
            listeners.put(clazz, clazzListeners);
        }

    }

    @Override
    public void unsubscribe(Object o) {
        listeners.remove(o.getClass());
    }
}