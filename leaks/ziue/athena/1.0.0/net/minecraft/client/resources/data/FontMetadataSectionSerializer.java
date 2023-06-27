package net.minecraft.client.resources.data;

import java.lang.reflect.*;
import net.minecraft.util.*;
import org.apache.commons.lang3.*;
import com.google.gson.*;

public class FontMetadataSectionSerializer extends BaseMetadataSectionSerializer<FontMetadataSection>
{
    public FontMetadataSection deserialize(final JsonElement p_deserialize_1_, final Type p_deserialize_2_, final JsonDeserializationContext p_deserialize_3_) throws JsonParseException {
        final JsonObject jsonobject = p_deserialize_1_.getAsJsonObject();
        final float[] afloat = new float[256];
        final float[] afloat2 = new float[256];
        final float[] afloat3 = new float[256];
        float f = 1.0f;
        float f2 = 0.0f;
        float f3 = 0.0f;
        if (jsonobject.has("characters")) {
            if (!jsonobject.get("characters").isJsonObject()) {
                throw new JsonParseException("Invalid font->characters: expected object, was " + jsonobject.get("characters"));
            }
            final JsonObject jsonobject2 = jsonobject.getAsJsonObject("characters");
            if (jsonobject2.has("default")) {
                if (!jsonobject2.get("default").isJsonObject()) {
                    throw new JsonParseException("Invalid font->characters->default: expected object, was " + jsonobject2.get("default"));
                }
                final JsonObject jsonobject3 = jsonobject2.getAsJsonObject("default");
                f = JsonUtils.getFloat(jsonobject3, "width", f);
                Validate.inclusiveBetween(0.0, 3.4028234663852886E38, (double)f, "Invalid default width");
                f2 = JsonUtils.getFloat(jsonobject3, "spacing", f2);
                Validate.inclusiveBetween(0.0, 3.4028234663852886E38, (double)f2, "Invalid default spacing");
                f3 = JsonUtils.getFloat(jsonobject3, "left", f2);
                Validate.inclusiveBetween(0.0, 3.4028234663852886E38, (double)f3, "Invalid default left");
            }
            for (int i = 0; i < 256; ++i) {
                final JsonElement jsonelement = jsonobject2.get(Integer.toString(i));
                float f4 = f;
                float f5 = f2;
                float f6 = f3;
                if (jsonelement != null) {
                    final JsonObject jsonobject4 = JsonUtils.getJsonObject(jsonelement, "characters[" + i + "]");
                    f4 = JsonUtils.getFloat(jsonobject4, "width", f);
                    Validate.inclusiveBetween(0.0, 3.4028234663852886E38, (double)f4, "Invalid width");
                    f5 = JsonUtils.getFloat(jsonobject4, "spacing", f2);
                    Validate.inclusiveBetween(0.0, 3.4028234663852886E38, (double)f5, "Invalid spacing");
                    f6 = JsonUtils.getFloat(jsonobject4, "left", f3);
                    Validate.inclusiveBetween(0.0, 3.4028234663852886E38, (double)f6, "Invalid left");
                }
                afloat[i] = f4;
                afloat2[i] = f5;
                afloat3[i] = f6;
            }
        }
        return new FontMetadataSection(afloat, afloat3, afloat2);
    }
    
    @Override
    public String getSectionName() {
        return "font";
    }
}
