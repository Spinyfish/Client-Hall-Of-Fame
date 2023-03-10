/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.api.type.types.minecraft;

import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.api.type.Type;

public abstract class BaseItemType
extends Type<Item> {
    protected BaseItemType() {
        super(Item.class);
    }

    protected BaseItemType(String typeName) {
        super(typeName, Item.class);
    }

    @Override
    public Class<? extends Type> getBaseClass() {
        return BaseItemType.class;
    }
}

