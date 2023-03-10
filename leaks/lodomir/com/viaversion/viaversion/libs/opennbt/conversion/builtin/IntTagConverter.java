/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.libs.opennbt.conversion.builtin;

import com.viaversion.viaversion.libs.opennbt.conversion.TagConverter;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.IntTag;

public class IntTagConverter
implements TagConverter<IntTag, Integer> {
    @Override
    public Integer convert(IntTag tag) {
        return tag.getValue();
    }

    @Override
    public IntTag convert(Integer value) {
        return new IntTag(value);
    }
}

