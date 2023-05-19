package club.marsh.bloom.impl.mods.combat;

import club.marsh.bloom.api.module.Category;
import club.marsh.bloom.api.module.Module;
import org.lwjgl.input.Keyboard;

public class KeepSprint extends Module {
	public static boolean on = false;
	public KeepSprint() {
		super("KeepSprint",Keyboard.KEY_NONE,Category.COMBAT);
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
