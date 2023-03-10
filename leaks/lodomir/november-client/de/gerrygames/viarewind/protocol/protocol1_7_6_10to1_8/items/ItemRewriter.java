/*
 * Decompiled with CFR 0.152.
 */
package de.gerrygames.viarewind.protocol.protocol1_7_6_10to1_8.items;

import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.ByteTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.CompoundTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.ListTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.NumberTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.ShortTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.StringTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.Tag;
import de.gerrygames.viarewind.protocol.protocol1_7_6_10to1_8.items.ReplacementRegistry1_7_6_10to1_8;
import de.gerrygames.viarewind.utils.ChatUtil;
import de.gerrygames.viarewind.utils.Enchantments;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ItemRewriter {
    public static Item toClient(Item item) {
        if (item == null) {
            return null;
        }
        CompoundTag tag = item.tag();
        if (tag == null) {
            tag = new CompoundTag();
            item.setTag(tag);
        }
        CompoundTag viaVersionTag = new CompoundTag();
        tag.put("ViaRewind1_7_6_10to1_8", viaVersionTag);
        viaVersionTag.put("id", new ShortTag((short)item.identifier()));
        viaVersionTag.put("data", new ShortTag(item.data()));
        CompoundTag display = (CompoundTag)tag.get("display");
        if (display != null && display.contains("Name")) {
            viaVersionTag.put("displayName", new StringTag((String)((Tag)display.get("Name")).getValue()));
        }
        if (display != null && display.contains("Lore")) {
            viaVersionTag.put("lore", new ListTag((List<Tag>)((ListTag)display.get("Lore")).getValue()));
        }
        if (tag.contains("ench") || tag.contains("StoredEnchantments")) {
            ListTag enchTag = tag.contains("ench") ? (ListTag)tag.get("ench") : (ListTag)tag.get("StoredEnchantments");
            ArrayList<Tag> lore = new ArrayList<Tag>();
            for (Tag ench : new ArrayList(enchTag.getValue())) {
                short id = ((NumberTag)((CompoundTag)ench).get("id")).asShort();
                short lvl = ((NumberTag)((CompoundTag)ench).get("lvl")).asShort();
                if (id != 8) continue;
                String s = "\u00a7r\u00a77Depth Strider ";
                enchTag.remove(ench);
                s = s + Enchantments.ENCHANTMENTS.getOrDefault(lvl, "enchantment.level." + lvl);
                lore.add(new StringTag(s));
            }
            if (!lore.isEmpty()) {
                ListTag loreTag;
                if (display == null) {
                    display = new CompoundTag();
                    tag.put("display", display);
                    viaVersionTag.put("noDisplay", new ByteTag());
                }
                if ((loreTag = (ListTag)display.get("Lore")) == null) {
                    loreTag = new ListTag(StringTag.class);
                    display.put("Lore", loreTag);
                }
                lore.addAll((Collection<Tag>)loreTag.getValue());
                loreTag.setValue(lore);
            }
        }
        if (item.identifier() == 387 && tag.contains("pages")) {
            ListTag pages = (ListTag)tag.get("pages");
            ListTag oldPages = new ListTag(StringTag.class);
            viaVersionTag.put("pages", oldPages);
            for (int i = 0; i < pages.size(); ++i) {
                StringTag page = (StringTag)pages.get(i);
                String value = page.getValue();
                oldPages.add(new StringTag(value));
                value = ChatUtil.jsonToLegacy(value);
                page.setValue(value);
            }
        }
        ReplacementRegistry1_7_6_10to1_8.replace(item);
        if (viaVersionTag.size() == 2 && ((Short)((Tag)viaVersionTag.get("id")).getValue()).shortValue() == item.identifier() && ((Short)((Tag)viaVersionTag.get("data")).getValue()).shortValue() == item.data()) {
            item.tag().remove("ViaRewind1_7_6_10to1_8");
            if (item.tag().isEmpty()) {
                item.setTag(null);
            }
        }
        return item;
    }

    public static Item toServer(Item item) {
        if (item == null) {
            return null;
        }
        CompoundTag tag = item.tag();
        if (tag == null || !item.tag().contains("ViaRewind1_7_6_10to1_8")) {
            return item;
        }
        CompoundTag viaVersionTag = (CompoundTag)tag.remove("ViaRewind1_7_6_10to1_8");
        item.setIdentifier(((Short)((Tag)viaVersionTag.get("id")).getValue()).shortValue());
        item.setData((Short)((Tag)viaVersionTag.get("data")).getValue());
        if (viaVersionTag.contains("noDisplay")) {
            tag.remove("display");
        }
        if (viaVersionTag.contains("displayName")) {
            StringTag name;
            CompoundTag display = (CompoundTag)tag.get("display");
            if (display == null) {
                display = new CompoundTag();
                tag.put("display", display);
            }
            if ((name = (StringTag)display.get("Name")) == null) {
                display.put("Name", new StringTag((String)((Tag)viaVersionTag.get("displayName")).getValue()));
            } else {
                name.setValue((String)((Tag)viaVersionTag.get("displayName")).getValue());
            }
        } else if (tag.contains("display")) {
            ((CompoundTag)tag.get("display")).remove("Name");
        }
        if (item.identifier() == 387) {
            ListTag oldPages = (ListTag)viaVersionTag.get("pages");
            tag.remove("pages");
            tag.put("pages", oldPages);
        }
        return item;
    }
}

