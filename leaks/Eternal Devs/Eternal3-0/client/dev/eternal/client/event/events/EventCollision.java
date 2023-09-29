package dev.eternal.client.event.events;

import dev.eternal.client.event.AbstractEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.block.Block;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;

@AllArgsConstructor
@Getter
@Setter
public class EventCollision extends AbstractEvent {

  private Block block;
  private BlockPos blockPos;
  private AxisAlignedBB boundingBox;

}
