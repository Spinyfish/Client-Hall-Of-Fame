package club.marsh.bloom.impl.mods.player;

import java.util.ArrayList;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import com.google.common.eventbus.Subscribe;

import club.marsh.bloom.api.module.Category;
import club.marsh.bloom.api.module.Module;
import club.marsh.bloom.impl.events.UpdateEvent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MovingObjectPosition;

public class MiddleClickFriends extends Module
{
	public static boolean on = false;
	boolean pressed = false, lastPressed = false;
	public static ArrayList<Entity> friends = new ArrayList<>();
	
	public MiddleClickFriends()
	{
		super("MCF", Keyboard.KEY_NONE, Category.PLAYER);
	}
	
	@Subscribe
	public void onUpdate(UpdateEvent e)
	{
		lastPressed = pressed;
		pressed = Mouse.isButtonDown(2);
		
		if (pressed && !lastPressed)
		{
			MovingObjectPosition movingObjectPosition = mc.objectMouseOver;
			
			if (movingObjectPosition.entityHit instanceof EntityPlayer)
			{
				addMessage((!friends.contains(movingObjectPosition.entityHit) ? "Added " : "Removed ") + ((EntityPlayer) movingObjectPosition.entityHit).getGameProfile().getName() + " to your friends list.");
				
				if (!friends.contains(movingObjectPosition.entityHit))
				{
					friends.add(movingObjectPosition.entityHit);
				}
				
				else
				{
					friends.remove(movingObjectPosition.entityHit);
				}
			}
		}
	}

	@Override
	public void onEnable()
	{
		on = true;
	}
	
	@Override
	public void onDisable()
	{
		on = false;
	}
}
