package rip.athena.client.config.types;

import rip.athena.client.config.*;
import java.lang.reflect.*;
import org.json.*;

public class BooleanEntry extends ConfigEntry
{
    public BooleanEntry(final Field field, final String key, final String description, final boolean visible) {
        super(field, key, description, visible);
    }
    
    @Override
    public Class<?> getType() {
        return Boolean.TYPE;
    }
    
    @Override
    public void appendToConfig(final String key, final Object value, final JSONObject config) {
        if (!(value instanceof Boolean)) {
            return;
        }
        config.put(key, Boolean.parseBoolean(String.valueOf(value)));
    }
}
