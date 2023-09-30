package axolotl.cheats.modules.impl.player;

import axolotl.Axolotl;
import axolotl.cheats.events.EventType;
import axolotl.cheats.events.EventUpdate;

import axolotl.cheats.events.Event;
import axolotl.cheats.modules.Module;
import axolotl.util.ItemUtil;
import net.minecraft.item.*;

public class InvManager extends Module {

	public InvManager() {
		super("InvManager", Category.PLAYER, true);
	}
	
	public void onEvent(Event e) {
		
		if(e instanceof EventUpdate && e.eventType == EventType.POST && !((AutoArmor) Axolotl.INSTANCE.moduleManager.getModule("AutoArmor")).isChestInventory()) {
			if(mc.currentScreen == null)return;
			ItemTool[] bestTools = ItemUtil.getBestTools();
			ItemAxe bestAxe = (ItemAxe)bestTools[0];
			ItemPickaxe bestPickaxe = (ItemPickaxe)bestTools[1];
			ItemSpade bestShovel = (ItemSpade)bestTools[2];
			ItemSword bestSword = ItemUtil.getBestSword();
			for(int i=0;i<36;i++) {
				ItemStack itemStack = this.mc.thePlayer.inventory.getStackInSlot(i);
				if (itemStack == null) continue;
				Item item = itemStack.getItem();
				if (item instanceof ItemArmor) {
					ItemStack armor = ItemUtil.getItemInSlot(i);
					ItemStack armorOnPlayer = ItemUtil.getItemInArmorSlot(((ItemArmor) armor.getItem()).armorType);
					if (armorOnPlayer == null || armor == armorOnPlayer || ItemUtil.betterArmor(armor, armorOnPlayer) == itemStack)
						continue;
					clearItem(i);
					break;
				} else if (item instanceof ItemTool) {
					if (item instanceof ItemAxe) {
						if (item == bestAxe) continue;
						clearItem(i);
					} else if (item instanceof ItemPickaxe) {
						if (item == bestPickaxe) continue;
						clearItem(i);
					} else if (item instanceof ItemSpade) {
						if (item == bestShovel) continue;
						clearItem(i);
					}
				} else if (item instanceof ItemSword) {
					if (item == bestSword) continue;
					clearItem(i);
				}
			}
		}
		
	}
	
	public void clearItem(int slot) {
 	    mc.playerController.windowClick(mc.thePlayer.openContainer.windowId, slot, 0, 0, mc.thePlayer);
	}
	
}
