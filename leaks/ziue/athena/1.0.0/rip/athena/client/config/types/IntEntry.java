package rip.athena.client.config.types;

import rip.athena.client.config.*;
import java.lang.reflect.*;
import org.json.*;

public class IntEntry extends ConfigEntry
{
    private int min;
    private int max;
    private boolean isKeyBind;
    
    public IntEntry(final Field field, final int min, final int max, final String key, final String description, final boolean visible) {
        super(field, key, description, visible);
        this.min = min;
        this.max = max;
    }
    
    public IntEntry(final Field field, final String key, final String description, final boolean visible) {
        super(field, key, description, visible);
        this.isKeyBind = true;
    }
    
    @Override
    public Class<?> getType() {
        return Integer.TYPE;
    }
    
    @Override
    public void appendToConfig(final String key, final Object value, final JSONObject config) {
        if (!(value instanceof Integer)) {
            return;
        }
        config.put(key, Integer.valueOf(String.valueOf(value)));
    }
    
    public int getMin() {
        return this.min;
    }
    
    public int getMax() {
        return this.max;
    }
    
    public boolean isKeyBind() {
        return this.isKeyBind;
    }
}
