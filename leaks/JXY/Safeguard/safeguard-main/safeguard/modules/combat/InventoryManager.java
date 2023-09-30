package intentions.modules.combat;

import intentions.Client;
import intentions.modules.Module;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;

public class InventoryManager extends Module {

	public InventoryManager() {
		super("InventoryManager", 0, Category.COMBAT, "Automatically arranges your inventory!", true);
	}
	
	public void onUpdate() {
		if(!AutoArmor.isChestInventory() && this.toggled && mc.thePlayer != null) {
			for(int i=0;i<36;i++) {
				ItemStack var0 = this.mc.thePlayer.inventory.getStackInSlot(i);
				
				
				if(var0 != null && var0.getItem() instanceof ItemArmor) {
					
					ItemArmor var1 = (ItemArmor)var0.getItem();
					ItemStack var2 = this.mc.thePlayer.inventory.getStackInSlot(39 - var1.armorType);
					if(var2 == null)return;
					ItemArmor var3 = (ItemArmor)var2.getItem();
					if(var1 == null || var3 == null)return;
					
					if(var1.getArmorMaterial().getDamageReductionAmount(var1.armorType) + AutoArmor.checkProtection(this.mc.thePlayer.inventory.getStackInSlot(i)) <= var3.getArmorMaterial().getDamageReductionAmount(var3.armorType) + AutoArmor.checkProtection(this.mc.thePlayer.inventory.getStackInSlot(39 - var3.armorType))) {
						
						this.mc.playerController.windowClick(this.mc.thePlayer.inventoryContainer.windowId, i, 0, 4, this.mc.thePlayer);
		                this.mc.playerController.windowClick(this.mc.thePlayer.inventoryContainer.windowId, 5 + var1.armorType, 0, 1, this.mc.thePlayer);
						
					}
					
				}
			}
		}
	}
	
}
