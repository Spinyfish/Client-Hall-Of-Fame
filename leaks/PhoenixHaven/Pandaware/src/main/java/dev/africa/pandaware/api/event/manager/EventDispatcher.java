package dev.africa.pandaware.api.event.manager;

import dev.africa.pandaware.Client;
import dev.africa.pandaware.api.event.Event;
import dev.africa.pandaware.api.event.interfaces.EventCallback;
import dev.africa.pandaware.api.event.interfaces.EventHandler;
import dev.africa.pandaware.api.event.interfaces.EventListenable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

// modified lennox event system
public class EventDispatcher {
    private final Map<Type, List<EventCallbackStorage>> storageMap = new HashMap<>();
    private final Map<Type, List<EventCallback<Event>>> callbackMap = new HashMap<>();

    // a little more performance by using for index loop
    @SuppressWarnings("ForLoopReplaceableByForEach")
    public void subscribe(EventListenable eventListener) {
        Field[] fields = eventListener.getClass().getDeclaredFields();

        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];

            try {
                if (field.getType() == EventCallback.class && field.isAnnotationPresent(EventHandler.class)) {
                    if (!field.isAccessible()) {
                        field.setAccessible(true);
                    }

                    Type type = ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
                    EventCallback<Event> callback = (EventCallback<Event>) field.get(eventListener);

                    if (this.storageMap.containsKey(type)) {
                        this.storageMap.get(type)
                                .add(new EventCallbackStorage(eventListener, callback));
                    } else {
                        this.storageMap.put(type, new ArrayList<>(Collections.singletonList(
                                new EventCallbackStorage(eventListener, callback))));
                    }
                }
                if (Client.getInstance().isKillSwitch()) {
                    throw new IllegalAccessException();
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        this.updateCallbacks();
    }

    public boolean unsubscribe(EventListenable listener) {
        boolean found = false;

        for (List<EventCallbackStorage> value : this.storageMap.values()) {
            if (value.removeIf(storage -> storage.getEventListener() == listener)) {
                found = true;
            }
        }

        this.updateCallbacks();

        return found;
    }

    // a little more performance by using for index loop
    @SuppressWarnings("ForLoopReplaceableByForEach")
    public Event dispatch(Event event) {
        List<EventCallback<Event>> callbacks = this.callbackMap.get(event.getClass());

        if (callbacks != null) {
            for (int i = 0; i < callbacks.size(); i++) {
                callbacks.get(i).invokeEvent(event);
            }
        }

        return event;
    }

    // a little more performance by using for index loop
    @SuppressWarnings("ForLoopReplaceableByForEach")
    void updateCallbacks() {
        for (Type type : this.storageMap.keySet()) {
            List<EventCallbackStorage> storages = this.storageMap.get(type);
            List<EventCallback<Event>> callbacks = new ArrayList<>(storages.size());

            for (int i = 0; i < storages.size(); i++) {
                callbacks.add(storages.get(i).getEventCallback());
            }

            this.callbackMap.put(type, callbacks);
        }
    }

    @Getter
    @AllArgsConstructor
    private static class EventCallbackStorage {
        private final EventListenable eventListener;
        private final EventCallback<Event> eventCallback;
    }
}
