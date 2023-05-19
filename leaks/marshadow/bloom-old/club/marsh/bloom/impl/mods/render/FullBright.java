package club.marsh.bloom.impl.mods.render;

import club.marsh.bloom.api.module.Category;
import club.marsh.bloom.api.module.Module;
import club.marsh.bloom.api.value.ModeValue;
import org.lwjgl.input.Keyboard;

public class FullBright extends Module {
	public static boolean on = false;
	public static ModeValue mode = new ModeValue("Mode","Normal", new String[] {"Normal", "Inverse"});
	public FullBright() {
		super("Full Bright",Keyboard.KEY_NONE,Category.VISUAL);
	}

	@Override
	public void addModesToModule() {
		autoSetName(mode);
	}

	@Override
	public void onEnable() {
		on = true;
	}
	@Override
	public void onDisable() {
		on = false;
	}
}
