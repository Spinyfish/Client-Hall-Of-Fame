package club.pulsive.impl.util;

import com.jhlabs.image.GaussianFilter;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.client.shader.ShaderGroup;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;


public class Blurrer {
    private final Minecraft mc = Minecraft.getMinecraft();
    private final HashMap<Integer, Integer> shadowCache = new HashMap();
    private final ResourceLocation blurLocation = new ResourceLocation("shaders/post/blur.json");
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
    private final boolean oldBlur;

    public Blurrer(boolean oldBlur) {
        this.oldBlur = oldBlur;
        this.blurBuffer = new Framebuffer(this.mc.displayWidth, this.mc.displayHeight, false);
        this.blurBuffer.setFramebufferColor(0.0f, 0.0f, 0.0f, 0.0f);
        ScaledResolution scaledResolution = new ScaledResolution(this.mc);
        int scaleFactor = scaledResolution.getScaleFactor();
        int width = scaledResolution.getScaledWidth();
        int height = scaledResolution.getScaledHeight();
        this.lastFactorBlur = this.lastFactorBuffer = scaleFactor;
        this.lastWidthBlur = this.lastWidthBuffer = width;
        this.lastHeightBlur = this.lastHeightBuffer = height;
    }

    public void init() {
        try {
            this.shaderGroup = new ShaderGroup(this.mc.getTextureManager(), this.mc.getResourceManager(), this.mc.getFramebuffer(), this.blurLocation);
            this.shaderGroup.createBindFramebuffers(this.mc.displayWidth, this.mc.displayHeight);
            this.framebuffer = this.shaderGroup.mainFramebuffer;
        }
        catch (Exception exc) {
            exc.printStackTrace();
        }
    }

    private void setPostValues() {
        this.shaderGroup.getListShaders().get(0).getShaderManager().getShaderUniform("BlurDir").set(1.0f, 0.0f);
        this.shaderGroup.loadShaderGroup(this.mc.timer.renderPartialTicks);
        this.shaderGroup.getListShaders().get(0).getShaderManager().getShaderUniform("BlurDir").set(0.0f, 1.0f);
        this.shaderGroup.loadShaderGroup(this.mc.timer.renderPartialTicks);
    }

    private void setPreValues(float strength) {
        this.shaderGroup.getListShaders().get(1).getShaderManager().getShaderUniform("Radius").set(0.0f);
        this.shaderGroup.getListShaders().get(1).getShaderManager().getShaderUniform("BlurDir").set(0.0f, 0.0f);
        this.shaderGroup.getListShaders().get(0).getShaderManager().getShaderUniform("Radius").set(strength);
    }

    public void update(float partialTicks) {
        int height;
        int width;
        ScaledResolution scaledResolution = new ScaledResolution(this.mc);
        int scaleFactor = scaledResolution.getScaleFactor();
        if (this.sizeHasChangedBuffer(scaleFactor, width = scaledResolution.getScaledWidth(), height = scaledResolution.getScaledHeight())) {
            this.blurBuffer = new Framebuffer(this.mc.displayWidth, this.mc.displayHeight, false);
            this.blurBuffer.setFramebufferColor(0.0f, 0.0f, 0.0f, 0.0f);
            this.loadShader(this.blurLocation, this.blurBuffer);
        }
        this.lastFactorBuffer = scaleFactor;
        this.lastWidthBuffer = width;
        this.lastHeightBuffer = height;
        if (this.blurShaderGroupBuffer == null) {
            this.loadShader(this.blurLocation, this.blurBuffer);
        }
        GlStateManager.enableDepth();
        this.mc.getFramebuffer().unbindFramebuffer();
        this.blurBuffer.bindFramebuffer(true);
        this.mc.getFramebuffer().framebufferRenderExt(this.mc.displayWidth, this.mc.displayHeight, true);
        if (OpenGlHelper.shadersSupported && this.blurShaderGroupBuffer != null) {
            GlStateManager.matrixMode(5890);
            GlStateManager.pushMatrix();
            GlStateManager.loadIdentity();
            this.blurShaderGroupBuffer.loadShaderGroup(partialTicks);
            GlStateManager.popMatrix();
        }
        this.blurBuffer.unbindFramebuffer();
        this.mc.getFramebuffer().bindFramebuffer(true);
        this.mc.entityRenderer.setupOverlayRendering();
    }

    private void loadShader(ResourceLocation resourceLocationIn, Framebuffer framebuffer) {
        if (OpenGlHelper.isFramebufferEnabled()) {
            try {
                this.blurShaderGroupBuffer = new ShaderGroup(this.mc.getTextureManager(), this.mc.getResourceManager(), framebuffer, resourceLocationIn);
                this.blurShaderGroupBuffer.createBindFramebuffers(this.mc.displayWidth, this.mc.displayHeight);
            }
            catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }

    public void blur(double x2, double y2, double areaWidth, double areaHeight, boolean setupOverlayRendering, boolean bloom, boolean reverseBloom, int bloomRadius, int bloomAlpha) {
        this.blur(x2, y2, areaWidth, areaHeight, setupOverlayRendering, bloom, reverseBloom, bloomRadius, bloomAlpha, false);
    }

    public void blur(double x2, double y2, double areaWidth, double areaHeight, boolean setupOverlayRendering, boolean bloom, boolean reverseBloom, int bloomRadius, int bloomAlpha, boolean ignoreModule) {
        GlStateManager.enableBlend();
        if (bloom && !reverseBloom) {
            this.bloom((int)x2 - 4, (int)y2 - 4, (int)areaWidth + 8, (int)areaHeight + 8, bloomRadius, bloomAlpha);
        }
        if (this.mc.theWorld != null && this.mc.thePlayer != null && !this.oldBlur) {
            GL11.glEnable(3089);
            this.scissor(x2, y2, areaWidth, areaHeight);
            this.blur(10.0f, setupOverlayRendering, true);
            GL11.glDisable(3089);
        } else {
            int height;
            int width;
            ScaledResolution scaledResolution = new ScaledResolution(this.mc);
            int scaleFactor = scaledResolution.getScaleFactor();
            if (this.sizeHasChangedBlur(scaleFactor, width = scaledResolution.getScaledWidth(), height = scaledResolution.getScaledHeight()) || this.framebuffer == null || this.shaderGroup == null) {
                this.init();
            }
            this.lastFactorBlur = scaleFactor;
            this.lastWidthBlur = width;
            this.lastHeightBlur = height;
            if (bloom && !reverseBloom) {
                this.bloom((int)x2 - 4, (int)y2 - 4, (int)areaWidth + 8, (int)areaHeight + 8, bloomRadius, bloomAlpha);
            }
            GlStateManager.disableDepth();
            GL11.glEnable(3089);
            this.scissor(x2, y2, areaWidth, areaHeight);
            this.setPreValues(10.0f);
            this.framebuffer.bindFramebuffer(true);
            this.setPostValues();
            this.mc.getFramebuffer().bindFramebuffer(false);
            GL11.glDisable(3089);
            GlStateManager.enableDepth();
        }
        if (bloom && reverseBloom) {
            this.bloom((int)x2 - 4, (int)y2 - 4, (int)areaWidth + 8, (int)areaHeight + 8, bloomRadius, bloomAlpha);
        }
    }

    public void blur(float blurStrength, boolean setupOverlayRendering) {
        this.blur(blurStrength, setupOverlayRendering, false);
    }

    public void blurOld(float blurStrength) {
        int height;
        int width;
        ScaledResolution scaledResolution = new ScaledResolution(this.mc);
        int scaleFactor = scaledResolution.getScaleFactor();
        if (this.sizeHasChangedBlur(scaleFactor, width = scaledResolution.getScaledWidth(), height = scaledResolution.getScaledHeight()) || this.framebuffer == null || this.shaderGroup == null) {
            this.init();
        }
        this.lastFactorBlur = scaleFactor;
        this.lastWidthBlur = width;
        this.lastHeightBlur = height;
        GlStateManager.disableDepth();
        this.setPreValues(blurStrength);
        this.framebuffer.bindFramebuffer(true);
        this.setPostValues();
        this.mc.getFramebuffer().bindFramebuffer(true);
        GlStateManager.enableDepth();
    }

    public void blur(float blurStrength, boolean setupOverlayRendering, boolean ignoreModule) {
        if (this.mc.theWorld != null && this.mc.thePlayer != null && !this.oldBlur) {
            GL11.glPushMatrix();
            GL11.glPushMatrix();
            this.blurShaderGroupBuffer.getListShaders().get(0).getShaderManager().getShaderUniform("Radius").set(blurStrength);
            this.blurShaderGroupBuffer.getListShaders().get(1).getShaderManager().getShaderUniform("Radius").set(blurStrength);
            this.blurBuffer.framebufferRender(this.mc.displayWidth, this.mc.displayHeight);
            GL11.glPopMatrix();
            if (setupOverlayRendering) {
                this.mc.entityRenderer.setupOverlayRendering();
            }
            GlStateManager.enableDepth();
            GlStateManager.enableAlpha();
            GL11.glPopMatrix();
        } else {
            int height;
            int width;
            ScaledResolution scaledResolution = new ScaledResolution(this.mc);
            int scaleFactor = scaledResolution.getScaleFactor();
            if (this.sizeHasChangedBlur(scaleFactor, width = scaledResolution.getScaledWidth(), height = scaledResolution.getScaledHeight()) || this.framebuffer == null || this.shaderGroup == null) {
                this.init();
            }
            this.lastFactorBlur = scaleFactor;
            this.lastWidthBlur = width;
            this.lastHeightBlur = height;
            GlStateManager.disableDepth();
            this.setPreValues(blurStrength);
            this.framebuffer.bindFramebuffer(true);
            this.setPostValues();
            this.mc.getFramebuffer().bindFramebuffer(true);
            GlStateManager.enableDepth();
        }
    }

    public void blur(double x2, double y2, double areaWidth, double areaHeight, boolean setupOverlayRendering, boolean ignoreModule) {
        this.blur(x2, y2, areaWidth, areaHeight, setupOverlayRendering, false, false, 0, 0, ignoreModule);
    }

    public void blur(double x2, double y2, double areaWidth, double areaHeight, boolean setupOverlayRendering) {
        this.blur(x2, y2, areaWidth, areaHeight, setupOverlayRendering, false, false, 0, 0);
    }

    public void bloom(int x2, int y2, int width, int height, int blurRadius, int bloomAlpha, boolean ignoreModule) {
        this.bloom(x2, y2, width, height, blurRadius, new Color(0, 0, 0, bloomAlpha), ignoreModule);
    }

    public void bloom(int x2, int y2, int width, int height, int blurRadius, int bloomAlpha) {
        this.bloom(x2, y2, width, height, blurRadius, new Color(0, 0, 0, bloomAlpha), false);
    }

    public void bloom(int x2, int y2, int width, int height, int blurRadius, Color color) {
        this.bloom(x2, y2, width, height, blurRadius, color, false);
    }

    public void bloom(int x2, int y2, int width, int height, int blurRadius, Color color, boolean ignoreModule) {
        GlStateManager.pushAttrib();
        GlStateManager.pushMatrix();
        GlStateManager.alphaFunc(516, 0.01f);
        height = Math.max(0, height);
        width = Math.max(0, width);
        float _X = (float)(x2 -= blurRadius) - 0.25f;
        float _Y = (float)(y2 -= blurRadius) + 0.25f;
        int identifier = (width += blurRadius * 2) * (height += blurRadius * 2) + width + color.hashCode() * blurRadius + blurRadius;
        GL11.glEnable(3553);
        GL11.glDisable(2884);
        GL11.glEnable(3008);
        GL11.glEnable(3042);
        if (this.shadowCache.containsKey(identifier)) {
            int texId = this.shadowCache.get(identifier);
            GlStateManager.bindTexture(texId);
        } else {
            BufferedImage original = new BufferedImage(width, height, 2);
            Graphics g2 = original.getGraphics();
            g2.setColor(color);
            g2.fillRect(blurRadius, blurRadius, width - blurRadius * 2, height - blurRadius * 2);
            g2.dispose();
            GaussianFilter op2 = new GaussianFilter(blurRadius);
            BufferedImage blurred = op2.filter(original, null);
            int texId = TextureUtil.uploadTextureImageAllocate(TextureUtil.glGenTextures(), blurred, true, false);
            this.shadowCache.put(identifier, texId);
        }
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glBegin(7);
        GL11.glTexCoord2f(0.0f, 0.0f);
        GL11.glVertex2d(_X, _Y);
        GL11.glTexCoord2f(0.0f, 1.0f);
        GL11.glVertex2d(_X, _Y + (float)height);
        GL11.glTexCoord2f(1.0f, 1.0f);
        GL11.glVertex2d(_X + (float)width, _Y + (float)height);
        GL11.glTexCoord2f(1.0f, 0.0f);
        GL11.glVertex2d(_X + (float)width, _Y);
        GL11.glEnd();
        GL11.glDisable(3553);
        GL11.glEnable(2884);
        GL11.glDisable(3008);
        GL11.glDisable(3042);
        GlStateManager.popAttrib();
        GlStateManager.popMatrix();
    }

    private boolean sizeHasChangedBlur(int scaleFactor, int width, int height) {
        return this.lastFactorBlur != scaleFactor || this.lastWidthBlur != width || this.lastHeightBlur != height;
    }

    private boolean sizeHasChangedBuffer(int scaleFactor, int width, int height) {
        return this.lastFactorBuffer != scaleFactor || this.lastWidthBuffer != width || this.lastHeightBuffer != height;
    }

    public void scissor(double x2, double y2, double width, double height) {
        ScaledResolution sr2 = new ScaledResolution(this.mc);
        double scale = sr2.getScaleFactor();
        y2 = (double)sr2.getScaledHeight() - y2;
        GL11.glScissor((int)(x2 *= scale), (int)((y2 *= scale) - (height *= scale)), (int)(width *= scale), (int)height);
    }
}