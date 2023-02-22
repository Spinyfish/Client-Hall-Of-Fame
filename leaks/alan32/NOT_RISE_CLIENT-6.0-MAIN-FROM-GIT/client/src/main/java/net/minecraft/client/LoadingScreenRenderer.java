package net.minecraft.client;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.util.MinecraftError;
import net.optifine.CustomLoadingScreen;
import net.optifine.CustomLoadingScreens;
import net.optifine.reflect.Reflector;

public class LoadingScreenRenderer implements IProgressUpdate {
    private String message = "";

    /**
     * A reference to the Minecraft object.
     */
    private final Minecraft mc;

    /**
     * The text currently displayed (i.e. the argument to the last call to printText or displayString)
     */
    private String currentlyDisplayedText = "";

    /**
     * The system's time represented in milliseconds.
     */
    private long systemTime = Minecraft.getSystemTime();
    private boolean field_73724_e;
    private final ScaledResolution scaledResolution;
    private final Framebuffer framebuffer;

    public LoadingScreenRenderer(final Minecraft mcIn) {
        this.mc = mcIn;
        this.scaledResolution = new ScaledResolution(mcIn);
        this.framebuffer = new Framebuffer(mcIn.displayWidth, mcIn.displayHeight, false);
        this.framebuffer.setFramebufferFilter(9728);
    }

    /**
     * this string, followed by "working..." and then the "% complete" are the 3 lines shown. This resets progress to 0,
     * and the WorkingString to "working...".
     */
    public void resetProgressAndMessage(final String message) {
        this.field_73724_e = false;
        this.displayString(message);
    }

    /**
     * Shows the 'Saving level' string.
     */
    public void displaySavingString(final String message) {
        this.field_73724_e = true;
        this.displayString(message);
    }

    private void displayString(final String message) {
        this.currentlyDisplayedText = message;

        if (!this.mc.running) {
            if (!this.field_73724_e) {
                throw new MinecraftError();
            }
        } else {
            GlStateManager.clear(256);
            GlStateManager.matrixMode(5889);
            GlStateManager.loadIdentity();

            if (OpenGlHelper.isFramebufferEnabled()) {
                final int i = this.scaledResolution.getScaleFactor();
                GlStateManager.ortho(0.0D, this.scaledResolution.getScaledWidth() * i, this.scaledResolution.getScaledHeight() * i, 0.0D, 100.0D, 300.0D);
            } else {
                final ScaledResolution scaledresolution = new ScaledResolution(this.mc);
                GlStateManager.ortho(0.0D, scaledresolution.getScaledWidth_double(), scaledresolution.getScaledHeight_double(), 0.0D, 100.0D, 300.0D);
            }

            GlStateManager.matrixMode(5888);
            GlStateManager.loadIdentity();
            GlStateManager.translate(0.0F, 0.0F, -200.0F);
        }
    }

    /**
     * Displays a string on the loading screen supposed to indicate what is being done currently.
     */
    public void displayLoadingString(final String message) {
        if (!this.mc.running) {
            if (!this.field_73724_e) {
                throw new MinecraftError();
            }
        } else {
            this.systemTime = 0L;
            this.message = message;
            this.setLoadingProgress(-1);
            this.systemTime = 0L;
        }
    }

    /**
     * Updates the progress bar on the loading screen to the specified amount. Args: loadProgress
     */
    public void setLoadingProgress(final int progress) {
        if (!this.mc.running) {
            if (!this.field_73724_e) {
                throw new MinecraftError();
            }
        } else {
            final long i = Minecraft.getSystemTime();

            if (i - this.systemTime >= 100L) {
                this.systemTime = i;
                final ScaledResolution scaledresolution = new ScaledResolution(this.mc);
                final int j = scaledresolution.getScaleFactor();
                final int k = scaledresolution.getScaledWidth();
                final int l = scaledresolution.getScaledHeight();

                if (OpenGlHelper.isFramebufferEnabled()) {
                    this.framebuffer.framebufferClear();
                } else {
                    GlStateManager.clear(256);
                }

                this.framebuffer.bindFramebuffer(false);
                GlStateManager.matrixMode(5889);
                GlStateManager.loadIdentity();
                GlStateManager.ortho(0.0D, scaledresolution.getScaledWidth_double(), scaledresolution.getScaledHeight_double(), 0.0D, 100.0D, 300.0D);
                GlStateManager.matrixMode(5888);
                GlStateManager.loadIdentity();
                GlStateManager.translate(0.0F, 0.0F, -200.0F);

                if (!OpenGlHelper.isFramebufferEnabled()) {
                    GlStateManager.clear(16640);
                }

                boolean flag = true;

                if (Reflector.FMLClientHandler_handleLoadingScreen.exists()) {
                    final Object object = Reflector.call(Reflector.FMLClientHandler_instance);

                    if (object != null) {
                        flag = !Reflector.callBoolean(object, Reflector.FMLClientHandler_handleLoadingScreen, scaledresolution);
                    }
                }

                if (flag) {
                    final Tessellator tessellator = Tessellator.getInstance();
                    final WorldRenderer worldrenderer = tessellator.getWorldRenderer();
                    final CustomLoadingScreen customloadingscreen = CustomLoadingScreens.getCustomLoadingScreen();

                    if (customloadingscreen != null) {
                        customloadingscreen.drawBackground(scaledresolution.getScaledWidth(), scaledresolution.getScaledHeight());
                    } else {
                        this.mc.getTextureManager().bindTexture(Gui.optionsBackground);
                        final float f = 32.0F;
                        worldrenderer.begin(7, DefaultVertexFormats.field_181709_i);
                        worldrenderer.pos(0.0D, l, 0.0D).tex(0.0D, (float) l / f).func_181669_b(64, 64, 64, 255).endVertex();
                        worldrenderer.pos(k, l, 0.0D).tex((float) k / f, (float) l / f).func_181669_b(64, 64, 64, 255).endVertex();
                        worldrenderer.pos(k, 0.0D, 0.0D).tex((float) k / f, 0.0D).func_181669_b(64, 64, 64, 255).endVertex();
                        worldrenderer.pos(0.0D, 0.0D, 0.0D).tex(0.0D, 0.0D).func_181669_b(64, 64, 64, 255).endVertex();
                        tessellator.draw();
                    }

                    if (progress >= 0) {
                        final int l1 = 100;
                        final int i1 = 2;
                        final int j1 = k / 2 - l1 / 2;
                        final int k1 = l / 2 + 16;
                        GlStateManager.disableTexture2D();
                        worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
                        worldrenderer.pos(j1, k1, 0.0D).func_181669_b(128, 128, 128, 255).endVertex();
                        worldrenderer.pos(j1, k1 + i1, 0.0D).func_181669_b(128, 128, 128, 255).endVertex();
                        worldrenderer.pos(j1 + l1, k1 + i1, 0.0D).func_181669_b(128, 128, 128, 255).endVertex();
                        worldrenderer.pos(j1 + l1, k1, 0.0D).func_181669_b(128, 128, 128, 255).endVertex();
                        worldrenderer.pos(j1, k1, 0.0D).func_181669_b(128, 255, 128, 255).endVertex();
                        worldrenderer.pos(j1, k1 + i1, 0.0D).func_181669_b(128, 255, 128, 255).endVertex();
                        worldrenderer.pos(j1 + progress, k1 + i1, 0.0D).func_181669_b(128, 255, 128, 255).endVertex();
                        worldrenderer.pos(j1 + progress, k1, 0.0D).func_181669_b(128, 255, 128, 255).endVertex();
                        tessellator.draw();
                        GlStateManager.enableTexture2D();
                    }

                    GlStateManager.enableBlend();
                    GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
                    this.mc.fontRendererObj.drawStringWithShadow(this.currentlyDisplayedText, (float) ((k - this.mc.fontRendererObj.width(this.currentlyDisplayedText)) / 2), (float) (l / 2 - 4 - 16), 16777215);
                    this.mc.fontRendererObj.drawStringWithShadow(this.message, (float) ((k - this.mc.fontRendererObj.width(this.message)) / 2), (float) (l / 2 - 4 + 8), 16777215);
                }

                this.framebuffer.unbindFramebuffer();

                if (OpenGlHelper.isFramebufferEnabled()) {
                    this.framebuffer.framebufferRender(k * j, l * j);
                }

                this.mc.updateDisplay();

                try {
                    Thread.yield();
                } catch (final Exception var16) {
                }
            }
        }
    }

    public void setDoneWorking() {
    }
}
