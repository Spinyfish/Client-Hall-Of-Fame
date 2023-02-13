package juul.inject.wrappers;

import java.awt.image.BufferedImage;

import juul.Juul;
import juul.inject.LUT.Version;
import juul.inject.Wrapper;

public class EntityPlayerWrapper extends Wrapper {
	
	public EntityPlayerWrapper(Object instance) {
		super("net.minecraft.entity.player.EntityPlayer", instance);
	}
	
	public boolean isUsingItem() { return (boolean) getMethodPrimitive("isUsingItem"); }
	
	public Object inventory() { return getField("inventory"); }

	public Object inventoryContainer() { return getField("inventoryContainer"); }

}
