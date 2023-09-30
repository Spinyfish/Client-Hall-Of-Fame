package felix.events.player;

import felix.events.Cancellable;
import felix.events.Event;
import net.minecraft.util.BlockPos;

public final class EventBlockDamaged extends Cancellable implements Event {
   private final BlockPos blockPos;

   public EventBlockDamaged(BlockPos blockPos) {
      this.blockPos = blockPos;
   }

   public BlockPos getBlockPos() {
      return this.blockPos;
   }
}
