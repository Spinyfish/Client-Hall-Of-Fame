/* November.lol © 2023 */
package lol.november.feature.module.impl.movement;

import lol.november.feature.module.Category;
import lol.november.feature.module.Module;
import lol.november.feature.module.Register;
import lol.november.feature.setting.Setting;
import lol.november.listener.bus.Listener;
import lol.november.listener.bus.Subscribe;
import lol.november.listener.event.input.EventItemSlowdown;
import lol.november.listener.event.player.EventUpdate;
import lol.november.utility.net.PacketUtils;
import net.minecraft.item.*;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;

/**
 * @author Gavin, Frap
 * @since 2.0.0
 */
@Register(
  name = "NoSlowDown",
  description = "Removes slowdown from using items",
  category = Category.MOVEMENT
)
public class NoSlowDownModule extends Module {

  private final Setting<Mode> mode = new Setting<>("Mode", Mode.VANILLA);
  private final Setting<Boolean> swords = new Setting<>("Swords", true);
  private final Setting<Boolean> consumables = new Setting<>(
    "Consumables",
    true
  );
  private final Setting<Boolean> bows = new Setting<>("Bows", true);

  @Subscribe
  private final Listener<EventItemSlowdown> itemSlowdown = event -> {
    // revert the *= 0.2f to strafe & forward moment that vanilla minecraft does

    if (blockNoslow()) return;

    if (mode.getValue() == Mode.GRIM) {
      // this could probably be made faster but...
      if (mc.thePlayer.ticksExisted % 2 == 0) {
        event.input().moveForward *= 5.0f;
        event.input().moveStrafe *= 5.0f;
      }

      return;
    }

    event.input().moveForward *= 5.0f;
    event.input().moveStrafe *= 5.0f;
  };

  @Subscribe
  private final Listener<EventUpdate> update = event -> {
    if (
      !mc.thePlayer.isUsingItem() || mc.thePlayer.isRiding() || blockNoslow()
    ) return;

    switch (mode.getValue()) {
      case NEW_NCP -> {
        if (mc.thePlayer.isBlocking()) {
          PacketUtils.repeated(new C08PacketPlayerBlockPlacement(null), 2);
        } else {
          int clientSlot = mc.thePlayer.inventory.currentItem;
          mc.thePlayer.sendQueue.addToSendQueue(
            new C09PacketHeldItemChange((clientSlot + 1) % 9)
          );
          mc.thePlayer.sendQueue.addToSendQueue(
            new C09PacketHeldItemChange(clientSlot)
          );
        }
      }
      case WATCHDOG -> {
        if (mc.thePlayer.isBlocking()) {
          int clientSlot = mc.thePlayer.inventory.currentItem;
          mc.thePlayer.sendQueue.addToSendQueue(
            new C09PacketHeldItemChange((clientSlot + 1) % 9)
          );
          mc.thePlayer.sendQueue.addToSendQueue(
            new C09PacketHeldItemChange(clientSlot)
          );
          mc.thePlayer.sendQueue.addToSendQueue(
            new C08PacketPlayerBlockPlacement(null)
          );
        }
      }
    }
  };

  private boolean blockNoslow() {
    if (!mc.thePlayer.isUsingItem()) return true;

    ItemStack using = mc.thePlayer.getItemInUse();
    if (using == null) return true;

    // idk wtf intellij spewed but i aint questioning
    return (
      (
        !consumables.getValue() &&
        (
          using.getItem() instanceof ItemFood ||
          using.getItem() instanceof ItemPotion
        )
      ) ||
      (!swords.getValue() && using.getItem() instanceof ItemSword) ||
      (!bows.getValue() && using.getItem() instanceof ItemBow)
    );
  }

  private enum Mode {
    VANILLA,
    NEW_NCP,
    WATCHDOG,
    GRIM,
  }
}
