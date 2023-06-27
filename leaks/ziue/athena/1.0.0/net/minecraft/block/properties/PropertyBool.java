package net.minecraft.block.properties;

import com.google.common.collect.*;
import java.util.*;

public class PropertyBool extends PropertyHelper<Boolean>
{
    private final ImmutableSet<Boolean> allowedValues;
    
    protected PropertyBool(final String name) {
        super(name, Boolean.class);
        this.allowedValues = (ImmutableSet<Boolean>)ImmutableSet.of((Object)true, (Object)false);
    }
    
    @Override
    public Collection<Boolean> getAllowedValues() {
        return (Collection<Boolean>)this.allowedValues;
    }
    
    public static PropertyBool create(final String name) {
        return new PropertyBool(name);
    }
    
    @Override
    public String getName(final Boolean value) {
        return value.toString();
    }
}
