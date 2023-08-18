/* November.lol © 2023 */
package lol.november.feature.module.impl.player;

import lol.november.November;
import lol.november.feature.module.Category;
import lol.november.feature.module.Module;
import lol.november.feature.module.Register;
import lol.november.feature.setting.Setting;
import lol.november.listener.bus.Listener;
import lol.november.listener.bus.Subscribe;
import lol.november.listener.event.net.EventPacket;
import lol.november.listener.event.player.EventUpdate;
import lol.november.utility.math.timer.Timer;
import net.minecraft.event.ClickEvent;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.network.play.server.S45PacketTitle;
import net.minecraft.util.IChatComponent;

/**
 * @author Gavin
 * @since 2.0.0
 */
@Register(
  name = "AutoPlay",
  description = "Automatically plays the next game for you",
  category = Category.PLAYER
)
public class AutoPlayModule extends Module {

  private final Setting<Mode> mode = new Setting<>("Mode", Mode.HYPIXEL);
  private final Setting<Double> delay = new Setting<>(
    "Delay",
    1.5,
    0.01,
    0.0,
    5.0
  );

  private final Timer timer = new Timer();
  private String playCommand;
  private int paperSlot;

  @Override
  public void disable() {
    super.disable();
    playCommand = null;

    if (mode.getValue() == Mode.BLOCKS_MC && paperSlot != -1) {
      November.instance().inventory().sync();
    }

    paperSlot = -1;
  }

  @Subscribe
  private final Listener<EventUpdate> update = event -> {
    if (mc.thePlayer.ticksExisted < 10) {
      paperSlot = -1;
      playCommand = null;
      return;
    }

    if (mode.getValue() == Mode.HYPIXEL || mode.getValue() == Mode.MUSH_MC) {
      if (
        playCommand == null ||
        !timer.passed(delay.getValue().longValue() * 1000L)
      ) return;

      timer.reset();
      mc.thePlayer.sendQueue.addToSendQueueSilent(
        new C01PacketChatMessage(playCommand)
      );
    } else if (mode.getValue() == Mode.BLOCKS_MC) {
      if (paperSlot == -1) {
        for (int i = 0; i < 9; ++i) {
          ItemStack itemStack = mc.thePlayer.inventory.getStackInSlot(i);
          if (itemStack != null && itemStack.getItem() == Items.paper) {
            paperSlot = i;
            timer.reset();
            break;
          }
        }

        return;
      }

      if (!timer.passed(delay.getValue().longValue() * 1000L)) return;

      timer.reset();

      mc.thePlayer.sendQueue.addToSendQueueSilent(
        new C09PacketHeldItemChange(paperSlot)
      );
      mc.thePlayer.sendQueue.addToSendQueueSilent(
        new C08PacketPlayerBlockPlacement(
          November.instance().inventory().itemStack()
        )
      );
      November.instance().inventory().sync();
    }
  };

  @Subscribe
  private final Listener<EventPacket.In> packetIn = event -> {
    if (mode.getValue() == Mode.HYPIXEL) {
      if (event.get() instanceof S02PacketChat packet) {
        if (packet.getChatComponent() == null) return;

        String raw = packet.getChatComponent().getUnformattedText();
        if (
          raw.contains("You won! Want to play again? Click here! ") ||
          raw.contains("You died! Want to play again? Click here! ")
        ) {
          timer.reset();

          for (IChatComponent component : packet
            .getChatComponent()
            .getSiblings()) {
            if (
              component.getUnformattedText().contains("Click here!") &&
              component.getChatStyle().getChatClickEvent() != null
            ) {
              ClickEvent clickEvent = component
                .getChatStyle()
                .getChatClickEvent();
              if (clickEvent.getAction() == ClickEvent.Action.RUN_COMMAND) {
                playCommand = clickEvent.getValue();
                timer.reset();
                return;
              }
            }
          }
        }
      } else if (event.get() instanceof S45PacketTitle packet) {
        if (packet.getMessage() == null) return;
      }
    }
  };

  private enum Mode {
    HYPIXEL,
    BLOCKS_MC,
    MUSH_MC,
  }
}
