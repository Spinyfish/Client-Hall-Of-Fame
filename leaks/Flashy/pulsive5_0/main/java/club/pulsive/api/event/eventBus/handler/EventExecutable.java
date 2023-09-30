package club.pulsive.api.event.eventBus.handler;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class EventExecutable {
    //Method instances for registered methods
    private final Method method;

    //Field instance for registered listeners
    private final Field field;

    public EventExecutable(final Method method, final Field field) {
        this.method = method;
        this.field = field;
    }

    public EventExecutable(final Method method) {
        this(method, null);
    }

    public EventExecutable(final Field field) {
        this(null, field);
    }

    public Field getField() {
        return field;
    }

    public Method getMethod() {
        return method;
    }
}