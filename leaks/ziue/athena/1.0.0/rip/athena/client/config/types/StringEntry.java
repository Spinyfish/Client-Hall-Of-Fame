package rip.athena.client.config.types;

import rip.athena.client.config.*;
import java.lang.reflect.*;
import org.json.*;

public class StringEntry extends ConfigEntry
{
    public StringEntry(final Field field, final String key, final String description, final boolean visible) {
        super(field, key, description, visible);
    }
    
    @Override
    public Class<?> getType() {
        return String.class;
    }
    
    @Override
    public void appendToConfig(final String key, final Object value, final JSONObject config) {
        if (!(value instanceof String)) {
            return;
        }
        config.put(key, String.valueOf(value));
    }
}
