package com.alan.clients.util.player;

import lombok.Getter;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;

@Getter
public class EnumFacingOffset {
    public EnumFacing enumFacing;
    private final Vec3 offset;

    public EnumFacingOffset(final EnumFacing enumFacing, final Vec3 offset) {
        this.enumFacing = enumFacing;
        this.offset = offset;
    }
}
