package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import dev.africa.pandaware.Client;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.optifine.CustomPanorama;
import net.optifine.CustomPanoramaProperties;
import org.apache.commons.io.Charsets;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Project;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Random;

public class GuiMainMenu extends GuiScreen implements GuiYesNoCallback {
    private static final Random RANDOM = new Random();
    private final float updateCounter;
    private int panoramaTimer;
    private DynamicTexture viewportTexture;
    private static final ResourceLocation splashTexts = new ResourceLocation("texts/splashes.txt");
    private static final ResourceLocation minecraftTitleTextures = new ResourceLocation("textures/gui/title/minecraft.png");
    private static final ResourceLocation[] titlePanoramaPaths = new ResourceLocation[]{new ResourceLocation("textures/gui/title/background/panorama_0.png"), new ResourceLocation("textures/gui/title/background/panorama_1.png"), new ResourceLocation("textures/gui/title/background/panorama_2.png"), new ResourceLocation("textures/gui/title/background/panorama_3.png"), new ResourceLocation("textures/gui/title/background/panorama_4.png"), new ResourceLocation("textures/gui/title/background/panorama_5.png")};
    private ResourceLocation backgroundTexture;

    public GuiMainMenu() {
        BufferedReader bufferedreader = null;

        try {
            List<String> list = Lists.newArrayList();
            bufferedreader = new BufferedReader(new InputStreamReader(Minecraft.getMinecraft().getResourceManager().getResource(splashTexts).getInputStream(), Charsets.UTF_8));
            String s;

            while ((s = bufferedreader.readLine()) != null) {
                s = s.trim();

                if (!s.isEmpty()) {
                    list.add(s);
                }
            }
        } catch (IOException ignored) {
        } finally {
            if (bufferedreader != null) {
                try {
                    bufferedreader.close();
                } catch (IOException ignored) {
                }
            }
        }

        this.updateCounter = RANDOM.nextFloat();
    }

    public void updateScreen() {
        this.panoramaTimer += 3;
    }

    public boolean doesGuiPauseGame() {
        return false;
    }

    protected void keyTyped(char typedChar, int keyCode) throws IOException {
    }

    public void initGui() {
        this.viewportTexture = new DynamicTexture(256, 256);
        this.backgroundTexture = this.mc.getTextureManager().getDynamicTextureLocation("background", this.viewportTexture);

        int j = this.height / 4 + 48;

        this.addSingleplayerMultiplayerButtons(j, 24);

        this.buttonList.add(new GuiButton(0, this.width / 2 - 100, j + 72 + 12, 98,
                20, I18n.format("menu.options")));
        this.buttonList.add(new GuiButton(4, this.width / 2 + 2,
                j + 72 + 12, 98, 20, I18n.format("menu.quit")));

        this.mc.func_181537_a(false);
    }

    private void addSingleplayerMultiplayerButtons(int p_73969_1_, int p_73969_2_) {
        this.buttonList.add(new GuiButton(1, this.width / 2 - 100, p_73969_1_, I18n.format("menu.singleplayer")));
        this.buttonList.add(new GuiButton(2, this.width / 2 - 100, p_73969_1_ + p_73969_2_ * 1, I18n.format("menu.multiplayer")));

        this.buttonList.add(new GuiButton(14, this.width / 2 - 100,
                p_73969_1_ + p_73969_2_ * 2, "Account Manager"));
    }

    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == 0) {
            this.mc.displayGuiScreen(new GuiOptions(this, this.mc.gameSettings));
        }

        if (button.id == 1) {
            this.mc.displayGuiScreen(new GuiSelectWorld(this));
        }

        if (button.id == 2) {
            this.mc.displayGuiScreen(new GuiMultiplayer(this));
        }

        if (button.id == 14) {
            mc.displayGuiScreen(Client.getInstance().getGuiAccountManager());
        }

        if (button.id == 4) {
            this.mc.shutdown();
        }
    }

    private void drawPanorama(int p_73970_1_, int p_73970_2_, float p_73970_3_) {
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        GlStateManager.matrixMode(5889);
        GlStateManager.pushMatrix();
        GlStateManager.loadIdentity();
        Project.gluPerspective(120.0F, 1.0F, 0.05F, 10.0F);
        GlStateManager.matrixMode(5888);
        GlStateManager.pushMatrix();
        GlStateManager.loadIdentity();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(90.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.disableCull();
        GlStateManager.depthMask(false);
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        int i = 8;
        int j = 64;
        CustomPanoramaProperties custompanoramaproperties = CustomPanorama.getCustomPanoramaProperties();

        if (custompanoramaproperties != null) {
            j = custompanoramaproperties.getBlur1();
        }

        for (int k = 0; k < j; ++k) {
            GlStateManager.pushMatrix();
            float f = ((float) (k % i) / (float) i - 0.5F) / 64.0F;
            float f1 = ((float) (k / i) / (float) i - 0.5F) / 64.0F;
            float f2 = 0.0F;
            GlStateManager.translate(f, f1, f2);
            GlStateManager.rotate(MathHelper.sin(((float) this.panoramaTimer + p_73970_3_) / 400.0F) * 25.0F + 20.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(-((float) this.panoramaTimer + p_73970_3_) * 0.1F, 0.0F, 1.0F, 0.0F);

            for (int l = 0; l < 6; ++l) {
                GlStateManager.pushMatrix();

                if (l == 1) {
                    GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
                }

                if (l == 2) {
                    GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
                }

                if (l == 3) {
                    GlStateManager.rotate(-90.0F, 0.0F, 1.0F, 0.0F);
                }

                if (l == 4) {
                    GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
                }

                if (l == 5) {
                    GlStateManager.rotate(-90.0F, 1.0F, 0.0F, 0.0F);
                }

                ResourceLocation[] aresourcelocation = titlePanoramaPaths;

                if (custompanoramaproperties != null) {
                    aresourcelocation = custompanoramaproperties.getPanoramaLocations();
                }

                this.mc.getTextureManager().bindTexture(aresourcelocation[l]);
                worldrenderer.begin(7, DefaultVertexFormats.field_181709_i);
                int i1 = 255 / (k + 1);
                float f3 = 0.0F;
                worldrenderer.pos(-1.0D, -1.0D, 1.0D).tex(0.0D, 0.0D).func_181669_b(255, 255, 255, i1).endVertex();
                worldrenderer.pos(1.0D, -1.0D, 1.0D).tex(1.0D, 0.0D).func_181669_b(255, 255, 255, i1).endVertex();
                worldrenderer.pos(1.0D, 1.0D, 1.0D).tex(1.0D, 1.0D).func_181669_b(255, 255, 255, i1).endVertex();
                worldrenderer.pos(-1.0D, 1.0D, 1.0D).tex(0.0D, 1.0D).func_181669_b(255, 255, 255, i1).endVertex();
                tessellator.draw();
                GlStateManager.popMatrix();
            }

            GlStateManager.popMatrix();
            GlStateManager.colorMask(true, true, true, false);
        }

        worldrenderer.setTranslation(0.0D, 0.0D, 0.0D);
        GlStateManager.colorMask(true, true, true, true);
        GlStateManager.matrixMode(5889);
        GlStateManager.popMatrix();
        GlStateManager.matrixMode(5888);
        GlStateManager.popMatrix();
        GlStateManager.depthMask(true);
        GlStateManager.enableCull();
        GlStateManager.enableDepth();
    }

    private void rotateAndBlurSkybox(float p_73968_1_) {
        this.mc.getTextureManager().bindTexture(this.backgroundTexture);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        GL11.glCopyTexSubImage2D(GL11.GL_TEXTURE_2D, 0, 0, 0, 0, 0, 256, 256);
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.colorMask(true, true, true, false);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(7, DefaultVertexFormats.field_181709_i);
        GlStateManager.disableAlpha();
        int i = 3;
        int j = 3;
        CustomPanoramaProperties custompanoramaproperties = CustomPanorama.getCustomPanoramaProperties();

        if (custompanoramaproperties != null) {
            j = custompanoramaproperties.getBlur2();
        }

        for (int k = 0; k < j; ++k) {
            float f = 1.0F / (float) (k + 1);
            int l = this.width;
            int i1 = this.height;
            float f1 = (float) (k - i / 2) / 256.0F;
            worldrenderer.pos(l, i1, this.zLevel).tex(0.0F + f1, 1.0D).func_181666_a(1.0F, 1.0F, 1.0F, f).endVertex();
            worldrenderer.pos(l, 0.0D, this.zLevel).tex(1.0F + f1, 1.0D).func_181666_a(1.0F, 1.0F, 1.0F, f).endVertex();
            worldrenderer.pos(0.0D, 0.0D, this.zLevel).tex(1.0F + f1, 0.0D).func_181666_a(1.0F, 1.0F, 1.0F, f).endVertex();
            worldrenderer.pos(0.0D, i1, this.zLevel).tex(0.0F + f1, 0.0D).func_181666_a(1.0F, 1.0F, 1.0F, f).endVertex();
        }

        tessellator.draw();
        GlStateManager.enableAlpha();
        GlStateManager.colorMask(true, true, true, true);
    }

    private void renderSkybox(int p_73971_1_, int p_73971_2_, float p_73971_3_) {
        this.mc.getFramebuffer().unbindFramebuffer();
        GlStateManager.viewport(0, 0, 256, 256);
        this.drawPanorama(p_73971_1_, p_73971_2_, p_73971_3_);
        this.rotateAndBlurSkybox(p_73971_3_);
        int i = 3;
        CustomPanoramaProperties custompanoramaproperties = CustomPanorama.getCustomPanoramaProperties();

        if (custompanoramaproperties != null) {
            i = custompanoramaproperties.getBlur3();
        }

        for (int j = 0; j < i; ++j) {
            this.rotateAndBlurSkybox(p_73971_3_);
            this.rotateAndBlurSkybox(p_73971_3_);
        }

        this.mc.getFramebuffer().bindFramebuffer(true);
        GlStateManager.viewport(0, 0, this.mc.displayWidth, this.mc.displayHeight);
        float f2 = this.width > this.height ? 120.0F / (float) this.width : 120.0F / (float) this.height;
        float f = (float) this.height * f2 / 256.0F;
        float f1 = (float) this.width * f2 / 256.0F;
        int k = this.width;
        int l = this.height;
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(7, DefaultVertexFormats.field_181709_i);
        worldrenderer.pos(0.0D, l, this.zLevel).tex(0.5F - f, 0.5F + f1).func_181666_a(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
        worldrenderer.pos(k, l, this.zLevel).tex(0.5F - f, 0.5F - f1).func_181666_a(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
        worldrenderer.pos(k, 0.0D, this.zLevel).tex(0.5F + f, 0.5F - f1).func_181666_a(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
        worldrenderer.pos(0.0D, 0.0D, this.zLevel).tex(0.5F + f, 0.5F + f1).func_181666_a(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
        tessellator.draw();
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        GlStateManager.disableAlpha();
        this.renderSkybox(mouseX, mouseY, partialTicks);
        GlStateManager.enableAlpha();
        int i = 274;
        int j = this.width / 2 - i / 2;
        int k = 30;
        int l = -2130706433;
        int i1 = 16777215;
        int j1 = 0;
        int k1 = Integer.MIN_VALUE;
        CustomPanoramaProperties custompanoramaproperties = CustomPanorama.getCustomPanoramaProperties();

        if (custompanoramaproperties != null) {
            l = custompanoramaproperties.getOverlay1Top();
            i1 = custompanoramaproperties.getOverlay1Bottom();
            j1 = custompanoramaproperties.getOverlay2Top();
            k1 = custompanoramaproperties.getOverlay2Bottom();
        }

        if (l != 0 || i1 != 0) {
            this.drawGradientRect(0, 0, this.width, this.height, l, i1);
        }

        if (j1 != 0 || k1 != 0) {
            this.drawGradientRect(0, 0, this.width, this.height, j1, k1);
        }

        this.mc.getTextureManager().bindTexture(minecraftTitleTextures);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        if ((double) this.updateCounter < 1.0E-4D) {
            this.drawTexturedModalRect(j, k, 0, 0, 99, 44);
            this.drawTexturedModalRect(j + 99, k, 129, 0, 27, 44);
            this.drawTexturedModalRect(j + 99 + 26, k, 126, 0, 3, 44);
            this.drawTexturedModalRect(j + 99 + 26 + 3, k, 99, 0, 26, 44);
            this.drawTexturedModalRect(j + 155, k, 0, 45, 155, 44);
        } else {
            this.drawTexturedModalRect(j, k, 0, 0, 155, 44);
            this.drawTexturedModalRect(j + 155, k, 0, 45, 155, 44);
        }

        String s = "Minecraft 1.8.9";

        this.drawString(this.fontRendererObj, s, 2, this.height - 10, -1);

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    /**
     * Called when the mouse is clicked. Args : mouseX, mouseY, clickedButton
     */
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }
}
