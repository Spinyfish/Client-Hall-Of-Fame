package intentions.modules.movement;

import intentions.Client;
import intentions.events.Event;
import intentions.events.listeners.EventMotion;
import intentions.modules.Module;
import intentions.settings.NumberSetting;
import intentions.util.BlockUtils;
import intentions.util.PlayerUtil;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;

public class LongJump extends Module{
	
	public NumberSetting ZSpeed = new NumberSetting("Z Speed", 3.5f, 1f, 5f, 0.5f);
	public NumberSetting XSpeed = new NumberSetting("X Speed", 3.5f, 1f, 5f, 0.5f);
	public NumberSetting YSpeed = new NumberSetting("Y Speed", 1.2f, 1f, 3f, 0.1f);
	
	public LongJump() {
		super("LongJump", 0, Category.MOVEMENT, "Makes you jump very far", false);
		this.addSettings(XSpeed, YSpeed, ZSpeed);
	}
	public boolean lastOnGround = true;
	private float startY = 0, ticksOn = 0;
	
	public void onEnable() {
		Client.addChatMessage("Jumped long");
		mc.thePlayer.jump();
	}
	public void onDisable() {
		mc.thePlayer.capabilities.isFlying = false;
		ticksOn = 0;
		startY = 0;
		mc.timer.timerSpeed = 1f;
	}
	
	public void onEvent(Event e) {
		if(e instanceof EventMotion) {
			ticksOn++;
			mc.timer.timerSpeed = (float) 1.25;
			mc.thePlayer.motionY += Math.min(0.07f + (ticksOn / 20000), 0.1);
			mc.thePlayer.setSpeed(0.35f + (ticksOn / 250));
		}
	}
	
}
