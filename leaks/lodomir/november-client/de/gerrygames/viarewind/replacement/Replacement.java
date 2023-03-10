/*
 * Decompiled with CFR 0.152.
 */
package de.gerrygames.viarewind.replacement;

import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.CompoundTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.StringTag;

public class Replacement {
    private int id;
    private int data;
    private String name;
    private String resetName;
    private String bracketName;

    public Replacement(int id) {
        this(id, -1);
    }

    public Replacement(int id, int data) {
        this(id, data, null);
    }

    public Replacement(int id, String name) {
        this(id, -1, name);
    }

    public Replacement(int id, int data, String name) {
        this.id = id;
        this.data = data;
        this.name = name;
        if (name != null) {
            this.resetName = "\u00a7r" + name;
            this.bracketName = " \u00a7r\u00a77(" + name + "\u00a7r\u00a77)";
        }
    }

    public int getId() {
        return this.id;
    }

    public int getData() {
        return this.data;
    }

    public String getName() {
        return this.name;
    }

    public Item replace(Item item) {
        item.setIdentifier(this.id);
        if (this.data != -1) {
            item.setData((short)this.data);
        }
        if (this.name != null) {
            CompoundTag display;
            CompoundTag compoundTag;
            CompoundTag compoundTag2 = compoundTag = item.tag() == null ? new CompoundTag() : item.tag();
            if (!compoundTag.contains("display")) {
                compoundTag.put("display", new CompoundTag());
            }
            if ((display = (CompoundTag)compoundTag.get("display")).contains("Name")) {
                StringTag name = (StringTag)display.get("Name");
                if (!name.getValue().equals(this.resetName) && !name.getValue().endsWith(this.bracketName)) {
                    name.setValue(name.getValue() + this.bracketName);
                }
            } else {
                display.put("Name", new StringTag(this.resetName));
            }
            item.setTag(compoundTag);
        }
        return item;
    }

    public int replaceData(int data) {
        return this.data == -1 ? data : this.data;
    }
}

