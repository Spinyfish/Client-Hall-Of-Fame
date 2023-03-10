/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.libs.opennbt.conversion.builtin;

import com.viaversion.viaversion.libs.opennbt.conversion.ConverterRegistry;
import com.viaversion.viaversion.libs.opennbt.conversion.TagConverter;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.CompoundTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.Tag;
import java.util.HashMap;
import java.util.Map;

public class CompoundTagConverter
implements TagConverter<CompoundTag, Map> {
    @Override
    public Map convert(CompoundTag tag) {
        HashMap ret = new HashMap();
        Object tags = tag.getValue();
        for (Map.Entry entry : tags.entrySet()) {
            ret.put(entry.getKey(), ConverterRegistry.convertToValue((Tag)entry.getValue()));
        }
        return ret;
    }

    @Override
    public CompoundTag convert(Map value) {
        HashMap<String, Tag> tags = new HashMap<String, Tag>();
        for (Object na : value.keySet()) {
            String n = (String)na;
            tags.put(n, (Tag)ConverterRegistry.convertToTag(value.get(n)));
        }
        return new CompoundTag(tags);
    }
}

