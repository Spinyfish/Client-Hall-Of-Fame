/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  com.google.gson.JsonPrimitive
 *  com.google.gson.JsonSerializationContext
 *  com.google.gson.JsonSerializer
 *  org.apache.commons.lang3.Validate
 */
package net.minecraft.client.resources.data;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.util.ArrayList;
import net.minecraft.client.resources.data.AnimationFrame;
import net.minecraft.client.resources.data.AnimationMetadataSection;
import net.minecraft.client.resources.data.BaseMetadataSectionSerializer;
import net.minecraft.util.JsonUtils;
import org.apache.commons.lang3.Validate;

public class AnimationMetadataSectionSerializer
extends BaseMetadataSectionSerializer<AnimationMetadataSection>
implements JsonSerializer<AnimationMetadataSection> {
    public AnimationMetadataSection deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException {
        ArrayList list = Lists.newArrayList();
        JsonObject jsonobject = JsonUtils.getJsonObject(p_deserialize_1_, "metadata section");
        int i = JsonUtils.getInt(jsonobject, "frametime", 1);
        if (i != 1) {
            Validate.inclusiveBetween((long)1L, (long)Integer.MAX_VALUE, (long)i, (String)"Invalid default frame time");
        }
        if (jsonobject.has("frames")) {
            try {
                JsonArray jsonarray = JsonUtils.getJsonArray(jsonobject, "frames");
                for (int j = 0; j < jsonarray.size(); ++j) {
                    JsonElement jsonelement = jsonarray.get(j);
                    AnimationFrame animationframe = this.parseAnimationFrame(j, jsonelement);
                    if (animationframe == null) continue;
                    list.add(animationframe);
                }
            }
            catch (ClassCastException classcastexception) {
                throw new JsonParseException("Invalid animation->frames: expected array, was " + jsonobject.get("frames"), (Throwable)classcastexception);
            }
        }
        int k = JsonUtils.getInt(jsonobject, "width", -1);
        int l = JsonUtils.getInt(jsonobject, "height", -1);
        if (k != -1) {
            Validate.inclusiveBetween((long)1L, (long)Integer.MAX_VALUE, (long)k, (String)"Invalid width");
        }
        if (l != -1) {
            Validate.inclusiveBetween((long)1L, (long)Integer.MAX_VALUE, (long)l, (String)"Invalid height");
        }
        boolean flag = JsonUtils.getBoolean(jsonobject, "interpolate", false);
        return new AnimationMetadataSection(list, k, l, i, flag);
    }

    private AnimationFrame parseAnimationFrame(int p_110492_1_, JsonElement p_110492_2_) {
        if (p_110492_2_.isJsonPrimitive()) {
            return new AnimationFrame(JsonUtils.getInt(p_110492_2_, "frames[" + p_110492_1_ + "]"));
        }
        if (p_110492_2_.isJsonObject()) {
            JsonObject jsonobject = JsonUtils.getJsonObject(p_110492_2_, "frames[" + p_110492_1_ + "]");
            int i = JsonUtils.getInt(jsonobject, "time", -1);
            if (jsonobject.has("time")) {
                Validate.inclusiveBetween((long)1L, (long)Integer.MAX_VALUE, (long)i, (String)"Invalid frame time");
            }
            int j = JsonUtils.getInt(jsonobject, "index");
            Validate.inclusiveBetween((long)0L, (long)Integer.MAX_VALUE, (long)j, (String)"Invalid frame index");
            return new AnimationFrame(j, i);
        }
        return null;
    }

    public JsonElement serialize(AnimationMetadataSection p_serialize_1_, Type p_serialize_2_, JsonSerializationContext p_serialize_3_) {
        JsonObject jsonobject = new JsonObject();
        jsonobject.addProperty("frametime", (Number)p_serialize_1_.getFrameTime());
        if (p_serialize_1_.getFrameWidth() != -1) {
            jsonobject.addProperty("width", (Number)p_serialize_1_.getFrameWidth());
        }
        if (p_serialize_1_.getFrameHeight() != -1) {
            jsonobject.addProperty("height", (Number)p_serialize_1_.getFrameHeight());
        }
        if (p_serialize_1_.getFrameCount() > 0) {
            JsonArray jsonarray = new JsonArray();
            for (int i = 0; i < p_serialize_1_.getFrameCount(); ++i) {
                if (p_serialize_1_.frameHasTime(i)) {
                    JsonObject jsonobject1 = new JsonObject();
                    jsonobject1.addProperty("index", (Number)p_serialize_1_.getFrameIndex(i));
                    jsonobject1.addProperty("time", (Number)p_serialize_1_.getFrameTimeSingle(i));
                    jsonarray.add((JsonElement)jsonobject1);
                    continue;
                }
                jsonarray.add((JsonElement)new JsonPrimitive((Number)p_serialize_1_.getFrameIndex(i)));
            }
            jsonobject.add("frames", (JsonElement)jsonarray);
        }
        return jsonobject;
    }

    @Override
    public String getSectionName() {
        return "animation";
    }
}

