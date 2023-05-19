package club.marsh.bloom.impl.utils.render;

import com.jhlabs.image.GaussianFilter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.client.shader.ShaderGroup;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;

public class BloomHandler {

    private final Minecraft mc;
    private final HashMap<Integer, Integer> shadowCache;
    private ShaderGroup shaderGroup;
    private Framebuffer framebuffer;
    private int lastFactorBlur;
    private int lastWidthBlur;
    private int lastHeightBlur;
    private int lastFactorBuffer;
    private int lastWidthBuffer;
    private int lastHeightBuffer;
    private ShaderGroup blurShaderGroupBuffer;
    private Framebuffer blurBuffer;


    public BloomHandler() {
        mc = Minecraft.getMinecraft();
        this.shadowCache = new HashMap<Integer, Integer>();
        (this.blurBuffer = new Framebuffer(mc.displayWidth, mc.displayHeight, false)).setFramebufferColor(0.0f, 0.0f, 0.0f, 0.0f);
        final ScaledResolution scaledResolution = new ScaledResolution(mc);
        final int scaleFactor = scaledResolution.getScaleFactor();
        final int width = scaledResolution.getScaledWidth();
        final int height = scaledResolution.getScaledHeight();
        final int n = scaleFactor;
        this.lastFactorBuffer = n;
        this.lastFactorBlur = n;
        final int n2 = width;
        this.lastWidthBuffer = n2;
        this.lastWidthBlur = n2;
        final int n3 = height;
        this.lastHeightBuffer = n3;
        this.lastHeightBlur = n3;
    }

    public void init() {
        try {
            (this.shaderGroup = new ShaderGroup(mc.getTextureManager(), mc.getResourceManager(), mc.getFramebuffer(), new ResourceLocation(""))).createBindFramebuffers(mc.displayWidth, mc.displayHeight);
            this.framebuffer = this.shaderGroup.mainFramebuffer;
        }
        catch (Exception exc) {
            exc.printStackTrace();
        }
    }




    private void loadShader(final ResourceLocation resourceLocationIn, final Framebuffer framebuffer) {
        if (OpenGlHelper.isFramebufferEnabled()) {
            try {
                (this.blurShaderGroupBuffer = new ShaderGroup(mc.getTextureManager(), mc.getResourceManager(), framebuffer, resourceLocationIn)).createBindFramebuffers(mc.displayWidth, mc.displayHeight);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }





    public void bloom(final int x, final int y, final int width, final int height, final int blurRadius, final int bloomAlpha, final boolean ignoreModule) {
        this.bloom(x, y, width, height, blurRadius, new Color(0, 0, 0, bloomAlpha), ignoreModule);
    }

    public void bloom(final int x, final int y, final int width, final int height, final int blurRadius, final int bloomAlpha) {
        this.bloom(x, y, width, height, blurRadius, new Color(0, 0, 0, bloomAlpha), false);
    }

    public void bloom(final int x, final int y, final int width, final int height, final int blurRadius, final Color color) {
        this.bloom(x, y, width, height, blurRadius, color, false);
    }

    public void bloom(int x, int y, int width, int height, final int blurRadius, final Color color, final boolean ignoreModule) {
        GlStateManager.pushAttrib();
        GlStateManager.pushMatrix();
        GlStateManager.alphaFunc(516, 0.01f);
        height = Math.max(0, height);
        width = Math.max(0, width);
        width += blurRadius * 2;
        height += blurRadius * 2;
        x -= blurRadius;
        y -= blurRadius;
        final float _X = x - 0.25f;
        final float _Y = y + 0.25f;
        final int identifier = width * height + width + color.hashCode() * blurRadius + blurRadius;
        GL11.glEnable(3553);
        GL11.glDisable(2884);
        GL11.glEnable(3008);
        GL11.glEnable(3042);
        if (this.shadowCache.containsKey(identifier)) {
            final int texId = this.shadowCache.get(identifier);
            GlStateManager.bindTexture(texId);
        }
        else {
            final BufferedImage original = new BufferedImage(width, height, 2);
            final Graphics g = original.getGraphics();
            g.setColor(color);
            g.fillRect(blurRadius, blurRadius, width - blurRadius * 2, height - blurRadius * 2);
            g.dispose();
            final GaussianFilter op = new GaussianFilter((float)blurRadius);
            final BufferedImage blurred = op.filter(original, null);
            final int texId = TextureUtil.uploadTextureImageAllocate(TextureUtil.glGenTextures(), blurred, true, false);
            this.shadowCache.put(identifier, texId);
        }
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glBegin(7);
        GL11.glTexCoord2f(0.0f, 0.0f);
        GL11.glVertex2d(_X, _Y);
        GL11.glTexCoord2f(0.0f, 1.0f);
        GL11.glVertex2d(_X, _Y + height);
        GL11.glTexCoord2f(1.0f, 1.0f);
        GL11.glVertex2d(_X + width, _Y + height);
        GL11.glTexCoord2f(1.0f, 0.0f);
        GL11.glVertex2d(_X + width, _Y);
        GL11.glEnd();
        GL11.glDisable(3553);
        GL11.glEnable(2884);
        GL11.glDisable(3008);
        GL11.glDisable(3042);
        GlStateManager.popAttrib();
        GlStateManager.popMatrix();
    }

    private boolean sizeHasChangedBlur(final int scaleFactor, final int width, final int height) {
        return this.lastFactorBlur != scaleFactor || this.lastWidthBlur != width || this.lastHeightBlur != height;
    }

    private boolean sizeHasChangedBuffer(final int scaleFactor, final int width, final int height) {
        return this.lastFactorBuffer != scaleFactor || this.lastWidthBuffer != width || this.lastHeightBuffer != height;
    }

    public void scissor(double x, double y, double width, double height) {
        final ScaledResolution sr = new ScaledResolution(mc);
        final double scale = sr.getScaleFactor();
        y = sr.getScaledHeight() - y;
        x *= scale;
        y *= scale;
        width *= scale;
        height *= scale;
        GL11.glScissor((int)x, (int)(y - height), (int)width, (int)height);
    }
}