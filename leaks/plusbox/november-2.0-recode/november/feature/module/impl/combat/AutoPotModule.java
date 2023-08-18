/* November.lol © 2023 */
package lol.november.feature.module.impl.combat;

import static lol.november.listener.bus.DefaultEventPriority.HIGH;

import java.util.List;
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
import lol.november.utility.player.InventoryUtils;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.server.S1DPacketEntityEffect;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

/**
 * @author Gavin
 * @since 2.0.0
 */
@Register(
  name = "AutoPot",
  description = "Automatically pots you",
  category = Category.COMBAT
)
public class AutoPotModule extends Module {

  private final Setting<Integer> delay = new Setting<>(
    "Delay",
    100,
    1,
    0,
    2500
  );
  private final Setting<Float> health = new Setting<>(
    "Health",
    14.0f,
    0.01f,
    1.0f,
    19.0f
  );
  private final Setting<Boolean> rotate = new Setting<>("Rotate", true);

  private final Timer timer = new Timer();
  private boolean waitingClientUpdate = false;
  private int potStage = -1;

  @Override
  public void disable() {
    super.disable();

    if (mc.thePlayer != null) {
      November.instance().inventory().sync();
    }

    waitingClientUpdate = false;
    potStage = -1;
  }

  @Subscribe
  private final Listener<EventUpdate> update = event -> {
    if (waitingClientUpdate) {
      return;
    }

    int slot = potSlot();
    if (slot == -1) {
      if (waitingClientUpdate || potStage != -1) November
        .instance()
        .inventory()
        .sync();

      potStage = -1;
      waitingClientUpdate = false;

      return;
    }

    if (rotate.getValue()) {
      November
        .instance()
        .rotations()
        .spoof(HIGH, 5, new float[] { mc.thePlayer.rotationYaw, 90.0f });
    }

    if (timer.passed(delay.getValue(), true)) {
      if (potStage <= 0) {
        potStage = 1;
        if (November.instance().inventory().slot() != slot) {
          mc.thePlayer.sendQueue.addToSendQueue(
            new C09PacketHeldItemChange(slot)
          );
        }
      } else if (potStage == 1) {
        potStage = 2;
        mc.thePlayer.sendQueue.addToSendQueue(
          new C08PacketPlayerBlockPlacement(
            November.instance().inventory().itemStack()
          )
        );
      } else if (potStage == 2) {
        if (!waitingClientUpdate) {
          November.instance().inventory().sync();
          potStage = 3;
          waitingClientUpdate = true;
        }
      }
    }
  };

  @Subscribe
  private final Listener<EventPacket.In> packetIn = event -> {
    if (mc.thePlayer == null) return;

    if (event.get() instanceof S1DPacketEntityEffect packet) {
      if (
        packet.getEntityId() == mc.thePlayer.getEntityId() &&
        waitingClientUpdate
      ) {
        waitingClientUpdate = false;
        potStage = -1;
        timer.reset();
        November.instance().inventory().sync();
      }
    }
  };

  public int potSlot() {
    for (int i = 0; i < 9; ++i) {
      ItemStack stack = mc.thePlayer.inventory.getStackInSlot(i);
      if (stack != null && stack.getItem() instanceof ItemPotion itemPotion) {
        if (!ItemPotion.isSplash(stack.getItemDamage())) continue;

        List<PotionEffect> effectList = itemPotion.getEffects(stack);
        for (PotionEffect effect : effectList) {
          Potion potion = Potion.potionTypes[effect.getPotionID()];

          if (
            mc.thePlayer.getHealth() <= health.getValue() &&
            potion.id == Potion.heal.id
          ) return i;

          if (
            InventoryUtils.validPotions.contains(potion) &&
            !mc.thePlayer.isPotionActive(potion) &&
            !potion.isInstant()
          ) return i;
        }
      }
    }

    return -1;
  }
}
