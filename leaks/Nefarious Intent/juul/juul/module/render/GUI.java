package juul.module.render;

import org.lwjgl.input.Keyboard;

import juul.Juul;
import juul.inject.wrappers.MinecraftWrapper;
import juul.module.Module;

public class GUI extends Module {

	public GUI() {
		super("GUI", "Gui for changing configuration", Keyboard.KEY_RSHIFT, Category.RENDER);
	}
	
	public void onEnable() {
		mc.displayGuiScreen(Juul.INSTANCE.gui);
	}
	
}
