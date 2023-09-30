package intentions.modules.world;

import intentions.Client;
import intentions.modules.Module;
import intentions.modules.movement.Flight;
import intentions.settings.NumberSetting;
import intentions.settings.Setting;
import net.minecraft.client.Minecraft;

public class TimerHack extends Module {
	public static boolean Timer;

	public static NumberSetting timerSpeed = new NumberSetting("Timer", 1.0D, 0.5D, 10.0D, 0.5D);

	public TimerHack() {
		super("Timer", 0, Category.WORLD, "Speeds up your game by speeding up your packets", true);
		addSettings(new Setting[] { (Setting) timerSpeed });
	}

	public static Minecraft mc = Minecraft.getMinecraft();

	public void onEnable() {
		Timer = true;
	}

	public void onDisable() {
		Timer = false;
		mc.timer.timerSpeed = 1.0F + (Flight.type.getMode().toString().equalsIgnoreCase("Redesky") ? 0.3F : 0.0F);
	}

	public void onTick() {
		if (this.toggled) {
			mc.timer.timerSpeed = (float) timerSpeed.getValue();
		}
	}
}
