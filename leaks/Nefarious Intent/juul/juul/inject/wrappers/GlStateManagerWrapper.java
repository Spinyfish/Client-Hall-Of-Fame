package juul.inject.wrappers;

import juul.inject.Wrapper;

public class GlStateManagerWrapper extends Wrapper {
	
	public GlStateManagerWrapper() {
		super("net.minecraft.client.renderer.GlStateManager");
	}


	public void bindTexture(int framebufferTexture) { getWithClassMethod("func_179144_i", framebufferTexture); }
	
	public void color(float red, float green, float blue, float alpha) { getWithClassMethod("color", red, green, blue, alpha); }
	
	public void scale(double x, double y, double z) { getWithClassMethod("scale", x, y, z); }

	public void tryBlendFuncSeparate(int one, int two, int three, int four) { getWithClassMethod("tryBlendFuncSeparate", one, two, three, four); }

	public void blendFunc(int one, int two) { getWithClassMethod("blendFunc", one, two); }

	public void disableBlend() { getWithClassMethod("disableBlend"); }

	public void enableTexture2D() { getWithClassMethod("func_179098_w"); }

	public void disableTexture2D() { getWithClassMethod("func_179090_x"); }

	public void enableBlend() { getWithClassMethod("enableBlend"); }

	public void enableAlpha() { getWithClassMethod("enableAlpha"); }
	
	public void alphaFunc(int func, float ref) { getWithClassMethod("alphaFunc", func, ref); }

	public void pushMatrix() { getWithClassMethod("pushMatrix"); }

	public void popMatrix() { getWithClassMethod("popMatrix"); }

	public void translate(float x, float y, float z) { getWithClassMethod("translate", x, y, z); }

	
}
