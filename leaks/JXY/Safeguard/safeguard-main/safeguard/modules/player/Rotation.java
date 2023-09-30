package intentions.modules.player;

import intentions.Client;
import intentions.modules.Module;
import intentions.settings.NumberSetting;

public class Rotation extends Module {

	public NumberSetting yaw = new NumberSetting("Yaw", 90, 0, 360, 1);
	public NumberSetting pitch = new NumberSetting("Pitch", 0, -90, 90, 1); 
	
	public Rotation() {
		super("Rotation", 0, Category.PLAYER, "Rotates your yaw and pitch to specified values", false);
		this.addSettings(yaw, pitch);
	}
	
	public void onEnable() {
		mc.thePlayer.rotationYaw = (float) yaw.getValue();
		mc.thePlayer.rotationPitch = (float) pitch.getValue();
		this.toggle();
		Client.addChatMessage("Changed yaw & pitch values!");
	}
	
}
