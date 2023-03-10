/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viabackwards.protocol.protocol1_11_1to1_12.packets;

import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.libs.gson.JsonArray;
import com.viaversion.viaversion.libs.gson.JsonElement;
import com.viaversion.viaversion.libs.gson.JsonObject;
import com.viaversion.viaversion.libs.gson.JsonPrimitive;

public class ChatItemRewriter {
    public static void toClient(JsonElement element, UserConnection user) {
        block6: {
            block4: {
                JsonObject obj;
                block5: {
                    JsonElement value;
                    String type;
                    JsonObject hoverEvent;
                    if (!(element instanceof JsonObject)) break block4;
                    obj = (JsonObject)element;
                    if (!obj.has("hoverEvent")) break block5;
                    if (obj.get("hoverEvent") instanceof JsonObject && (hoverEvent = (JsonObject)obj.get("hoverEvent")).has("action") && hoverEvent.has("value") && ((type = hoverEvent.get("action").getAsString()).equals("show_item") || type.equals("show_entity")) && (value = hoverEvent.get("value")).isJsonArray()) {
                        JsonArray newArray = new JsonArray();
                        int index = 0;
                        for (JsonElement valueElement : value.getAsJsonArray()) {
                            if (!valueElement.isJsonPrimitive() || !valueElement.getAsJsonPrimitive().isString()) continue;
                            String newValue = index + ":" + valueElement.getAsString();
                            newArray.add(new JsonPrimitive(newValue));
                        }
                        hoverEvent.add("value", newArray);
                    }
                    break block6;
                }
                if (!obj.has("extra")) break block6;
                ChatItemRewriter.toClient(obj.get("extra"), user);
                break block6;
            }
            if (element instanceof JsonArray) {
                JsonArray array = (JsonArray)element;
                for (JsonElement value : array) {
                    ChatItemRewriter.toClient(value, user);
                }
            }
        }
    }
}

