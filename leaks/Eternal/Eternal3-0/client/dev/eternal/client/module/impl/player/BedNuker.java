package dev.eternal.client.module.impl.player;

import com.google.common.eventbus.Subscribe;
import dev.eternal.client.event.events.EventUpdate;
import dev.eternal.client.module.Module;
import dev.eternal.client.module.api.ModuleInfo;
import dev.eternal.client.property.impl.NumberSetting;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

@ModuleInfo(name = "BedNuker", description = "Nukes Beds...", category = Module.Category.PLAYER)
public class BedNuker extends Module {

  private final NumberSetting maxDist = new NumberSetting(this, "Distance", "Maximum distance to the bed.", 3, 3, 8, 1);

  @Subscribe
  public void handleUpdate(EventUpdate eventUpdate) {
    if (!eventUpdate.pre())
      return;
    int range = maxDist.value().intValue();
    for (int x = -range; x < range; x++) {
      for (int y = range; y > -range; y--) {
        for (int z = -range; z < range; z++) {
          final BlockPos position = new BlockPos(mc.thePlayer).add(x, y, z);
          final Block block = mc.theWorld.getBlockState(position).getBlock();
          if (block instanceof BlockBed) {
            mc.playerController.onPlayerDestroyBlock(position, EnumFacing.DOWN);
            break;
          }
        }
      }
    }
  }

}
