package intentions.modules.player;

import intentions.modules.Module;
import intentions.settings.ModeSetting;
import intentions.settings.NumberSetting;
import intentions.util.InventoryUtils;
import net.minecraft.block.BlockContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.init.Items;
import net.minecraft.item.ItemSoup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;

public class AutoSoup extends Module {
	public static boolean enabled;
	public final NumberSetting health = new NumberSetting("Health", 6.5, 0.5, 10, 0.5);
		public ModeSetting screen = new ModeSetting("Screen", "Ignore", new String[] {"Open", "Ignore"});
		
		private int oldSlot = -1;
		
		public AutoSoup() {
			super("AutoSoup", 0, Category.PLAYER, "Automatically eats soup (This expects soup to be auto eaten once right clicked)", true);
			this.addSettings(health, screen);
		}
		
		public void onEnable()
		{
			enabled = true;
			
		}
		
		public void onDisable()
		{
			
			stopIfEating();
			
			enabled = false;
		}
		
		public void onUpdate()
		{
			if(!this.enabled)return;
			// sort empty bowls
			for(int i = 0; i < 36; i++)
			{
				// filter out non-bowl items and empty bowl slot
				ItemStack stack = mc.thePlayer.inventory.getStackInSlot(i);
				if(stack == null || stack.getItem() != Items.bowl || i == 9)
					continue;
				
				// check if empty bowl slot contains a non-bowl item
				ItemStack emptyBowlStack = mc.thePlayer.inventory.getStackInSlot(9);
				boolean swap = !InventoryUtils.isEmptySlot(emptyBowlStack)
					&& emptyBowlStack.getItem() != Items.bowl;
				
				// place bowl in empty bowl slot
				mc.playerController.windowClick(0, i < 9 ? 36 + i : i, 0,
					1, mc.thePlayer);
				mc.playerController.windowClick(0, 9, 0, 1,
					mc.thePlayer);
				
				// place non-bowl item from empty bowl slot in current slot
				if(swap)
					mc.playerController.windowClick(0, i < 9 ? 36 + i : i, 0,
						1, mc.thePlayer);
			}
			
			// search soup in hotbar
			int soupInHotbar = findSoup(0, 9);
			
			// check if any soup was found
			if(soupInHotbar != -1)
			{
				// check if player should eat soup
				if(!shouldEatSoup())
				{
					stopIfEating();
					return;
				}
				
				if(screen.getMode().equalsIgnoreCase("Open") && mc.currentScreen != null) {
					mc.displayGuiScreen(null);
				}
				
				// save old slot
				if(oldSlot == -1)
					oldSlot = mc.thePlayer.inventory.currentItem;
				
				// slot stuff
				
				mc.thePlayer.inventory.currentItem = soupInHotbar;
				
				// eat soup
				mc.gameSettings.keyBindUseItem.pressed = true;
				mc.playerController.func_178890_a(mc.thePlayer, mc.theWorld, mc.thePlayer.getCurrentEquippedItem(), mc.thePlayer.getPosition(), EnumFacing.UP, mc.thePlayer.getPositionVector());
				mc.gameSettings.keyBindUseItem.pressed = false;
				
				return;
			}
			
			stopIfEating();
			
			// search soup in inventory
			int soupInInventory = findSoup(9, 36);
			
			// move soup in inventory to hotbar
			if(soupInInventory != -1)
				mc.playerController.windowClick(0, soupInInventory, 0, 1, mc.thePlayer);
		}
		
		private int findSoup(int startSlot, int endSlot)
		{
			for(int i = startSlot; i < endSlot; i++)
			{
				ItemStack stack = mc.thePlayer.inventory.getStackInSlot(i);
				
				if(stack != null && stack.getItem() instanceof ItemSoup)
					return i;
			}
			
			return -1;
		}
		
		private boolean shouldEatSoup()
		{
			// check health
			if(mc.thePlayer.getHealth() > health.getValue() * 2F)
				return false;
			
			if(screen.getMode().equalsIgnoreCase("Open")) {
				mc.displayGuiScreen(new GuiInventory(mc.thePlayer));
			}
			else if(!screen.getMode().equalsIgnoreCase("Ignore") && mc.currentScreen != null)
					return false;
			
			
			// check for clickable objects
			if(mc.currentScreen == null && mc.objectMouseOver != null)
			{
				// clickable entities
				Entity entity = mc.objectMouseOver.entityHit;
				if(entity instanceof EntityVillager
					|| entity instanceof EntityTameable)
					return false;
				
				// clickable blocks
				if(mc.objectMouseOver.getBlockPos() != null
					&& mc.theWorld.getBlockState(mc.objectMouseOver.getBlockPos())
						.getBlock() instanceof BlockContainer)
					return false;
			}
			
			return true;
		}
		
		private void stopIfEating()
		{
			// check if eating
			if(oldSlot == -1)
				return;
			
			// stop eating
			mc.gameSettings.keyBindUseItem.pressed = false;
			
			// reset slot
			mc.thePlayer.inventory.currentItem = oldSlot;
			oldSlot = -1;
		}
	
}
