/*
 * Decompiled with CFR 0.152.
 */
package de.gerrygames.viarewind.replacement;

import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.libs.fastutil.ints.Int2ObjectMap;
import com.viaversion.viaversion.libs.fastutil.ints.Int2ObjectOpenHashMap;
import de.gerrygames.viarewind.replacement.Replacement;

public class ReplacementRegistry {
    private final Int2ObjectMap<Replacement> itemReplacements = new Int2ObjectOpenHashMap<Replacement>();
    private final Int2ObjectMap<Replacement> blockReplacements = new Int2ObjectOpenHashMap<Replacement>();

    public void registerItem(int id, Replacement replacement) {
        this.registerItem(id, -1, replacement);
    }

    public void registerBlock(int id, Replacement replacement) {
        this.registerBlock(id, -1, replacement);
    }

    public void registerItemBlock(int id, Replacement replacement) {
        this.registerItemBlock(id, -1, replacement);
    }

    public void registerItem(int id, int data, Replacement replacement) {
        this.itemReplacements.put(ReplacementRegistry.combine(id, data), replacement);
    }

    public void registerBlock(int id, int data, Replacement replacement) {
        this.blockReplacements.put(ReplacementRegistry.combine(id, data), replacement);
    }

    public void registerItemBlock(int id, int data, Replacement replacement) {
        this.registerItem(id, data, replacement);
        this.registerBlock(id, data, replacement);
    }

    public Item replace(Item item) {
        Replacement replacement = (Replacement)this.itemReplacements.get(ReplacementRegistry.combine(item.identifier(), item.data()));
        if (replacement == null) {
            replacement = (Replacement)this.itemReplacements.get(ReplacementRegistry.combine(item.identifier(), -1));
        }
        return replacement == null ? item : replacement.replace(item);
    }

    public Replacement replace(int id, int data) {
        Replacement replacement = (Replacement)this.blockReplacements.get(ReplacementRegistry.combine(id, data));
        if (replacement == null) {
            replacement = (Replacement)this.blockReplacements.get(ReplacementRegistry.combine(id, -1));
        }
        return replacement;
    }

    public static int combine(int id, int data) {
        return id << 16 | data & 0xFFFF;
    }
}

