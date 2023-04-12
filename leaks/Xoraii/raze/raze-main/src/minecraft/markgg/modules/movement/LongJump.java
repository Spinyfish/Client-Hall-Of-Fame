package markgg.modules.movement;

import markgg.events.Event;
import markgg.events.listeners.EventUpdate;
import markgg.modules.Module;
import markgg.settings.BooleanSetting;
import markgg.settings.ModeSetting;
import markgg.ui.HUD;
import markgg.ui.notifs.Notification;
import markgg.ui.notifs.NotificationManager;
import markgg.ui.notifs.NotificationType;
import markgg.util.MoveUtil;
import markgg.util.Timer;
import net.minecraft.potion.Potion;

public class LongJump extends Module{

	public ModeSetting jumpMode = new ModeSetting("Mode", this, "Vanilla", "Vanilla", "Verus", "Mineplex", "Vulcan", "Matrix");
	public BooleanSetting landDisable = new BooleanSetting("Safe Mode", this, true);

	public Timer timer = new Timer();
	
	public LongJump() {
		super("LongJump", "Jump long distances", 0, Category.MOVEMENT);
		this.addSettings(jumpMode, landDisable);
	}
	
	public void onEvent(Event e) {
		if (e instanceof EventUpdate) {
			switch (jumpMode.getMode()) {
			case "Vanilla":
				if (mc.gameSettings.keyBindForward.pressed || mc.gameSettings.keyBindBack.pressed || mc.gameSettings.keyBindLeft.pressed || mc.gameSettings.keyBindRight.pressed && mc.gameSettings.keyBindJump.pressed) {
					if (mc.thePlayer.isAirBorne){
						mc.thePlayer.motionX *= 1.11;
						mc.thePlayer.motionZ *= 1.11;
					}
				}else{
					mc.thePlayer.motionX *= 1;
					mc.thePlayer.motionZ *= 1;  
				}
				if(landDisable.isEnabled()) {
					if(mc.thePlayer.onGround)
						toggle();
				}
				break;
			case "Verus":
				if(mc.gameSettings.keyBindForward.pressed || mc.gameSettings.keyBindBack.pressed || mc.gameSettings.keyBindLeft.pressed || mc.gameSettings.keyBindRight.pressed && mc.gameSettings.keyBindJump.pressed){
					float dir = mc.thePlayer.rotationYaw + ((mc.thePlayer.moveForward < 0) ? 180 : 0) + ((mc.thePlayer.moveStrafing > 0) ? (-90F * ((mc.thePlayer.moveForward < 0) ? -.5F : ((mc.thePlayer.moveForward > 0) ? .4F : 1F))) : 0);
					float xDir = (float)Math.cos((dir + 90F) * Math.PI / 180);
					float zDir = (float)Math.sin((dir + 90F) * Math.PI / 180);
					if(mc.thePlayer.isCollidedVertically && (mc.gameSettings.isKeyDown(mc.gameSettings.keyBindForward) || mc.gameSettings.isKeyDown(mc.gameSettings.keyBindLeft) || mc.gameSettings.isKeyDown(mc.gameSettings.keyBindRight) || mc.gameSettings.isKeyDown(mc.gameSettings.keyBindBack)) && mc.gameSettings.isKeyDown(mc.gameSettings.keyBindJump)) {
						mc.thePlayer.motionX = xDir * .29F;
						mc.thePlayer.motionZ = zDir * .29F;
					}
					if(mc.thePlayer.motionY == .33319999363422365 && (mc.gameSettings.isKeyDown(mc.gameSettings.keyBindForward) || mc.gameSettings.isKeyDown(mc.gameSettings.keyBindLeft) || mc.gameSettings.isKeyDown(mc.gameSettings.keyBindRight) || mc.gameSettings.isKeyDown(mc.gameSettings.keyBindBack))) {
						if (mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
							mc.thePlayer.motionX = xDir * 1.34;
							mc.thePlayer.motionZ = zDir * 1.34;
						}else {
							mc.thePlayer.motionX = xDir * 0.50;
							mc.thePlayer.motionZ = zDir * 0.50;
						}
					}
				}
				if(landDisable.isEnabled()) {
					if(mc.thePlayer.onGround)
						toggle();
				}
				break;
			case "Mineplex":
				if(mc.gameSettings.keyBindForward.pressed || mc.gameSettings.keyBindBack.pressed || mc.gameSettings.keyBindLeft.pressed || mc.gameSettings.keyBindRight.pressed && mc.gameSettings.keyBindJump.pressed){
					float dir = mc.thePlayer.rotationYaw + ((mc.thePlayer.moveForward < 0) ? 180 : 0) + ((mc.thePlayer.moveStrafing > 0) ? (-90F * ((mc.thePlayer.moveForward < 0) ? -.5F : ((mc.thePlayer.moveForward > 0) ? .4F : 1F))) : 0);
					float xDir = (float)Math.cos((dir + 90F) * Math.PI / 180);
					float zDir = (float)Math.sin((dir + 90F) * Math.PI / 180);
					if(mc.thePlayer.isCollidedVertically && (mc.gameSettings.isKeyDown(mc.gameSettings.keyBindForward) || mc.gameSettings.isKeyDown(mc.gameSettings.keyBindLeft) || mc.gameSettings.isKeyDown(mc.gameSettings.keyBindRight) || mc.gameSettings.isKeyDown(mc.gameSettings.keyBindBack)) && mc.gameSettings.isKeyDown(mc.gameSettings.keyBindJump)) {
						mc.thePlayer.motionX = xDir * .29F;
						mc.thePlayer.motionZ = zDir * .29F;
					}
					if(mc.thePlayer.motionY == .33319999363422365 && (mc.gameSettings.isKeyDown(mc.gameSettings.keyBindForward) || mc.gameSettings.isKeyDown(mc.gameSettings.keyBindLeft) || mc.gameSettings.isKeyDown(mc.gameSettings.keyBindRight) || mc.gameSettings.isKeyDown(mc.gameSettings.keyBindBack))) {
						if (mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
							mc.thePlayer.motionX = xDir * 1.34;
							mc.thePlayer.motionZ = zDir * 1.34;
						}else {
							mc.thePlayer.motionX = xDir * 0.70;
							mc.thePlayer.motionZ = zDir * 0.70;
						}
					}
				}
				if(landDisable.isEnabled()) {
					if(mc.thePlayer.onGround)
						toggle();
				}
				break;
			case "Vulcan":
				mc.thePlayer.speedInAir = 0.079912F;
				if(mc.thePlayer.onGround) {
					mc.thePlayer.speedInAir = 0.02F;
					toggle();
				}
				break;
			}
		}
	}
	
	public void onEnable() {
		NotificationManager.show(new Notification(NotificationType.ENABLE, getName() + " was enabled!", 1));
		switch(jumpMode.getMode()) {
		case "Vulcan":
			mc.thePlayer.setPositionAndUpdate(mc.thePlayer.posX, mc.thePlayer.posY + 3.7, mc.thePlayer.posZ);
			break;
		default:
			mc.thePlayer.jump();
			break;
		}
	}
	
	public void onDisable() {
		NotificationManager.show(new Notification(NotificationType.DISABLE, getName() + " was disabled!", 1));
		mc.thePlayer.speedInAir = 0.02F;
	}

}
