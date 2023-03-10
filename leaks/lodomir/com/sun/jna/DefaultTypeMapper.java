/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jna;

import com.sun.jna.FromNativeConverter;
import com.sun.jna.ToNativeConverter;
import com.sun.jna.TypeConverter;
import com.sun.jna.TypeMapper;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DefaultTypeMapper
implements TypeMapper {
    private List<Entry> toNativeConverters = new ArrayList<Entry>();
    private List<Entry> fromNativeConverters = new ArrayList<Entry>();

    private Class<?> getAltClass(Class<?> cls) {
        if (cls == Boolean.class) {
            return Boolean.TYPE;
        }
        if (cls == Boolean.TYPE) {
            return Boolean.class;
        }
        if (cls == Byte.class) {
            return Byte.TYPE;
        }
        if (cls == Byte.TYPE) {
            return Byte.class;
        }
        if (cls == Character.class) {
            return Character.TYPE;
        }
        if (cls == Character.TYPE) {
            return Character.class;
        }
        if (cls == Short.class) {
            return Short.TYPE;
        }
        if (cls == Short.TYPE) {
            return Short.class;
        }
        if (cls == Integer.class) {
            return Integer.TYPE;
        }
        if (cls == Integer.TYPE) {
            return Integer.class;
        }
        if (cls == Long.class) {
            return Long.TYPE;
        }
        if (cls == Long.TYPE) {
            return Long.class;
        }
        if (cls == Float.class) {
            return Float.TYPE;
        }
        if (cls == Float.TYPE) {
            return Float.class;
        }
        if (cls == Double.class) {
            return Double.TYPE;
        }
        if (cls == Double.TYPE) {
            return Double.class;
        }
        return null;
    }

    public void addToNativeConverter(Class<?> cls, ToNativeConverter converter) {
        this.toNativeConverters.add(new Entry(cls, converter));
        Class<?> alt = this.getAltClass(cls);
        if (alt != null) {
            this.toNativeConverters.add(new Entry(alt, converter));
        }
    }

    public void addFromNativeConverter(Class<?> cls, FromNativeConverter converter) {
        this.fromNativeConverters.add(new Entry(cls, converter));
        Class<?> alt = this.getAltClass(cls);
        if (alt != null) {
            this.fromNativeConverters.add(new Entry(alt, converter));
        }
    }

    public void addTypeConverter(Class<?> cls, TypeConverter converter) {
        this.addFromNativeConverter(cls, converter);
        this.addToNativeConverter(cls, converter);
    }

    private Object lookupConverter(Class<?> javaClass, Collection<? extends Entry> converters) {
        for (Entry entry : converters) {
            if (!entry.type.isAssignableFrom(javaClass)) continue;
            return entry.converter;
        }
        return null;
    }

    @Override
    public FromNativeConverter getFromNativeConverter(Class<?> javaType) {
        return (FromNativeConverter)this.lookupConverter(javaType, this.fromNativeConverters);
    }

    @Override
    public ToNativeConverter getToNativeConverter(Class<?> javaType) {
        return (ToNativeConverter)this.lookupConverter(javaType, this.toNativeConverters);
    }

    private static class Entry {
        public Class<?> type;
        public Object converter;

        public Entry(Class<?> type, Object converter) {
            this.type = type;
            this.converter = converter;
        }
    }
}

