package markgg.modules.movement;

import markgg.Client;
import markgg.events.Event;
import markgg.events.listeners.EventUpdate;
import markgg.modules.Module;
import markgg.settings.BooleanSetting;
import markgg.settings.NumberSetting;
import markgg.ui.notifs.Notification;
import markgg.ui.notifs.NotificationManager;
import markgg.ui.notifs.NotificationType;
import markgg.util.MoveUtil;
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition;

public class CustomSpeed extends Module {

	private int onGroundPackets;
	
	public NumberSetting moreOnGroundPackets = new NumberSetting("OnGround Packets", this, 0, 0, 20, 1);
	public NumberSetting strafeSpeed = new NumberSetting("Strafe Speed", this, 0.3, 0, 1, 0.1);
	public NumberSetting motion = new NumberSetting("Motion Speed", this, 0.42, 0, 2, 0.01);
	public NumberSetting boost = new NumberSetting("Boost", this, 1, 0, 2, 0.1);
	public NumberSetting timerSpeed = new NumberSetting("Timer Speed", this, 1, 0, 10, 0.01);
	public BooleanSetting debugMode = new BooleanSetting("Debug Mode", this, false);
	public BooleanSetting autoJump = new BooleanSetting("Auto Jump", this, true);
	public BooleanSetting stop = new BooleanSetting("Auto Stop", this, true);

	public CustomSpeed(){
		super("CSpeed", "Custom speed", 0, Category.MOVEMENT);
		addSettings(moreOnGroundPackets, strafeSpeed, motion, boost, timerSpeed, debugMode, autoJump, stop);
	}

	public void onEnable() {
		NotificationManager.show(new Notification(NotificationType.ENABLE, getName() + " was enabled!", 1));
		Client.addChatMessage("If your motion isn't high enough, all of the onGround packets might not send!");
	}

	public void onDisable(){
		NotificationManager.show(new Notification(NotificationType.DISABLE, getName() + " was disabled!", 1));
		mc.timer.timerSpeed = 1;
		mc.thePlayer.speedInAir = 0.02F;
	}

	public void onEvent(Event e){
		if(e instanceof EventUpdate && e.isPre()) {
			double x = mc.thePlayer.posX, y = mc.thePlayer.posY, z = mc.thePlayer.posZ;
			mc.timer.timerSpeed = (float) timerSpeed.getValue();
			if(!MoveUtil.isMoving() && stop.isEnabled())
				MoveUtil.stop();
			mc.thePlayer.motionX *= boost.getValue();
			mc.thePlayer.motionZ *= boost.getValue();
			if(strafeSpeed.getValue() != 0)
				MoveUtil.strafe((float) strafeSpeed.getValue());
			if(mc.thePlayer.onGround) {
				if(autoJump.isEnabled() && MoveUtil.isMoving())
					mc.thePlayer.motionY = motion.getValue();
				onGroundPackets = 0;
			} else if(!mc.thePlayer.onGround)
				onGroundPackets++;

			if(onGroundPackets < moreOnGroundPackets.getValue()) {
				if(debugMode.isEnabled())
					Client.addChatMessage("Sent onGround Packets sucsesfully!");
				mc.thePlayer.sendQueue.addToSendQueue(new C04PacketPlayerPosition(x, y, z, true));
			} else {

			}
		}
	}

}
