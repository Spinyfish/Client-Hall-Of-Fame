package net.optifine.util;

import java.util.*;

public class PropertiesOrdered extends Properties
{
    private Set<Object> keysOrdered = new LinkedHashSet();

    public synchronized Object put(Object key, Object value)
    {
        this.keysOrdered.add(key);
        return super.put(key, value);
    }

    public Set<Object> keySet()
    {
        Set<Object> set = super.keySet();
        this.keysOrdered.retainAll(set);
        return Collections.<Object>unmodifiableSet(this.keysOrdered);
    }

    public synchronized Enumeration<Object> keys()
    {
        return Collections.<Object>enumeration(this.keySet());
    }
}
