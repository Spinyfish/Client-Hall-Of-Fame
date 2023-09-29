package juul.inject.wrappers;

import java.awt.image.BufferedImage;

import juul.Juul;
import juul.inject.LUT.Version;
import juul.inject.Wrapper;

public class DynamicTextureWrapper extends Wrapper {

	public String parent = "net.minecraft.client.renderer.texture.AbstractTexture";
	
	public DynamicTextureWrapper(BufferedImage image) {
		super("net.minecraft.client.renderer.texture.DynamicTexture",
				Juul.INSTANCE.getReflectionHelper().construct(Juul.INSTANCE.getLookupTable().getClassName("net.minecraft.client.renderer.texture.DynamicTexture"), new Object[] { image }, new Class[] { BufferedImage.class }));
	}
	
	public int getGlTextureId() {
		return (int) Juul.INSTANCE.getReflectionHelper().getOnlyPrimitives(getGlTextureIdName(), getClassInstance());
	}
	
	public String getGlTextureIdName() {
		return Juul.INSTANCE.getLookupTable().getMethodName(parent, "getGlTextureId");
	}

	
}
