/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viabackwards.api.rewriters;

import com.viaversion.viabackwards.api.rewriters.EnchantmentRewriter;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.ByteTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.CompoundTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.IntTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.ListTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.NumberTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.ShortTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.StringTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.Tag;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class LegacyEnchantmentRewriter {
    private final Map<Short, String> enchantmentMappings = new HashMap<Short, String>();
    private final String nbtTagName;
    private Set<Short> hideLevelForEnchants;

    public LegacyEnchantmentRewriter(String nbtTagName) {
        this.nbtTagName = nbtTagName;
    }

    public void registerEnchantment(int id, String replacementLore) {
        this.enchantmentMappings.put((short)id, replacementLore);
    }

    public void rewriteEnchantmentsToClient(CompoundTag tag, boolean storedEnchant) {
        String key = storedEnchant ? "StoredEnchantments" : "ench";
        ListTag enchantments = (ListTag)tag.get(key);
        ListTag remappedEnchantments = new ListTag(CompoundTag.class);
        ArrayList<Tag> lore = new ArrayList<Tag>();
        for (Tag enchantmentEntry : enchantments.clone()) {
            short newId;
            String enchantmentName;
            Object idTag = ((CompoundTag)enchantmentEntry).get("id");
            if (idTag == null || (enchantmentName = this.enchantmentMappings.get(newId = ((NumberTag)idTag).asShort())) == null) continue;
            enchantments.remove(enchantmentEntry);
            short level = ((NumberTag)((CompoundTag)enchantmentEntry).get("lvl")).asShort();
            if (this.hideLevelForEnchants != null && this.hideLevelForEnchants.contains(newId)) {
                lore.add(new StringTag(enchantmentName));
            } else {
                lore.add(new StringTag(enchantmentName + " " + EnchantmentRewriter.getRomanNumber(level)));
            }
            remappedEnchantments.add(enchantmentEntry);
        }
        if (!lore.isEmpty()) {
            ListTag loreTag;
            if (!storedEnchant && enchantments.size() == 0) {
                CompoundTag dummyEnchantment = new CompoundTag();
                dummyEnchantment.put("id", new ShortTag(0));
                dummyEnchantment.put("lvl", new ShortTag(0));
                enchantments.add(dummyEnchantment);
                tag.put(this.nbtTagName + "|dummyEnchant", new ByteTag());
                IntTag hideFlags = (IntTag)tag.get("HideFlags");
                if (hideFlags == null) {
                    hideFlags = new IntTag();
                } else {
                    tag.put(this.nbtTagName + "|oldHideFlags", new IntTag(hideFlags.asByte()));
                }
                int flags = hideFlags.asByte() | 1;
                hideFlags.setValue(flags);
                tag.put("HideFlags", hideFlags);
            }
            tag.put(this.nbtTagName + "|" + key, remappedEnchantments);
            CompoundTag display = (CompoundTag)tag.get("display");
            if (display == null) {
                display = new CompoundTag();
                tag.put("display", display);
            }
            if ((loreTag = (ListTag)display.get("Lore")) == null) {
                loreTag = new ListTag(StringTag.class);
                display.put("Lore", loreTag);
            }
            lore.addAll((Collection<Tag>)loreTag.getValue());
            loreTag.setValue(lore);
        }
    }

    public void rewriteEnchantmentsToServer(CompoundTag tag, boolean storedEnchant) {
        CompoundTag display;
        String key = storedEnchant ? "StoredEnchantments" : "ench";
        ListTag remappedEnchantments = (ListTag)tag.remove(this.nbtTagName + "|" + key);
        ListTag enchantments = (ListTag)tag.get(key);
        if (enchantments == null) {
            enchantments = new ListTag(CompoundTag.class);
        }
        if (!storedEnchant && tag.remove(this.nbtTagName + "|dummyEnchant") != null) {
            for (Tag enchantment : enchantments.clone()) {
                short id = ((NumberTag)((CompoundTag)enchantment).get("id")).asShort();
                short level = ((NumberTag)((CompoundTag)enchantment).get("lvl")).asShort();
                if (id != 0 || level != 0) continue;
                enchantments.remove(enchantment);
            }
            IntTag hideFlags = (IntTag)tag.remove(this.nbtTagName + "|oldHideFlags");
            if (hideFlags != null) {
                tag.put("HideFlags", new IntTag(hideFlags.asByte()));
            } else {
                tag.remove("HideFlags");
            }
        }
        ListTag lore = (display = (CompoundTag)tag.get("display")) != null ? (ListTag)display.get("Lore") : null;
        for (Tag enchantment : remappedEnchantments.clone()) {
            enchantments.add(enchantment);
            if (lore == null || lore.size() == 0) continue;
            lore.remove((Tag)lore.get(0));
        }
        if (lore != null && lore.size() == 0) {
            display.remove("Lore");
            if (display.isEmpty()) {
                tag.remove("display");
            }
        }
        tag.put(key, enchantments);
    }

    public void setHideLevelForEnchants(int ... enchants) {
        this.hideLevelForEnchants = new HashSet<Short>();
        for (int enchant : enchants) {
            this.hideLevelForEnchants.add((short)enchant);
        }
    }
}

