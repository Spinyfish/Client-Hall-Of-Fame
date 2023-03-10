/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.libs.kyori.adventure.text.serializer.gson;

import com.viaversion.viaversion.libs.gson.Gson;
import com.viaversion.viaversion.libs.gson.JsonParseException;
import com.viaversion.viaversion.libs.gson.TypeAdapter;
import com.viaversion.viaversion.libs.gson.stream.JsonReader;
import com.viaversion.viaversion.libs.gson.stream.JsonToken;
import com.viaversion.viaversion.libs.gson.stream.JsonWriter;
import com.viaversion.viaversion.libs.kyori.adventure.key.Key;
import com.viaversion.viaversion.libs.kyori.adventure.nbt.api.BinaryTagHolder;
import com.viaversion.viaversion.libs.kyori.adventure.text.event.HoverEvent;
import com.viaversion.viaversion.libs.kyori.adventure.text.serializer.gson.SerializerFactory;
import java.io.IOException;

final class ShowItemSerializer
extends TypeAdapter<HoverEvent.ShowItem> {
    static final String ID = "id";
    static final String COUNT = "count";
    static final String TAG = "tag";
    private final Gson gson;

    static TypeAdapter<HoverEvent.ShowItem> create(Gson gson) {
        return new ShowItemSerializer(gson).nullSafe();
    }

    private ShowItemSerializer(Gson gson) {
        this.gson = gson;
    }

    @Override
    public HoverEvent.ShowItem read(JsonReader in) throws IOException {
        in.beginObject();
        Key key = null;
        int count = 1;
        BinaryTagHolder nbt = null;
        while (in.hasNext()) {
            String fieldName = in.nextName();
            if (fieldName.equals(ID)) {
                key = (Key)this.gson.fromJson(in, SerializerFactory.KEY_TYPE);
                continue;
            }
            if (fieldName.equals(COUNT)) {
                count = in.nextInt();
                continue;
            }
            if (fieldName.equals(TAG)) {
                JsonToken token = in.peek();
                if (token == JsonToken.STRING || token == JsonToken.NUMBER) {
                    nbt = BinaryTagHolder.of(in.nextString());
                    continue;
                }
                if (token == JsonToken.BOOLEAN) {
                    nbt = BinaryTagHolder.of(String.valueOf(in.nextBoolean()));
                    continue;
                }
                if (token == JsonToken.NULL) {
                    in.nextNull();
                    continue;
                }
                throw new JsonParseException("Expected tag to be a string");
            }
            in.skipValue();
        }
        if (key == null) {
            throw new JsonParseException("Not sure how to deserialize show_item hover event");
        }
        in.endObject();
        return HoverEvent.ShowItem.of(key, count, nbt);
    }

    @Override
    public void write(JsonWriter out, HoverEvent.ShowItem value) throws IOException {
        BinaryTagHolder nbt;
        out.beginObject();
        out.name(ID);
        this.gson.toJson((Object)value.item(), SerializerFactory.KEY_TYPE, out);
        int count = value.count();
        if (count != 1) {
            out.name(COUNT);
            out.value(count);
        }
        if ((nbt = value.nbt()) != null) {
            out.name(TAG);
            out.value(nbt.string());
        }
        out.endObject();
    }
}

