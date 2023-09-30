package axolotl.cheats.modules.impl.player;

import axolotl.cheats.events.Event;
import axolotl.cheats.events.EventType;
import axolotl.cheats.events.EventUpdate;
import axolotl.cheats.modules.Module;
import axolotl.cheats.settings.NumberSetting;
import axolotl.util.ItemUtil;
import axolotl.util.Timer;
import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;

public class AutoArmor extends Module {
	   Timer timer = new Timer();
	   public NumberSetting DelayArmor = new NumberSetting("Delay", 100.0F, 0.0F, 1000.0F, 50.0F);

	   public AutoArmor() {
	      super("AutoArmor", Category.PLAYER, true);
	      this.addSettings(this.DelayArmor);
		   this.setSpecialSetting(DelayArmor);
	   }

	   public boolean isChestInventory() {
	      return Minecraft.getMinecraft().thePlayer.openContainer != null && Minecraft.getMinecraft().thePlayer.openContainer instanceof ContainerChest;
	   }

	   public static int checkProtection(ItemStack var0) {
	      return EnchantmentHelper.getEnchantmentLevel(0, var0);
	   }

	   public void onEvent(Event var1) {
	      if (var1 instanceof EventUpdate && !this.isChestInventory() && var1.eventType == EventType.PRE) {
	         for(int var2 = 0; var2 < 36; ++var2) {
	            ItemStack var3 = this.mc.thePlayer.inventory.getStackInSlot(var2);
	            if (var3 != null && var3.getItem() instanceof ItemArmor) {
	               ItemArmor var4 = (ItemArmor)this.mc.thePlayer.inventory.getStackInSlot(var2).getItem();
	               int var5 = 0;
	               int var6 = 0;
	               int var7 = 0;
	               if (this.mc.thePlayer.inventory.getStackInSlot(39 - var4.armorType) != null) {
	                  ItemArmor var8 = (ItemArmor)this.mc.thePlayer.inventory.getStackInSlot(39 - var4.armorType).getItem();
	                  ItemStack var9 = this.mc.thePlayer.inventory.getStackInSlot(39 - var4.armorType);
	                  var5 = var8.getArmorMaterial().getDamageReductionAmount(var4.armorType);
	                  var5 += checkProtection(this.mc.thePlayer.inventory.getStackInSlot(39 - var4.armorType));
	                  var6 = var9.getItemDamage();
	                  var7 = var4.getArmorMaterial().getDamageReductionAmount(var4.armorType);
	                  var7 += checkProtection(this.mc.thePlayer.inventory.getStackInSlot(var2));
	               }

	               if (ItemUtil.getFreeSlot() != -1 && this.mc.thePlayer.inventory.getStackInSlot(39 - var4.armorType) != null && (var7 > var5 || var7 == var5 && var3.getItemDamage() < var6)) {
	                  if (var2 < 9) {
	                     var2 += 36;
	                  }

	                  this.mc.playerController.windowClick(this.mc.thePlayer.inventoryContainer.windowId, 5 + var4.armorType, 0, 4, this.mc.thePlayer);
	                  this.mc.playerController.windowClick(this.mc.thePlayer.inventoryContainer.windowId, var2, 0, 1, this.mc.thePlayer);
	               }

	               if (this.mc.thePlayer.inventory.getStackInSlot(39 - var4.armorType) == null && this.timer.hasTimeElapsed((long)this.DelayArmor.getNumberValue(), true)) {
	                  if (var2 < 9) {
	                     var2 += 36;
	                  }

	                  this.mc.playerController.windowClick(this.mc.thePlayer.inventoryContainer.windowId, var2, 0, 1, this.mc.thePlayer);
	               }
	            }
	         }
	      }

	   }
	}