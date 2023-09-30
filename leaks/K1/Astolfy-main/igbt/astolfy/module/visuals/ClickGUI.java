package igbt.astolfy.module.visuals;

import java.util.ArrayList;

import org.lwjgl.input.Keyboard;

import igbt.astolfy.module.ModuleBase;
import igbt.astolfy.settings.settings.ModeSetting;
import igbt.astolfy.ui.ClickGUI.Compact.CompactGUI;
import igbt.astolfy.ui.ClickGUI.dropDown.ClickGui;

public class ClickGUI extends ModuleBase {

	public ClickGui clickGUI;
	public igbt.astolfy.ui.ClickGUI.dropDownLight.ClickGui dropDownLite;
	public ModeSetting mode;
	
	public ClickGUI() {
		super("ClickGUI", Keyboard.KEY_RSHIFT, Category.VISUALS);
		mode = new ModeSetting("Mode", "Dropdown","DropdownLite");
		addSettings(mode);
	}
	
	public void onEnable() {
		if(clickGUI == null)
			clickGUI = new ClickGui();
		clickGUI.onGuiOpened();
		//if(winGUI == null)
			dropDownLite = new igbt.astolfy.ui.ClickGUI.dropDownLight.ClickGui();
		switch(mode.getCurrentValue()) {
			case "Dropdown":
				mc.displayGuiScreen(clickGUI);
				break;
			case "DropdownLite":
				mc.displayGuiScreen(dropDownLite);
				break;
		}
	}

}
