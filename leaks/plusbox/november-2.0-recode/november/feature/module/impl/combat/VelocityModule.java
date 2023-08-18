/* November.lol © 2023 */
package lol.november.feature.module.impl.combat;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import lol.november.feature.module.Category;
import lol.november.feature.module.Module;
import lol.november.feature.module.Register;
import lol.november.feature.setting.Setting;
import lol.november.listener.bus.Listener;
import lol.november.listener.bus.Subscribe;
import lol.november.listener.event.net.EventPacket;
import lol.november.listener.event.player.EventUpdate;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S32PacketConfirmTransaction;

/**
 * @author Gavin
 * @since 2.0.0
 */
@Register(
  name = "Velocity",
  description = "Reduces or cancels out taken velocity",
  category = Category.COMBAT
)
public class VelocityModule extends Module {

  private final Setting<Mode> mode = new Setting<>("Mode", Mode.CANCEL);

  private final Setting<Float> horizontal = new Setting<>(
    () -> mode.getValue() == Mode.REDUCE || mode.getValue() == Mode.REVERSE,
    "Horizontal",
    50.0f,
    0.01f,
    0.0f,
    200.0f
  );
  private final Setting<Float> vertical = new Setting<>(
    () -> mode.getValue() == Mode.REDUCE || mode.getValue() == Mode.REVERSE,
    "Vertical",
    50.0f,
    0.01f,
    0.0f,
    200.0f
  );

  private final Queue<Short> transactionQueue = new ConcurrentLinkedQueue<>();
  private boolean funny;

  @Override
  public void disable() {
    super.disable();

    funny = false;
    transactionQueue.clear();
  }

  @Subscribe
  private final Listener<EventUpdate> update = event -> {
    if (mode.getValue() == Mode.GRIM) {
      // finish trolling
      if (transactionQueue.isEmpty() && funny) funny = false;
    }
  };

  @Subscribe
  private final Listener<EventPacket.In> packetIn = event -> {
    if (mc.thePlayer == null || mc.theWorld == null) {
      // basic resets
      funny = false;
      if (!transactionQueue.isEmpty()) transactionQueue.clear();

      return;
    }

    if (event.get() instanceof S12PacketEntityVelocity packet) {
      if (packet.getEntityID() != mc.thePlayer.getEntityId()) return;

      switch (mode.getValue()) {
        case REDUCE, REVERSE -> {
          packet.setMotionX(
            (int) (packet.getMotionX() * (horizontal.getValue() / 100.0f))
          );
          packet.setMotionY(
            (int) (packet.getMotionY() * (vertical.getValue() / 100.0f))
          );
          packet.setMotionZ(
            (int) (packet.getMotionZ() * (horizontal.getValue() / 100.0f))
          );

          if (mode.getValue() == Mode.REVERSE) {
            packet.setMotionX(-packet.getMotionX());
            packet.setMotionY(-packet.getMotionY());
            packet.setMotionZ(-packet.getMotionZ());
          }
        }
        case CANCEL, GRIM, VULCAN -> {
          event.setCanceled(true);
          if (
            mode.getValue() == Mode.GRIM || mode.getValue() == Mode.VULCAN
          ) funny = true;
        }
        case INTAVE -> {
          // TODO: i have no clue
        }
        case WATCHDOG -> {
          // TODO: frappy or plus add this i dont have alts
        }
        case WATCHDOG_SAFE -> {
          event.setCanceled(true);

          if (mc.thePlayer.onGround) {
            // as far as im aware, this should work
            mc.thePlayer.motionY += (double) packet.getMotionY() / 8000.0;
          }
        }
      }
    } else if (event.get() instanceof S32PacketConfirmTransaction packet) {
      if (mode.getValue() == Mode.GRIM) {
        if (!funny) return;
        event.setCanceled(true);
        transactionQueue.add(packet.getActionNumber());
      }
    }
  };

  @Subscribe
  private final Listener<EventPacket.Out> packetOut = event -> {
    if (event.get() instanceof C0FPacketConfirmTransaction packet) {
      if (mode.getValue() == Mode.GRIM) {
        if (!funny || transactionQueue.isEmpty()) return;

        // if the queue contains our transaction UID, cancel
        if (transactionQueue.remove(packet.getUid())) event.setCanceled(true);
      } else if (mode.getValue() == Mode.VULCAN) {
        // this works on older versions of vulcan
        if (funny) {
          event.setCanceled(true);
          funny = false;
        }
      }
    }
  };

  private enum Mode {
    REDUCE,
    REVERSE,
    CANCEL,
    GRIM,
    INTAVE,
    VULCAN,
    WATCHDOG,
    WATCHDOG_SAFE,
  }
}
