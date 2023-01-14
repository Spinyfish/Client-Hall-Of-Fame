package com.srt.module.player;

import org.lwjgl.input.Keyboard;

import com.srt.events.Event;
import com.srt.events.listeners.EventPacket;
import com.srt.events.listeners.EventUpdate;
import com.srt.module.ModuleBase;
import com.srt.settings.settings.NumberSetting;
import com.thunderware.utils.TimerUtils;

import net.minecraft.inventory.ContainerChest;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGlassBottle;
import net.minecraft.network.play.client.C03PacketPlayer;

public class ChestStealer extends ModuleBase {

	public TimerUtils timer = new TimerUtils();
	
	public NumberSetting delay = new NumberSetting("Delay", 50, 1, 5, 150);
	
	public ChestStealer() {
		super("ChestStealer", Keyboard.KEY_G, Category.PLAYER);
		setDisplayName("Chest Stealer");
		addSettings(delay);
	}
	
	public void onEvent(Event e) {
		if(e instanceof EventUpdate) {
			setSuffix(Math.floor(delay.getValue() * 10) / 10 + "");
			if(mc.thePlayer == null)
				return;
			if(mc.thePlayer.openContainer != null && mc.thePlayer.openContainer instanceof ContainerChest) {
				ContainerChest chest = (ContainerChest) mc.thePlayer.openContainer;
				int items = 0;
				for (int i = 0; i < chest.getLowerChestInventory().getSizeInventory(); i++) {
					if(chest.getLowerChestInventory().getStackInSlot(i) != null && !isBad(chest.getLowerChestInventory().getStackInSlot(i).getItem())) {
						items++;
					}
				}
				for (int i = 0; i < chest.getLowerChestInventory().getSizeInventory(); i++) {
					if(chest.getLowerChestInventory().getStackInSlot(i) != null && !isBad(chest.getLowerChestInventory().getStackInSlot(i).getItem()) && timer.hasReached(delay.getValue())) {
						mc.playerController.windowClick(chest.windowId, i, 0, 1, mc.thePlayer);
						timer.reset();
					}
				}
				if(items == 0) {
					mc.thePlayer.closeScreen();
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
}
