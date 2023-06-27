package rip.athena.client.config.types;

import rip.athena.client.config.*;
import java.lang.reflect.*;
import java.awt.*;
import org.json.*;

public class ColorEntry extends ConfigEntry
{
    public ColorEntry(final Field field, final String key, final String description, final boolean visible) {
        super(field, key, description, visible);
    }
    
    @Override
    public Class<?> getType() {
        return Color.class;
    }
    
    @Override
    public void appendToConfig(final String key, final Object value, final JSONObject config) {
        if (!(value instanceof Color)) {
            return;
        }
        final Color color = (Color)value;
        final JSONObject obj = new JSONObject();
        obj.put("red", color.getRed());
        obj.put("green", color.getGreen());
        obj.put("blue", color.getBlue());
        obj.put("alpha", color.getAlpha());
        config.put(key, obj);
    }
}
