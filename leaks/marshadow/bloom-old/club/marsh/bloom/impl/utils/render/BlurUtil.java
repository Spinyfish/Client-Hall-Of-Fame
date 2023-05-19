package club.marsh.bloom.impl.utils.render;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.client.shader.ShaderGroup;
import net.minecraft.util.ResourceLocation;

public class BlurUtil
{
	private static ResourceLocation shader = new ResourceLocation("shaders/post/blur.json");
	private static int lastScale, lastScaleWidth, lastScaleHeight;
	private static Minecraft mc = Minecraft.getMinecraft();
	private static Framebuffer framebuffer;
	private static ShaderGroup shaderGroup;
	
	public static void initialize()
	{
		try
		{
			shaderGroup = new ShaderGroup(mc.getTextureManager(), mc.getResourceManager(), mc.getFramebuffer(), shader);
			shaderGroup.createBindFramebuffers(mc.displayWidth, mc.displayHeight);
			framebuffer = shaderGroup.mainFramebuffer;
		}
		
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	private static void setShaderConfigs(float intensity, float blurWidth, float blurHeight)
	{
		shaderGroup.getShaders().get(0).getShaderManager().getShaderUniform("Radius").set(intensity);
		shaderGroup.getShaders().get(1).getShaderManager().getShaderUniform("Radius").set(intensity);
		shaderGroup.getShaders().get(0).getShaderManager().getShaderUniform("BlurDir").set(blurWidth, blurHeight);
		shaderGroup.getShaders().get(1).getShaderManager().getShaderUniform("BlurDir").set(blurHeight, blurWidth);
	}
	
	public static void blur(float intensity)
	{
		ScaledResolution scaledResolution = new ScaledResolution(mc);
		int factor1 = scaledResolution.getScaleFactor();
		int factor2 = scaledResolution.getScaledWidth();
		int factor3 = scaledResolution.getScaledHeight();
		
		if (lastScale != factor1 || lastScaleWidth != factor2 || lastScaleHeight != factor3 || framebuffer == null || shaderGroup == null)
		{
			initialize();
		}
		
		lastScale = factor1;
		lastScaleWidth = factor2;
		lastScaleHeight = factor3;
		setShaderConfigs(intensity, 0, 1);
		framebuffer.bindFramebuffer(true);
		shaderGroup.loadShaderGroup(mc.timer.renderPartialTicks);
		mc.getFramebuffer().bindFramebuffer(true);
	}
	
	public static void blur(int x, int y, int width, int height, float intensity)
	{
		GL11.glEnable(GL11.GL_SCISSOR_TEST);
		scissor(x, y, width, height);
		blur(intensity);
		GL11.glDisable(GL11.GL_SCISSOR_TEST);
	}
	
	private static void scissor(int x, int y, int width, int height)
	{
		ScaledResolution scaledResolution = new ScaledResolution(mc);
		x *= scaledResolution.getScaleFactor();
		y *= scaledResolution.getScaleFactor();
		width *= scaledResolution.getScaleFactor();
		height *= scaledResolution.getScaleFactor();
		GL11.glScissor(x, mc.displayHeight - height, width - x, height - y);
	}
}
