package club.marsh.bloom.impl.mods.player;

import org.lwjgl.input.Keyboard;

import com.google.common.eventbus.Subscribe;

import club.marsh.bloom.api.module.Category;
import club.marsh.bloom.api.module.Module;
import club.marsh.bloom.api.value.NumberValue;
import club.marsh.bloom.impl.events.Render2DEvent;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.inventory.Slot;

public class ChestStealer extends Module
{
	public static boolean stealing = false;
	private long lastTime = System.currentTimeMillis();
	public static NumberValue<Double> time = new NumberValue<Double>("Time",50D,50D,1500D,0);
	
	public ChestStealer()
	{
		super("ChestStealer", Keyboard.KEY_I, Category.PLAYER);
	}
	
	@Override
	public void onEnable()
	{
		lastTime = System.currentTimeMillis();
	}
	
	@Subscribe
	public void onRender2D(Render2DEvent e)
	{
		try
		{
			if (mc.currentScreen instanceof GuiChest)
			{
            	stealing = true;
            	
                if (Math.abs(lastTime - System.currentTimeMillis()) > time.value.doubleValue())
                {
                    GuiChest chest = (GuiChest) mc.currentScreen;
                    int slots = mc.thePlayer.inventoryContainer.inventorySlots.size();
                    boolean hasclicked = false;
                    
                    if (isEmpty(chest))
                    {
                        stealing = false;
                        mc.thePlayer.closeScreen();
                    }
                    
                    for (int slot = 0; slot <= slots; ++slot)
                    {
                        Slot thing = chest.inventorySlots.getSlot(slot);
                        
                        if (thing != null && thing.getStack() != null && thing.getStack().getItem() != null && !hasclicked)
                        {
//                          chest.handleMouseClick(thing, thing.slotNumber, 0, 1);
                            hasclicked = true;
                            mc.playerController.windowClick(mc.thePlayer.openContainer.windowId, thing.slotNumber, 0, 1, mc.thePlayer);
                            lastTime = System.currentTimeMillis();
                        }
                    }
                }
			}
			
			else
	        {
	        	stealing = false;
	        }
		}
		
		catch (Exception exception)
		{
			exception.printStackTrace();
		}
	}
	
	public boolean isEmpty(GuiChest chest)
	{
        for (Slot slot : mc.thePlayer.openContainer.inventorySlots)
        {
            if (slot != null && slot.getStack() != null && slot.getStack().getItem() != null)
            {
                return false;
            }
        }
        
        return true;
    }
}
