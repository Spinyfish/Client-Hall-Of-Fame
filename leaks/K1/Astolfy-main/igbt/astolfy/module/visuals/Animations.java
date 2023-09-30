package igbt.astolfy.module.visuals;

import java.util.ArrayList;

import igbt.astolfy.settings.settings.NumberSetting;
import org.lwjgl.input.Keyboard;

import igbt.astolfy.events.Event;
import igbt.astolfy.events.listeners.EventUpdate;
import igbt.astolfy.module.ModuleBase;
import igbt.astolfy.settings.settings.ModeSetting;

public class Animations extends ModuleBase {

	public static ModeSetting mode;
	public static NumberSetting scale = new NumberSetting("Scale", 1, 0.1, 0.1, 1.5);
	public static NumberSetting x = new NumberSetting("X", 1, 0.1, 0.1, 1.5);
	public static NumberSetting y = new NumberSetting("Y", 1, 0.1, 0.1, 1.5);
	public static NumberSetting z = new NumberSetting("Z", 1, 0.1, 0.1, 1.5);
	
	public Animations() {
		super("Animations", Keyboard.KEY_NONE, Category.VISUALS);
		mode = new ModeSetting("Block Mode", "Smooth", "1.7", "Spin", "Test");
		addSettings(mode, scale, x, y, z);
	}
	
	public void onEvent(Event e) {
		if(e instanceof EventUpdate) {
			setSuffix(mode.getCurrentValue());
		}
	}
	
	

}
