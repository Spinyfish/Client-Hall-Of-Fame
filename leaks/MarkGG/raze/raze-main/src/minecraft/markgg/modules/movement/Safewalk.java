package markgg.modules.movement;

import markgg.events.Event;
import markgg.events.listeners.EventUpdate;
import markgg.modules.Module;
import markgg.modules.Module.Category;
import markgg.ui.HUD;
import markgg.ui.notifs.Notification;
import markgg.ui.notifs.NotificationManager;
import markgg.ui.notifs.NotificationType;
import net.minecraft.block.BlockAir;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.BlockPos;

public class Safewalk extends Module{

	public Safewalk() {
		super("Safewalk", "Prevents you from walking off edges", 0, Category.MOVEMENT);
	}
	
	public void onEvent(Event e) {
		if (e instanceof EventUpdate && e.isPre()) {
			if (mc.theWorld.getBlockState(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1.0, mc.thePlayer.posZ)).getBlock() instanceof BlockAir && mc.thePlayer.onGround) {
				mc.gameSettings.keyBindSneak.pressed = true;
			}
			else {
				mc.gameSettings.keyBindSneak.pressed = false;
			}
		}
	}
	
	public void onDisable() {
		NotificationManager.show(new Notification(NotificationType.DISABLE, getName() + " was disabled!", 1));
		if (!GameSettings.isKeyDown(this.mc.gameSettings.keyBindSneak)) {
			mc.gameSettings.keyBindSneak.pressed = false;;
		}
	}

}
