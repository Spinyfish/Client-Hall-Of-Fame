
package de.fanta.utils;

import com.google.gson.JsonSyntaxException;
import java.io.IOException;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.client.shader.Shader;
import net.minecraft.client.shader.ShaderGroup;
import net.minecraft.client.shader.ShaderManager;
import net.minecraft.client.shader.ShaderUniform;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Timer;
import org.lwjgl.opengl.GL11;

public class BlurHelper {
	private ResourceLocation resourceLocation = new ResourceLocation("shaders/post/blur2.json");
	private ShaderGroup shaderGroup;
	private Framebuffer framebuffer;
	private int lastFactor;
	private int lastWidth;
	private int lastHeight;
	private Minecraft mc;

	public void init() {
		try {
			mc = Minecraft.getMinecraft();
			shaderGroup = new ShaderGroup(Minecraft.getMinecraft().getTextureManager(),
					Minecraft.getMinecraft().getResourceManager(), mc.getFramebuffer(), resourceLocation);
			shaderGroup.createBindFramebuffers(mc.displayWidth, mc.displayHeight);
			framebuffer = shaderGroup.mainFramebuffer;
			return;
		} catch (JsonSyntaxException | IOException e) {
			e.printStackTrace();
		}
	}

	private void setValues(float strength) {
		((Shader) shaderGroup.getShaders().get(0)).getShaderManager().getShaderUniform("Radius").set(strength);
		((Shader) shaderGroup.getShaders().get(1)).getShaderManager().getShaderUniform("Radius").set(strength);
		((Shader) shaderGroup.getShaders().get(2)).getShaderManager().getShaderUniform("Radius").set(strength);
		((Shader) shaderGroup.getShaders().get(3)).getShaderManager().getShaderUniform("Radius").set(strength);
	}

	public final void blur(float blurStrength) {
		ScaledResolution scaledResolution = new ScaledResolution(mc);
		int scaleFactor = scaledResolution.getScaleFactor();
		int width = scaledResolution.getScaledWidth();
		int height = scaledResolution.getScaledHeight();
		if (lastFactor != scaleFactor || lastWidth != width || lastHeight != height || framebuffer == null
				|| shaderGroup == null) {
			init();
		}
		lastFactor = scaleFactor;
		lastWidth = width;
		lastHeight = height;
		setValues(blurStrength);
		framebuffer.bindFramebuffer(true);
		shaderGroup.loadShaderGroup(mc.timer.renderPartialTicks);
		mc.getFramebuffer().bindFramebuffer(true);
		GlStateManager.enableAlpha();
	}

	public void blurhotbar(float x, float y, float areaWidth, float areaHeight, float blurStrength) {
		ScaledResolution scaledResolution = new ScaledResolution(mc);
		int scaleFactor = scaledResolution.getScaleFactor();
		int width = scaledResolution.getScaledWidth();
		int height = scaledResolution.getScaledHeight();
		if (lastFactor != scaleFactor || lastWidth != width || lastHeight != height || framebuffer == null
				|| shaderGroup == null) {
			init();
		}
		lastFactor = scaleFactor;
		lastWidth = width;
		lastHeight = height;

		GL11.glEnable((int) 3089);
		prepareScissorBox(x, y + 1.0f, areaWidth, areaHeight - 1.0f);
		prepareScissorBox(x, y + 51.0f, areaWidth, areaHeight - 1.0f);
		framebuffer.bindFramebuffer(true);
		shaderGroup.loadShaderGroup(mc.timer.renderPartialTicks);
		setValues(blurStrength);
		mc.getFramebuffer().bindFramebuffer(false);
		GL11.glDisable((int) 3089);
		GlStateManager.enableAlpha();
	}

	public final void blur2(float x, float y, float areaWidth, float areaHeight, float blurStrength) {
		ScaledResolution scaledResolution = new ScaledResolution(mc);
		int scaleFactor = scaledResolution.getScaleFactor();
		int width = scaledResolution.getScaledWidth();
		int height = scaledResolution.getScaledHeight();
		if (lastFactor != scaleFactor || lastWidth != width || lastHeight != height || framebuffer == null
				|| shaderGroup == null) {
			init();
		}
		lastFactor = scaleFactor;
		lastWidth = width;
		lastHeight = height;
		GL11.glPushMatrix();
		GL11.glEnable((int) 3089);
		prepareScissorBox(x, y + 1.0f, areaWidth, areaHeight - 1.0f);

		framebuffer.bindFramebuffer(true);

		shaderGroup.loadShaderGroup(mc.timer.renderPartialTicks);

		setValues(blurStrength);
		mc.getFramebuffer().bindFramebuffer(false);
		GL11.glDisable((int) 3089);
		GL11.glPopMatrix();
		GlStateManager.enableAlpha();

	}

	public void prepareScissorBox(float x, float y, float x2, float y2) {
		ScaledResolution scale = new ScaledResolution(Minecraft.getMinecraft());
		int factor = scale.getScaleFactor();
		GL11.glScissor((int) ((int) (x * (float) factor)),
				(int) ((int) (((float) scale.getScaledHeight() - y2) * (float) factor)),
				(int) ((int) ((x2 - x) * (float) factor)), (int) ((int) ((y2 - y) * (float) factor)));
	}

}
