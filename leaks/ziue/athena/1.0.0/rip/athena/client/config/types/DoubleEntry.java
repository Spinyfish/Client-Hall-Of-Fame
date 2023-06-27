package rip.athena.client.config.types;

import rip.athena.client.config.*;
import java.lang.reflect.*;
import org.json.*;

public class DoubleEntry extends ConfigEntry
{
    private double min;
    private double max;
    
    public DoubleEntry(final Field field, final double min, final double max, final String key, final String description, final boolean visible) {
        super(field, key, description, visible);
        this.min = min;
        this.max = max;
    }
    
    @Override
    public Class<?> getType() {
        return Double.TYPE;
    }
    
    @Override
    public void appendToConfig(final String key, final Object value, final JSONObject config) {
        if (!(value instanceof Double)) {
            return;
        }
        config.put(key, Double.valueOf(String.valueOf(value)));
    }
    
    public double getMin() {
        return this.min;
    }
    
    public double getMax() {
        return this.max;
    }
}
