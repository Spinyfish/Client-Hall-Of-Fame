/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  org.apache.commons.lang3.Validate
 */
package net.minecraft.client.resources.data;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import net.minecraft.client.resources.data.BaseMetadataSectionSerializer;
import net.minecraft.client.resources.data.FontMetadataSection;
import net.minecraft.util.JsonUtils;
import org.apache.commons.lang3.Validate;

public class FontMetadataSectionSerializer
extends BaseMetadataSectionSerializer<FontMetadataSection> {
    public FontMetadataSection deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException {
        JsonObject jsonobject = p_deserialize_1_.getAsJsonObject();
        float[] afloat = new float[256];
        float[] afloat1 = new float[256];
        float[] afloat2 = new float[256];
        float f = 1.0f;
        float f1 = 0.0f;
        float f2 = 0.0f;
        if (jsonobject.has("characters")) {
            if (!jsonobject.get("characters").isJsonObject()) {
                throw new JsonParseException("Invalid font->characters: expected object, was " + jsonobject.get("characters"));
            }
            JsonObject jsonobject1 = jsonobject.getAsJsonObject("characters");
            if (jsonobject1.has("default")) {
                if (!jsonobject1.get("default").isJsonObject()) {
                    throw new JsonParseException("Invalid font->characters->default: expected object, was " + jsonobject1.get("default"));
                }
                JsonObject jsonobject2 = jsonobject1.getAsJsonObject("default");
                f = JsonUtils.getFloat(jsonobject2, "width", f);
                Validate.inclusiveBetween((double)0.0, (double)3.4028234663852886E38, (double)f, (String)"Invalid default width");
                f1 = JsonUtils.getFloat(jsonobject2, "spacing", f1);
                Validate.inclusiveBetween((double)0.0, (double)3.4028234663852886E38, (double)f1, (String)"Invalid default spacing");
                f2 = JsonUtils.getFloat(jsonobject2, "left", f1);
                Validate.inclusiveBetween((double)0.0, (double)3.4028234663852886E38, (double)f2, (String)"Invalid default left");
            }
            for (int i = 0; i < 256; ++i) {
                JsonElement jsonelement = jsonobject1.get(Integer.toString(i));
                float f3 = f;
                float f4 = f1;
                float f5 = f2;
                if (jsonelement != null) {
                    JsonObject jsonobject3 = JsonUtils.getJsonObject(jsonelement, "characters[" + i + "]");
                    f3 = JsonUtils.getFloat(jsonobject3, "width", f);
                    Validate.inclusiveBetween((double)0.0, (double)3.4028234663852886E38, (double)f3, (String)"Invalid width");
                    f4 = JsonUtils.getFloat(jsonobject3, "spacing", f1);
                    Validate.inclusiveBetween((double)0.0, (double)3.4028234663852886E38, (double)f4, (String)"Invalid spacing");
                    f5 = JsonUtils.getFloat(jsonobject3, "left", f2);
                    Validate.inclusiveBetween((double)0.0, (double)3.4028234663852886E38, (double)f5, (String)"Invalid left");
                }
                afloat[i] = f3;
                afloat1[i] = f4;
                afloat2[i] = f5;
            }
        }
        return new FontMetadataSection(afloat, afloat2, afloat1);
    }

    @Override
    public String getSectionName() {
        return "font";
    }
}

