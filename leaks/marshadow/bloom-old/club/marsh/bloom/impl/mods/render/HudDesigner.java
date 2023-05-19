package club.marsh.bloom.impl.mods.render;

import club.marsh.bloom.Bloom;
import club.marsh.bloom.api.module.Category;
import club.marsh.bloom.api.module.Module;
import org.lwjgl.input.Keyboard;


public class HudDesigner extends Module {
	public HudDesigner() {
		super("Hud Designer",Keyboard.KEY_NONE,Category.VISUAL);
	}
	@Override
	public void onEnable() {
		mc.displayGuiScreen(Bloom.INSTANCE.hudDesignerUI);
		this.setToggled(false);
	}
}
