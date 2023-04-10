//From Skizzme#0001 (dont delete this or bad)
package crispy.util.render.shaders;

import com.google.gson.JsonSyntaxException;
import crispy.util.render.gui.Draw;
import crispy.util.render.gui.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.client.shader.Shader;
import net.minecraft.client.shader.ShaderGroup;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.io.IOException;

public class BlurUtil {

    public static final Minecraft mc = Minecraft.getMinecraft();
    public static ShaderGroup shaderGroup;
    public static Framebuffer framebuffer;

    public static int lastFactor;
    public static int lastWidth;
    public static int lastHeight;

    public static void init() {
        try {
            shaderGroup = new ShaderGroup(mc.getTextureManager(), mc.getResourceManager(), mc.getFramebuffer(), new ResourceLocation("Client/gui/blur.json"));
            shaderGroup.createBindFramebuffers(mc.displayWidth, mc.displayHeight);
            framebuffer = shaderGroup.mainFramebuffer;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void setValues(double strength) {
        for (int i = 0; i < 4; i++) {
            try{
                ((Shader)shaderGroup.listShaders.get(i)).getShaderManager().getShaderUniform("Radius").set((float)strength);
            }catch(Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void blur(double x, double y, double areaWidth, double areaHeight, int blurStrength) {

//        blur(0);

        GlStateManager.disableAlpha();
        GlStateManager.enableBlend();
        Draw.drawRoundedRect(x, y, x + areaWidth, y + areaHeight, 3);
        GlStateManager.enableBlend();
        blur(blurStrength);


    }

    private static boolean sizeHasChanged(int scaleFactor, int width, int height) {
        return (lastFactor != scaleFactor || lastWidth != width || lastHeight != height);
    }

    public static void blur(int blurStrength) {
        ScaledResolution scaledResolution = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
        final int scaleFactor = scaledResolution.getScaleFactor();
        final int width = scaledResolution.getScaledWidth();
        final int height = scaledResolution.getScaledHeight();

        if (sizeHasChanged(scaleFactor, width, height) || framebuffer == null || shaderGroup == null) {
            init();
        }

        BlurUtil.lastFactor = scaleFactor;
        lastWidth = width;
        lastHeight = height;

        setValues(blurStrength);
        framebuffer.bindFramebuffer(true);

        shaderGroup.loadShaderGroup(mc.timer.renderPartialTicks);
        mc.getFramebuffer().bindFramebuffer(true);
        GlStateManager.enableAlpha();
    }

    public static void blurMove(double x, double y, double areaWidth, double areaHeight, int blurStrength) {
        ScaledResolution scaledResolution = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
        int scaleFactor = scaledResolution.getScaleFactor();
        int width = scaledResolution.getScaledWidth();
        int height = scaledResolution.getScaledHeight();

        if (sizeHasChanged(scaleFactor, width, height) || framebuffer == null || shaderGroup == null) {
            init();
        }

        BlurUtil.lastFactor = scaleFactor;
        lastWidth = width;
        lastHeight = height;

        setValues(blurStrength);
        framebuffer.bindFramebuffer(true);
        GlStateManager.disableAlpha();
        GlStateManager.enableBlend();
        RenderUtil.drawRoundedRect(x, y, areaWidth, areaHeight, 3, 0x000000);
        GlStateManager.enableBlend();
        shaderGroup.loadShaderGroup(mc.timer.renderPartialTicks);

        shaderGroup.loadShaderGroup(mc.timer.renderPartialTicks);

        mc.getFramebuffer().bindFramebuffer(true);
        GL11.glDisable(GL11.GL_SCISSOR_TEST);

        mc.getFramebuffer().bindFramebuffer(true);

        GlStateManager.enableAlpha();

    }

}