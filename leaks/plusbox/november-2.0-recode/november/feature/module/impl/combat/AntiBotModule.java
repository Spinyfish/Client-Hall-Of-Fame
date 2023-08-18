/* November.lol © 2023 */
package lol.november.feature.module.impl.combat;

import io.netty.util.internal.ConcurrentSet;
import java.util.Set;
import lol.november.feature.module.Category;
import lol.november.feature.module.Module;
import lol.november.feature.module.Register;
import lol.november.feature.setting.Setting;
import lol.november.listener.bus.Listener;
import lol.november.listener.bus.Subscribe;
import lol.november.listener.event.net.EventPacket;
import lol.november.listener.event.player.EventUpdate;
import lol.november.utility.chat.Printer;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.S0CPacketSpawnPlayer;
import net.minecraft.network.play.server.S13PacketDestroyEntities;

/**
 * @author Gavin
 * @since 2.0.0
 */
@Register(
  name = "AntiBot",
  description = "Prevents you from attacking bots",
  category = Category.COMBAT
)
public class AntiBotModule extends Module {

  private final Setting<Mode> mode = new Setting<>("Mode", Mode.WATCHDOG);

  private final Setting<Integer> tabTicks = new Setting<>(
    () -> mode.getValue() == Mode.TAB_TICKS,
    "Tab Ticks",
    50,
    1,
    0,
    500
  );
  private final Setting<Boolean> noTab = new Setting<>(
    () -> mode.getValue() == Mode.NPC,
    "No Tab List",
    true
  );

  private final Set<Integer> bots = new ConcurrentSet<>();
  private int aboveEntityId = -1;
  private boolean aboveDespawn;

  @Override
  public void disable() {
    super.disable();
    bots.clear();
    aboveEntityId = -1;
    aboveDespawn = false;
  }

  @Subscribe
  private final Listener<EventUpdate> update = event -> {
    if (mc.thePlayer.ticksExisted < 5) {
      bots.clear();

      if (mode.getValue() == Mode.WATCHDOG) {
        aboveEntityId = -1;
        aboveDespawn = false;
      }

      return;
    }

    switch (mode.getValue()) {
      case WATCHDOG -> {
        // todo i have no clue

        for (EntityPlayer player : mc.theWorld.playerEntities) {
          if (player == null || player.equals(mc.thePlayer)) continue;

          if (!tabList(player) && player.isInvisible()) {
            bots.add(player.getEntityId());
          }
        }

        if (aboveEntityId != -1) return;
        for (int bot : bots) {
          Entity entity = mc.theWorld.getEntityByID(bot);
          if (!(entity instanceof EntityPlayer)) {
            bots.remove(bot);
            continue;
          }

          if (Math.abs(entity.posY - mc.thePlayer.posY) <= 10.0) {
            aboveEntityId = entity.getEntityId();
            Printer.print(
              "Detected bot above you: " +
              aboveEntityId +
              " (" +
              entity.getName() +
              ")"
            );
            break;
          }
        }
      }
      case TAB_TICKS -> {
        for (EntityPlayer player : mc.theWorld.playerEntities) {
          if (player == null || player.equals(mc.thePlayer)) continue;

          if (
            player.ticksExisted > tabTicks.getValue() && !tabList(player)
          ) bots.add(player.getEntityId());
        }
      }
      case NPC -> {
        for (EntityPlayer player : mc.theWorld.playerEntities) {
          if (player == null || player.equals(mc.thePlayer)) continue;
          String name = player.getGameProfile().getName();

          if (
            name.startsWith("CIT-") ||
            name.contains("[NPC]") ||
            name.contains("\u00A7") ||
            (!tabList(player) && noTab.getValue())
          ) bots.add(player.getEntityId());
        }
      }
    }
  };

  @Subscribe
  private final Listener<EventPacket.In> packetIn = event -> {
    if (mode.getValue() == Mode.WATCHDOG) {
      if (event.get() instanceof S13PacketDestroyEntities packet) {
        for (int entityId : packet.getEntityIDs()) {
          if (entityId == aboveEntityId) {
            Printer.print(entityId + " was removed from the world. Staff?");
            aboveEntityId = -1;
          }
        }
      } else if (event.get() instanceof S0CPacketSpawnPlayer packet) {
        if (!aboveDespawn) return;

        bots.add(packet.getEntityID());
        Printer.print("Suspicious entity spawn: " + packet.getEntityID());
      }
    }
  };

  private boolean tabList(EntityPlayer player) {
    for (NetworkPlayerInfo info : mc.thePlayer.sendQueue.getPlayerInfoMap()) {
      if (
        info
          .getGameProfile()
          .getName()
          .equals(player.getGameProfile().getName())
      ) return true;
    }

    return false;
  }

  public boolean bot(int entityId) {
    return bots.contains(entityId);
  }

  private enum Mode {
    TAB_TICKS,
    NPC,
    WATCHDOG,
  }
}
