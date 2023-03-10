/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  org.jetbrains.annotations.Nullable
 */
package com.viaversion.viaversion.libs.opennbt.tag.builtin;

import com.google.common.base.Preconditions;
import com.viaversion.viaversion.libs.opennbt.tag.TagCreateException;
import com.viaversion.viaversion.libs.opennbt.tag.TagRegistry;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.Tag;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.EOFException;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import org.jetbrains.annotations.Nullable;

public class CompoundTag
extends Tag
implements Iterable<Map.Entry<String, Tag>> {
    public static final int ID = 10;
    private Map<String, Tag> value;

    public CompoundTag() {
        this(new LinkedHashMap<String, Tag>());
    }

    public CompoundTag(Map<String, Tag> value) {
        this.value = new LinkedHashMap<String, Tag>(value);
    }

    public CompoundTag(LinkedHashMap<String, Tag> value) {
        Preconditions.checkNotNull(value);
        this.value = value;
    }

    @Override
    public Map<String, Tag> getValue() {
        return this.value;
    }

    public void setValue(Map<String, Tag> value) {
        Preconditions.checkNotNull(value);
        this.value = new LinkedHashMap<String, Tag>(value);
    }

    public void setValue(LinkedHashMap<String, Tag> value) {
        Preconditions.checkNotNull(value);
        this.value = value;
    }

    public boolean isEmpty() {
        return this.value.isEmpty();
    }

    public boolean contains(String tagName) {
        return this.value.containsKey(tagName);
    }

    @Nullable
    public <T extends Tag> T get(String tagName) {
        return (T)this.value.get(tagName);
    }

    @Nullable
    public <T extends Tag> T put(String tagName, T tag) {
        return (T)this.value.put(tagName, tag);
    }

    @Nullable
    public <T extends Tag> T remove(String tagName) {
        return (T)this.value.remove(tagName);
    }

    public Set<String> keySet() {
        return this.value.keySet();
    }

    public Collection<Tag> values() {
        return this.value.values();
    }

    public Set<Map.Entry<String, Tag>> entrySet() {
        return this.value.entrySet();
    }

    public int size() {
        return this.value.size();
    }

    public void clear() {
        this.value.clear();
    }

    @Override
    public Iterator<Map.Entry<String, Tag>> iterator() {
        return this.value.entrySet().iterator();
    }

    @Override
    public void read(DataInput in) throws IOException {
        try {
            byte id;
            while ((id = in.readByte()) != 0) {
                String name = in.readUTF();
                Tag tag = TagRegistry.createInstance(id);
                tag.read(in);
                this.value.put(name, tag);
            }
        }
        catch (TagCreateException e) {
            throw new IOException("Failed to create tag.", e);
        }
        catch (EOFException e) {
            throw new IOException("Closing tag was not found!");
        }
    }

    @Override
    public void write(DataOutput out) throws IOException {
        for (Map.Entry<String, Tag> entry : this.value.entrySet()) {
            Tag tag = entry.getValue();
            out.writeByte(tag.getTagId());
            out.writeUTF(entry.getKey());
            tag.write(out);
        }
        out.writeByte(0);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        CompoundTag tags = (CompoundTag)o;
        return this.value.equals(tags.value);
    }

    public int hashCode() {
        return this.value.hashCode();
    }

    @Override
    public final CompoundTag clone() {
        LinkedHashMap<String, Tag> newMap = new LinkedHashMap<String, Tag>();
        for (Map.Entry<String, Tag> entry : this.value.entrySet()) {
            newMap.put(entry.getKey(), entry.getValue().clone());
        }
        return new CompoundTag(newMap);
    }

    @Override
    public int getTagId() {
        return 10;
    }
}

