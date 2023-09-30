package juul.util.player;

import juul.Juul;
import juul.inject.wrappers.ContainerWrapper;
import juul.inject.wrappers.EntityPlayerWrapper;
import juul.inject.wrappers.InventoryPlayerWrapper;
import juul.inject.wrappers.ItemPotionWrapper;
import juul.inject.wrappers.ItemSoupWrapper;
import juul.inject.wrappers.ItemStackWrapper;
import juul.inject.wrappers.SlotWrapper;
import juul.util.Utils;

public class InventoryUtil implements Utils {

	public boolean hotbarHasSpace() {
		InventoryPlayerWrapper inventory = new InventoryPlayerWrapper(thePlayer.inventory());
		
        int i = 0;
        while (i < 9) {
            if (inventory.getStackInSlot(i) == null) {
                return true;
            }
            ++i;
        }
        return false;
    }
	
	public boolean inventoryHasPotion() {
		ContainerWrapper inventoryContainer = new ContainerWrapper(thePlayer.inventoryContainer());
        int i = 9;
        while (i <= 35) {
        	ItemStackWrapper stack = new ItemStackWrapper(new SlotWrapper(inventoryContainer.getSlot(i)).getStack());
            if (itemPotion.isInstance(stack.getItem())) {
                return true;
            }
            ++i;
        }
        return false;
    }
	
	public boolean inventoryHasSoup() {
		ContainerWrapper inventoryContainer = new ContainerWrapper(thePlayer.inventoryContainer());
        int i = 9;
        while (i <= 35) {
        	ItemStackWrapper stack = new ItemStackWrapper(new SlotWrapper(inventoryContainer.getSlot(i)).getStack());
            if (itemSoup.isInstance(stack.getItem())) {
                return true;
            }
            ++i;
        }
        return false;
    }
	
	public int potionInventorySlot() {
		ContainerWrapper inventoryContainer = new ContainerWrapper(thePlayer.inventoryContainer());
        int i = 9;
        while (i <= 35) {
        	ItemStackWrapper stack = new ItemStackWrapper(new SlotWrapper(inventoryContainer.getSlot(i)).getStack());
            if (itemPotion.isInstance(stack.getItem())) {
                return i;
            }
            ++i;
        }
        return -999;
    }
    
    public int soupInventorySlot() {
		ContainerWrapper inventoryContainer = new ContainerWrapper(thePlayer.inventoryContainer());
        int i = 9;
        while (i <= 35) {
        	ItemStackWrapper stack = new ItemStackWrapper(new SlotWrapper(inventoryContainer.getSlot(i)).getStack());
            if (itemSoup.isInstance(stack.getItem())) {
                return i;
            }
            ++i;
        }
        return -999;
    }
	
}
