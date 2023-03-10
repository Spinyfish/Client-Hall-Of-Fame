/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.protocols.protocol1_12to1_11_1;

import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.libs.gson.JsonArray;
import com.viaversion.viaversion.libs.gson.JsonElement;
import com.viaversion.viaversion.libs.gson.JsonObject;
import com.viaversion.viaversion.libs.gson.JsonPrimitive;
import java.util.regex.Pattern;

public class ChatItemRewriter {
    private static final Pattern indexRemoval = Pattern.compile("(?<![\\w-.+])\\d+:(?=([^\"\\\\]*(\\\\.|\"([^\"\\\\]*\\\\.)*[^\"\\\\]*\"))*[^\"]*$)");

    public static void toClient(JsonElement element, UserConnection user) {
        block5: {
            block3: {
                JsonObject obj;
                block4: {
                    JsonElement value;
                    JsonObject hoverEvent;
                    block6: {
                        String type;
                        if (!(element instanceof JsonObject)) break block3;
                        obj = (JsonObject)element;
                        if (!obj.has("hoverEvent")) break block4;
                        if (!(obj.get("hoverEvent") instanceof JsonObject) || !(hoverEvent = (JsonObject)obj.get("hoverEvent")).has("action") || !hoverEvent.has("value") || !(type = hoverEvent.get("action").getAsString()).equals("show_item") && !type.equals("show_entity")) break block5;
                        value = hoverEvent.get("value");
                        if (!value.isJsonPrimitive() || !value.getAsJsonPrimitive().isString()) break block6;
                        String newValue = indexRemoval.matcher(value.getAsString()).replaceAll("");
                        hoverEvent.addProperty("value", newValue);
                        break block5;
                    }
                    if (!value.isJsonArray()) break block5;
                    JsonArray newArray = new JsonArray();
                    for (JsonElement valueElement : value.getAsJsonArray()) {
                        if (!valueElement.isJsonPrimitive() || !valueElement.getAsJsonPrimitive().isString()) continue;
                        String newValue = indexRemoval.matcher(valueElement.getAsString()).replaceAll("");
                        newArray.add(new JsonPrimitive(newValue));
                    }
                    hoverEvent.add("value", newArray);
                    break block5;
                }
                if (!obj.has("extra")) break block5;
                ChatItemRewriter.toClient(obj.get("extra"), user);
                break block5;
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

