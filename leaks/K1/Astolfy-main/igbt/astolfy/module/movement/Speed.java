package igbt.astolfy.module.movement;

import java.util.ArrayList;

import igbt.astolfy.Astolfy;
import igbt.astolfy.ui.Notifications.Notification;
import igbt.astolfy.ui.Notifications.NotificationManager;
import igbt.astolfy.ui.Notifications.NotificationType;
import org.lwjgl.input.Keyboard;

import igbt.astolfy.events.Event;
import igbt.astolfy.events.listeners.EventMotion;
import igbt.astolfy.module.ModuleBase;
import igbt.astolfy.settings.settings.ModeSetting;
import igbt.astolfy.settings.settings.NumberSetting;

public class Speed extends ModuleBase {

	public ModeSetting mode;
	public NumberSetting speed = new NumberSetting("Speed", 0.5, 0.05, 0.15, 1);
	
	public Speed() {
		super("Speed", Keyboard.KEY_R, Category.MOVEMENT);
		mode = new ModeSetting("Mode", "Bhop", "Hypixel", "NCP");
		addSettings(mode ,speed);
	}
	
	public void onEnable() {
		if(Astolfy.moduleManager.getModuleByName("Flight").isToggled()){
			Astolfy.moduleManager.getModuleByName("Flight").setToggled(false);
			NotificationManager.showNotification(new Notification(NotificationType.WARNING, "Anti-Flag","Disabled Flight To Prevent Flags",3));
		}
	}
	
	public void onDisable() {
		mc.gameSettings.keyBindJump.pressed = Keyboard.isKeyDown(mc.gameSettings.keyBindJump.getKeyCode());
		mc.timer.timerSpeed = 1;
	}
	
	public void onEvent(Event e) {
		if(e instanceof EventMotion) {
			mc.thePlayer.capabilities.isFlying = false;
			setSuffix(mode.getCurrentValue());
			switch(mode.getCurrentValue()) {
			case "Bhop":
				//mc.timer.timerSpeed = 1.3f;
				if(mc.thePlayer.moveForward != 0 || mc.thePlayer.moveStrafing != 0) {
					mc.gameSettings.keyBindJump.pressed = false;
					
					if(mc.thePlayer.onGround)
						mc.gameSettings.keyBindJump.pressed = true;
					//else 
					//	mc.thePlayer.motionY -=0.0001;
						mc.thePlayer.setSpeed(speed.getValue());
				}else {
					mc.thePlayer.motionX = 0;
					mc.thePlayer.motionZ = 0;
				}
				break;
			
			case "Hypixel":
				if(mc.thePlayer.moveForward != 0 || mc.thePlayer.moveStrafing != 0) {
					//if(mc.thePlayer.motionY < 0.0)
						//mc.thePlayer.motionY -=0.0025;
					//mc.timer.timerSpeed = 1.2f;
					mc.gameSettings.keyBindJump.pressed = false;
					//if(mc.thePlayer.motionX + mc.thePlayer.motionZ < 0.28 * 2 - 0.01) {
					//	mc.thePlayer.motionX *= 1.009;
					//	mc.thePlayer.motionZ *= 1.009;
					//}
					if(mc.thePlayer.onGround)
						mc.thePlayer.motionY = 0.4;
					//	mc.gameSettings.keyBindJump.pressed = true;
					
					if(!mc.thePlayer.onGround)
						mc.thePlayer.setSpeed(speed.getValue());
					else{
						mc.thePlayer.motionX = 0;
						mc.thePlayer.motionZ = 0;
					}
				}else {
					mc.thePlayer.motionX = 0;
					mc.thePlayer.motionZ = 0;
				}
				break;
				
			case "NCP":
				if(mc.thePlayer.moveForward != 0 || mc.thePlayer.moveStrafing != 0) {
					//if(mc.thePlayer.motionY < 0.16)
					//	mc.thePlayer.motionY -=0.017;
					//mc.timer.timerSpeed = 2.295f;
					mc.gameSettings.keyBindJump.pressed = false;
					if(mc.thePlayer.motionX + mc.thePlayer.motionZ < 0.28 * 2 - 0.01) {
						//mc.thePlayer.motionX *= 1.007;
						//mc.thePlayer.motionZ *= 1.007;
					}
					if(mc.thePlayer.onGround)
						mc.gameSettings.keyBindJump.pressed = true;
					
					if(mc.thePlayer.onGround)
						mc.thePlayer.setSpeed(speed.getValue());
				}else {
					mc.thePlayer.motionX = 0;
					mc.thePlayer.motionZ = 0;
				}
				break;
			}
		}
	}

}
