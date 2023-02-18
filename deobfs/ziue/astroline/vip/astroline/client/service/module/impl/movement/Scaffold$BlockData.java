/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.BlockPos
 *  net.minecraft.util.EnumFacing
 */
package vip.astroline.client.service.module.impl.movement;

import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

private class Scaffold.BlockData {
    private BlockPos position;
    private EnumFacing facing;

    public Scaffold.BlockData(BlockPos position, EnumFacing facing) {
        this.position = position;
        this.facing = facing;
    }

    static /* synthetic */ BlockPos access$000(Scaffold.BlockData x0) {
        return x0.position;
    }

    static /* synthetic */ EnumFacing access$100(Scaffold.BlockData x0) {
        return x0.facing;
    }
}
