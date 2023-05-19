package club.marsh.bloom.impl.mods.combat;

import org.lwjgl.input.Keyboard;

import com.google.common.eventbus.Subscribe;

import club.marsh.bloom.api.module.Category;
import club.marsh.bloom.api.module.Module;
import club.marsh.bloom.api.value.BooleanValue;
import club.marsh.bloom.impl.events.AttackEvent;
import club.marsh.bloom.impl.events.UpdateEvent;
import net.minecraft.network.play.client.C0BPacketEntityAction;

public class WTap extends Module
{
	private BooleanValue legit = new BooleanValue("Legit", false, () -> true);
	private int ticks = 0;
	
	public WTap()
	{
		super("W-Tap", Keyboard.KEY_NONE, Category.COMBAT);
	}
	
	@Subscribe
	public void onAttack(AttackEvent e)
	{
		ticks = 0;
	}
	
	@Subscribe
	public void onUpdate(UpdateEvent e)
	{
		++ticks;
		
		if (mc.thePlayer.isSprinting())
		{
			if (legit.isOn())
			{
				if (ticks == 2)
				{
					mc.thePlayer.setSprinting(false);
				}
				
				else if (ticks == 3)
				{
					mc.thePlayer.setSprinting(true);
				}
			}
			
			else
			{
				if (ticks < 10)
				{
					mc.getNetHandler().addToSendQueueSilent(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SPRINTING));
					mc.getNetHandler().addToSendQueueSilent(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SPRINTING));
				}
			}
		}
	}
}
