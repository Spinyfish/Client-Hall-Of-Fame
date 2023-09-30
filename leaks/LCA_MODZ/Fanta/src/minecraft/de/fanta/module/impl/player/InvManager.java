package de.fanta.module.impl.player;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import de.fanta.Client;
import de.fanta.events.Event;
import de.fanta.events.listeners.EventPreMotion;
import de.fanta.events.listeners.EventTick;
import de.fanta.module.Module;
import de.fanta.module.impl.combat.Killaura;
import de.fanta.setting.Setting;
import de.fanta.setting.settings.CheckBox;
import de.fanta.setting.settings.Slider;
import de.fanta.utils.TimeUtil;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.network.play.client.C0BPacketEntityAction;

public class InvManager extends Module {
	public InvManager() {
		super("InvManager", 0, Type.Player, Color.cyan);
		this.settings.add(new Setting("PrevSwords", new CheckBox(false)));
		this.settings.add(new Setting("OpenInv", new CheckBox(false)));
		this.settings.add(new Setting("FakeInv", new CheckBox(false)));
		this.settings.add(new Setting("Delay", new Slider(1, 1000, 1, 60)));
	}

	TimeUtil time = new TimeUtil();
	private static int[] bestArmorDamageReducement;

	private int[] bestArmorSlots;
	private float bestSwordDamage;
	private int bestSwordSlot;
	private List<Integer> trash = new ArrayList<>();
	private boolean canFake;
	public static double DelayY;

	@Override
	public void onEvent(Event event) {
		if (event instanceof EventTick) {
			searchForItems();

			for (int i = 0; i < 4; i++) {
				if (bestArmorSlots[i] != -1) {
					int bestSlot = bestArmorSlots[i];
					DelayY = ((Slider) this.getSetting("Delay").getSetting()).curValue;
					ItemStack oldArmor = mc.thePlayer.inventory.armorItemInSlot(i);
					if (((CheckBox) this.getSetting("OpenInv").getSetting()).state
							&& mc.currentScreen instanceof GuiInventory
							&& !((CheckBox) this.getSetting("FakeInv").getSetting()).state)
						if (oldArmor != null && oldArmor.getItem() != null) {
							if (time.hasReached((long) DelayY)) {
								mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, 8 - i, 0, 1,
										mc.thePlayer);
								time.reset();
							}
						}
					if (((CheckBox) this.getSetting("FakeInv").getSetting()).state) {
						if (canFakeInv()) {
							mc.thePlayer.sendQueue.addToSendQueue(new C0BPacketEntityAction(mc.thePlayer,
									C0BPacketEntityAction.Action.OPEN_INVENTORY));
							if (oldArmor != null && oldArmor.getItem() != null) {
								if (time.hasReached((long) DelayY)) {
									mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, 8 - i, 0,
											1, mc.thePlayer);
									time.reset();
								}
							}
						}
					}

					if (((CheckBox) this.getSetting("OpenInv").getSetting()).state
							&& mc.currentScreen instanceof GuiInventory
							&& !((CheckBox) this.getSetting("FakeInv").getSetting()).state)
						if (time.hasReached((long) DelayY)) {
							mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId,
									bestSlot < 9 ? bestSlot + 36 : bestSlot, 0, 1, mc.thePlayer);
							time.reset();
						}
					if (((CheckBox) this.getSetting("FakeInv").getSetting()).state) {
						if (canFakeInv()) {
							mc.thePlayer.sendQueue.addToSendQueue(new C0BPacketEntityAction(mc.thePlayer,
									C0BPacketEntityAction.Action.OPEN_INVENTORY));
							if (time.hasReached((long) DelayY)) {
								mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId,
										bestSlot < 9 ? bestSlot + 36 : bestSlot, 0, 1, mc.thePlayer);
								time.reset();
							}
						}
					}
				}
			}

			if (((CheckBox) this.getSetting("OpenInv").getSetting()).state && mc.currentScreen instanceof GuiInventory
					&& !((CheckBox) this.getSetting("FakeInv").getSetting()).state)
				if (bestSwordSlot != -1 && bestSwordDamage != -1) {
					if (time.hasReached((long) DelayY)) {
						mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId,
								bestSwordSlot < 9 ? bestSwordSlot + 36 : bestSwordSlot, 0, 2, mc.thePlayer); // mouse
																												// button
																												// clicked
																												// =
																												// slot
																												// of
																												// hotbar
						time.reset();
					}
				}
			if (((CheckBox) this.getSetting("FakeInv").getSetting()).state) {
				if (canFakeInv()) {
					mc.thePlayer.sendQueue.addToSendQueue(
							new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.OPEN_INVENTORY));
					if (bestSwordSlot != -1 && bestSwordDamage != -1) {
						if (time.hasReached((long) DelayY)) {
							mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId,
									bestSwordSlot < 9 ? bestSwordSlot + 36 : bestSwordSlot, 0, 2, mc.thePlayer); // mouse
																													// button
																													// clicked
																													// =
																													// slot
																													// of
																													// hotbar
							time.reset();
						}
					}
				}
			}

			searchForTrash();
			Collections.shuffle(trash);
			if (((CheckBox) this.getSetting("OpenInv").getSetting()).state && mc.currentScreen instanceof GuiInventory
					&& !((CheckBox) this.getSetting("FakeInv").getSetting()).state)
				for (Integer integer : trash) {
					if (time.hasReached((long) DelayY)) {
						mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId,
								integer < 9 ? integer + 36 : integer, 0, 4, mc.thePlayer); // mode 4 = drop all
						time.reset();
					}
				}
			if (((CheckBox) this.getSetting("FakeInv").getSetting()).state) {
				if (canFakeInv()) {
					mc.thePlayer.sendQueue.addToSendQueue(
							new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.OPEN_INVENTORY));
					for (Integer integer : trash) {
						if (time.hasReached((long) DelayY)) {
							mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId,
									integer < 9 ? integer + 36 : integer, 0, 4, mc.thePlayer); // mode 4 = drop all
							time.reset();
						}
					}
				}
			}
		}
	}

	public boolean canFakeInv() {
		return !mc.thePlayer.isUsingItem() && !mc.thePlayer.isEating() && mc.currentScreen == null
				 && !mc.gameSettings.keyBindUseItem.isKeyDown()
				&& !mc.gameSettings.keyBindAttack.isKeyDown() && !mc.gameSettings.keyBindJump.isKeyDown()
				&& mc.thePlayer.swingProgress == 0.0;
	}

	private void searchForItems() {
		bestArmorDamageReducement = new int[4];
		bestArmorSlots = new int[4];
		bestSwordDamage = -1;
		bestSwordSlot = -1;

		Arrays.fill(bestArmorDamageReducement, -1);
		Arrays.fill(bestArmorSlots, -1);

		for (int i = 0; i < bestArmorSlots.length; i++) {
			ItemStack itemStack = mc.thePlayer.inventory.armorItemInSlot(i);

			if (itemStack != null && itemStack.getItem() != null) {
				if (itemStack.getItem() instanceof ItemArmor) {
					ItemArmor armor = (ItemArmor) itemStack.getItem();

					bestArmorDamageReducement[i] = armor.damageReduceAmount;
				}
			}
		}
		for (int i = 0; i < 9 * 4; i++) { // 9 rows 4 columns
			ItemStack itemStack = mc.thePlayer.inventory.getStackInSlot(i);

			if (itemStack == null || itemStack.getItem() == null)
				continue;

			if (itemStack.getItem() instanceof ItemArmor) {
				ItemArmor armor = (ItemArmor) itemStack.getItem();

				int armorType = 3 - armor.armorType; // The armor Types are inverted

				if (bestArmorDamageReducement[armorType] < armor.damageReduceAmount) {
					bestArmorDamageReducement[armorType] = armor.damageReduceAmount;
					bestArmorSlots[armorType] = i;
				}
			}
			if (itemStack.getItem() instanceof ItemSword) {
				ItemSword sword = (ItemSword) itemStack.getItem();

				if (bestSwordDamage < sword.getDamageVsEntity() + EnchantmentHelper.getEnchantmentLevel(Enchantment.sharpness.effectId, itemStack)) {
					bestSwordDamage = sword.getDamageVsEntity() + EnchantmentHelper.getEnchantmentLevel(Enchantment.sharpness.effectId, itemStack);
					bestSwordSlot = i;
				}
			}
			if (itemStack.getItem() instanceof ItemTool) {
				ItemTool sword = (ItemTool) itemStack.getItem();

				float damage = sword.getToolMaterial().getDamageVsEntity() + EnchantmentHelper.getEnchantmentLevel(Enchantment.sharpness.effectId, itemStack);
				try {
					if (((CheckBox) this.getSetting("PrevSwords").getSetting()).state)
						damage -= 1.0f;

					if (bestSwordDamage < damage) {
						bestSwordDamage = damage;
						bestSwordSlot = i;
					}
				} catch (NullPointerException e) {

				}
			}

		}
	}

	private void searchForTrash() {
		trash.clear();
		bestArmorDamageReducement = new int[4];
		bestArmorSlots = new int[4];
		bestSwordDamage = -1;
		bestSwordSlot = -1;

		Arrays.fill(bestArmorDamageReducement, -1);
		Arrays.fill(bestArmorSlots, -1);

		List<Integer>[] allItems = new List[4];

		List<Integer> allSwords = new ArrayList<>();

		for (int i = 0; i < bestArmorSlots.length; i++) {
			ItemStack itemStack = mc.thePlayer.inventory.armorItemInSlot(i);

			allItems[i] = new ArrayList<>();

			if (itemStack != null && itemStack.getItem() != null) {
				if (itemStack.getItem() instanceof ItemArmor) {
					ItemArmor armor = (ItemArmor) itemStack.getItem();

					bestArmorDamageReducement[i] = armor.damageReduceAmount;
					bestArmorSlots[i] = 8 + i;
				}
			}
		}
		for (int i = 0; i < 9 * 4; i++) { // 9 rows 4 columns
			ItemStack itemStack = mc.thePlayer.inventory.getStackInSlot(i);

			if (itemStack == null || itemStack.getItem() == null)
				continue;

			if (itemStack.getItem() instanceof ItemArmor) {
				ItemArmor armor = (ItemArmor) itemStack.getItem();

				int armorType = 3 - armor.armorType; // The armor Types are inverted

				allItems[armorType].add(i);

				if (bestArmorDamageReducement[armorType] < armor.damageReduceAmount) {
					bestArmorDamageReducement[armorType] = armor.damageReduceAmount;
					bestArmorSlots[armorType] = i;
				}
			}
			if (itemStack.getItem() instanceof ItemSword) {
				ItemSword sword = (ItemSword) itemStack.getItem();

				allSwords.add(i);

				if (bestSwordDamage < sword.getDamageVsEntity() + EnchantmentHelper.getEnchantmentLevel(Enchantment.sharpness.effectId, itemStack)) {
					bestSwordDamage = sword.getDamageVsEntity() + EnchantmentHelper.getEnchantmentLevel(Enchantment.sharpness.effectId, itemStack);
					bestSwordSlot = i;
				}
			}
			if (itemStack.getItem() instanceof ItemTool) {
				ItemTool sword = (ItemTool) itemStack.getItem();

				float damage = sword.getToolMaterial().getDamageVsEntity() + EnchantmentHelper.getEnchantmentLevel(Enchantment.sharpness.effectId, itemStack);
				try {
					if (((CheckBox) this.getSetting("PrevSwords").getSetting()).state)
						damage -= 1.0f;

					if (bestSwordDamage < damage) {
						bestSwordDamage = damage;
						bestSwordSlot = i;
					}
				} catch (NullPointerException e) {

				}
			}
		}
		for (int i = 0; i < allItems.length; i++) {
			List<Integer> allItem = allItems[i];
			int finalI = i;
			allItem.stream().filter(slot -> slot != bestArmorSlots[finalI]).forEach(trash::add);
		}
		allSwords.stream().filter(slot -> slot != bestSwordSlot).forEach(trash::add);

	}

}
