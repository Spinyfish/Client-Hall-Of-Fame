package rip.athena.client.config.types;

import rip.athena.client.config.*;
import rip.athena.client.modules.*;
import java.lang.reflect.*;
import rip.athena.client.*;
import org.json.*;

public class ListEntry extends ConfigEntry
{
    private Module module;
    private String[] values;
    
    public ListEntry(final Module module, final Field field, final String[] values, final String key, final String description, final boolean visible) {
        super(field, key, description, visible);
        this.module = module;
        this.values = values;
    }
    
    @Override
    public Class<?> getType() {
        return String[].class;
    }
    
    @Override
    public void appendToConfig(final String key, final Object value, final JSONObject config) {
        if (!(value instanceof String)) {
            return;
        }
        try {
            config.put(key, String.valueOf(this.getField().get(this.module)));
        }
        catch (JSONException | IllegalArgumentException | IllegalAccessException ex2) {
            final Exception ex;
            final Exception e = ex;
            Athena.INSTANCE.getLog().error("Failed to read list entry for " + this.module + ", " + this.getField() + "." + e);
        }
    }
    
    public String[] getValues() {
        return this.values;
    }
}
