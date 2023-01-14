package cafe.corrosion.event.bus;

import cafe.corrosion.event.Event;
import cafe.corrosion.event.handler.IHandler;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class EventBus {
    private static final Map<IHandler, Map<Class<? extends Event>, List<Consumer<? extends Event>>>> EVENT_HANDLERS = new HashMap();

    public <T extends Event> void register(IHandler handler, Class<T> clazz, Consumer<T> consumer) {
        Map<Class<? extends Event>, List<Consumer<? extends Event>>> map = (Map)EVENT_HANDLERS.getOrDefault(handler, new HashMap());
        List<Consumer<? extends Event>> consumers = (List)map.getOrDefault(clazz, new ArrayList());
        consumers.add(consumer);
        map.put(clazz, consumers);
        EVENT_HANDLERS.put(handler, map);
    }

    public <T extends Event> void handle(T event) {
        try {
            Class<Event> clazz = event.getClass();
            EVENT_HANDLERS.forEach((handler, map) -> {
                if (handler.isEnabled()) {
                    map.forEach((eventClass, list) -> {
                        if (eventClass.equals(clazz)) {
                            list.forEach((consumer) -> {
                                consumer.accept(event);
                            });
                        }
                    });
                }
            });
        } catch (Exception var3) {
            var3.printStackTrace();
        }

    }
}
