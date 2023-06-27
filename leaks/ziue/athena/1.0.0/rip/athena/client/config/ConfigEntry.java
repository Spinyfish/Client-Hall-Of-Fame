package rip.athena.client.config;

import java.lang.reflect.*;
import rip.athena.client.*;

public abstract class ConfigEntry implements IConfigEntry
{
    private Field field;
    private String key;
    private String description;
    private boolean visible;
    
    public ConfigEntry(final Field field, final String key, final String description, final boolean visible) {
        this.field = field;
        this.key = key;
        this.description = description;
        this.visible = visible;
    }
    
    public Field getField() {
        return this.field;
    }
    
    public String getKey() {
        return this.key;
    }
    
    public String getDescription() {
        return this.description;
    }
    
    public Object getValue(final Object obj) {
        try {
            return this.getField().get(obj);
        }
        catch (IllegalArgumentException | IllegalAccessException ex2) {
            final Exception ex;
            final Exception e = ex;
            Athena.INSTANCE.getLog().error("Failed to returning value for module " + obj + "," + this.key + "." + e);
            return null;
        }
    }
    
    public boolean isVisible() {
        return this.visible;
    }
    
    public boolean hasDescription() {
        return this.description != null && this.description.length() > 0;
    }
    
    public void setValue(final Object obj, final Object value) {
        try {
            this.field.set(obj, value);
        }
        catch (IllegalArgumentException | IllegalAccessException ex2) {
            final Exception ex;
            final Exception e = ex;
            Athena.INSTANCE.getLog().error("Failed to set config value for module " + obj + "." + e);
        }
    }
    
    public void setVisible(final boolean visible) {
        this.visible = visible;
    }
    
    public boolean isInstance(final Object object) {
        return object.getClass() == this.getType();
    }
}
