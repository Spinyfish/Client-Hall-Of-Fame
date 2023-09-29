package net.minecraft.client.resources.data;

import com.google.gson.*;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.JsonUtils;

import java.lang.reflect.Type;

public class PackMetadataSectionSerializer extends BaseMetadataSectionSerializer<PackMetadataSection> implements JsonSerializer<PackMetadataSection> {
    public PackMetadataSection deserialize(final JsonElement p_deserialize_1_, final Type p_deserialize_2_, final JsonDeserializationContext p_deserialize_3_) throws JsonParseException {
        final JsonObject jsonobject = p_deserialize_1_.getAsJsonObject();
        final IChatComponent ichatcomponent = p_deserialize_3_.deserialize(jsonobject.get("description"), IChatComponent.class);

        if (ichatcomponent == null) {
            throw new JsonParseException("Invalid/missing description!");
        } else {
            final int i = JsonUtils.getInt(jsonobject, "pack_format");
            return new PackMetadataSection(ichatcomponent, i);
        }
    }

    public JsonElement serialize(final PackMetadataSection p_serialize_1_, final Type p_serialize_2_, final JsonSerializationContext p_serialize_3_) {
        final JsonObject jsonobject = new JsonObject();
        jsonobject.addProperty("pack_format", Integer.valueOf(p_serialize_1_.getPackFormat()));
        jsonobject.add("description", p_serialize_3_.serialize(p_serialize_1_.getPackDescription()));
        return jsonobject;
    }

    /**
     * The name of this section type as it appears in JSON.
     */
    public String getSectionName() {
        return "pack";
    }
}
