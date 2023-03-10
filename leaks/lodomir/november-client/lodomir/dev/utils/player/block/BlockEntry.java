/*
 * Decompiled with CFR 0.152.
 */
package lodomir.dev.utils.player.block;

import lodomir.dev.utils.player.block.CustomHitVec;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;

public class BlockEntry {
    private final BlockPos position;
    private final EnumFacing facing;
    private final Vec3 vector;

    public BlockPos getPosition() {
        return this.position;
    }

    public EnumFacing getFacing() {
        return this.facing;
    }

    public Vec3 getVector() {
        return this.vector;
    }

    public BlockEntry(BlockPos position, EnumFacing facing) {
        this.position = position;
        this.facing = facing;
        CustomHitVec customHitVec = new CustomHitVec(0.5f, 0.5f, 0.5f, 0.5f);
        this.vector = new Vec3(position).add(new Vec3(customHitVec.getX(), customHitVec.getY(), customHitVec.getZ())).add(new Vec3(facing.getDirectionVec()).scale(customHitVec.getScale()));
    }
}

