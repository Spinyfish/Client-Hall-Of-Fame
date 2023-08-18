/* November.lol © 2023 */
package lol.november.feature.module.impl.movement;

import lol.november.feature.module.Category;
import lol.november.feature.module.Module;
import lol.november.feature.module.Register;
import lol.november.feature.setting.Setting;
import lol.november.listener.bus.Listener;
import lol.november.listener.bus.Subscribe;
import lol.november.listener.event.player.move.EventMove;
import lol.november.utility.player.MoveUtils;

/**
 * @author Gavin
 * @since 2.0.0
 */
@Register(
  name = "Flight",
  description = "Makes anti-cheats scratch their heads as to how you got across the map",
  category = Category.MOVEMENT
)
public class FlightModule extends Module {

  private final Setting<Mode> mode = new Setting<>("Mode", Mode.MOTION);

  private final Setting<Double> speed = new Setting<>(
    () -> mode.getValue() == Mode.MOTION,
    "Speed",
    0.5,
    0.01,
    0.0,
    5.0
  );

  @Subscribe
  private final Listener<EventMove> move = event -> {
    if (mode.getValue() == Mode.MOTION) {
      if (MoveUtils.moving()) MoveUtils.setSpeed(event, speed.getValue());

      if (mc.gameSettings.keyBindJump.pressed) {
        mc.thePlayer.motionY = speed.getValue();
      } else if (mc.gameSettings.keyBindSneak.pressed) {
        mc.thePlayer.motionY = -speed.getValue();
      } else {
        mc.thePlayer.motionY = 0.0;
      }
    }
  };

  private enum Mode {
    MOTION,
  }
}
