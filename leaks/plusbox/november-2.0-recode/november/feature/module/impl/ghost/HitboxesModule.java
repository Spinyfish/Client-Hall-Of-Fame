/* November.lol © 2023 */
package lol.november.feature.module.impl.ghost;

import lol.november.feature.module.Category;
import lol.november.feature.module.Module;
import lol.november.feature.module.Register;
import lol.november.feature.setting.Setting;
import lol.november.listener.bus.Listener;
import lol.november.listener.bus.Subscribe;
import lol.november.listener.event.player.EventHitbox;
import net.minecraft.entity.player.EntityPlayer;

/**
 * @author Gavin
 * @since 2.0.0
 */
@Register(
  name = "Hitboxes",
  description = "Expands hitboxes",
  category = Category.GHOST
)
public class HitboxesModule extends Module {

  private final Setting<Double> expand = new Setting<>(
    "Expand",
    0.05,
    0.01,
    0.0,
    0.5
  );

  @Subscribe
  private final Listener<EventHitbox> hitbox = event -> {
    if (
      !(event.getEntity() instanceof EntityPlayer) ||
      event.getEntity().equals(mc.thePlayer)
    ) return;

    double x = expand.getValue();
    event.setBox(event.getBox().expand(x, x, x));
  };
}
