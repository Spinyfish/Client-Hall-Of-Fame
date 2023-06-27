package net.minecraft.client.resources.data;

import java.lang.reflect.*;
import com.google.common.collect.*;
import net.minecraft.util.*;
import org.apache.commons.lang3.*;
import java.util.*;
import com.google.gson.*;

public class AnimationMetadataSectionSerializer extends BaseMetadataSectionSerializer<AnimationMetadataSection> implements JsonSerializer<AnimationMetadataSection>
{
    public AnimationMetadataSection deserialize(final JsonElement p_deserialize_1_, final Type p_deserialize_2_, final JsonDeserializationContext p_deserialize_3_) throws JsonParseException {
        final List<AnimationFrame> list = (List<AnimationFrame>)Lists.newArrayList();
        final JsonObject jsonobject = JsonUtils.getJsonObject(p_deserialize_1_, "metadata section");
        final int i = JsonUtils.getInt(jsonobject, "frametime", 1);
        if (i != 1) {
            Validate.inclusiveBetween(1L, 2147483647L, (long)i, "Invalid default frame time");
        }
        if (jsonobject.has("frames")) {
            try {
                final JsonArray jsonarray = JsonUtils.getJsonArray(jsonobject, "frames");
                for (int j = 0; j < jsonarray.size(); ++j) {
                    final JsonElement jsonelement = jsonarray.get(j);
                    final AnimationFrame animationframe = this.parseAnimationFrame(j, jsonelement);
                    if (animationframe != null) {
                        list.add(animationframe);
                    }
                }
            }
            catch (ClassCastException classcastexception) {
                throw new JsonParseException("Invalid animation->frames: expected array, was " + jsonobject.get("frames"), (Throwable)classcastexception);
            }
        }
        final int k = JsonUtils.getInt(jsonobject, "width", -1);
        final int l = JsonUtils.getInt(jsonobject, "height", -1);
        if (k != -1) {
            Validate.inclusiveBetween(1L, 2147483647L, (long)k, "Invalid width");
        }
        if (l != -1) {
            Validate.inclusiveBetween(1L, 2147483647L, (long)l, "Invalid height");
        }
        final boolean flag = JsonUtils.getBoolean(jsonobject, "interpolate", false);
        return new AnimationMetadataSection(list, k, l, i, flag);
    }
    
    private AnimationFrame parseAnimationFrame(final int p_110492_1_, final JsonElement p_110492_2_) {
        if (p_110492_2_.isJsonPrimitive()) {
            return new AnimationFrame(JsonUtils.getInt(p_110492_2_, "frames[" + p_110492_1_ + "]"));
        }
        if (p_110492_2_.isJsonObject()) {
            final JsonObject jsonobject = JsonUtils.getJsonObject(p_110492_2_, "frames[" + p_110492_1_ + "]");
            final int i = JsonUtils.getInt(jsonobject, "time", -1);
            if (jsonobject.has("time")) {
                Validate.inclusiveBetween(1L, 2147483647L, (long)i, "Invalid frame time");
            }
            final int j = JsonUtils.getInt(jsonobject, "index");
            Validate.inclusiveBetween(0L, 2147483647L, (long)j, "Invalid frame index");
            return new AnimationFrame(j, i);
        }
        return null;
    }
    
    public JsonElement serialize(final AnimationMetadataSection p_serialize_1_, final Type p_serialize_2_, final JsonSerializationContext p_serialize_3_) {
        final JsonObject jsonobject = new JsonObject();
        jsonobject.addProperty("frametime", (Number)p_serialize_1_.getFrameTime());
        if (p_serialize_1_.getFrameWidth() != -1) {
            jsonobject.addProperty("width", (Number)p_serialize_1_.getFrameWidth());
        }
        if (p_serialize_1_.getFrameHeight() != -1) {
            jsonobject.addProperty("height", (Number)p_serialize_1_.getFrameHeight());
        }
        if (p_serialize_1_.getFrameCount() > 0) {
            final JsonArray jsonarray = new JsonArray();
            for (int i = 0; i < p_serialize_1_.getFrameCount(); ++i) {
                if (p_serialize_1_.frameHasTime(i)) {
                    final JsonObject jsonobject2 = new JsonObject();
                    jsonobject2.addProperty("index", (Number)p_serialize_1_.getFrameIndex(i));
                    jsonobject2.addProperty("time", (Number)p_serialize_1_.getFrameTimeSingle(i));
                    jsonarray.add((JsonElement)jsonobject2);
                }
                else {
                    jsonarray.add((JsonElement)new JsonPrimitive((Number)p_serialize_1_.getFrameIndex(i)));
                }
            }
            jsonobject.add("frames", (JsonElement)jsonarray);
        }
        return (JsonElement)jsonobject;
    }
    
    public String getSectionName() {
        return "animation";
    }
}
