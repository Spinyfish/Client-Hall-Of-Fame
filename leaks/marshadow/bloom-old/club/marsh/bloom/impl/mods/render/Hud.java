package club.marsh.bloom.impl.mods.render;

import java.awt.Color;

import club.marsh.bloom.impl.ui.hud.Component;
import org.lwjgl.input.Keyboard;

import com.google.common.eventbus.Subscribe;

import club.marsh.bloom.Bloom;
import club.marsh.bloom.api.module.Category;
import club.marsh.bloom.api.module.Module;
import club.marsh.bloom.impl.events.Render2DEvent;
import club.marsh.bloom.impl.utils.render.FontRenderer;
import club.marsh.bloom.api.value.NumberValue;


public class Hud extends Module {
	public Hud() {
		super("Hud",Keyboard.KEY_NONE,Category.VISUAL);
	}

	public FontRenderer fr = Bloom.INSTANCE.fontManager.defaultFont;
	public static NumberValue<Float> hue = new NumberValue<Float>("Hue",65F,0F,100F,0);
	public static boolean hudEnabled = false;

    public static Color rgb(long index) {
		if (index == 0) {
			index = 1L;
		}
		//addMessage("hue: " + (System.currentTimeMillis() % 360F / 360F));
		float generatedHue = (float) (((System.currentTimeMillis() / 1.5) + index*25) % (360));
		generatedHue = (generatedHue > 180 ? 360 - generatedHue : generatedHue) + 180;
		generatedHue /= 360;

		Color color = Color.getHSBColor((float) (hue.getValDouble()/100),0.5F, (float) (0.5 + generatedHue/2));
        return color;
    }

	@Override
	public void onEnable() {
		hudEnabled = true;
	}

	@Override
	public void onDisable() {
		hudEnabled = false;
	}

	@Subscribe
	public void onRender2D(Render2DEvent event) {
		if (Bloom.INSTANCE.hudDesignerUI.open)
			return;
		try {
			for (Component component : Bloom.INSTANCE.hudDesigner.components) {
				if (component.isEnabled())
					component.render();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
