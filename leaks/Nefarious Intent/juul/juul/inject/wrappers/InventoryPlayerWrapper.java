package juul.inject.wrappers;

import java.awt.image.BufferedImage;

import juul.Juul;
import juul.inject.LUT.Version;
import juul.inject.Wrapper;

public class InventoryPlayerWrapper extends Wrapper {
	
	public InventoryPlayerWrapper(Object instance) {
		super("net.minecraft.entity.player.InventoryPlayer", instance);
	}
	
	public Object getStackInSlot(int id) { return getMethodPrimitive("getStackInSlot", id); }
	
}
