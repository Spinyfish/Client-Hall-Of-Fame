package dev.eternal.client.module.impl.combat;

import dev.eternal.client.event.Subscribe;
import dev.eternal.client.Client;
import dev.eternal.client.event.events.EventUpdate;
import dev.eternal.client.module.Module;
import dev.eternal.client.module.api.ModuleInfo;
import dev.eternal.client.property.impl.NumberSetting;
import dev.eternal.client.util.time.Stopwatch;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;

@ModuleInfo(name = "AutoArmor", description = "Automatically equips armor.", category = Module.Category.COMBAT)
public class AutoArmor extends Module {

  private final NumberSetting delay = new NumberSetting(this, "Delay", "Delay between equipping armor.",150, 25, 500, 25);

  private final Stopwatch timer = new Stopwatch();

  @Subscribe
  public void onUpdate(EventUpdate event) {
    if (mc.currentScreen instanceof GuiInventory) {
      if (!Client.singleton().moduleManager().<AutoPot>getByClass(AutoPot.class).isPotting()) {
        equipArmor(timer, delay.value().intValue());
      }
    }
  }

  private void equipArmor(Stopwatch stopwatch, int delay) {
    for (int i = 9; i < 45; i++) {
      if (!mc.thePlayer.inventoryContainer.getSlot(i).getHasStack())
        continue;

      ItemStack stackInSlot = mc.thePlayer.inventoryContainer.getSlot(i).getStack();

      if (!(stackInSlot.getItem() instanceof ItemArmor))
        continue;

      if (getArmorItemsEquipSlot(stackInSlot, false) == -1)
        continue;

      if (mc.thePlayer.getEquipmentInSlot(getArmorItemsEquipSlot(stackInSlot, true)) == null) {
        if (stopwatch.hasElapsed(delay)) {
          mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, i, 0, 0,
              mc.thePlayer);
          mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId,
              getArmorItemsEquipSlot(stackInSlot, false), 0, 0, mc.thePlayer);

          stopwatch.reset();
          return;
        }
      } else {
        ItemStack stackInEquipmentSlot = mc.thePlayer
            .getEquipmentInSlot(getArmorItemsEquipSlot(stackInSlot, true));
        if (compareProtection(stackInSlot, stackInEquipmentSlot) == stackInSlot) {
          System.out.println("Stack in slot : " + stackInSlot.getUnlocalizedName());
          if (stopwatch.hasElapsed(delay)) {
            mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, i, 0, 0,
                mc.thePlayer);
            mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId,
                getArmorItemsEquipSlot(stackInSlot, false), 0, 0, mc.thePlayer);
            mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, i, 0, 0,
                mc.thePlayer);
            stopwatch.reset();
            return;
          }
        }
      }
    }
  }

  private int getArmorItemsEquipSlot(ItemStack stack, boolean equipmentSlot) {
    if (stack.getUnlocalizedName().contains("helmet"))
      return equipmentSlot ? 4 : 5;
    if (stack.getUnlocalizedName().contains("chestplate"))
      return equipmentSlot ? 3 : 6;
    if (stack.getUnlocalizedName().contains("leggings"))
      return equipmentSlot ? 2 : 7;
    if (stack.getUnlocalizedName().contains("boots"))
      return equipmentSlot ? 1 : 8;
    return -1;
  }

  private ItemStack compareProtection(ItemStack item1, ItemStack item2) {
    if (!(item1.getItem() instanceof ItemArmor) && !(item2.getItem() instanceof ItemArmor))
      return null;

    if (!(item1.getItem() instanceof ItemArmor))
      return item2;

    if (!(item2.getItem() instanceof ItemArmor))
      return item1;

    if (getArmorProtection(item1) > getArmorProtection(item2)) {
      return item1;
    } else if (getArmorProtection(item2) > getArmorProtection(item1)) {
      return item2;
    }

    return null;
  }

  private double getArmorProtection(ItemStack armorStack) {
    if (!(armorStack.getItem() instanceof final ItemArmor armorItem))
      return 0.0;

    final double protectionLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.protection.effectId, armorStack);

    return armorItem.damageReduceAmount + ((6 + protectionLevel * protectionLevel) * 0.75 / 3);

  }

  public String getSuffix() {
    return Integer.toString(delay.value().intValue());
  }
}
