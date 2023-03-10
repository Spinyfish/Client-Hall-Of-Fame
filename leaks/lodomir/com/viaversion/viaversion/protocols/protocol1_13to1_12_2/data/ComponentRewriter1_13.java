/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.protocols.protocol1_13to1_12_2.data;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.minecraft.item.DataItem;
import com.viaversion.viaversion.api.minecraft.nbt.BinaryTagIO;
import com.viaversion.viaversion.api.protocol.Protocol;
import com.viaversion.viaversion.libs.gson.JsonArray;
import com.viaversion.viaversion.libs.gson.JsonElement;
import com.viaversion.viaversion.libs.gson.JsonObject;
import com.viaversion.viaversion.libs.gson.JsonPrimitive;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.CompoundTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.ShortTag;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.Protocol1_13To1_12_2;
import com.viaversion.viaversion.rewriter.ComponentRewriter;
import java.io.IOException;

public class ComponentRewriter1_13
extends ComponentRewriter {
    public ComponentRewriter1_13(Protocol protocol) {
        super(protocol);
    }

    @Override
    protected void handleHoverEvent(JsonObject hoverEvent) {
        CompoundTag tag;
        super.handleHoverEvent(hoverEvent);
        String action = hoverEvent.getAsJsonPrimitive("action").getAsString();
        if (!action.equals("show_item")) {
            return;
        }
        JsonElement value = hoverEvent.get("value");
        if (value == null) {
            return;
        }
        String text = this.findItemNBT(value);
        if (text == null) {
            return;
        }
        try {
            tag = BinaryTagIO.readString(text);
        }
        catch (Exception e) {
            if (!Via.getConfig().isSuppressConversionWarnings() || Via.getManager().isDebug()) {
                Via.getPlatform().getLogger().warning("Error reading NBT in show_item:" + text);
                e.printStackTrace();
            }
            return;
        }
        CompoundTag itemTag = (CompoundTag)tag.get("tag");
        ShortTag damageTag = (ShortTag)tag.get("Damage");
        short damage = damageTag != null ? damageTag.asShort() : (short)0;
        DataItem item = new DataItem();
        item.setData(damage);
        item.setTag(itemTag);
        this.protocol.getItemRewriter().handleItemToClient(item);
        if (damage != item.data()) {
            tag.put("Damage", new ShortTag(item.data()));
        }
        if (itemTag != null) {
            tag.put("tag", itemTag);
        }
        JsonArray array = new JsonArray();
        JsonObject object = new JsonObject();
        array.add(object);
        try {
            String serializedNBT = BinaryTagIO.writeString(tag);
            object.addProperty("text", serializedNBT);
            hoverEvent.add("value", array);
        }
        catch (IOException e) {
            Via.getPlatform().getLogger().warning("Error writing NBT in show_item:" + text);
            e.printStackTrace();
        }
    }

    protected String findItemNBT(JsonElement element) {
        if (element.isJsonArray()) {
            for (JsonElement jsonElement : element.getAsJsonArray()) {
                String value = this.findItemNBT(jsonElement);
                if (value == null) continue;
                return value;
            }
        } else if (element.isJsonObject()) {
            JsonPrimitive text = element.getAsJsonObject().getAsJsonPrimitive("text");
            if (text != null) {
                return text.getAsString();
            }
        } else if (element.isJsonPrimitive()) {
            return element.getAsJsonPrimitive().getAsString();
        }
        return null;
    }

    @Override
    protected void handleTranslate(JsonObject object, String translate) {
        super.handleTranslate(object, translate);
        String newTranslate = Protocol1_13To1_12_2.MAPPINGS.getTranslateMapping().get(translate);
        if (newTranslate == null) {
            newTranslate = Protocol1_13To1_12_2.MAPPINGS.getMojangTranslation().get(translate);
        }
        if (newTranslate != null) {
            object.addProperty("translate", newTranslate);
        }
    }
}

