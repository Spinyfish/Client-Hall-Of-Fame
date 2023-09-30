package club.pulsive.impl.event.player;

import club.pulsive.api.event.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MinecraftError;

@Getter
@Setter
@AllArgsConstructor
public class PlayerCollideBoiEvent extends Event {
    private AxisAlignedBB axisAlignedBB;
    private final Block block;
    private final Entity collidingEntity;
    private final BlockPos blockPos;
}
