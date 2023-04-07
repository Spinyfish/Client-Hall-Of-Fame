package intentions.util;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C10PacketCreativeInventoryAction;

public class InventoryUtils
{
	private static final Minecraft mc = Minecraft.getMinecraft();
	
	public static boolean placeStackInHotbar(ItemStack stack)
	{
		for(int i = 0; i < 9; i++)
			if(isSlotEmpty(i))
			{
				mc.thePlayer.sendQueue.addToSendQueue(
					new C10PacketCreativeInventoryAction(36 + i, stack));
				return true;
			}
		
		return false;
	}
	
	public static boolean isSlotEmpty(int slot)
	{
		return mc.thePlayer.inventory.getStackInSlot(slot) == null;
	}
	
	public static boolean isEmptySlot(ItemStack slot)
	{
		return slot == null;
	}
}