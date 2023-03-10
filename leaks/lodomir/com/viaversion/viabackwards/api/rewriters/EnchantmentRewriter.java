/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viabackwards.api.rewriters;

import com.viaversion.viabackwards.api.rewriters.ItemRewriter;
import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.CompoundTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.ListTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.NumberTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.ShortTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.StringTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.Tag;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.ChatRewriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class EnchantmentRewriter {
    private final Map<String, String> enchantmentMappings = new HashMap<String, String>();
    private final ItemRewriter itemRewriter;
    private final boolean jsonFormat;

    public EnchantmentRewriter(ItemRewriter itemRewriter, boolean jsonFormat) {
        this.itemRewriter = itemRewriter;
        this.jsonFormat = jsonFormat;
    }

    public EnchantmentRewriter(ItemRewriter itemRewriter) {
        this(itemRewriter, true);
    }

    public void registerEnchantment(String key, String replacementLore) {
        this.enchantmentMappings.put(key, replacementLore);
    }

    public void handleToClient(Item item) {
        CompoundTag tag = item.tag();
        if (tag == null) {
            return;
        }
        if (tag.get("Enchantments") instanceof ListTag) {
            this.rewriteEnchantmentsToClient(tag, false);
        }
        if (tag.get("StoredEnchantments") instanceof ListTag) {
            this.rewriteEnchantmentsToClient(tag, true);
        }
    }

    public void handleToServer(Item item) {
        CompoundTag tag = item.tag();
        if (tag == null) {
            return;
        }
        if (tag.contains(this.itemRewriter.getNbtTagName() + "|Enchantments")) {
            this.rewriteEnchantmentsToServer(tag, false);
        }
        if (tag.contains(this.itemRewriter.getNbtTagName() + "|StoredEnchantments")) {
            this.rewriteEnchantmentsToServer(tag, true);
        }
    }

    public void rewriteEnchantmentsToClient(CompoundTag tag, boolean storedEnchant) {
        String key = storedEnchant ? "StoredEnchantments" : "Enchantments";
        ListTag enchantments = (ListTag)tag.get(key);
        ArrayList<Tag> loreToAdd = new ArrayList<Tag>();
        boolean changed = false;
        Iterator<Tag> iterator = enchantments.iterator();
        while (iterator.hasNext()) {
            String enchantmentId;
            String remappedName;
            CompoundTag enchantmentEntry = (CompoundTag)iterator.next();
            Object idTag = enchantmentEntry.get("id");
            if (!(idTag instanceof StringTag) || (remappedName = this.enchantmentMappings.get(enchantmentId = ((StringTag)idTag).getValue())) == null) continue;
            if (!changed) {
                this.itemRewriter.saveListTag(tag, enchantments, key);
                changed = true;
            }
            iterator.remove();
            int level = ((NumberTag)enchantmentEntry.get("lvl")).asInt();
            String loreValue = remappedName + " " + EnchantmentRewriter.getRomanNumber(level);
            if (this.jsonFormat) {
                loreValue = ChatRewriter.legacyTextToJsonString(loreValue);
            }
            loreToAdd.add(new StringTag(loreValue));
        }
        if (!loreToAdd.isEmpty()) {
            ListTag loreTag;
            CompoundTag display;
            if (!storedEnchant && enchantments.size() == 0) {
                CompoundTag dummyEnchantment = new CompoundTag();
                dummyEnchantment.put("id", new StringTag());
                dummyEnchantment.put("lvl", new ShortTag(0));
                enchantments.add(dummyEnchantment);
            }
            if ((display = (CompoundTag)tag.get("display")) == null) {
                display = new CompoundTag();
                tag.put("display", display);
            }
            if ((loreTag = (ListTag)display.get("Lore")) == null) {
                loreTag = new ListTag(StringTag.class);
                display.put("Lore", loreTag);
            } else {
                this.itemRewriter.saveListTag(display, loreTag, "Lore");
            }
            loreToAdd.addAll((Collection<Tag>)loreTag.getValue());
            loreTag.setValue(loreToAdd);
        }
    }

    public void rewriteEnchantmentsToServer(CompoundTag tag, boolean storedEnchant) {
        String key = storedEnchant ? "StoredEnchantments" : "Enchantments";
        this.itemRewriter.restoreListTag(tag, key);
    }

    public static String getRomanNumber(int number) {
        switch (number) {
            case 1: {
                return "I";
            }
            case 2: {
                return "II";
            }
            case 3: {
                return "III";
            }
            case 4: {
                return "IV";
            }
            case 5: {
                return "V";
            }
            case 6: {
                return "VI";
            }
            case 7: {
                return "VII";
            }
            case 8: {
                return "VIII";
            }
            case 9: {
                return "IX";
            }
            case 10: {
                return "X";
            }
        }
        return Integer.toString(number);
    }
}

