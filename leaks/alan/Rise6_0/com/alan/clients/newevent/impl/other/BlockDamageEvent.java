package com.alan.clients.newevent.impl.other;

import com.alan.clients.newevent.CancellableEvent;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.block.Block;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

@Getter
@Setter
public final class BlockDamageEvent extends CancellableEvent {

    private float relativeBlockHardness;
    private Block block;
    private EntityPlayerSP player;
    private World world;
    private BlockPos blockPos;

    public BlockDamageEvent(final Block block, final EntityPlayerSP player, final World world, final BlockPos blockPos) {
        this.block = block;
        this.player = player;
        this.world = world;
        this.blockPos = blockPos;
        relativeBlockHardness = block.getPlayerRelativeBlockHardness(player, world, blockPos);
    }
}