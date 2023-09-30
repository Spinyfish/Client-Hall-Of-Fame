/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity;

import net.minecraft.entity.boss.EntityDragonPart;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public interface IEntityMultiPart {
    public World getWorld();

    public boolean attackEntityFromPart(EntityDragonPart var1, DamageSource var2, float var3);
}

