package axolotl.cheats.modules.impl.movement;

import axolotl.cheats.events.Event;
import axolotl.cheats.events.EventType;
import axolotl.cheats.events.MoveEvent;
import axolotl.cheats.modules.Module;
import axolotl.cheats.settings.ModeSetting;
import axolotl.cheats.settings.NumberSetting;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.stats.StatList;
import org.lwjgl.input.Keyboard;

public class Longjump extends Module {

	public ModeSetting mode = new ModeSetting("Mode", "Vanilla", "Vanilla", "Boost");
	public NumberSetting speed = new NumberSetting("Speed", 1, 0.1, 2, 0.1);

	public Longjump() {
		super("Longjump", Category.MOVEMENT, true);
		this.addSettings(mode, speed);
		this.setSpecialSetting(mode);
	}

	public void onEnable() {

		mc.thePlayer.jump();
		mc.thePlayer.setSpeed((float)speed.getNumberValue() * 2);

	}

	
	public void onEvent(Event e) {
		
		if(!(e instanceof MoveEvent) || e.eventType != EventType.POST)return;

		if(mc.thePlayer.onGround) {
			this.toggle();
		}

		switch(mode.getMode()) {
			case "Boost":

				if(mc.gameSettings.keyBindForward.getIsKeyPressed()) {
					if(mc.thePlayer.motionY < -0.05)
						mc.thePlayer.motionY += 0.03;
					mc.thePlayer.setSpeed((float)speed.getNumberValue() * 0.6f);
				}

				break;
			default:
				break;
		}

	}
	
}
