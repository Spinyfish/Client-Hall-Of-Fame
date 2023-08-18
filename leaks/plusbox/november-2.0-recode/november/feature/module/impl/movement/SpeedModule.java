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
  name = "Speed",
  description = "Makes you go faster",
  category = Category.MOVEMENT
)
public class SpeedModule extends Module {

  private final Setting<Mode> mode = new Setting<>("Mode", Mode.BHOP);

  private final Setting<Double> speed = new Setting<>(
    () -> mode.getValue() == Mode.BHOP,
    "Speed",
    0.5,
    0.01,
    0.1,
    5.0
  );
  private final Setting<Boolean> jump = new Setting<>(
    () -> mode.getValue() == Mode.STRAFE,
    "Jump",
    true
  );

  @Subscribe
  private final Listener<EventMove> move = event -> {

    switch (mode.getValue()) {
      case BHOP -> {
        if (MoveUtils.moving()) {
          if (mc.thePlayer.onGround) {
            mc.thePlayer.motionY = 0.42f;
            event.setY(mc.thePlayer.motionY);
          }

          MoveUtils.setSpeed(event, speed.getValue());
        }
      }

      case STRAFE -> {
        if (MoveUtils.moving()) {
          if (jump.getValue() && mc.thePlayer.onGround) {
            mc.thePlayer.motionY = 0.42f;
            event.setY(mc.thePlayer.motionY);

            MoveUtils.setSpeed(event, MoveUtils.speed() + 0.2);
          }

          MoveUtils.setSpeed(event, MoveUtils.speed());
        }
      }
    }
  };

  private enum Mode {
    BHOP,
    STRAFE,
  }
}
