package club.pulsive.impl.event.player;

import club.pulsive.api.event.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;

@Getter
@Setter
@AllArgsConstructor
public class BlockPlaceEvent extends Event {
    private final BlockPos blockPos;
    private final EnumFacing side;
    private final Vec3 hitVec;
}
