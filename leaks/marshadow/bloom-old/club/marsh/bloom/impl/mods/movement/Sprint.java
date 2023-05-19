package club.marsh.bloom.impl.mods.movement;

import org.lwjgl.input.Keyboard;

import com.google.common.eventbus.Subscribe;

import club.marsh.bloom.api.module.Category;
import club.marsh.bloom.api.module.Module;
import club.marsh.bloom.api.value.BooleanValue;
import club.marsh.bloom.impl.events.UpdateEvent;
import club.marsh.bloom.impl.mods.player.Scaffold;
import club.marsh.bloom.impl.utils.movement.MovementUtils;


public class Sprint extends Module {
	BooleanValue omni = new BooleanValue("Omni Sprint", false);
	
	public Sprint() {
		super("Sprint",Keyboard.KEY_NONE,Category.MOVEMENT);
	}
	
	@Subscribe
	public void onUpdate(UpdateEvent e) {
		if (!(Scaffold.on && !Scaffold.sprint.isOn()))
		{
			if (omni.isOn())
			{
				mc.thePlayer.setSprinting(canSprint());
			}
			
			else
			{
				mc.gameSettings.keyBindSprint.pressed = true;
			}
		}
		
		else
		{
			mc.gameSettings.keyBindSprint.pressed = false;
		}
	}
	
	@Override
	public void onDisable()
	{
		super.onDisable();
		
		if (omni.isOn())
		{
			mc.thePlayer.setSprinting(Keyboard.isKeyDown(mc.gameSettings.keyBindSprint.getKeyCode()));
		}
		
		else
		{
			mc.gameSettings.keyBindSprint.pressed = Keyboard.isKeyDown(mc.gameSettings.keyBindSprint.getKeyCode());
		}
	}
	
	public static boolean canSprint()
	{
		return MovementUtils.isMoving() && mc.currentScreen == null && !mc.thePlayer.isSneaking() && !mc.thePlayer.isEating() && !mc.thePlayer.isUsingItem() && !mc.thePlayer.isCollidedHorizontally;
	}
}
