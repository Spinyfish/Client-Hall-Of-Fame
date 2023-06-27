package rip.athena.client.config;

import org.json.*;

public interface IConfigEntry
{
    Class<?> getType();
    
    void appendToConfig(final String p0, final Object p1, final JSONObject p2);
}
