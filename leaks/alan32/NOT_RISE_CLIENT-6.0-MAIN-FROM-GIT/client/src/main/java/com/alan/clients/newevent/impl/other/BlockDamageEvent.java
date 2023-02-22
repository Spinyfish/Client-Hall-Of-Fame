package com.alan.clients.newevent.impl.other;

import com.alan.clients.newevent.CancellableEvent;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

@Getter
@Setter
public final class BlockDamageEvent extends CancellableEvent {
    private EntityPlayerSP player;
    private World world;
    private BlockPos blockPos;

    public BlockDamageEvent(final EntityPlayerSP player, final World world, final BlockPos blockPos) {
        this.player = player;
        this.world = world;
        this.blockPos = blockPos;
    }
}