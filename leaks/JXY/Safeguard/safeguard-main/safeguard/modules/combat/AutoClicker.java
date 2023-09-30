package intentions.modules.combat;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import intentions.Client;
import intentions.modules.Module;
import intentions.settings.NumberSetting;
import intentions.util.Timer;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C02PacketUseEntity;

public class AutoClicker extends Module {

	public AutoClicker() {
		super("AutoClicker", 0, Category.COMBAT, "Automatically clicks for you when you hold attack button", true);
		this.addSettings(speed);
	}
	
	public NumberSetting speed = new NumberSetting("Speed", 10, 1, 20, 1);
	
	private final Timer timer = new Timer();
	
	public void onUpdate() {
		if(mc.thePlayer == null || mc.theWorld == null || !this.toggled)return;
		try {
			if(Keyboard.isKeyDown(mc.gameSettings.keyBindAttack.getKeyCode())) {
				
				swing();
				
			}
		} catch(IndexOutOfBoundsException e) {
			
			if(Mouse.isButtonDown(100 + mc.gameSettings.keyBindAttack.getKeyCode())) {
				
				swing();
				
			}
			
		}
	}
	
	public void swing() {
		if(timer.hasTimeElapsed((long) (1000.0D / speed.getValue() + (Math.floor(Math.random() * 20) - 10)), true)) {
			
			mc.thePlayer.swingItem();
			
			if(mc.objectMouseOver.entityHit != null) {
			
				 this.mc.thePlayer.sendQueue.addToSendQueue((Packet)new C02PacketUseEntity((Entity)mc.objectMouseOver.entityHit, C02PacketUseEntity.Action.ATTACK));
				
			}
			
		}
	}
	
}
