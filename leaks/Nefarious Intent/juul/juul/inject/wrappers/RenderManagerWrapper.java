package juul.inject.wrappers;

import juul.Juul;
import juul.inject.Wrapper;

public class RenderManagerWrapper extends Wrapper {

	public RenderManagerWrapper(Object instance) {
		super("net.minecraft.client.renderer.entity.RenderManager", instance);
	}
	
	public double viewerPosX() { return (double) getField("viewerPosX"); }
	
	public double viewerPosY() { return (double) getField("viewerPosY"); }
	
	public double viewerPosZ() { return (double) getField("viewerPosZ");}

}
