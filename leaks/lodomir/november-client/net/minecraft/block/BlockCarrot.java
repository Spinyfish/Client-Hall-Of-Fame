/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block;

import net.minecraft.block.BlockCrops;
import net.minecraft.init.Items;
import net.minecraft.item.Item;

public class BlockCarrot
extends BlockCrops {
    @Override
    protected Item getSeed() {
        return Items.carrot;
    }

    @Override
    protected Item getCrop() {
        return Items.carrot;
    }
}

