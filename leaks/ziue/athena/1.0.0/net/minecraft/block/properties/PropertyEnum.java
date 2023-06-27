package net.minecraft.block.properties;

import net.minecraft.util.*;
import java.util.*;
import com.google.common.base.*;
import com.google.common.collect.*;

public class PropertyEnum<T extends Enum> extends PropertyHelper<T>
{
    private final ImmutableSet<T> allowedValues;
    private final Map<String, T> nameToValue;
    
    protected PropertyEnum(final String name, final Class<T> valueClass, final Collection<T> allowedValues) {
        super(name, valueClass);
        this.nameToValue = (Map<String, T>)Maps.newHashMap();
        this.allowedValues = (ImmutableSet<T>)ImmutableSet.copyOf((Collection)allowedValues);
        for (final T t : allowedValues) {
            final String s = ((IStringSerializable)t).getName();
            if (this.nameToValue.containsKey(s)) {
                throw new IllegalArgumentException("Multiple values have the same name '" + s + "'");
            }
            this.nameToValue.put(s, t);
        }
    }
    
    @Override
    public Collection<T> getAllowedValues() {
        return (Collection<T>)this.allowedValues;
    }
    
    @Override
    public String getName(final T value) {
        return ((IStringSerializable)value).getName();
    }
    
    public static <T extends java.lang.Enum> PropertyEnum<T> create(final String name, final Class<T> clazz) {
        return create(name, clazz, (com.google.common.base.Predicate<T>)Predicates.alwaysTrue());
    }
    
    public static <T extends java.lang.Enum> PropertyEnum<T> create(final String name, final Class<T> clazz, final Predicate<T> filter) {
        return create(name, clazz, Collections2.filter((Collection)Lists.newArrayList((Object[])clazz.getEnumConstants()), (Predicate)filter));
    }
    
    public static <T extends java.lang.Enum> PropertyEnum<T> create(final String name, final Class<T> clazz, final T... values) {
        return create(name, clazz, Lists.newArrayList((Object[])values));
    }
    
    public static <T extends java.lang.Enum> PropertyEnum<T> create(final String name, final Class<T> clazz, final Collection<T> values) {
        return new PropertyEnum<T>(name, clazz, values);
    }
}
