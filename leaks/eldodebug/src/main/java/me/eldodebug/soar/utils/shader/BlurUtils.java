package me.eldodebug.soar.utils.shader;

import me.eldodebug.soar.Soar;
import me.eldodebug.soar.management.mods.impl.MenuBlurMod;
import me.eldodebug.soar.utils.interfaces.IMixinMinecraft;
import me.eldodebug.soar.utils.interfaces.IMixinShaderGroup;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.client.shader.ShaderGroup;
import net.minecraft.util.ResourceLocation;

public class BlurUtils {
	
	private static ShaderGroup blurShader;
	private static Minecraft mc = Minecraft.getMinecraft();
	private static Framebuffer buffer;
	
    private static float lastScale = 0;
    private static float lastScaleWidth = 0;
    private static float lastScaleHeight = 0;
    
    private static void reinitShader() {
		try {
			buffer = new Framebuffer(mc.displayWidth, mc.displayHeight, true);
			buffer.setFramebufferColor(0.0F, 0.0F, 0.0F, 0.0F);
			blurShader = new ShaderGroup(mc.getTextureManager(), mc.getResourceManager(), mc.getFramebuffer(), new ResourceLocation("shaders/post/blurArea.json"));
			blurShader.createBindFramebuffers(mc.displayWidth, mc.displayHeight);
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    public static void drawBlurScreen() {
    	
    	ScaledResolution sr = new ScaledResolution(mc);
    	
        int factor = sr.getScaleFactor();
        int factor2 = sr.getScaledWidth();
        int factor3 = sr.getScaledHeight();
        int x = 0;
        int y = 0;
        int width = sr.getScaledWidth();
        int height = sr.getScaledHeight();
        
        if (lastScale != factor || lastScaleWidth != factor2 || lastScaleHeight != factor3) {
            reinitShader();
        }
        
        lastScale = factor;
        lastScaleWidth = factor2;
        lastScaleHeight = factor3;
        
        ((IMixinShaderGroup)blurShader).getListShaders().get(0).getShaderManager().getShaderUniform("BlurXY").set(x * (sr.getScaleFactor() / 2.0F), (factor3 - height) * (sr.getScaleFactor() / 2.0F));
        ((IMixinShaderGroup)blurShader).getListShaders().get(1).getShaderManager().getShaderUniform("BlurXY").set(x * (sr.getScaleFactor() / 2.0F), (factor3 - height) * (sr.getScaleFactor() / 2.0F));
        ((IMixinShaderGroup)blurShader).getListShaders().get(0).getShaderManager().getShaderUniform("BlurCoord").set((width - x) * (sr.getScaleFactor() / 2.0F), (height - y) * (sr.getScaleFactor() / 2.0F));
        ((IMixinShaderGroup)blurShader).getListShaders().get(1).getShaderManager().getShaderUniform("BlurCoord").set((width - x) * (sr.getScaleFactor() / 2.0F), (height - y) * (sr.getScaleFactor() / 2.0F));
        ((IMixinShaderGroup)blurShader).getListShaders().get(0).getShaderManager().getShaderUniform("Radius").set((int) Soar.instance.settingsManager.getSettingByClass(MenuBlurMod.class, "Radius").getValDouble());
        ((IMixinShaderGroup)blurShader).getListShaders().get(1).getShaderManager().getShaderUniform("Radius").set((int) Soar.instance.settingsManager.getSettingByClass(MenuBlurMod.class, "Radius").getValDouble());
        blurShader.loadShaderGroup(((IMixinMinecraft)mc).getTimer().renderPartialTicks);
        mc.getFramebuffer().bindFramebuffer(true);
    }
}