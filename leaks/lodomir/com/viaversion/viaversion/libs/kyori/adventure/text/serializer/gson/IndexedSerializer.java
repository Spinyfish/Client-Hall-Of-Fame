/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.libs.kyori.adventure.text.serializer.gson;

import com.viaversion.viaversion.libs.gson.JsonParseException;
import com.viaversion.viaversion.libs.gson.TypeAdapter;
import com.viaversion.viaversion.libs.gson.stream.JsonReader;
import com.viaversion.viaversion.libs.gson.stream.JsonWriter;
import com.viaversion.viaversion.libs.kyori.adventure.util.Index;
import java.io.IOException;

final class IndexedSerializer<E>
extends TypeAdapter<E> {
    private final String name;
    private final Index<String, E> map;

    public static <E> TypeAdapter<E> of(String name, Index<String, E> map) {
        return new IndexedSerializer<E>(name, map).nullSafe();
    }

    private IndexedSerializer(String name, Index<String, E> map) {
        this.name = name;
        this.map = map;
    }

    @Override
    public void write(JsonWriter out, E value) throws IOException {
        out.value(this.map.key(value));
    }

    @Override
    public E read(JsonReader in) throws IOException {
        String string = in.nextString();
        E value = this.map.value(string);
        if (value != null) {
            return value;
        }
        throw new JsonParseException("invalid " + this.name + ":  " + string);
    }
}

