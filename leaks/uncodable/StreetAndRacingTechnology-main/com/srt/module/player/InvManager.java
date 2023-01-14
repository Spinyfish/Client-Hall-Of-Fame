package com.srt.module.player;

import com.srt.events.Event;
import com.srt.events.listeners.EventUpdate;
import com.srt.module.ModuleBase;
import com.srt.settings.settings.BooleanSetting;
import com.srt.settings.settings.NumberSetting;
import com.thunderware.utils.TimerUtils;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemGlassBottle;
import net.minecraft.item.ItemSkull;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;

import org.lwjgl.input.Keyboard;

public class InvManager extends ModuleBase {

	public TimerUtils timer = new TimerUtils();
	public TimerUtils hotkeyTimer = new TimerUtils();

	public NumberSetting delay = new NumberSetting("Delay", 50, 1, 5, 150);
	public BooleanSetting openInv = new BooleanSetting("OpenInv", true);

	public InvManager() {
		super("InvManager", Keyboard.KEY_H, Category.PLAYER);
		setDisplayName("Inventory Manager");
		addSettings(delay);
		addSettings(openInv);
	}
	
	public void onEvent(Event e) {
		setSuffix(Math.floor(delay.getValue() * 10) / 10 + "");
		if(e instanceof EventUpdate) {
			if (openInv.getValue())
				if (!(mc.currentScreen instanceof GuiInventory))
					return;

			for (int i = 9; i < 45; ++i) {
				if (!mc.thePlayer.inventoryContainer.getSlot(i).getHasStack())
					continue;

				ItemStack stackInSlot = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
				Item iteminSlot = stackInSlot.getItem();
				Slot slot = mc.thePlayer.inventoryContainer.getSlot(i);
				if(timer.hasReached(delay.getValue())){
					if (isBad(iteminSlot)) {
						dropItem(i);
					} else {
						if (iteminSlot.getUnlocalizedName().contains("sword") && i != 36) {
							if(mc.thePlayer.inventoryContainer.getSlot(36).getHasStack()) {
								if(iteminSlot.isItemTool(stackInSlot)) {
									if(getItemDamage(stackInSlot) < getItemDamage(mc.thePlayer.inventoryContainer.getSlot(36).getStack())) {
										dropItem(i);
									}else {
										dropItem(36);
									}
									continue;
								}
							}
							mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, i, 0, 0, mc.thePlayer);
							mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, 36, 0, 0, mc.thePlayer);
						}
						if (iteminSlot instanceof ItemArmor) {
							if (iteminSlot.getUnlocalizedName().contains("helmet") && !(iteminSlot instanceof ItemSkull)) {
								try { 
									if(!(mc.thePlayer.inventoryContainer.getSlot(5).getHasStack())) {
										try {
											if (((ItemArmor) iteminSlot).damageReduceAmount < ((ItemArmor) mc.thePlayer.inventoryContainer.getSlot(5).getStack().getItem()).damageReduceAmount) {
												dropItem(i);
											} else {
												dropItem(5);
											}
										} catch(NullPointerException e2) {
											dropItem(5);
											mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, i, 0, 0, mc.thePlayer);
											mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, 5, 0, 0, mc.thePlayer);
										}
										continue;
									}else if(mc.thePlayer.inventoryContainer.getSlot(5).getStack().getItem() instanceof ItemSkull) {
										dropItem(5);
									}
								} catch(ClassCastException e1){
									dropItem(5);
								}
								mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, i, 0, 0, mc.thePlayer);
								mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, 5, 0, 0, mc.thePlayer);
							}
							if (iteminSlot.getUnlocalizedName().contains("chestplate")) {
								if(mc.thePlayer.inventoryContainer.getSlot(6).getHasStack()) {
									if(((ItemArmor)iteminSlot).damageReduceAmount < ((ItemArmor)mc.thePlayer.inventoryContainer.getSlot(6).getStack().getItem()).damageReduceAmount) {
										dropItem(i);
									}else {
										dropItem(6);
									}
									continue;
								}
								mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, i, 0, 0, mc.thePlayer);
								mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, 6, 0, 0, mc.thePlayer);
							}
							if (iteminSlot.getUnlocalizedName().contains("leggings")) {
								if(mc.thePlayer.inventoryContainer.getSlot(7).getHasStack()) {
									if(((ItemArmor)iteminSlot).damageReduceAmount < ((ItemArmor)mc.thePlayer.inventoryContainer.getSlot(7).getStack().getItem()).damageReduceAmount) {
										dropItem(i);
									}else {
										dropItem(7);
									}
									continue;
								}
								mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, i, 0, 0, mc.thePlayer);
								mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, 7, 0, 0, mc.thePlayer);
							}
							if (iteminSlot.getUnlocalizedName().contains("boots")) {
								if(mc.thePlayer.inventoryContainer.getSlot(8).getHasStack()) {
									if(((ItemArmor)iteminSlot).damageReduceAmount < ((ItemArmor)mc.thePlayer.inventoryContainer.getSlot(8).getStack().getItem()).damageReduceAmount) {
										dropItem(i);
									}else {
										dropItem(8);
									}
									continue;
								}
								mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, i, 0, 0, mc.thePlayer);
								mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, 8, 0, 0, mc.thePlayer);
							}

							timer.reset();
						}
					}
				}
			}
		}
	}

	private boolean isBad(Item i) {
		return i.getUnlocalizedName().contains("stick") ||
				i.getUnlocalizedName().contains("string") ||
				i.getUnlocalizedName().contains("flint") ||
				i.getUnlocalizedName().contains("bucket") ||
				i.getUnlocalizedName().contains("feather") ||
				i.getUnlocalizedName().contains("snow") ||
				i.getUnlocalizedName().contains("piston") ||
				i instanceof ItemGlassBottle ||
				i.getUnlocalizedName().contains("web") ||
				i.getUnlocalizedName().contains("slime") ||
				i.getUnlocalizedName().contains("trip") ||
				i.getUnlocalizedName().contains("wire") ||
				i.getUnlocalizedName().contains("sugar") ||
				i.getUnlocalizedName().contains("note") ||
				i.getUnlocalizedName().contains("record") ||
				i.getUnlocalizedName().contains("flower") ||
				i.getUnlocalizedName().contains("wheat") ||
				i.getUnlocalizedName().contains("fishing") ||
				i.getUnlocalizedName().contains("boat") ||
				i.getUnlocalizedName().contains("leather") ||
				i.getUnlocalizedName().contains("seeds") ||
				i.getUnlocalizedName().contains("skull") ||
				i.getUnlocalizedName().contains("torch") ||
				i.getUnlocalizedName().contains("anvil") ||
				i.getUnlocalizedName().contains("enchant") ||
				i.getUnlocalizedName().contains("exp") ||
				i.getUnlocalizedName().contains("shears");
	}

	private void dropItem(int slot) {
		mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, slot, 0, 0, mc.thePlayer);
		mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, -999, 0, 0, mc.thePlayer);
	}

	private float getItemDamage(ItemStack itemStack) {
		if (itemStack == null) {
			return 0.0f;
		}
		if (!(itemStack.getItem() instanceof ItemSword)) {
			return 0.0f;
		}
		float damage = ((ItemSword)itemStack.getItem()).getDamageVsEntity();
		damage += EnchantmentHelper.getEnchantmentLevel(Enchantment.sharpness.effectId, itemStack) * 1.25f;
		damage += EnchantmentHelper.getEnchantmentLevel(Enchantment.fireAspect.effectId, itemStack) * 0.01f;
		return damage;
	}
}