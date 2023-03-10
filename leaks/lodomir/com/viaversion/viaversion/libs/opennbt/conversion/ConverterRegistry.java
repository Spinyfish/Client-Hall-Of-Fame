/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.libs.opennbt.conversion;

import com.viaversion.viaversion.libs.opennbt.conversion.ConversionException;
import com.viaversion.viaversion.libs.opennbt.conversion.ConverterRegisterException;
import com.viaversion.viaversion.libs.opennbt.conversion.TagConverter;
import com.viaversion.viaversion.libs.opennbt.conversion.builtin.ByteArrayTagConverter;
import com.viaversion.viaversion.libs.opennbt.conversion.builtin.ByteTagConverter;
import com.viaversion.viaversion.libs.opennbt.conversion.builtin.CompoundTagConverter;
import com.viaversion.viaversion.libs.opennbt.conversion.builtin.DoubleTagConverter;
import com.viaversion.viaversion.libs.opennbt.conversion.builtin.FloatTagConverter;
import com.viaversion.viaversion.libs.opennbt.conversion.builtin.IntArrayTagConverter;
import com.viaversion.viaversion.libs.opennbt.conversion.builtin.IntTagConverter;
import com.viaversion.viaversion.libs.opennbt.conversion.builtin.ListTagConverter;
import com.viaversion.viaversion.libs.opennbt.conversion.builtin.LongArrayTagConverter;
import com.viaversion.viaversion.libs.opennbt.conversion.builtin.LongTagConverter;
import com.viaversion.viaversion.libs.opennbt.conversion.builtin.ShortTagConverter;
import com.viaversion.viaversion.libs.opennbt.conversion.builtin.StringTagConverter;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.ByteArrayTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.ByteTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.CompoundTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.DoubleTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.FloatTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.IntArrayTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.IntTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.ListTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.LongArrayTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.LongTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.ShortTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.StringTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.Tag;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ConverterRegistry {
    private static final Map<Class<? extends Tag>, TagConverter<? extends Tag, ?>> tagToConverter = new HashMap();
    private static final Map<Class<?>, TagConverter<? extends Tag, ?>> typeToConverter = new HashMap();

    public static <T extends Tag, V> void register(Class<T> tag, Class<V> type, TagConverter<T, V> converter) throws ConverterRegisterException {
        if (tagToConverter.containsKey(tag)) {
            throw new ConverterRegisterException("Type conversion to tag " + tag.getName() + " is already registered.");
        }
        if (typeToConverter.containsKey(type)) {
            throw new ConverterRegisterException("Tag conversion to type " + type.getName() + " is already registered.");
        }
        tagToConverter.put(tag, converter);
        typeToConverter.put(type, converter);
    }

    public static <T extends Tag, V> void unregister(Class<T> tag, Class<V> type) {
        tagToConverter.remove(tag);
        typeToConverter.remove(type);
    }

    public static <T extends Tag, V> V convertToValue(T tag) throws ConversionException {
        if (tag == null || tag.getValue() == null) {
            return null;
        }
        if (!tagToConverter.containsKey(tag.getClass())) {
            throw new ConversionException("Tag type " + tag.getClass().getName() + " has no converter.");
        }
        TagConverter<Tag, ?> converter = tagToConverter.get(tag.getClass());
        return (V)converter.convert(tag);
    }

    public static <V, T extends Tag> T convertToTag(V value) throws ConversionException {
        if (value == null) {
            return null;
        }
        TagConverter<Tag, ?> converter = typeToConverter.get(value.getClass());
        if (converter == null) {
            for (Class<?> clazz : ConverterRegistry.getAllClasses(value.getClass())) {
                if (!typeToConverter.containsKey(clazz)) continue;
                try {
                    converter = typeToConverter.get(clazz);
                    break;
                }
                catch (ClassCastException classCastException) {
                }
            }
        }
        if (converter == null) {
            throw new ConversionException("Value type " + value.getClass().getName() + " has no converter.");
        }
        return (T)converter.convert(value);
    }

    private static Set<Class<?>> getAllClasses(Class<?> clazz) {
        LinkedHashSet ret = new LinkedHashSet();
        for (Class<?> c = clazz; c != null; c = c.getSuperclass()) {
            ret.add(c);
            ret.addAll(ConverterRegistry.getAllSuperInterfaces(c));
        }
        if (ret.contains(Serializable.class)) {
            ret.remove(Serializable.class);
            ret.add(Serializable.class);
        }
        return ret;
    }

    private static Set<Class<?>> getAllSuperInterfaces(Class<?> clazz) {
        HashSet ret = new HashSet();
        for (Class<?> c : clazz.getInterfaces()) {
            ret.add(c);
            ret.addAll(ConverterRegistry.getAllSuperInterfaces(c));
        }
        return ret;
    }

    static {
        ConverterRegistry.register(ByteTag.class, Byte.class, new ByteTagConverter());
        ConverterRegistry.register(ShortTag.class, Short.class, new ShortTagConverter());
        ConverterRegistry.register(IntTag.class, Integer.class, new IntTagConverter());
        ConverterRegistry.register(LongTag.class, Long.class, new LongTagConverter());
        ConverterRegistry.register(FloatTag.class, Float.class, new FloatTagConverter());
        ConverterRegistry.register(DoubleTag.class, Double.class, new DoubleTagConverter());
        ConverterRegistry.register(ByteArrayTag.class, byte[].class, new ByteArrayTagConverter());
        ConverterRegistry.register(StringTag.class, String.class, new StringTagConverter());
        ConverterRegistry.register(ListTag.class, List.class, new ListTagConverter());
        ConverterRegistry.register(CompoundTag.class, Map.class, new CompoundTagConverter());
        ConverterRegistry.register(IntArrayTag.class, int[].class, new IntArrayTagConverter());
        ConverterRegistry.register(LongArrayTag.class, long[].class, new LongArrayTagConverter());
    }
}

