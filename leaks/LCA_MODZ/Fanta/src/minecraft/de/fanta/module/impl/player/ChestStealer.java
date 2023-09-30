package de.fanta.module.impl.player;

import java.awt.Color;

import org.lwjgl.input.Keyboard;



import de.fanta.events.Event;
import de.fanta.events.listeners.EventTick;
import de.fanta.module.Module;
import de.fanta.setting.Setting;
import de.fanta.setting.settings.Slider;
import de.fanta.utils.ChatUtil;
import de.fanta.utils.TimeUtil;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.item.*;

public class ChestStealer extends Module {

	public ChestStealer() {
		super("Stealer", Keyboard.KEY_Q, Type.World, Color.RED);

		this.settings.add(new Setting("Delay", new Slider(0, 500, 1, 110)));

	}

	public static double dellay;
	TimeUtil time = new TimeUtil();

	@Override
	public void onEvent(Event event) {

//		if (mc.currentScreen instanceof GuiChest) {
//			if (!this.isContainerEmpty(mc.thePlayer.openContainer)) {
//				GuiChest guiChest = (GuiChest) mc.currentScreen;
//				boolean chestFull = true;
//
//				for (ItemStack stack : mc.thePlayer.inventory.mainInventory) {
//					if (stack != null)
//						continue;
//					chestFull = false;
//
//					break;
//				}

//			
//					for (int index = 0; index < guiChest.lowerChestInventory.getSizeInventory(); ++index) {
//						ItemStack stack = guiChest.lowerChestInventory.getStackInSlot(index);
//						if (stack == null || stack.getItem() instanceof ItemArmor && isBestArmor(stack) == false
//								|| stack.getItem() instanceof ItemSword && isBestSword(stack) == false) {
//							continue;
//						} else {
//							if (stack != null && stack.getItem() instanceof ItemArmor == false
//									&& stack.getItem() instanceof ItemSword == false
//									&& stack.getItem() instanceof ItemBlock == false
//									&& stack.getItem() instanceof ItemFood == false
//									&& stack.getItem() instanceof ItemTool == false) {
//								continue;
//							}
//						}
		if (event instanceof EventTick) {
			this.dellay = ((Slider) this.getSetting("Delay").getSetting()).curValue;

			if (mc.thePlayer.openContainer != null && mc.thePlayer.openContainer instanceof ContainerChest) {
				ContainerChest container = (ContainerChest) mc.thePlayer.openContainer;
				int i = 0;
				while (i < container.getLowerChestInventory().getSizeInventory()) {

					if (container.getLowerChestInventory().getStackInSlot(i) != null
							&& time.hasReached((long) dellay)) {

						mc.playerController.windowClick(container.windowId, i, 0, 1, mc.thePlayer);
						time.reset();
					}
					++i;
				}
				GuiChest chest = (GuiChest) mc.currentScreen;
				if (this.isChestEmpty(chest) || this.isInventoryFull()) {

					mc.thePlayer.closeScreen();

				}
			}
		}
	}

	// mc.displayGuiScreen(null);
	/*
	 * if (time.hasReached((int) ((Slider)
	 * this.getSetting("Delay").getSetting()).curValue)) { if (stack.getItem()
	 * instanceof ItemArmor && this.isBestArmor(stack)) {
	 * 
	 * mc.playerController.windowClick(guiChest.inventorySlots.windowId, index, 0,
	 * 1, mc.thePlayer); }
	 * 
	 * if (stack.getItem() instanceof ItemSword && this.isBestSword(stack)) {
	 * mc.playerController.windowClick(guiChest.inventorySlots.windowId, index, 0,
	 * 1, mc.thePlayer); }
	 * 
	 * if (stack.getItem() instanceof ItemBlock) {
	 * mc.playerController.windowClick(guiChest.inventorySlots.windowId, index, 0,
	 * 1, mc.thePlayer); } if (stack.getItem() instanceof ItemFood) {
	 * mc.playerController.windowClick(guiChest.inventorySlots.windowId, index, 0,
	 * 1, mc.thePlayer); }
	 * 
	 * if (stack.getItem() instanceof ItemTool) {
	 * mc.playerController.windowClick(guiChest.inventorySlots.windowId, index, 0,
	 * 1, mc.thePlayer); } time.reset(); }
	 */

//			}
//		}

	// }

	public boolean isContainerEmpty(Container container) {
		boolean temp = true;
		int slotAmount = container.inventorySlots.size() == 90 ? 54 : 27;
		for (int i = 0; i < slotAmount; ++i) {
			if (!container.getSlot(i).getHasStack())
				continue;
			temp = false;
		}
		return temp;
	}

	private boolean isBestSword(ItemStack input) {
		boolean best = true;

		for (int i = 9; i < 45; i++) {
			if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
				ItemStack stack = mc.thePlayer.inventoryContainer.getSlot(i).getStack();

				if (stack.getItem() instanceof ItemSword) {
					if (this.getWeaponStrength(stack) > this.getWeaponStrength(input)) {
						best = false;
					}
				}
			}
		}

		return best;
	}

	private boolean isBestArmor(ItemStack input) {
		boolean best = true;

		for (int i = 5; i < 45; i++) {
			if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
				ItemStack stack = mc.thePlayer.inventoryContainer.getSlot(i).getStack();

				if (stack.getItem() instanceof ItemArmor) {
					ItemArmor armor = (ItemArmor) stack.getItem();

					if (armor.armorType == ((ItemArmor) input.getItem()).armorType) {
						if (getProtection(stack) >= getProtection(input)) {
							best = false;
						}
					}
				}
			}
		}

		return best;
	}

	private double getProtection(ItemStack stack) {

		double protection = 0;

		if (stack.getItem() instanceof ItemArmor) {
			ItemArmor armor = (ItemArmor) stack.getItem();

			protection += armor.damageReduceAmount;
			protection += EnchantmentHelper.getEnchantmentLevel(Enchantment.protection.effectId, stack) * 0.25;
		}

		return protection;
	}

	private double getWeaponStrength(ItemStack stack) {

		double damage = 0;

		if (stack != null) {
			if (stack.getItem() instanceof ItemSword) {
				ItemSword sword = (ItemSword) stack.getItem();
				damage += sword.getDamageVsEntity();
			}

			if (stack.getItem() instanceof ItemTool) {
				ItemTool tool = (ItemTool) stack.getItem();
				damage += tool.getToolMaterial().getDamageVsEntity();
			}

			damage += EnchantmentHelper.getEnchantmentLevel(Enchantment.sharpness.effectId, stack) * 1.25;
		}

		return damage;
	}

	private boolean isValidItem(ItemStack itemStack) {
		return itemStack.getItem() instanceof ItemArmor || itemStack.getItem() instanceof ItemSword
				|| itemStack.getItem() instanceof ItemTool || itemStack.getItem() instanceof ItemFood
				|| itemStack.getItem() instanceof ItemPotion || itemStack.getItem() instanceof ItemBlock;
	}

	private boolean isChestEmpty(GuiChest chest) {
		for (int index = 0; index < chest.getLowerChestInventory().getSizeInventory(); ++index) {
			ItemStack stack = chest.getLowerChestInventory().getStackInSlot(index);
			if (stack != null && this.isValidItem(stack)) {
				return false;
			}
		}

		return true;
	}

	private boolean isInventoryFull() {
		for (int index = 9; index <= 44; ++index) {
			ItemStack stack = mc.thePlayer.inventoryContainer.getSlot(index).getStack();
			if (stack == null) {
				return false;
			}
		}

		return true;
	}
}
