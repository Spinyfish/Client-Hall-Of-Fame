package axolotl.cheats.modules.impl.render;

import org.lwjgl.input.Keyboard;

public class ClickGui extends axolotl.cheats.modules.Module {

	public axolotl.ui.ClickGUI.dropDown.ClickGui clickGui;
	
	public ClickGui() {
		super("ClickGUI", Keyboard.KEY_RSHIFT, Category.RENDER, false);
	}

	public void onEnable() {
		if(clickGui == null)
			clickGui = new axolotl.ui.ClickGUI.dropDown.ClickGui();
		mc.displayGuiScreen(clickGui);
	}
	
	
}
