/* November.lol © 2023 */
package lol.november.feature.module.impl.player;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import lol.november.feature.module.Category;
import lol.november.feature.module.Module;
import lol.november.feature.module.Register;
import lol.november.feature.setting.Setting;
import lol.november.listener.bus.Listener;
import lol.november.listener.bus.Subscribe;
import lol.november.listener.event.Stage;
import lol.november.listener.event.net.EventPacket;
import lol.november.listener.event.player.EventUpdate;
import lol.november.listener.event.player.move.EventWalkingUpdate;
import net.minecraft.init.Blocks;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.BlockPos;

/**
 * @author Gavin
 * @since 2.0.0
 */
@Register(
  name = "AntiVoid",
  description = "Prevents you from falling into the void",
  category = Category.PLAYER
)
public class AntiVoidModule extends Module {

  private static final double GROUND = 1.0 / 64.0;

  private final Setting<Mode> mode = new Setting<>("Mode", Mode.SETBACK);
  private final Setting<Boolean> voidCheck = new Setting<>("Void Check", true);
  private final Setting<Float> distance = new Setting<>(
    "Fall Distance",
    3.0f,
    0.01f,
    3.0f,
    20.0f
  );

  private final Queue<Packet<?>> packetQueue = new ConcurrentLinkedQueue<>();

  @Subscribe
  private final Listener<EventUpdate> update = event -> {
    if (mode.getValue() == Mode.WATCHDOG && packetQueue.size() > 120) {
      // idek if this works

      mc.thePlayer.fallDistance = 0.0f;

      while (!packetQueue.isEmpty()) {
        mc.thePlayer.sendQueue.addToSendQueueSilent(packetQueue.poll());
      }
      mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer());
    }
  };

  @Subscribe
  private final Listener<EventWalkingUpdate> walkingUpdate = event -> {
    if (event.getStage() != Stage.PRE) return;

    if (
      mc.thePlayer.fallDistance < distance.getValue() ||
      (!aboveVoid() && voidCheck.getValue())
    ) return;

    switch (mode.getValue()) {
      case SETBACK -> event.setY(event.getY() + 1);
      case VULCAN -> {
        event.setY(event.getY() + (event.getY() % GROUND));
        event.setOnGround(true);
      }
      case JUMP -> mc.thePlayer.jump();
    }

    mc.thePlayer.fallDistance = 0.0f;
  };

  @Subscribe
  private final Listener<EventPacket.Out> packetIn = event -> {
    if (event.get() instanceof C03PacketPlayer packet) {
      if (mode.getValue() != Mode.WATCHDOG) return;

      if (aboveVoid() && mc.thePlayer.fallDistance > 0.1f) {
        event.setCanceled(true);
        packetQueue.add(packet);
      }
    }
  };

  private boolean aboveVoid() {
    for (int y = (int) Math.floor(mc.thePlayer.posY); y > 0; --y) {
      BlockPos pos = new BlockPos(mc.thePlayer.posX, y, mc.thePlayer.posZ);
      if (mc.theWorld.getBlockState(pos).getBlock() != Blocks.air) return false;
    }
    return true;
  }

  private enum Mode {
    SETBACK,
    JUMP,
    VULCAN,
    WATCHDOG,
  }
}
