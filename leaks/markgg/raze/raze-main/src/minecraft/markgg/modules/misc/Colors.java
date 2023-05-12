package markgg.modules.misc;

import org.lwjgl.input.Keyboard;

import markgg.Client;
import markgg.modules.Module;
import markgg.modules.Module.Category;
import markgg.settings.ModeSetting;
import markgg.settings.NumberSetting;

public class Colors extends Module {

	public ModeSetting colorMode = new ModeSetting("Mode", this, "Rainbow", "Rainbow", "Custom");
	public NumberSetting red1 = new NumberSetting("Red", this, 0, 0, 255, 1);
	public NumberSetting green1 = new NumberSetting("Green", this, 0, 0, 255, 1);
	public NumberSetting blue1 = new NumberSetting("Blue", this, 0, 0, 255, 1);
	
	public Colors() {
	    super("Colors", "Change GUI colors", 0, Category.MISC);
	    addSettings(colorMode, red1, green1, blue1);
	}
	
}
