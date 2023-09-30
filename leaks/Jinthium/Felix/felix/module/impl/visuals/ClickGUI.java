package felix.module.impl.visuals;

import org.lwjgl.input.Keyboard;

import felix.api.annotations.Handler;
import felix.events.render.EventRender2D;
import felix.gui.click.ClickGui;
import felix.gui.click.Panel;
import felix.module.Module;

public class ClickGUI extends Module {
	
	public ClickGUI() {
		super("ClickGUI", Keyboard.KEY_RSHIFT, ModuleCategory.VISUALS);
	}
	
	public void onEnable() {
		toggle();
		mc.displayGuiScreen(new Panel());
		super.onEnable();
	}
	
	public void onDisable() {
		super.onDisable();
	}
}
