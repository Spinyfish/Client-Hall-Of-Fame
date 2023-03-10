/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.libs.opennbt.conversion.builtin;

import com.viaversion.viaversion.libs.opennbt.conversion.ConverterRegistry;
import com.viaversion.viaversion.libs.opennbt.conversion.TagConverter;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.ListTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.Tag;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ListTagConverter
implements TagConverter<ListTag, List> {
    @Override
    public List convert(ListTag tag) {
        ArrayList ret = new ArrayList();
        Object tags = tag.getValue();
        Iterator iterator = tags.iterator();
        while (iterator.hasNext()) {
            Tag t = (Tag)iterator.next();
            ret.add(ConverterRegistry.convertToValue(t));
        }
        return ret;
    }

    @Override
    public ListTag convert(List value) {
        ArrayList<Tag> tags = new ArrayList<Tag>();
        for (Object o : value) {
            tags.add((Tag)ConverterRegistry.convertToTag(o));
        }
        return new ListTag(tags);
    }
}

