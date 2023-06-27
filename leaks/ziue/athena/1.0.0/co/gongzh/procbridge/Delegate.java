package co.gongzh.procbridge;

import java.util.*;
import org.jetbrains.annotations.*;
import org.json.*;
import java.lang.reflect.*;

public abstract class Delegate implements IDelegate
{
    @NotNull
    private final Map<String, Method> handlers;
    
    protected Delegate() {
        this.handlers = new HashMap<String, Method>();
        for (final Method m : this.getClass().getDeclaredMethods()) {
            if (m.getAnnotation(Handler.class) != null) {
                final String key = m.getName();
                if (this.handlers.containsKey(key)) {
                    throw new UnsupportedOperationException("duplicate handler name: " + key);
                }
                m.setAccessible(true);
                this.handlers.put(key, m);
            }
        }
    }
    
    protected void willHandleRequest(@Nullable final String method, @Nullable final Object payload) {
    }
    
    @Nullable
    protected Object handleUnknownRequest(@Nullable final String method, @Nullable final Object payload) {
        throw new ServerException("unknown method: " + method);
    }
    
    @Nullable
    @Override
    public final Object handleRequest(@Nullable final String method, @Nullable final Object payload) {
        this.willHandleRequest(method, payload);
        final Method m = this.handlers.get(method);
        if (m == null) {
            return this.handleUnknownRequest(method, payload);
        }
        Object result = null;
        try {
            final int pcnt = m.getParameterCount();
            if (pcnt == 0) {
                result = m.invoke(this, new Object[0]);
            }
            else if (pcnt == 1) {
                result = m.invoke(this, payload);
            }
            else {
                if (!(payload instanceof JSONArray)) {
                    throw new ServerException("payload must be an array");
                }
                final JSONArray arr = (JSONArray)payload;
                if (arr.length() != pcnt) {
                    throw new ServerException(String.format("method needs %d elements", pcnt));
                }
                result = m.invoke(this, arr.toList().toArray());
            }
        }
        catch (InvocationTargetException e) {
            throw new ServerException(e.getTargetException());
        }
        catch (IllegalAccessException e2) {
            throw new ServerException(e2);
        }
        return result;
    }
}
