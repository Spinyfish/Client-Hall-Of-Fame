package org.json;

import java.util.*;

public class Property
{
    public static JSONObject toJSONObject(final Properties properties) throws JSONException {
        final JSONObject jo = new JSONObject();
        if (properties != null && !properties.isEmpty()) {
            final Enumeration<?> enumProperties = properties.propertyNames();
            while (enumProperties.hasMoreElements()) {
                final String name = (String)enumProperties.nextElement();
                jo.put(name, properties.getProperty(name));
            }
        }
        return jo;
    }
    
    public static Properties toProperties(final JSONObject jo) throws JSONException {
        final Properties properties = new Properties();
        if (jo != null) {
            for (final String key : jo.keySet()) {
                final Object value = jo.opt(key);
                if (!JSONObject.NULL.equals(value)) {
                    ((Hashtable<String, String>)properties).put(key, value.toString());
                }
            }
        }
        return properties;
    }
}
