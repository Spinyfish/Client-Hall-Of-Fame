package juul.inject.wrappers;

import java.awt.image.BufferedImage;

import juul.Juul;
import juul.inject.LUT.Version;
import juul.inject.Wrapper;

public class WorldRendererWrapper extends Wrapper {
	
	public WorldRendererWrapper(Object instance) {
		super("net.minecraft.client.renderer.WorldRenderer", instance);
	}
	
	public void startDrawingQuads() { getMethodPrimitive("startDrawingQuads"); }
	
	public void addVertex(double x, double y, double z) { getMethodPrimitive("addVertex", x, y, z); }
}
