// 
// Decompiled by Procyon v0.5.36
// 

package org.slf4j.helpers;

import java.util.Map;
import org.slf4j.spi.MDCAdapter;

public class NOPMakerAdapter implements MDCAdapter
{
    public void clear() {
    }
    
    public String get(final String key) {
        return null;
    }
    
    public void put(final String key, final String val) {
    }
    
    public void remove(final String key) {
    }
    
    public Map getCopyOfContextMap() {
        return null;
    }
    
    public void setContextMap(final Map contextMap) {
    }
}
