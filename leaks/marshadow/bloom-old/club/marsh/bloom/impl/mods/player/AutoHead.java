package club.marsh.bloom.impl.mods.player;

import org.lwjgl.input.Keyboard;

import com.google.common.eventbus.Subscribe;

import club.marsh.bloom.api.module.Category;
import club.marsh.bloom.api.module.Module;
import club.marsh.bloom.api.value.NumberValue;
import club.marsh.bloom.impl.events.UpdateEvent;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;

public class AutoHead extends Module
{
	private long lastHead = System.currentTimeMillis();
	NumberValue health = new NumberValue("Health", 10, 0, 20, 1, () -> true);
	
	public AutoHead()
	{
		super("Auto Head",Keyboard.KEY_NONE,Category.PLAYER);
	}
	
	@Subscribe
	public void onUpdate(UpdateEvent e)
	{
		try
		{
			if (mc.thePlayer.getHealth() < health.getObject().doubleValue())
			{
				for (int i = 1; i < 9; ++i)
				{
					ItemStack itemStack = mc.thePlayer.inventory.getStackInSlot(i);
					
					if (itemStack != null)
					{
						if (itemStack.getDisplayName().contains("Head") && itemStack.getDisplayName().contains("Gold") && System.currentTimeMillis() - lastHead > 2000)
						{
							mc.getNetHandler().addToSendQueueSilent(new C09PacketHeldItemChange(i));
							mc.getNetHandler().addToSendQueueSilent(new C08PacketPlayerBlockPlacement(itemStack));
							addMessage("Used head at slot: " + i);
							mc.getNetHandler().addToSendQueueSilent(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
							lastHead = System.currentTimeMillis();
						}
					}
				}
			}
		}
		
		catch (Exception exception)
		{
			exception.printStackTrace();
		}
	}
}
