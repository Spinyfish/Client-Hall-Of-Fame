package juul.inject.wrappers;

import java.awt.image.BufferedImage;
import java.util.List;

import juul.Juul;
import juul.inject.LUT.Version;
import juul.inject.Wrapper;

public class MouseHelperWrapper extends Wrapper {
	
	public MouseHelperWrapper(Object instance) {
		super("net.minecraft.util.MouseHelper", instance);
	}
	
	public int deltaX() { return (int) getField("deltaX"); }

	public int deltaY() { return (int) getField("deltaY"); }

}
