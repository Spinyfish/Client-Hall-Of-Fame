package com.alan.clients.newevent.impl.other;


import com.alan.clients.newevent.CancellableEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.block.Block;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

/**
 * @author Strikeless
 * @since 15.03.2022
 */
@Getter
@Setter
@AllArgsConstructor
public class BlockAABBEvent extends CancellableEvent {

    private final World world;
    private final Block block;
    private final BlockPos blockPos;
    private AxisAlignedBB boundingBox;
    private final AxisAlignedBB maskBoundingBox;

}
