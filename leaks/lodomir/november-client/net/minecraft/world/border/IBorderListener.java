/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.world.border;

import net.minecraft.world.border.WorldBorder;

public interface IBorderListener {
    public void onSizeChanged(WorldBorder var1, double var2);

    public void onTransitionStarted(WorldBorder var1, double var2, double var4, long var6);

    public void onCenterChanged(WorldBorder var1, double var2, double var4);

    public void onWarningTimeChanged(WorldBorder var1, int var2);

    public void onWarningDistanceChanged(WorldBorder var1, int var2);

    public void onDamageAmountChanged(WorldBorder var1, double var2);

    public void onDamageBufferChanged(WorldBorder var1, double var2);
}

