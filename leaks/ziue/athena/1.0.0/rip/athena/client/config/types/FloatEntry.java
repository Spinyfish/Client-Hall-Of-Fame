package rip.athena.client.config.types;

import rip.athena.client.config.*;
import java.lang.reflect.*;
import org.json.*;

public class FloatEntry extends ConfigEntry
{
    private float min;
    private float max;
    
    public FloatEntry(final Field field, final float min, final float max, final String key, final String description, final boolean visible) {
        super(field, key, description, visible);
        this.min = min;
        this.max = max;
    }
    
    @Override
    public Class<?> getType() {
        return Float.TYPE;
    }
    
    @Override
    public void appendToConfig(final String key, final Object value, final JSONObject config) {
        if (!(value instanceof Float)) {
            return;
        }
        config.put(key, Float.valueOf(String.valueOf(value)));
    }
    
    public float getMin() {
        return this.min;
    }
    
    public float getMax() {
        return this.max;
    }
}
