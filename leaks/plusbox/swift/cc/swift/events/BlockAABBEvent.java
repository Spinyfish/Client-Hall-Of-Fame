package cc.swift.events;

import dev.codeman.eventbus.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.block.Block;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

@Getter
@Setter
@AllArgsConstructor
public class BlockAABBEvent extends Event {

    private World world;
    private Block block;
    private BlockPos blockPos;
    private AxisAlignedBB boundingBox;
    private AxisAlignedBB maskBoundingBox;

}
