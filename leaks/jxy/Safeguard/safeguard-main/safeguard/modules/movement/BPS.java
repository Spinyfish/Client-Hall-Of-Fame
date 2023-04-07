package intentions.modules.movement;

import org.lwjgl.input.Keyboard;

import intentions.events.Event;
import intentions.events.listeners.EventRenderGUI;
import intentions.modules.Module;
import intentions.settings.NumberSetting;
import intentions.util.Timer;
import net.minecraft.client.gui.FontRenderer;

public class BPS extends Module {

	public NumberSetting sec = new NumberSetting("Sec", 1, 1, 10, 0.5);
	
	public BPS() {
		super("BPS", Keyboard.KEY_NONE, Category.MOVEMENT, "Tracks your blocks per second", true);
		this.addSettings(sec);
	}
	
	private int secBPS;
	private double secPosZ = 0, secPosX = 0;
	private double xDis = 0, zDis = 0, bps = 0, hbps = 0;;

	private Timer timer = new Timer();
	
	public void onDisable() {
		hbps = 0;
		bps = 0;
	}
	
	public void onUpdate() {
		if(timer.hasTimeElapsed(50, true))
		secBPS++;
	}
	
	public void onEvent(Event e) {
		if(e instanceof EventRenderGUI) {
			
			if(mc.thePlayer == null || mc.theWorld == null) return;
			
			FontRenderer fr = mc.fontRendererObj;
			
			double roundedBps = Math.round(bps * 100);
			double highestRoundBps = Math.round(hbps * 100);
			
			if(bps > hbps) {
				hbps = bps;
			}
			
			fr.drawStringWithShadow((roundedBps / 100) + " BP" + (sec.getValue() == 1.0 ? "S" : sec.getValue() + "S"), 15, 100 + ((Module.Category.values().length) * 3), 0x0FF4500);
			fr.drawStringWithShadow((highestRoundBps / 100) + " HBP" + (sec.getValue() == 1.0 ? "S" : sec.getValue() + "S"), 15, 110 + ((Module.Category.values().length) * 3), 0x0FF4500);
			
			if(secBPS < sec.getValue() * 20 / 4) return;
			
			secBPS = 0;
			
			if(secPosX != 0 && secPosZ != 0) {
				xDis = (secPosX > mc.thePlayer.posX ?
						secPosX - mc.thePlayer.posX :
							mc.thePlayer.posX - secPosX);
				zDis = (secPosZ > mc.thePlayer.posZ ?
						secPosZ - mc.thePlayer.posZ :
							mc.thePlayer.posZ - secPosZ);
			}
			secPosZ = mc.thePlayer.posZ;
			secPosX = mc.thePlayer.posX;
			
			bps = (xDis + zDis) * 4;
		}
	}
	
}
