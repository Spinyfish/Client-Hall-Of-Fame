package net.minecraft.client.resources.data;

import com.google.common.collect.Lists;
import com.google.gson.*;
import net.minecraft.util.JsonUtils;

import java.lang.reflect.Type;
import java.util.List;

public class TextureMetadataSectionSerializer extends BaseMetadataSectionSerializer<TextureMetadataSection> {
    public TextureMetadataSection deserialize(final JsonElement p_deserialize_1_, final Type p_deserialize_2_, final JsonDeserializationContext p_deserialize_3_) throws JsonParseException {
        final JsonObject jsonobject = p_deserialize_1_.getAsJsonObject();
        final boolean flag = JsonUtils.getBoolean(jsonobject, "blur", false);
        final boolean flag1 = JsonUtils.getBoolean(jsonobject, "clamp", false);
        final List<Integer> list = Lists.newArrayList();

        if (jsonobject.has("mipmaps")) {
            try {
                final JsonArray jsonarray = jsonobject.getAsJsonArray("mipmaps");

                for (int i = 0; i < jsonarray.size(); ++i) {
                    final JsonElement jsonelement = jsonarray.get(i);

                    if (jsonelement.isJsonPrimitive()) {
                        try {
                            list.add(Integer.valueOf(jsonelement.getAsInt()));
                        } catch (final NumberFormatException numberformatexception) {
                            throw new JsonParseException("Invalid texture->mipmap->" + i + ": expected number, was " + jsonelement, numberformatexception);
                        }
                    } else if (jsonelement.isJsonObject()) {
                        throw new JsonParseException("Invalid texture->mipmap->" + i + ": expected number, was " + jsonelement);
                    }
                }
            } catch (final ClassCastException classcastexception) {
                throw new JsonParseException("Invalid texture->mipmaps: expected array, was " + jsonobject.get("mipmaps"), classcastexception);
            }
        }

        return new TextureMetadataSection(flag, flag1, list);
    }

    /**
     * The name of this section type as it appears in JSON.
     */
    public String getSectionName() {
        return "texture";
    }
}
