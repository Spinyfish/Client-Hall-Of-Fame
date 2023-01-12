package dev.eternal.client.module.impl.movement;

import dev.eternal.client.event.Subscribe;;
import dev.eternal.client.event.events.EventMove;
import dev.eternal.client.module.Module;
import dev.eternal.client.module.api.ModuleInfo;
import dev.eternal.client.util.world.WorldUtil;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;

import java.util.Objects;

@ModuleInfo(name = "SafeWalk", category = Module.Category.MOVEMENT, description = "Prevents you from walking off cliffs")
public class SafeWalk extends Module {
  @Subscribe
  public void onMove(EventMove eventMove) {

  }
}
