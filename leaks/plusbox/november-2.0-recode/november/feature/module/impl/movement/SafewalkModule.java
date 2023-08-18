/* November.lol © 2023 */
package lol.november.feature.module.impl.movement;

import lol.november.feature.module.Category;
import lol.november.feature.module.Module;
import lol.november.feature.module.Register;
import lol.november.listener.bus.Listener;
import lol.november.listener.bus.Subscribe;
import lol.november.listener.event.player.EventUpdate;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;

/**
 * @author Gavin
 * @since 2.0.0
 */
@Register(
  name = "SafeWalk",
  description = "Stops you from falling off blocks",
  category = Category.MOVEMENT
)
public class SafewalkModule extends Module {

  private boolean override;

  @Override
  public void disable() {
    super.disable();

    if (override) {
      override = false;
      mc.gameSettings.keyBindSneak.pressed = false;
    }
  }

  @Subscribe
  private final Listener<EventUpdate> update = event -> {
    BlockPos below = new BlockPos(mc.thePlayer.getPositionVector()).down();
    if (mc.theWorld.getBlockState(below).getBlock() == Blocks.air) {
      override = true;
      mc.gameSettings.keyBindSneak.pressed = true;
    } else {
      if (override) {
        override = false;
        mc.gameSettings.keyBindSneak.pressed = false;
      }
    }
  };
}
