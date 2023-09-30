package juul.inject.wrappers;

import java.awt.image.BufferedImage;

import juul.Juul;
import juul.inject.LUT.Version;
import juul.inject.Wrapper;

public class SlotWrapper extends Wrapper {

	
	public SlotWrapper(Object instance) {
		super("net.minecraft.inventory.Slot", instance);
	}

	public Object getStack() { return getMethodPrimitive("getStack"); }
}
