package markgg.modules.player;

import markgg.modules.Module.Category;
import markgg.settings.ModeSetting;
import markgg.settings.NumberSetting;
import markgg.util.InvUtil;
import markgg.util.Timer;
import net.minecraft.block.Block;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C0DPacketCloseWindow;
import net.minecraft.network.play.client.C16PacketClientStatus;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.input.Keyboard;

import com.google.common.base.Stopwatch;

import markgg.Client;
import markgg.events.Event;
import markgg.events.listeners.EventMotion;
import markgg.modules.Module;

public class InvManager extends Module{	

	private List<Integer> allSwords = new ArrayList<>();
	private List[] allArmors = new List[4];
	private List<Integer> trash = new ArrayList<>();
	private int bestSwordSlot;
	private int[] bestArmorSlot;
	private boolean cleaning;

	public NumberSetting delay = new NumberSetting("Delay", this, 250, 0, 1000, 50);
	public NumberSetting swordSlot = new NumberSetting("Sword Slot", this, 1, 1, 9, 1);
	public NumberSetting blockSlot = new NumberSetting("Block Slot", this, 2, 1, 9, 1);
	public Timer timer = new Timer();

	public InvManager() {
		super("InvManager", "Sorts your inventory", 0, Category.PLAYER);
		addSettings(delay, swordSlot, blockSlot);
	}

	public void onEvent(Event e) {
		if(e instanceof EventMotion && e.isPre()) {

			this.collectItems();
			this.collectTrash();
			this.collectBestArmor();

			int trashSize = this.trash.size();
			boolean trashPresent = (trashSize > 0);
			EntityPlayerSP player = mc.thePlayer;
			int windowId = player.openContainer.windowId;
			int bestSwordSlot = this.bestSwordSlot;

			if (this.mc.currentScreen instanceof GuiInventory) {
				if (trashPresent) {
					if (!this.cleaning) {
						this.cleaning = true;
						player.sendQueue.addToSendQueue(new C16PacketClientStatus(C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT));
					}

					for(int i = 0; i < trashSize; ++i) {
						int slot = (Integer)this.trash.get(i);
						if (this.checkDelay()) {
							break;
						}

						mc.playerController.windowClick(windowId, slot < 9 ? slot + 36 : slot, 1, 4, player);
						timer.reset();
					}

					if (this.cleaning) {
						player.sendQueue.addToSendQueue(new C0DPacketCloseWindow(windowId));
						this.cleaning = false;
					}
				}

				if (bestSwordSlot != -1 && !this.checkDelay()) {
					mc.playerController.windowClick(windowId, bestSwordSlot < 9 ? bestSwordSlot + 36 : bestSwordSlot, (int) (swordSlot.getValue() - 1), 2, player);
					timer.reset();
				}
			}
		}
	}

	private boolean checkDelay() {
		return !timer.hasTimeElapsed((long) delay.getValue(), true);
	}

	private void collectBestArmor() {
		int[] bestArmorDamageReducement = new int[4];
		this.bestArmorSlot = new int[4];
		Arrays.fill(bestArmorDamageReducement, -1);
		Arrays.fill(this.bestArmorSlot, -1);

		int i;
		ItemStack itemStack;
		ItemArmor armor;
		int armorType;
		for(i = 0; i < this.bestArmorSlot.length; ++i) {
			itemStack = mc.thePlayer.inventory.armorItemInSlot(i);
			this.allArmors[i] = new ArrayList();
			if (itemStack != null && itemStack.getItem() != null && itemStack.getItem() instanceof ItemArmor) {
				armor = (ItemArmor)itemStack.getItem();
				armorType = armor.damageReduceAmount + EnchantmentHelper.getEnchantmentModifierDamage(new ItemStack[]{itemStack}, DamageSource.generic);
				bestArmorDamageReducement[i] = armorType;
			}
		}

		for(i = 0; i < 36; ++i) {
			itemStack = mc.thePlayer.inventory.getStackInSlot(i);
			if (itemStack != null && itemStack.getItem() != null && itemStack.getItem() instanceof ItemArmor) {
				armor = (ItemArmor)itemStack.getItem();
				armorType = 3 - armor.armorType;
				this.allArmors[armorType].add(i);
				int slotProtectionLevel = armor.damageReduceAmount + EnchantmentHelper.getEnchantmentModifierDamage(new ItemStack[]{itemStack}, DamageSource.generic);
				if (bestArmorDamageReducement[armorType] < slotProtectionLevel) {
					bestArmorDamageReducement[armorType] = slotProtectionLevel;
					this.bestArmorSlot[armorType] = i;
				}
			}
		}

	}

	public void collectItems() {
		this.bestSwordSlot = -1;
		this.allSwords.clear();
		float bestSwordDamage = -1.0F;
		for (int i = 0; i < 36; i++) {
			ItemStack itemStack = mc.thePlayer.inventory.getStackInSlot(i);
			if (itemStack != null && itemStack.getItem() != null)
				if (itemStack.getItem() instanceof ItemSword) {
					float damageLevel = InvUtil.getDamageLevel(itemStack);
					this.allSwords.add(Integer.valueOf(i));
					if (bestSwordDamage < damageLevel) {
						bestSwordDamage = damageLevel;
						this.bestSwordSlot = i;
					} 
				}
		} 
	}

	private void collectTrash() {
		this.trash.clear();
		int i;
		for (i = 0; i < 36; i++) {
			ItemStack itemStack = mc.thePlayer.inventory.getStackInSlot(i);
			if (itemStack != null && itemStack.getItem() != null)
				if (!InvUtil.isValidItem(itemStack))
					this.trash.add(Integer.valueOf(i));  
		} 
		List<Integer> integers = this.trash;
		for (int j = 0, allSwordsSize = this.allSwords.size(); j < allSwordsSize; j++) {
			Integer slot = this.allSwords.get(j);
			if (slot.intValue() != this.bestSwordSlot)
				integers.add(slot); 
		} 
	}
}
