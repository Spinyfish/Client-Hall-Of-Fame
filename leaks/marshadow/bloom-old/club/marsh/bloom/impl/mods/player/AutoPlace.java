package club.marsh.bloom.impl.mods.player;

import org.lwjgl.input.Keyboard;

import com.google.common.eventbus.Subscribe;

import club.marsh.bloom.api.module.Category;
import club.marsh.bloom.api.module.Module;
import club.marsh.bloom.impl.events.UpdateEvent;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;

public class AutoPlace extends Module
{
	public AutoPlace()
	{
		super("Auto Place", Keyboard.KEY_NONE, Category.PLAYER);
	}
	
	@Subscribe
	public void onUpdate(UpdateEvent e)
	{
		MovingObjectPosition mouseOver = mc.objectMouseOver;
		
		if (mouseOver == null)
		{
			return;
		}
		
		if (mouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK)
		{
			EnumFacing sideHit = mouseOver.sideHit;
			boolean isAboutToRunIntoAir = false;
			
			for (int i = 2; i > 0; --i)
			{
				if (i == 0)
				{
					break;
				}
				
				if (!isAboutToRunIntoAir && mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, mc.thePlayer.getEntityBoundingBox().offset(mc.thePlayer.motionX * i, -0.25D, mc.thePlayer.motionZ * i)).isEmpty())
				{
					isAboutToRunIntoAir = true;
				}
			}
			
			if ((sideHit == EnumFacing.DOWN || sideHit == EnumFacing.UP) && !isAboutToRunIntoAir)
			{
				mc.rightClickDelayTimer -= 1;
				return;
			}
			
			mc.rightClickDelayTimer = 0;
		}
	}
}
