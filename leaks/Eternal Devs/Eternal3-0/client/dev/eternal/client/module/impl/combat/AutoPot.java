package dev.eternal.client.module.impl.combat;

import dev.eternal.client.event.Subscribe;
import dev.eternal.client.event.events.EventUpdate;
import dev.eternal.client.module.Module;
import dev.eternal.client.module.api.ModuleInfo;
import dev.eternal.client.property.impl.BooleanSetting;
import dev.eternal.client.property.impl.EnumSetting;
import dev.eternal.client.property.impl.NumberSetting;
import dev.eternal.client.property.impl.interfaces.INameable;
import dev.eternal.client.util.movement.MovementUtil;
import dev.eternal.client.util.movement.data.Rotation;
import dev.eternal.client.util.network.PacketUtil;
import dev.eternal.client.util.time.Stopwatch;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.block.BlockGlass;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.BlockPos;

import java.util.List;

@ModuleInfo(name = "AutoPot", description = "Automatically throws splash potions", category = Module.Category.COMBAT)
public class AutoPot extends Module {

  @Getter
  @Setter
  private boolean isPotting;
  private final Object[] badPotionData = {false};
  private final Stopwatch timer = new Stopwatch();

  private final EnumSetting<Mode> enumValue = new EnumSetting<>(this, "Mode", AutoPot.Mode.values());
  private final BooleanSetting skywars = new BooleanSetting(this, "Skywars", true);
  private final NumberSetting minHealth = new NumberSetting(this, "Min Health", 12, 5, 20, 1);
  private final NumberSetting delay = new NumberSetting(this, "Delay", 150, 50, 1000, 1);

  private boolean invCheck() {
    for (int i = 0; i < 45; ++i) {
      if (!mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()
          || getPotionData(mc.thePlayer.inventoryContainer.getSlot(i).getStack()) == badPotionData)
        continue;
      Object[] dataArray = getPotionData(mc.thePlayer.inventoryContainer.getSlot(i).getStack());
      int data = (int) dataArray[1];
      if (data == Potion.heal.id)
        return mc.thePlayer.getHealth() <= minHealth.value().floatValue();
      if ((data == Potion.regeneration.id || data == Potion.moveSpeed.id) && !mc.thePlayer.isPotionActive(data))
        return true;
    }
    return false;
  }

  private void swap(int slot) {
    mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, slot, 1, 2, mc.thePlayer);
  }

  private Object[] getPotionData(ItemStack stack) {
    if (stack == null || !(stack.getItem() instanceof ItemPotion itemPotion) || !ItemPotion.isSplash(stack.getMetadata())) {
      return badPotionData;
    }
    List<PotionEffect> potionEffectList = itemPotion.getEffects(stack);
    for (PotionEffect potionEffect : potionEffectList) {
      if (potionEffect.getPotionID() == Potion.heal.id || potionEffect.getPotionID() == Potion.regeneration.id || potionEffect.getPotionID() == Potion.moveSpeed.id) {
        return new Object[]{true, potionEffect.getPotionID(), stack};
      }
    }
    return badPotionData;
  }

  @Subscribe
  public void onUpdate(EventUpdate event) {
    if (!mc.thePlayer.onGround || skywars.value() && mc.theWorld.getBlockState(new BlockPos(mc.thePlayer).down()).getBlock() instanceof BlockGlass)
      return;
    if (event.pre()) {
      if (timer.hasElapsed(400L + delay.value().longValue()) && invCheck()) {
        useMode(event);
        refillPotions();
        isPotting(true);
        timer.reset();
        PacketUtil.sendSilent(new C09PacketHeldItemChange(1));
      }
    } else if (isPotting()) {
      MovementUtil.setMotion(0);
      PacketUtil.sendSilent(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
      PacketUtil.sendSilent(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
      isPotting(false);
    }
  }

  private void useMode(EventUpdate event) {
    Rotation rotation = event.rotation();
    switch (enumValue.value()) {
      case DOWN -> rotation.rotationPitch(90F);
      case UP -> rotation.rotationPitch(-90F);
      case JUMP -> {
        if (MovementUtil.isMovingOnGround()) {
          mc.thePlayer.motionY = MovementUtil.getJumpMotion(0.42F);
        }
        rotation.rotationPitch(-90F);
      }
      case INSTANT_JUMP -> {
        if (MovementUtil.isMovingOnGround()) {
          mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + MovementUtil.getJumpMotion(0.42F), mc.thePlayer.posZ);
        }
        rotation.rotationPitch(-90F);
      }
    }
  }

  private void refillPotions() {
    if (invCheck()) {
      for (int i = 0; i < 45; ++i) {
        Slot slot = mc.thePlayer.inventoryContainer.getSlot(i);
        if (!slot.getHasStack())
          continue;
        if (getPotionData(slot.getStack()) != badPotionData) {
          swap(i);
          break;
        }
      }
    }
  }

  @Override
  public String getSuffix() {
    return enumValue.value().getName();
  }

  @Getter
  public enum Mode implements INameable {
    DOWN("Down"), UP("Up"), JUMP("Jump"), INSTANT_JUMP("Instant Jump");
    private final String getName;

    Mode(String name) {
      this.getName = name;
    }
  }
}
