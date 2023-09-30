package juul.module.movement;

import juul.event.Event;
import juul.event.EventUpdate;
import juul.inject.wrappers.EntityLivingBaseWrapper;
import juul.inject.wrappers.EntityPlayerSPWrapper;
import juul.inject.wrappers.EntityPlayerWrapper;
import juul.inject.wrappers.MinecraftWrapper;
import juul.module.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import org.lwjgl.input.Keyboard;

public class Bhop extends Module
{
	public Bhop() {
		super("Bhop", "Automatically jumps for you.", 0, Category.MOVEMENT);
	}
	
	public void onEvent(Event e)
	{
		if (e instanceof EventUpdate)
		{
			EntityPlayerSPWrapper wEntityPlayerSP = thePlayerSP;

			// if (wEntityPlayerSP.getOnGround()) wEntityPlayerSP.jump(); (broken wrap)

		}
	}
}
