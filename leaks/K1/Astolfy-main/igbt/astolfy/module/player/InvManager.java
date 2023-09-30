package igbt.astolfy.module.player;

import java.util.ArrayList;

import igbt.astolfy.events.Event;
import igbt.astolfy.events.listeners.EventUpdate;
import igbt.astolfy.module.ModuleBase;
import igbt.astolfy.settings.settings.NumberSetting;
import igbt.astolfy.utils.TimerUtils;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;

public class InvManager extends ModuleBase {

	public NumberSetting moveDelay = new NumberSetting("Delay", 50, 1, 5, 150);

	public TimerUtils timer = new TimerUtils();
	public int sec = 0;
	
	public InvManager() {
		super("InvManager", 0, Category.PLAYER);
		addSettings(moveDelay);
	}
	ArrayList<Slot> sortedSwords;

	Slot lastSwordSlot = null;
	public void onEvent(Event event) {
		int swords = 0;
		if(event instanceof EventUpdate && mc.currentScreen instanceof GuiInventory) {
			for(Slot s : mc.thePlayer.inventoryContainer.inventorySlots) {
				if(s.getStack() != null && timer.hasReached(moveDelay.getValue())) {
					Slot fuckYou = s;
					if(s.getStack().getItem() instanceof ItemSword) {
						//System.out.println(swordCompare(((ItemSword)s.getStack().getItem()),(ItemSword)lastSwordSlot));
						
						if(lastSwordSlot != null && lastSwordSlot.getStack() != null
								&& s != lastSwordSlot && lastSwordSlot.getStack().getItem() instanceof ItemSword) {

							if(swordCompare((ItemSword)s.getStack().getItem(), (ItemSword)lastSwordSlot.getStack().getItem(), s.getStack(),lastSwordSlot.getStack())) {
								dropItem(s);

								timer.reset();
							}
						}else {
							moveItem(s.slotNumber, 0);
							this.lastSwordSlot = s;
						}
					}
					else
					if(isBad(s.getStack()))
						dropItem(s);
				}
			}
		}
	}

	public float swordMaterial(ItemSword sword, ItemStack stack) {
		float mat = 0;
		switch(sword.getToolMaterialName()) {
		
		case "WOOD":
			mat = 0;
			break;
		case "GOLD":
			mat = 1;
			break;
		case "STONE":
			mat = 1;
			break;
		case "IRON":
			mat = 2;
			break;
		case "EMERALD":
			mat = 3;
			break;
		}
		mat+= EnchantmentHelper.getEnchantmentLevel(Enchantment.sharpness.effectId, stack) * 1.25f;
		return mat;
	}
	
	public boolean swordCompare(ItemSword s1, ItemSword s2, ItemStack st1, ItemStack st2) {
		return swordMaterial(s1, st1) <= swordMaterial(s2, st2);
	}
	
	public void moveItem(int slot, int nextSlot) {
		mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, slot, nextSlot, 2, mc.thePlayer);
	}

	public void dropItem(Slot slot) {
		mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, slot.slotNumber, 1, 4, mc.thePlayer);
	}
	
	private boolean isBad(ItemStack stack) {
		Item i = stack.getItem();
		return !(i instanceof ItemBlock
				|| i instanceof ItemSword || i instanceof ItemTool
				|| i instanceof ItemFood || i instanceof ItemArmor);
	}
	
}
