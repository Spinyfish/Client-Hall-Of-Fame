package net.minecraft.util;

import java.lang.reflect.*;
import java.util.*;
import com.google.gson.*;

public interface IChatComponent extends Iterable<IChatComponent>
{
    IChatComponent setChatStyle(final ChatStyle p0);
    
    ChatStyle getChatStyle();
    
    IChatComponent appendText(final String p0);
    
    IChatComponent appendSibling(final IChatComponent p0);
    
    String getUnformattedTextForChat();
    
    String getUnformattedText();
    
    String getFormattedText();
    
    List<IChatComponent> getSiblings();
    
    IChatComponent createCopy();
    
    public static class Serializer implements JsonDeserializer<IChatComponent>, JsonSerializer<IChatComponent>
    {
        private static final Gson GSON;
        
        public IChatComponent deserialize(final JsonElement p_deserialize_1_, final Type p_deserialize_2_, final JsonDeserializationContext p_deserialize_3_) throws JsonParseException {
            if (p_deserialize_1_.isJsonPrimitive()) {
                return new ChatComponentText(p_deserialize_1_.getAsString());
            }
            if (p_deserialize_1_.isJsonObject()) {
                final JsonObject jsonobject = p_deserialize_1_.getAsJsonObject();
                IChatComponent ichatcomponent;
                if (jsonobject.has("text")) {
                    ichatcomponent = new ChatComponentText(jsonobject.get("text").getAsString());
                }
                else if (jsonobject.has("translate")) {
                    final String s = jsonobject.get("translate").getAsString();
                    if (jsonobject.has("with")) {
                        final JsonArray jsonarray = jsonobject.getAsJsonArray("with");
                        final Object[] aobject = new Object[jsonarray.size()];
                        for (int i = 0; i < aobject.length; ++i) {
                            aobject[i] = this.deserialize(jsonarray.get(i), p_deserialize_2_, p_deserialize_3_);
                            if (aobject[i] instanceof ChatComponentText) {
                                final ChatComponentText chatcomponenttext = (ChatComponentText)aobject[i];
                                if (chatcomponenttext.getChatStyle().isEmpty() && chatcomponenttext.getSiblings().isEmpty()) {
                                    aobject[i] = chatcomponenttext.getChatComponentText_TextValue();
                                }
                            }
                        }
                        ichatcomponent = new ChatComponentTranslation(s, aobject);
                    }
                    else {
                        ichatcomponent = new ChatComponentTranslation(s, new Object[0]);
                    }
                }
                else if (jsonobject.has("score")) {
                    final JsonObject jsonobject2 = jsonobject.getAsJsonObject("score");
                    if (!jsonobject2.has("name") || !jsonobject2.has("objective")) {
                        throw new JsonParseException("A score component needs a least a name and an objective");
                    }
                    ichatcomponent = new ChatComponentScore(JsonUtils.getString(jsonobject2, "name"), JsonUtils.getString(jsonobject2, "objective"));
                    if (jsonobject2.has("value")) {
                        ((ChatComponentScore)ichatcomponent).setValue(JsonUtils.getString(jsonobject2, "value"));
                    }
                }
                else {
                    if (!jsonobject.has("selector")) {
                        throw new JsonParseException("Don't know how to turn " + p_deserialize_1_.toString() + " into a Component");
                    }
                    ichatcomponent = new ChatComponentSelector(JsonUtils.getString(jsonobject, "selector"));
                }
                if (jsonobject.has("extra")) {
                    final JsonArray jsonarray2 = jsonobject.getAsJsonArray("extra");
                    if (jsonarray2.size() <= 0) {
                        throw new JsonParseException("Unexpected empty array of components");
                    }
                    for (int j = 0; j < jsonarray2.size(); ++j) {
                        ichatcomponent.appendSibling(this.deserialize(jsonarray2.get(j), p_deserialize_2_, p_deserialize_3_));
                    }
                }
                ichatcomponent.setChatStyle((ChatStyle)p_deserialize_3_.deserialize(p_deserialize_1_, (Type)ChatStyle.class));
                return ichatcomponent;
            }
            if (p_deserialize_1_.isJsonArray()) {
                final JsonArray jsonarray3 = p_deserialize_1_.getAsJsonArray();
                IChatComponent ichatcomponent2 = null;
                for (final JsonElement jsonelement : jsonarray3) {
                    final IChatComponent ichatcomponent3 = this.deserialize(jsonelement, jsonelement.getClass(), p_deserialize_3_);
                    if (ichatcomponent2 == null) {
                        ichatcomponent2 = ichatcomponent3;
                    }
                    else {
                        ichatcomponent2.appendSibling(ichatcomponent3);
                    }
                }
                return ichatcomponent2;
            }
            throw new JsonParseException("Don't know how to turn " + p_deserialize_1_.toString() + " into a Component");
        }
        
        private void serializeChatStyle(final ChatStyle style, final JsonObject object, final JsonSerializationContext ctx) {
            final JsonElement jsonelement = ctx.serialize((Object)style);
            if (jsonelement.isJsonObject()) {
                final JsonObject jsonobject = (JsonObject)jsonelement;
                for (final Map.Entry<String, JsonElement> entry : jsonobject.entrySet()) {
                    object.add((String)entry.getKey(), (JsonElement)entry.getValue());
                }
            }
        }
        
        public JsonElement serialize(final IChatComponent p_serialize_1_, final Type p_serialize_2_, final JsonSerializationContext p_serialize_3_) {
            if (p_serialize_1_ instanceof ChatComponentText && p_serialize_1_.getChatStyle().isEmpty() && p_serialize_1_.getSiblings().isEmpty()) {
                return (JsonElement)new JsonPrimitive(((ChatComponentText)p_serialize_1_).getChatComponentText_TextValue());
            }
            final JsonObject jsonobject = new JsonObject();
            if (!p_serialize_1_.getChatStyle().isEmpty()) {
                this.serializeChatStyle(p_serialize_1_.getChatStyle(), jsonobject, p_serialize_3_);
            }
            if (!p_serialize_1_.getSiblings().isEmpty()) {
                final JsonArray jsonarray = new JsonArray();
                for (final IChatComponent ichatcomponent : p_serialize_1_.getSiblings()) {
                    jsonarray.add(this.serialize(ichatcomponent, ichatcomponent.getClass(), p_serialize_3_));
                }
                jsonobject.add("extra", (JsonElement)jsonarray);
            }
            if (p_serialize_1_ instanceof ChatComponentText) {
                jsonobject.addProperty("text", ((ChatComponentText)p_serialize_1_).getChatComponentText_TextValue());
            }
            else if (p_serialize_1_ instanceof ChatComponentTranslation) {
                final ChatComponentTranslation chatcomponenttranslation = (ChatComponentTranslation)p_serialize_1_;
                jsonobject.addProperty("translate", chatcomponenttranslation.getKey());
                if (chatcomponenttranslation.getFormatArgs() != null && chatcomponenttranslation.getFormatArgs().length > 0) {
                    final JsonArray jsonarray2 = new JsonArray();
                    for (final Object object : chatcomponenttranslation.getFormatArgs()) {
                        if (object instanceof IChatComponent) {
                            jsonarray2.add(this.serialize((IChatComponent)object, object.getClass(), p_serialize_3_));
                        }
                        else {
                            jsonarray2.add((JsonElement)new JsonPrimitive(String.valueOf(object)));
                        }
                    }
                    jsonobject.add("with", (JsonElement)jsonarray2);
                }
            }
            else if (p_serialize_1_ instanceof ChatComponentScore) {
                final ChatComponentScore chatcomponentscore = (ChatComponentScore)p_serialize_1_;
                final JsonObject jsonobject2 = new JsonObject();
                jsonobject2.addProperty("name", chatcomponentscore.getName());
                jsonobject2.addProperty("objective", chatcomponentscore.getObjective());
                jsonobject2.addProperty("value", chatcomponentscore.getUnformattedTextForChat());
                jsonobject.add("score", (JsonElement)jsonobject2);
            }
            else {
                if (!(p_serialize_1_ instanceof ChatComponentSelector)) {
                    throw new IllegalArgumentException("Don't know how to serialize " + p_serialize_1_ + " as a Component");
                }
                final ChatComponentSelector chatcomponentselector = (ChatComponentSelector)p_serialize_1_;
                jsonobject.addProperty("selector", chatcomponentselector.getSelector());
            }
            return (JsonElement)jsonobject;
        }
        
        public static String componentToJson(final IChatComponent component) {
            return Serializer.GSON.toJson((Object)component);
        }
        
        public static IChatComponent jsonToComponent(final String json) {
            return (IChatComponent)Serializer.GSON.fromJson(json, (Class)IChatComponent.class);
        }
        
        static {
            final GsonBuilder gsonbuilder = new GsonBuilder();
            gsonbuilder.registerTypeHierarchyAdapter((Class)IChatComponent.class, (Object)new Serializer());
            gsonbuilder.registerTypeHierarchyAdapter((Class)ChatStyle.class, (Object)new ChatStyle.Serializer());
            gsonbuilder.registerTypeAdapterFactory((TypeAdapterFactory)new EnumTypeAdapterFactory());
            GSON = gsonbuilder.create();
        }
    }
}
