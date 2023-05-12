package markgg.modules.movement;

import markgg.events.Event;
import markgg.events.listeners.EventMotion;
import markgg.events.listeners.EventUpdate;
import markgg.modules.Module;
import markgg.modules.combat.KillAura;
import markgg.settings.ModeSetting;
import markgg.ui.HUD;
import markgg.ui.notifs.Notification;
import markgg.ui.notifs.NotificationManager;
import markgg.ui.notifs.NotificationType;
import markgg.util.MoveUtil;
import markgg.util.Timer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

public class NoSlowDown extends Module{

	public Timer timer = new Timer();
	public static boolean doNotSlow;
	public ModeSetting mode = new ModeSetting("Mode", this, "Vanilla", "Vanilla", "NCP");

	public NoSlowDown() {
		super("NoSlowDown", "Disables player slowdown", 0, Category.MOVEMENT);
		addSettings(mode);
	}

	public void onEvent(Event e) {
		if (mc.gameSettings.keyBindSprint.pressed && this.mc.thePlayer.moveForward != 0.0f && !this.mc.thePlayer.isCollidedVertically && !this.mc.thePlayer.isSprinting()) {
			mc.thePlayer.setSprinting(true);
		}
		if (mode.is("Vanilla") || mode.is("NCP"))
			doNotSlow = true;
		if(e.isPre()) {
			switch (mode.getMode()) {
			case "Vanilla":
				if(mc.thePlayer.isBlocking() || mc.thePlayer.isEating() || mc.thePlayer.isUsingItem())
					MoveUtil.setSpeed(0.2875);
				break;
			case "NCP":
				if(mc.thePlayer.isBlocking() || mc.thePlayer.isEating() || mc.thePlayer.isUsingItem())
					mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(mc.thePlayer.getCurrentEquippedItem()));
				break;
			}
		}
		if (e instanceof EventMotion && e.isPost()) {
			if (mode.is("NCP") && mc.thePlayer.isBlocking())
				mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
		}
	}
	
	public void onEnable() {
		NotificationManager.show(new Notification(NotificationType.ENABLE, getName() + " was enabled!", 1));
		doNotSlow = true;
	}

	public void onDisable() {
		NotificationManager.show(new Notification(NotificationType.DISABLE, getName() + " was disabled!", 1));
		doNotSlow = false;
	}

}
