/* November.lol © 2023 */
package lol.november.feature.module.impl.ghost;

import lol.november.feature.module.Category;
import lol.november.feature.module.Module;
import lol.november.feature.module.Register;
import lol.november.feature.setting.Setting;
import lol.november.listener.bus.Listener;
import lol.november.listener.bus.Subscribe;
import lol.november.listener.event.net.EventPacket;
import lol.november.listener.event.player.EventUpdate;
import lol.november.utility.math.timer.Timer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C0BPacketEntityAction;

/**
 * @author Gavin
 * @since 2.0.0
 */
@Register(
  name = "WTap",
  description = "Automatically sprint resets",
  category = Category.GHOST
)
public class WTapModule extends Module {

  private final Setting<Mode> mode = new Setting<>("Mode", Mode.LEGIT);
  private final Setting<Double> delay = new Setting<>(
    "Delay",
    250.0,
    0.5,
    0.0,
    5000.0
  );
  private final Setting<Double> holdDelay = new Setting<>(
    () -> mode.getValue() == Mode.LEGIT,
    "Hold Delay",
    250.0,
    0.5,
    0.0,
    5000.0
  );

  private final Timer timer = new Timer();
  private final Timer holdTimer = new Timer();

  private boolean canWTap = true;

  @Override
  public void disable() {
    super.disable();

    if (mc.thePlayer != null && !canWTap) {
      legitSprint(true);
    }

    canWTap = true;
  }

  @Subscribe
  private final Listener<EventUpdate> update = event -> {
    if (holdTimer.passed(holdDelay.getValue().longValue()) && !canWTap) {
      canWTap = true;
      legitSprint(true);
    }
  };

  @Subscribe
  private final Listener<EventPacket.Out> packetOut = event -> {
    if (event.get() instanceof C02PacketUseEntity packet) {
      if (
        packet.getAction() != C02PacketUseEntity.Action.ATTACK ||
        !(
          packet.getEntityFromWorld(
            mc.theWorld
          ) instanceof EntityLivingBase living
        ) ||
        !mc.thePlayer.isSprinting()
      ) return;

      if (!timer.passed(delay.getValue().longValue())) return;

      if (mode.getValue() == Mode.SILENT) {
        canWTap = true;
        mc.thePlayer.sendQueue.addToSendQueue(
          new C0BPacketEntityAction(
            mc.thePlayer,
            C0BPacketEntityAction.Action.STOP_SPRINTING
          )
        );
        mc.thePlayer.sendQueue.addToSendQueue(
          new C0BPacketEntityAction(
            mc.thePlayer,
            C0BPacketEntityAction.Action.START_SPRINTING
          )
        );
      } else if (mode.getValue() == Mode.LEGIT) {
        if (canWTap) {
          holdTimer.reset();
          legitSprint(false);
          canWTap = false;
        }
      }
    }
  };

  private void legitSprint(boolean state) {
    mc.gameSettings.keyBindSprint.pressed = state;
    mc.thePlayer.setSprinting(state);
  }

  private enum Mode {
    LEGIT,
    SILENT,
  }
}
