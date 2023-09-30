package igbt.astolfy.module.player;

import java.util.ArrayList;

import org.lwjgl.input.Keyboard;

import igbt.astolfy.Astolfy;
import igbt.astolfy.events.Event;
import igbt.astolfy.events.listeners.EventMotion;
import igbt.astolfy.module.ModuleBase;
import igbt.astolfy.settings.settings.ModeSetting;
import net.minecraft.network.play.client.C03PacketPlayer;

public class Sprint extends ModuleBase {
	
	public Sprint() {
		super("Sprint", Keyboard.KEY_M, Category.PLAYER);
	}
	
	public void onEvent(Event e) {
		if(e instanceof EventMotion) {
			if(mc.thePlayer.moveForward > 0 && mc.thePlayer.getFoodStats().getFoodLevel() > 6 && !Astolfy.moduleManager.getModuleByName("Scaffold").isToggled())
				mc.thePlayer.setSprinting(true);
		}
	}
}
