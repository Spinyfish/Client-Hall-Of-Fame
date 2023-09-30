package de.lirium.impl.events;

import best.azura.eventbus.events.CancellableEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.block.Block;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

@AllArgsConstructor
public class CollideEvent extends CancellableEvent {
    public final BlockPos pos;
    public final Block block;
    public AxisAlignedBB boundingBox;
}