package club.marsh.bloom.impl.mods.render;

import org.lwjgl.input.Keyboard;

import club.marsh.bloom.api.module.Category;
import club.marsh.bloom.api.module.Module;
import club.marsh.bloom.api.value.ModeValue;
import club.marsh.bloom.impl.ui.clickgui.dropdown.DropdownUI;
import club.marsh.bloom.impl.ui.clickgui.jelloforsigma.JelloForSigmaUI;
import club.marsh.bloom.impl.ui.clickgui.window.BloomUI;

public class ClickGUI extends Module {
	public BloomUI bloomUI = new BloomUI();
	public DropdownUI dropdownUI = new DropdownUI();
	public JelloForSigmaUI jelloForSigmaUI;

	public ClickGUI() {
		super("Click GUI",Keyboard.KEY_RSHIFT,Category.VISUAL);
	}
	ModeValue clickGuiMode = new ModeValue("Mode", "Dropdown", new String[] {"Dropdown", "Jello For Sigma", "Window"});
	@Override
	public void onEnable() {
		if (jelloForSigmaUI == null)
		{
			jelloForSigmaUI = new JelloForSigmaUI();
		}
		switch (clickGuiMode.getMode()) {
			case "Window":
				mc.displayGuiScreen(bloomUI);
				break;
			case "Jello For Sigma":
				mc.displayGuiScreen(jelloForSigmaUI);
				break;
			case "Dropdown":
				mc.displayGuiScreen(dropdownUI);
				break;
		}
		this.setToggled(false);
	}
}
