package juul.inject.wrappers;

import java.awt.image.BufferedImage;

import juul.Juul;
import juul.inject.LUT.Version;
import juul.inject.Wrapper;

public class TessellatorWrapper extends Wrapper {

	public TessellatorWrapper() {
		super("net.minecraft.client.renderer.Tessellator",
				Juul.INSTANCE.getReflectionHelper().getWithClass(Juul.INSTANCE.getLookupTable().getMethodName("net.minecraft.client.renderer.Tessellator", "getInstance"),
						Juul.INSTANCE.getLookupTable().getClassName("net.minecraft.client.renderer.Tessellator"), null));
	}

	public Object getWorldRenderer() { return getMethodPrimitive("getWorldRenderer"); }
	
	public int draw() { return (int) getMethodPrimitive("draw"); }
	
}
