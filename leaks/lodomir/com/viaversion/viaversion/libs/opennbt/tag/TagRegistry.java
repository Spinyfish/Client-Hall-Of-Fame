/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package com.viaversion.viaversion.libs.opennbt.tag;

import com.viaversion.viaversion.libs.fastutil.ints.Int2ObjectMap;
import com.viaversion.viaversion.libs.fastutil.ints.Int2ObjectOpenHashMap;
import com.viaversion.viaversion.libs.fastutil.objects.Object2IntMap;
import com.viaversion.viaversion.libs.fastutil.objects.Object2IntOpenHashMap;
import com.viaversion.viaversion.libs.opennbt.tag.TagCreateException;
import com.viaversion.viaversion.libs.opennbt.tag.TagRegisterException;
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
import java.util.function.Supplier;
import org.jetbrains.annotations.Nullable;

public class TagRegistry {
    private static final Int2ObjectMap<Class<? extends Tag>> idToTag = new Int2ObjectOpenHashMap<Class<? extends Tag>>();
    private static final Object2IntMap<Class<? extends Tag>> tagToId = new Object2IntOpenHashMap<Class<? extends Tag>>();
    private static final Int2ObjectMap<Supplier<? extends Tag>> instanceSuppliers = new Int2ObjectOpenHashMap<Supplier<? extends Tag>>();

    public static void register(int id, Class<? extends Tag> tag, Supplier<? extends Tag> supplier) throws TagRegisterException {
        if (idToTag.containsKey(id)) {
            throw new TagRegisterException("Tag ID \"" + id + "\" is already in use.");
        }
        if (tagToId.containsKey(tag)) {
            throw new TagRegisterException("Tag \"" + tag.getSimpleName() + "\" is already registered.");
        }
        instanceSuppliers.put(id, supplier);
        idToTag.put(id, tag);
        tagToId.put(tag, id);
    }

    public static void unregister(int id) {
        tagToId.removeInt(TagRegistry.getClassFor(id));
        idToTag.remove(id);
    }

    @Nullable
    public static Class<? extends Tag> getClassFor(int id) {
        return (Class)idToTag.get(id);
    }

    public static int getIdFor(Class<? extends Tag> clazz) {
        return tagToId.getInt(clazz);
    }

    public static Tag createInstance(int id) throws TagCreateException {
        Supplier supplier = (Supplier)instanceSuppliers.get(id);
        if (supplier == null) {
            throw new TagCreateException("Could not find tag with ID \"" + id + "\".");
        }
        return (Tag)supplier.get();
    }

    static {
        tagToId.defaultReturnValue(-1);
        TagRegistry.register(1, ByteTag.class, ByteTag::new);
        TagRegistry.register(2, ShortTag.class, ShortTag::new);
        TagRegistry.register(3, IntTag.class, IntTag::new);
        TagRegistry.register(4, LongTag.class, LongTag::new);
        TagRegistry.register(5, FloatTag.class, FloatTag::new);
        TagRegistry.register(6, DoubleTag.class, DoubleTag::new);
        TagRegistry.register(7, ByteArrayTag.class, ByteArrayTag::new);
        TagRegistry.register(8, StringTag.class, StringTag::new);
        TagRegistry.register(9, ListTag.class, ListTag::new);
        TagRegistry.register(10, CompoundTag.class, CompoundTag::new);
        TagRegistry.register(11, IntArrayTag.class, IntArrayTag::new);
        TagRegistry.register(12, LongArrayTag.class, LongArrayTag::new);
    }
}

