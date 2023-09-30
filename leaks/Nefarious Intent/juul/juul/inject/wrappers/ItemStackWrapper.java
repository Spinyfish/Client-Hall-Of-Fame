package juul.inject.wrappers;

import java.awt.image.BufferedImage;

import juul.Juul;
import juul.inject.LUT.Version;
import juul.inject.Wrapper;

public class ItemStackWrapper extends Wrapper {

	
	public ItemStackWrapper(Object instance) {
		super("net.minecraft.item.ItemStack", instance);
	}
	
	public Object getItem() { return getMethodPrimitive("getItem"); }
	
}
