// 
// Decompiled by Procyon v0.5.36
// 

package org.slf4j.spi;

import java.util.Deque;
import java.util.Map;

public interface MDCAdapter
{
    void put(final String p0, final String p1);
    
    String get(final String p0);
    
    void remove(final String p0);
    
    void clear();
    
    Map<String, String> getCopyOfContextMap();
    
    void setContextMap(final Map<String, String> p0);
    
    void pushByKey(final String p0, final String p1);
    
    String popByKey(final String p0);
    
    Deque<String> getCopyOfDequeByKey(final String p0);
    
    void clearDequeByKey(final String p0);
}
