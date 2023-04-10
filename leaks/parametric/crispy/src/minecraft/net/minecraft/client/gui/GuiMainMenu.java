package net.minecraft.client.gui;

import arithmo.gui.altmanager.Colors;
import arithmo.gui.altmanager.GuiAltManager;
import arithmo.gui.altmanager.Translate;
import crispy.Crispy;
import crispy.util.MainButton;
import crispy.util.render.ExitButton;
import crispy.util.render.GuiServerFinder;
import crispy.util.render.gui.Draw;
import crispy.util.render.particle.ParticleUtil;
import crispy.util.render.shaders.GLSLSandboxShader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.realms.RealmsBridge;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.demo.DemoWorldServer;
import net.minecraft.world.storage.ISaveFormat;
import net.minecraft.world.storage.WorldInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.util.glu.Project;
import viamcp.gui.GuiProtocolSelector;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URI;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class GuiMainMenu extends GuiScreen implements GuiYesNoCallback {


    public static final String field_96138_a = "Please click " + EnumChatFormatting.UNDERLINE + "here" + EnumChatFormatting.RESET + " for more information.";
    private static final AtomicInteger field_175373_f = new AtomicInteger(0);
    private static final Logger logger = LogManager.getLogger();
    private static final Random RANDOM = new Random();
    private static final ResourceLocation splashTexts = new ResourceLocation("texts/splashes.txt");
    /**
     * An array of all the paths to the panorama pictures.
     */
    private static final ResourceLocation[] titlePanoramaPaths = new ResourceLocation[]{new ResourceLocation("textures/gui/title/background/panorama_0.png"), new ResourceLocation("textures/gui/title/background/panorama_1.png"), new ResourceLocation("textures/gui/title/background/panorama_2.png"), new ResourceLocation("textures/gui/title/background/panorama_3.png"), new ResourceLocation("textures/gui/title/background/panorama_4.png"), new ResourceLocation("textures/gui/title/background/panorama_5.png")};
    /**
     * The Object object utilized as a thread lock when performing non thread-safe operations
     */

    private final Object threadLock = new Object();
    String read = "";
    TextureManager textureManager;
    private Translate opacity = new Translate(255, 255);
    /**
     * Counts the number of screen updates.
     */
    private float updateCounter;
    /**
     * The splash message.
     */
    private String splashText;
    private GuiButton buttonResetDemo;
    /**
     * Timer used to rotate the panorama, increases every tick.
     */
    private int panoramaTimer;
    /**
     * Texture allocated for the current viewport of the main menu's panorama background.
     */
    private DynamicTexture viewportTexture;
    private boolean field_175375_v = true;
    /**
     * OpenGL graphics card warning.
     */
    private String openGLWarning1;
    /**
     * OpenGL graphics card warning.
     */
    private String openGLWarning2;
    /**
     * Link to the Mojang Support about minimum requirements
     */
    private String openGLWarningLink;
    private ParticleUtil particleUtil;
    private int field_92024_r;
    private int field_92023_s;
    private int field_92022_t;
    private int field_92021_u;
    private int field_92020_v;
    private int field_92019_w;
    private GLSLSandboxShader sandboxShader;
    private ResourceLocation backgroundTexture;

    /**
     * Minecraft Realms button.
     */
    private GuiButton realmsButton;
    private Translate translat;
    private long initTime;
    /**
     * Renders the skybox in the main menu
     */

    private float currentX, targetX, currentY, targetY;

    public GuiMainMenu() {


        this.openGLWarning2 = field_96138_a;
        this.splashText = "";
        BufferedReader bufferedreader = null;


        this.updateCounter = RANDOM.nextFloat();
        this.openGLWarning1 = "";

        if (!GLContext.getCapabilities().OpenGL20 && !OpenGlHelper.areShadersSupported()) {
            this.openGLWarning1 = I18n.format("title.oldgl1", new Object[0]);
            this.openGLWarning2 = I18n.format("title.oldgl2", new Object[0]);
            this.openGLWarningLink = "https://help.mojang.com/customer/portal/articles/325948?ref=game";
        }
    }

    public static void drawBG() {

    }

    /**
     * Called from the main game loop to update the screen.
     */
    public void updateScreen() {

        ++this.panoramaTimer;
    }

    /**
     * Returns true if this GUI should pause the game when it is displayed in single-player
     */
    public boolean doesGuiPauseGame() {
        return false;
    }

    /**
     * Fired when a key is typed (except F11 which toggles full screen). This is the equivalent of
     * KeyListener.keyTyped(KeyEvent e). Args : character (character on the key), keyCode (lwjgl Keyboard key code)
     */
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
    }

    /**
     * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
     * window resizes, the buttonList is cleared beforehand.
     */
    public void initGui() {

        initTime = System.currentTimeMillis();
        try {
            sandboxShader = new GLSLSandboxShader("");
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.viewportTexture = new DynamicTexture(256, 256);
        this.backgroundTexture = this.mc.getTextureManager().getDynamicTextureLocation("background", this.viewportTexture);
        particleUtil = new ParticleUtil(150, width, height);

        translat = new Translate(0, -10);
        this.viewportTexture = new DynamicTexture(256, 256);

        Calendar calendar = Calendar.getInstance();
        drawBG();
        calendar.setTime(new Date());


        int i = 24;
        int j = this.height / 4 + 70;

        if (this.mc.isDemo()) {
            this.addDemoButtons(j, 24);
        } else {
            this.addSingleplayerMultiplayerButtons(j, 24);
        }


        synchronized (this.threadLock) {
            this.field_92023_s = this.fontRendererObj.getStringWidth(this.openGLWarning1);
            this.field_92024_r = this.fontRendererObj.getStringWidth(this.openGLWarning2);
            int k = Math.max(this.field_92023_s, this.field_92024_r);
            this.field_92022_t = (this.width - k) / 2;
            this.field_92020_v = this.field_92022_t + k;
            this.field_92019_w = this.field_92021_u + 24;
        }


    }

    /**
     * Adds Singleplayer and Multiplayer buttons on Main Menu for players who have bought the game.
     */

    private void addSingleplayerMultiplayerButtons(int p_73969_1_, int p_73969_2_) {
        this.buttonList.add(new ExitButton(3, width - 20, 0, 20, 20, ""));
        this.buttonList.add(new MainButton(2, this.width / 2 + 32 - 35, p_73969_1_, 60, 28, "      Multi"));
        this.buttonList.add(new MainButton(1, this.width / 2 - 32 - 35, p_73969_1_, 60, 28, "      Single"));
        this.buttonList.add(new MainButton(500, this.width / 2 + 32 - 35, p_73969_1_ + p_73969_2_ + 10, 60, 28, "      Alt"));
        this.buttonList.add(new MainButton(502, this.width / 2 - 32 - 35, p_73969_1_ + p_73969_2_ + 10, 60, 28, "      Vers"));
        this.buttonList.add(new MainButton(501, this.width / 2 - 32 - 35, p_73969_1_ + p_73969_2_ * 3, 125, 28, "              Server Find"));
    }

    /**
     * Adds Demo buttons on Main Menu for players who are playing Demo.
     */
    private void addDemoButtons(int p_73972_1_, int p_73972_2_) {
        this.buttonList.add(new GuiButton(11, this.width / 2 - 100, p_73972_1_, I18n.format("menu.playdemo", new Object[0])));
        this.buttonList.add(this.buttonResetDemo = new GuiButton(12, this.width / 2 - 100, p_73972_1_ + p_73972_2_ * 1, I18n.format("menu.resetdemo", new Object[0])));
        ISaveFormat isaveformat = this.mc.getSaveLoader();
        WorldInfo worldinfo = isaveformat.getWorldInfo("Demo_World");

        if (worldinfo == null) {
            this.buttonResetDemo.enabled = false;
        }
    }

    /**
     * Called by the controls from the buttonList when activated. (Mouse pressed for buttons)
     */
    protected void actionPerformed(GuiButton button) throws IOException {

        if (button.id == 0) {
            this.mc.displayGuiScreen(new GuiOptions(this, this.mc.gameSettings));
        }

        if (button.id == 5) {
            this.mc.displayGuiScreen(new GuiLanguage(this, this.mc.gameSettings, this.mc.getLanguageManager()));
        }

        if (button.id == 1) {
            this.mc.displayGuiScreen(new GuiSelectWorld(this));
        }
        if (button.id == 3) {
            mc.shutdown();
        }

        if (button.id == 2) {
            this.mc.displayGuiScreen(new GuiMultiplayer(this));
        }
        if (button.id == 501) {
            this.mc.displayGuiScreen(new GuiServerFinder(this));
        }
        if (button.id == 502) {
            this.mc.displayGuiScreen(new GuiProtocolSelector(this));
        }

        if (button.id == 14 && this.realmsButton.visible) {
            this.switchToRealms();
        }

        if (button.id == 4) {
            this.mc.shutdownMinecraftApplet();
        }

        if (button.id == 11) {
            this.mc.launchIntegratedServer("Demo_World", "Demo_World", DemoWorldServer.demoWorldSettings);
        }

        if (button.id == 12) {
            ISaveFormat isaveformat = this.mc.getSaveLoader();
            WorldInfo worldinfo = isaveformat.getWorldInfo("Demo_World");

            if (worldinfo != null) {
                GuiYesNo guiyesno = GuiSelectWorld.func_152129_a(this, worldinfo.getWorldName(), 12);
                this.mc.displayGuiScreen(guiyesno);
            }
        }
        if (button.id == 500) {
            this.mc.displayGuiScreen(new GuiAltManager());

        }


    }

    /**
     * Draws the main menu panorama
     */


    /**
     * Rotate and blurs the skybox view in the main menu
     */

    private void switchToRealms() {
        RealmsBridge realmsbridge = new RealmsBridge();
        realmsbridge.switchToRealms(this);
    }

    public void confirmClicked(boolean result, int id) {
        if (result && id == 12) {
            ISaveFormat isaveformat = this.mc.getSaveLoader();
            isaveformat.flushCache();
            isaveformat.deleteWorldDirectory("Demo_World");
            this.mc.displayGuiScreen(this);
        } else if (id == 13) {
            if (result) {
                try {
                    Class<?> oclass = Class.forName("java.awt.Desktop");
                    Object object = oclass.getMethod("getDesktop", new Class[0]).invoke((Object) null, new Object[0]);
                    oclass.getMethod("browse", new Class[]{URI.class}).invoke(object, new Object[]{new URI(this.openGLWarningLink)});
                } catch (Throwable throwable) {
                    logger.error("Couldn\'t open link", throwable);
                }
            }

            this.mc.displayGuiScreen(this);
        }
    }

    /**
     * Draws the screen and all the components in it. Args : mouseX, mouseY, renderPartialTicks
     */
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {


        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft(), Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight);
        int w = ScaledResolution.getScaledWidth();
        int h = ScaledResolution.getScaledHeight();

        try {
            GL11.glPushMatrix();
            GlStateManager.disableCull();
            this.sandboxShader.useShader(width, height, mouseX, mouseY, (System.currentTimeMillis() - initTime) / 1000F);
            GL11.glBegin(GL11.GL_QUADS);
            GL11.glVertex2f(-1f, -1f);
            GL11.glVertex2f(-1f, 1f);
            GL11.glVertex2f(1f, 1f);
            GL11.glVertex2f(1f, -1f);

            GL11.glEnd();
            GL20.glUseProgram(0);
            GL11.glPopMatrix();
        } catch (Exception ignored) {
        }
        GL11.glPushMatrix();
        GlStateManager.enableBlend();
        Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation("Client/misc/exit.png"));
        ;
        GL11.glPopMatrix();
        Gui.drawModalRectWithCustomSizedTexture(w - 20, 0, 20, 20, 20, 20, 20, 20);
        drawCenteredString(Crispy.INSTANCE.getFontManager().getFont("ROBO 42"), "Crispy ", width / 2, ScaledResolution.scaledHeight / 3, Colors.getColor(255, 255, 255, 95));
        drawString(Crispy.INSTANCE.getFontManager().getFont("ROBO 20"), "Changelog", 7, 3, Colors.getColor(255, 255, 255));
        int height = 10;

        try {
            int calc = Crispy.INSTANCE.getChangeLog().length * 10;
            String[] screwu = Crispy.INSTANCE.getSortedChange();
            List<String> bop = Arrays.asList(screwu);

            GlStateManager.color(0, 0, 0, 0.2f);
            Draw.drawRoundedRect(10 - 3, 20 - 4, Crispy.INSTANCE.getFontManager().getFont("ROBO 12").getWidth(bop.get(bop.size() - 1)) + 5, calc + 4, 3);

            for (int i = 0; i < Crispy.INSTANCE.getChangeLog().length; i++) {
                height += 10;
                drawString(Crispy.INSTANCE.getFontManager().getFont("ROBO 12"), Crispy.INSTANCE.getChangeLog()[i], 10, height, Colors.getColor(255, 255, 255));
            }
        } catch (Exception e) {
           // System.exit(0);
        }
        drawString(Crispy.INSTANCE.getFontManager().getFont("ROBO 14"), "Made by Parametric#6379 and IWolfZ#1000", 0, ScaledResolution.getScaledHeight() - 10, -1);
        opacity.interpolate(0, 0, 1);
        drawCenteredString(Crispy.INSTANCE.getFontManager().getFont("clean 30"), "Welcome " + Crispy.INSTANCE.getDiscordName(), width / 2, ScaledResolution.scaledHeight / 4, Colors.getColor(255, 255, 255, (int) opacity.getX()));

        super.drawScreen(mouseX, mouseY, partialTicks);

    }

    private void renderSkybox(int p_73971_1_, int p_73971_2_, float p_73971_3_) {
        this.mc.getFramebuffer().unbindFramebuffer();
        GlStateManager.viewport(0, 0, 256, 256);
        this.drawPanorama(p_73971_1_, p_73971_2_, p_73971_3_);
        this.rotateAndBlurSkybox(p_73971_3_);
        this.rotateAndBlurSkybox(p_73971_3_);
        this.rotateAndBlurSkybox(p_73971_3_);
        this.rotateAndBlurSkybox(p_73971_3_);
        this.rotateAndBlurSkybox(p_73971_3_);
        this.rotateAndBlurSkybox(p_73971_3_);
        this.rotateAndBlurSkybox(p_73971_3_);
        this.mc.getFramebuffer().bindFramebuffer(true);
        GlStateManager.viewport(0, 0, this.mc.displayWidth, this.mc.displayHeight);
        Tessellator var4 = Tessellator.getInstance();
        WorldRenderer var5 = var4.getWorldRenderer();
        var5.startDrawingQuads();
        float var6 = this.width > this.height ? 120.0F / (float) this.width : 120.0F / (float) this.height;
        float var7 = (float) this.height * var6 / 256.0F;
        float var8 = (float) this.width * var6 / 256.0F;
        var5.setColorRGBA_F(1.0F, 1.0F, 1.0F, 1.0F);
        int var9 = this.width;
        int var10 = this.height;
        var5.addVertexWithUV(0.0D, (double) var10, (double) this.zLevel, (double) (0.5F - var7), (double) (0.5F + var8));
        var5.addVertexWithUV((double) var9, (double) var10, (double) this.zLevel, (double) (0.5F - var7), (double) (0.5F - var8));
        var5.addVertexWithUV((double) var9, 0.0D, (double) this.zLevel, (double) (0.5F + var7), (double) (0.5F - var8));
        var5.addVertexWithUV(0.0D, 0.0D, (double) this.zLevel, (double) (0.5F + var7), (double) (0.5F + var8));
        var4.draw();

    }

    private void drawPanorama(int p_73970_1_, int p_73970_2_, float p_73970_3_) {
        Tessellator var4 = Tessellator.getInstance();
        WorldRenderer var5 = var4.getWorldRenderer();
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
        byte var6 = 8;

        for (int var7 = 0; var7 < var6 * var6; ++var7) {
            GlStateManager.pushMatrix();
            float var8 = ((float) (var7 % var6) / (float) var6 - 0.5F) / 64.0F;
            float var9 = ((float) (var7 / var6) / (float) var6 - 0.5F) / 64.0F;
            float var10 = 0.0F;
            GlStateManager.translate(var8, var9, var10);
            GlStateManager.rotate(MathHelper.sin(((float) this.panoramaTimer + p_73970_3_) / 400.0F) * 25.0F + 20.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(-((float) this.panoramaTimer + p_73970_3_) * 0.1F, 0.0F, 1.0F, 0.0F);

            for (int var11 = 0; var11 < 6; ++var11) {
                GlStateManager.pushMatrix();

                if (var11 == 1) {
                    GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
                }

                if (var11 == 2) {
                    GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
                }

                if (var11 == 3) {
                    GlStateManager.rotate(-90.0F, 0.0F, 1.0F, 0.0F);
                }

                if (var11 == 4) {
                    GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
                }

                if (var11 == 5) {
                    GlStateManager.rotate(-90.0F, 1.0F, 0.0F, 0.0F);
                }

                this.mc.getTextureManager().bindTexture(titlePanoramaPaths[var11]);
                var5.startDrawingQuads();
                var5.setColorRGBA_I(16777215, 255 / (var7 + 1));
                float var12 = 0.0F;
                var5.addVertexWithUV(-1.0D, -1.0D, 1.0D, (double) (0.0F + var12), (double) (0.0F + var12));
                var5.addVertexWithUV(1.0D, -1.0D, 1.0D, (double) (1.0F - var12), (double) (0.0F + var12));
                var5.addVertexWithUV(1.0D, 1.0D, 1.0D, (double) (1.0F - var12), (double) (1.0F - var12));
                var5.addVertexWithUV(-1.0D, 1.0D, 1.0D, (double) (0.0F + var12), (double) (1.0F - var12));
                var4.draw();
                GlStateManager.popMatrix();
            }

            GlStateManager.popMatrix();
            GlStateManager.colorMask(true, true, true, false);


        }

        var5.setTranslation(0.0D, 0.0D, 0.0D);
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
        Tessellator var2 = Tessellator.getInstance();
        WorldRenderer var3 = var2.getWorldRenderer();
        var3.startDrawingQuads();
        GlStateManager.disableAlpha();
        byte var4 = 3;

        for (int var5 = 0; var5 < var4; ++var5) {
            var3.setColorRGBA_F(1.0F, 1.0F, 1.0F, 1.0F / (float) (var5 + 1));
            int var6 = this.width;
            int var7 = this.height;
            float var8 = (float) (var5 - var4 / 2) / 256.0F;
            var3.addVertexWithUV((double) var6, (double) var7, (double) this.zLevel, (double) (0.0F + var8), 1.0D);
            var3.addVertexWithUV((double) var6, 0.0D, (double) this.zLevel, (double) (1.0F + var8), 1.0D);
            var3.addVertexWithUV(0.0D, 0.0D, (double) this.zLevel, (double) (1.0F + var8), 0.0D);
            var3.addVertexWithUV(0.0D, (double) var7, (double) this.zLevel, (double) (0.0F + var8), 0.0D);
        }

        var2.draw();
        GlStateManager.enableAlpha();
        GlStateManager.colorMask(true, true, true, true);

    }

    /**
     * Called when the mouse is clicked. Args : mouseX, mouseY, clickedButton
     */
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        synchronized (this.threadLock) {
            if (this.openGLWarning1.length() > 0 && mouseX >= this.field_92022_t && mouseX <= this.field_92020_v && mouseY >= this.field_92021_u && mouseY <= this.field_92019_w) {
                GuiConfirmOpenLink guiconfirmopenlink = new GuiConfirmOpenLink(this, this.openGLWarningLink, 13, true);
                guiconfirmopenlink.disableSecurityWarning();
                this.mc.displayGuiScreen(guiconfirmopenlink);
            }
        }
    }

}
