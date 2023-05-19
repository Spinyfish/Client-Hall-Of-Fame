package club.marsh.bloom.impl.mods.combat;


import org.lwjgl.input.Keyboard;

import club.marsh.bloom.api.module.Category;
import club.marsh.bloom.api.module.Module;
import club.marsh.bloom.api.value.NumberValue;
import net.minecraft.client.renderer.EntityRenderer;


public class Reach extends Module {
	public Reach() {
		super("Reach",Keyboard.KEY_NONE,Category.COMBAT);
	}
	public static NumberValue<Double> reach = new NumberValue<>("Reach",3.0D,0.1D,7D);
	@Override
	public void onDisable() {
		EntityRenderer.reach = 3.0D;
	}
	@Override
	public void onEnable() {
		EntityRenderer.reach = reach.getValDouble();
	}


}
