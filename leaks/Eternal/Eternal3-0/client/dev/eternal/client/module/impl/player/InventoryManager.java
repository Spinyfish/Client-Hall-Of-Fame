package dev.eternal.client.module.impl.player;

import dev.eternal.client.event.Subscribe;;
import dev.eternal.client.event.events.EventUpdate;
import dev.eternal.client.module.Module;
import dev.eternal.client.module.api.ModuleInfo;
import dev.eternal.client.property.impl.BooleanSetting;
import dev.eternal.client.property.impl.NumberSetting;
import dev.eternal.client.util.inventory.InventoryUtil;
import dev.eternal.client.util.time.Stopwatch;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.init.Items;
import net.minecraft.inventory.Slot;
import net.minecraft.item.*;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Tuple;
import org.lwjgl.input.Keyboard;

import java.util.*;

/**
 * @author Jinthium (?) (from dortware)
 */
@ModuleInfo(name = "InvManager", description = "Cleans your inventory", category = Module.Category.PLAYER, defaultKey = Keyboard.KEY_I)
public class InventoryManager extends Module {

  public static final Comparator<Slot> DAMAGE_REDUCTION_COMPARATOR =
      Comparator.comparingDouble(o -> getDamageReductionAmount(o.getStack()));
  private final List<Slot> SLOTS = new ArrayList<>();
  private final int FAVOURED_SLOT = 0;
  private final Stopwatch timer = new Stopwatch();

  private final BooleanSetting openInventory = new BooleanSetting(this, "Open Inventory", "Will only run whilst in inventory.", true);
  private final NumberSetting currentDelay = new NumberSetting(this, "Delay", "Delay between items in milliseconds.", 40, 0, 200, 5);

  public static boolean doesPlayerNeedItem(Slot slot) {
    if (slot.slotNumber == 36) return true;
    if (!slot.getHasStack()) return false;
    return doesPlayerNeedItem(slot.getStack());
  }

  private static Tuple<ItemStack, Float> getBestDamageItem() {
    final ArrayList<Slot> slots = new ArrayList<>();
    for (Slot inventorySlot : mc.thePlayer.inventoryContainer.inventorySlots) {
      if (inventorySlot.getHasStack()) slots.add(inventorySlot);
    }

    if (slots.isEmpty()) return new Tuple<>(null, 0.0F);

    slots.sort(Comparator.comparingDouble(value -> -getDamageAgainstPlayer(value.getStack())));

    final Slot bestSlot = slots.get(0);
    final int currentBestSlot = bestSlot.slotNumber;

    final Slot foundBestSlot = mc.thePlayer.inventoryContainer.getSlot(currentBestSlot);
    final ItemStack itemStack = foundBestSlot.getStack();

    return new Tuple<>(itemStack, getDamageAgainstPlayer(itemStack));
  }

  public static float getDamageReductionAmount(ItemStack itemStack) {
    if (itemStack == null || !(itemStack.getItem() instanceof final ItemArmor armor)) return 0.0F;

    return armor.getArmorMaterial().getDamageReductionAmount(armor.armorType)
        + EnchantmentHelper.getEnchantmentModifierDamage(
        new ItemStack[] {itemStack}, DamageSource.generic);
  }

  public static boolean doesPlayerNeedItem(ItemStack stack) {
    final Tuple<ItemStack, Float> bestStack = getBestDamageItem();
    final Item item = stack.getItem();

    if (item instanceof ItemTool || item instanceof ItemSword) {
      if (stack == bestStack.getFirst() || getDamageAgainstPlayer(stack) > bestStack.getSecond())
        return true;
    }

    final String displayName = stack.getDisplayName();

    if (displayName.contains("§")) return true;

    if (item instanceof ItemArmor) {
      final ItemArmor armor = (ItemArmor) item;
      final ItemStack current =
          mc.thePlayer.inventoryContainer.getSlot(armor.armorType + 5).getStack();

      if (current == null || getDamageReductionAmount(current) < getDamageReductionAmount(stack)) {
        return true;
      }
    }

    return item instanceof ItemBlock
        || item instanceof ItemFood
        || item instanceof ItemEnderPearl
        || item instanceof ItemPotion
        || item instanceof ItemBow
        || item instanceof ItemFishingRod
        || item == Items.arrow;
  }

  public static float getDamageAgainstPlayer(ItemStack itemStack) {
    float damage =
        (float)
            mc.thePlayer
                .getEntityAttribute(SharedMonsterAttributes.attackDamage)
                .getAttributeValue();
    if (itemStack == null) return damage;

    float enchantmentModifier =
        EnchantmentHelper.getModifierForCreature(itemStack, mc.thePlayer.getCreatureAttribute());
    final Item item = itemStack.getItem();

    if (item instanceof ItemTool) {
      final ItemTool tool = (ItemTool) item;
      damage = 2f + tool.getToolMaterial().getDamageVsEntity();
    } else if (item instanceof ItemSword) {
      final ItemSword sword = (ItemSword) item;
      // base damage is wooden sword
      damage = 4f + sword.getDamageVsEntity();
    }

    return damage + enchantmentModifier;
  }

  public static float getEfficiencyModifier(ItemStack itemStack) {
    if (itemStack == null
        || itemStack.getItem() == null
        || !(itemStack.getItem() instanceof ItemTool)) return 0F;

    final ItemTool tool = (ItemTool) itemStack.getItem();
    final int efficiencyModifier =
        EnchantmentHelper.getEnchantmentLevel(Enchantment.efficiency.effectId, itemStack);

    final float totalEfficiency =
        tool.getToolMaterial().getEfficiencyOnProperMaterial() + efficiencyModifier;
    return -totalEfficiency;
  }

  @Override
  public void onDisable() {
    super.onDisable();
  }

  @Override
  public void onEnable() {
    super.onEnable();
  }

  @Subscribe
  public void onMotion(EventUpdate eventUpdate) {
    boolean flag =
        (!openInventory.value() && !(mc.currentScreen instanceof GuiContainer)
            || mc.currentScreen instanceof GuiInventory);
    if (!flag) return;
    SLOTS.clear();
    for (var slot : mc.thePlayer.inventoryContainer.inventorySlots) {
      if (slot.getHasStack()) SLOTS.add(slot);
    }
    if (SLOTS.isEmpty()) return;
    sortSword();
    sortBestArmor();
    cleanInventory();
  }

  private void sortBestArmor() {
    for (int armorType = 0; armorType <= 3; armorType++) {
      final Slot bestSlot = getBestArmor(armorType);
      if (bestSlot == null || bestSlot.slotNumber <= 8) continue;

      final ItemStack current = mc.thePlayer.inventoryContainer.getSlot(armorType + 5).getStack();
      final ItemStack toPutOn = bestSlot.getStack();

      final float currentDamageReduction = getDamageReductionAmount(current);
      final float toPutOnDamageReduction = getDamageReductionAmount(toPutOn);

      if (currentDamageReduction < toPutOnDamageReduction && toPutOnDamageReduction > 0) {
        if (current != null
            && (current.getItem() != null
            && toPutOn.getItem() != null
            && toPutOn.getItem() instanceof ItemArmor
            && toPutOn != current)) {
          click(0, armorType + 5, 1, 4);
        } else {
          SLOTS.remove(bestSlot);
          click(0, bestSlot.slotNumber, 0, 1);
        }
      }
    }
  }

  private Slot getBestArmor(int armorType) {
    boolean seen = false;
    Slot best = null;

    for (Slot slot : SLOTS) {
      if (slot.getHasStack()
          && slot.getStack().getItem() instanceof ItemArmor
          && ((ItemArmor) slot.getStack().getItem()).armorType == armorType) {
        if (!seen || DAMAGE_REDUCTION_COMPARATOR.compare(slot, best) > 0) {
          seen = true;
          best = slot;
        }
      }
    }

    if (best != null) {
      SLOTS.remove(best);
      return best;
    }
    return null;
  }

  public void sortSword() {
    SLOTS.stream()
        .min(Comparator.comparingDouble(value -> -getDamageAgainstPlayer(value.getStack())))
        .ifPresentOrElse(
            bestSlot -> {
              var requiredBestSlot = 35 + FAVOURED_SLOT;
              var currentBestSlot = bestSlot.slotNumber;
              var playerSlot = mc.thePlayer.inventoryContainer.getSlot(requiredBestSlot + 1);
              var foundBestSlot = mc.thePlayer.inventoryContainer.getSlot(currentBestSlot);
              float currentDamage = getDamageAgainstPlayer(playerSlot.getStack());
              float bestDamage = getDamageAgainstPlayer(foundBestSlot.getStack());

              if (requiredBestSlot + 1 != currentBestSlot
                  && bestDamage > currentDamage
                  && bestDamage != 0) {
                SLOTS.remove(bestSlot);
                click(0, currentBestSlot, FAVOURED_SLOT, 2);
              }
            },
            () -> {});
  }

  private void cleanInventory() {
    for (int i = SLOTS.size() - 1; i > 0; i--) {
      final Slot slot = SLOTS.get(i);

      if (!doesPlayerNeedItem(slot)) {
        SLOTS.remove(slot);
        click(0, slot.slotNumber, 1, 4);
      }
    }
  }

  private void click(int windowId, int slotId, int mouseButton, int mode) {
    click(windowId, slotId, mouseButton, mode, this.currentDelay.value().intValue());
  }

  public boolean click(int windowID, int slotID, int mouseButton, int mode, int clickDelay) {
    if (timer.hasElapsed(Math.round(clickDelay), true)) {
      mc.playerController.windowClick(windowID, slotID, mouseButton, mode, mc.thePlayer);
      return true;
    }
    return false;
  }
}