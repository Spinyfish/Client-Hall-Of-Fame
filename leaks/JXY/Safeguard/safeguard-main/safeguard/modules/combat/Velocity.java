package intentions.modules.combat;

import org.lwjgl.input.Keyboard;

import intentions.modules.Module;
import intentions.settings.ModeSetting;
import intentions.settings.NumberSetting;
import intentions.util.TeleportUtils;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;

public class Velocity extends Module {
	
	public static NumberSetting horizontal = new NumberSetting("Horizontal", 0, 0, 500, 5);
	public static NumberSetting vertical = new NumberSetting("Vertical", 0, 0, 500, 5);
	
	public static ModeSetting mode = new ModeSetting("Mode", "Normal", new String[] {"Normal", "Packet"});

	public Velocity() {
		super("Velocity", Keyboard.KEY_NONE, Category.COMBAT, "Prevents knockback", true);
		this.addSettings(horizontal, vertical, mode);
	}
	
	public static boolean velocity;
	
	public void onEnable() {
		velocity = true;
	}
	public void onDisable() {
		velocity = true;
	}
	
	public void onUpdate() {
		
		if(mode.getMode().equalsIgnoreCase("Packet") || !this.toggled) return;
		
		double h = horizontal.getValue() / 100;
		double v = vertical.getValue() / 100;
		
		if(mc.thePlayer.hurtTime == mc.thePlayer.maxHurtTime - 1 && mc.thePlayer.maxHurtTime - 1 > 0) {
			
			mc.thePlayer.motionZ *= h;
			mc.thePlayer.motionX *= h;
			mc.thePlayer.motionY *= v;
		}
	}
}
