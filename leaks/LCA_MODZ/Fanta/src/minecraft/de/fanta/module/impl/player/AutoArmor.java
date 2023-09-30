package de.fanta.module.impl.player;

import java.awt.Color;

import de.fanta.events.Event;
import de.fanta.events.listeners.EventTick;
import de.fanta.module.Module;
import de.fanta.setting.Setting;
import de.fanta.setting.settings.Slider;
import de.fanta.utils.TimeUtil;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;

public class AutoArmor extends Module {

	public AutoArmor() {
		super("AutoArmor", 0, Type.Player, Color.YELLOW);
		this.settings.add(new Setting("Delay", new Slider(1, 600, 1, 200)));
	}

	public static boolean needArmor = false;

	private int[] bestArmor;

	TimeUtil time = new TimeUtil();

	private int[] boots;

	private int[] chestplate;

	private int[] helmet;

	private int[] leggings;

	@Override
	public void onEvent(Event e) {
		this.boots = new int[] { 313, 309, 317, 305, 301 };
		this.chestplate = new int[] { 311, 307, 315, 303, 299 };
		this.helmet = new int[] { 310, 306, 314, 302, 298 };
		this.leggings = new int[] { 312, 308, 316, 304, 300 };
		if (mc.currentScreen instanceof GuiInventory)
			for (int i = 5; i < 9; i++) {
				needArmor = true;
				ItemStack stack = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
				int value = getProtection(stack);
				int type = i - 5;
				int bestSlot = -1;
				int highestValue = 0;
				for (int inv = 9; inv < 45; inv++) {
					ItemStack invStx = mc.thePlayer.inventoryContainer.getSlot(inv).getStack();
					if (invStx != null && invStx.getItem() instanceof ItemArmor) {
						ItemArmor armour = (ItemArmor) invStx.getItem();
						int armourProtection = armour.damageReduceAmount
								+ EnchantmentHelper.getEnchantmentLevel(Enchantment.protection.effectId, invStx);
						if (armour.armorType == type && armourProtection > value && armourProtection > highestValue) {
							highestValue = armourProtection;
							bestSlot = inv;
						}
					}
					if (inv == 45) {
						needArmor = false;
					}
				}
				if (bestSlot != -1) {
					if (time.hasReached((long) ((Slider) this.getSetting("Delay").getSetting()).curValue)) {
						if (stack != null) {
							if (e instanceof EventTick) {
								this.mc.playerController.windowClick(0, i, 1, 4, mc.thePlayer);
							}
						}
						if (e instanceof EventTick) {
							this.mc.playerController.windowClick(0, bestSlot, 0, 1, mc.thePlayer);
						}
						time.reset();
					}
				}
			}
	}

	private int getProtection(ItemStack stack) {
		if (stack != null && stack.getItem() instanceof ItemArmor) {
			int normalValue = ((ItemArmor) stack.getItem()).damageReduceAmount;
			int enchantmentValue = EnchantmentHelper.getEnchantmentLevel(Enchantment.protection.effectId, stack);
			return normalValue + enchantmentValue;
		}
		return -1;
	}

}