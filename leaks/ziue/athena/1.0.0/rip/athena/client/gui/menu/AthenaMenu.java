package rip.athena.client.gui.menu;

import java.util.concurrent.atomic.*;
import net.minecraft.client.renderer.texture.*;
import rip.athena.client.utils.animations.*;
import com.google.common.collect.*;
import net.minecraft.client.*;
import org.apache.commons.io.*;
import java.io.*;
import org.lwjgl.opengl.*;
import net.minecraft.client.resources.*;
import java.util.*;
import rip.athena.client.*;
import rip.athena.client.utils.animations.impl.*;
import java.net.*;
import net.minecraft.world.demo.*;
import net.minecraft.world.storage.*;
import net.minecraft.realms.*;
import rip.athena.client.utils.input.*;
import rip.athena.client.utils.font.*;
import java.awt.*;
import net.minecraft.client.renderer.*;
import rip.athena.client.utils.render.*;
import rip.athena.client.gui.menu.altmanager.*;
import net.minecraft.client.gui.*;
import org.apache.logging.log4j.*;
import net.minecraft.util.*;

public class AthenaMenu extends GuiScreen implements GuiYesNoCallback
{
    private static final AtomicInteger field_175373_f;
    private static final Logger logger;
    private static final Random RANDOM;
    private float updateCounter;
    private String splashText;
    private GuiButton buttonResetDemo;
    private int panoramaTimer;
    private DynamicTexture viewportTexture;
    private boolean field_175375_v;
    private final Object threadLock;
    private String openGLWarning1;
    private String openGLWarning2;
    private String openGLWarningLink;
    private static final ResourceLocation splashTexts;
    private static final ResourceLocation minecraftTitleTextures;
    private static final ResourceLocation[] titlePanoramaPaths;
    public static final String field_96138_a;
    private int field_92024_r;
    private int field_92023_s;
    private int field_92022_t;
    private int field_92021_u;
    private int field_92020_v;
    private int field_92019_w;
    private ResourceLocation backgroundTexture;
    private GuiButton realmsButton;
    private boolean field_183502_L;
    private GuiScreen field_183503_M;
    public Animation introAnimation;
    public boolean closeIntro;
    private CloseType closeType;
    
    public AthenaMenu() {
        this.field_175375_v = true;
        this.threadLock = new Object();
        this.openGLWarning2 = AthenaMenu.field_96138_a;
        this.field_183502_L = false;
        this.splashText = "missingno";
        BufferedReader bufferedreader = null;
        try {
            final List<String> list = (List<String>)Lists.newArrayList();
            bufferedreader = new BufferedReader(new InputStreamReader(Minecraft.getMinecraft().getResourceManager().getResource(AthenaMenu.splashTexts).getInputStream(), Charsets.UTF_8));
            String s;
            while ((s = bufferedreader.readLine()) != null) {
                s = s.trim();
                if (!s.isEmpty()) {
                    list.add(s);
                }
            }
            if (!list.isEmpty()) {
                do {
                    this.splashText = list.get(AthenaMenu.RANDOM.nextInt(list.size()));
                } while (this.splashText.hashCode() == 125780783);
            }
        }
        catch (IOException ex) {}
        finally {
            if (bufferedreader != null) {
                try {
                    bufferedreader.close();
                }
                catch (IOException ex2) {}
            }
        }
        this.updateCounter = AthenaMenu.RANDOM.nextFloat();
        this.openGLWarning1 = "";
        if (!GLContext.getCapabilities().OpenGL20 && !OpenGlHelper.areShadersSupported()) {
            this.openGLWarning1 = I18n.format("title.oldgl1", new Object[0]);
            this.openGLWarning2 = I18n.format("title.oldgl2", new Object[0]);
            this.openGLWarningLink = "https://help.mojang.com/customer/portal/articles/325948?ref=game";
        }
    }
    
    @Override
    public void updateScreen() {
        ++this.panoramaTimer;
    }
    
    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
    
    @Override
    protected void keyTyped(final char typedChar, final int keyCode) throws IOException {
    }
    
    @Override
    public void initGui() {
        if (Athena.INSTANCE.getAccountManager().isFirstLogin) {
            this.mc.displayGuiScreen(new GuiAltManager());
        }
        this.introAnimation = new EaseBackIn(450, 1.0, 1.5f);
        this.viewportTexture = new DynamicTexture(256, 256);
        this.backgroundTexture = this.mc.getTextureManager().getDynamicTextureLocation("background", this.viewportTexture);
        final int j = this.height / 4 + 78;
        this.addSingleplayerMultiplayerButtons(j, 24);
        this.buttonList.add(new GuiButton(0, this.width / 2 - 100, j + 72 - 24, 98, 20, I18n.format("menu.options", new Object[0])));
        this.buttonList.add(new GuiButton(4, this.width / 2 + 2, j + 72 - 24, 98, 20, "Store"));
    }
    
    private void addSingleplayerMultiplayerButtons(final int p_73969_1_, final int p_73969_2_) {
        this.buttonList.add(new GuiButton(1, this.width / 2 - 100, p_73969_1_, I18n.format("menu.singleplayer", new Object[0])));
        this.buttonList.add(new GuiButton(2, this.width / 2 - 100, p_73969_1_ + p_73969_2_ * 1, I18n.format("menu.multiplayer", new Object[0])));
    }
    
    @Override
    protected void actionPerformed(final GuiButton button) throws IOException {
        if (button.id == 0) {
            this.mc.displayGuiScreen(new GuiOptions(this, this.mc.gameSettings));
        }
        if (button.id == 5) {
            this.mc.displayGuiScreen(new GuiLanguage(this, this.mc.gameSettings, this.mc.getLanguageManager()));
        }
        if (button.id == 1) {
            this.mc.displayGuiScreen(new GuiSelectWorld(this));
        }
        if (button.id == 2) {
            this.mc.displayGuiScreen(new GuiMultiplayer(this));
        }
        if (button.id == 14 && this.realmsButton.visible) {
            this.switchToRealms();
        }
        if (button.id == 4) {
            try {
                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().browse(new URI("http://store.athena.rip"));
                }
            }
            catch (Exception ex) {}
        }
        if (button.id == 11) {
            this.mc.launchIntegratedServer("Demo_World", "Demo_World", DemoWorldServer.demoWorldSettings);
        }
        if (button.id == 12) {
            final ISaveFormat isaveformat = this.mc.getSaveLoader();
            final WorldInfo worldinfo = isaveformat.getWorldInfo("Demo_World");
            if (worldinfo != null) {
                final GuiYesNo guiyesno = GuiSelectWorld.func_152129_a(this, worldinfo.getWorldName(), 12);
                this.mc.displayGuiScreen(guiyesno);
            }
        }
    }
    
    private void switchToRealms() {
        final RealmsBridge realmsbridge = new RealmsBridge();
        realmsbridge.switchToRealms(this);
    }
    
    @Override
    public void confirmClicked(final boolean result, final int id) {
        if (result && id == 12) {
            final ISaveFormat isaveformat = this.mc.getSaveLoader();
            isaveformat.flushCache();
            isaveformat.deleteWorldDirectory("Demo_World");
            this.mc.displayGuiScreen(this);
        }
        else if (id == 13) {
            if (result) {
                try {
                    final Class<?> oclass = Class.forName("java.awt.Desktop");
                    final Object object = oclass.getMethod("getDesktop", (Class<?>[])new Class[0]).invoke(null, new Object[0]);
                    oclass.getMethod("browse", URI.class).invoke(object, new URI(this.openGLWarningLink));
                }
                catch (Throwable throwable) {
                    AthenaMenu.logger.error("Couldn't open link", throwable);
                }
            }
            this.mc.displayGuiScreen(this);
        }
    }
    
    @Override
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
        DrawUtils.drawImage(new ResourceLocation("Athena/menu/wallpaper3.png"), 0, 0, this.width, this.height);
        final int[] size = InputUtils.getWindowsSize();
        final int startX = size[0] / 2;
        final int startY = size[1] / 2;
        final int x = startX - 75;
        final int y = this.height / 4 + 35;
        final int width = 150;
        final int height = 100;
        FontManager.getProductSansBold(60).drawCenteredString(Athena.INSTANCE.getClientName(), this.width / 2.0f, y, new Color(255, 255, 255).getRGB());
        GlStateManager.pushMatrix();
        DrawUtils.drawImage(new ResourceLocation("Athena/menu/exit.png"), startX + startX - 20, 10, 10, 10);
        DrawUtils.drawImage(new ResourceLocation("Athena/menu/usericon.png"), startX + startX - 49, 6, 18, 18);
        GlStateManager.popMatrix();
        GlStateManager.pushMatrix();
        final boolean isOverAccountManager = mouseX >= startX + startX - 50 && mouseX <= startX + startX - 30 && mouseY >= startY - startY + 5 && mouseY <= startY - startY + 25;
        RoundedUtils.drawRoundedRect((float)(startX + startX - 50), (float)(startY - startY + 5), (float)(startX + startX - 30), (float)(startY - startY + 25), 14.0f, isOverAccountManager ? new Color(150, 150, 150, 100).getRGB() : new Color(100, 100, 100, 100).getRGB());
        RoundedUtils.drawRoundedGradientOutlineCorner((float)(startX + startX - 50 + 1), (float)(startY - startY + 5 + 1), (float)(startX + startX - 30 - 1), (float)(startY - startY + 25 - 1), 3.0f, 12.0f, ColorUtil.getClientColor(0, 255).getRGB(), ColorUtil.getClientColor(90, 255).getRGB(), ColorUtil.getClientColor(180, 255).getRGB(), ColorUtil.getClientColor(270, 255).getRGB());
        final boolean isOverExit = mouseX >= startX + startX - 25 && mouseX <= startX + startX - 5 && mouseY >= startY - startY + 5 && mouseY <= startY - startY + 25;
        RoundedUtils.drawRoundedRect((float)(startX + startX - 25), (float)(startY - startY + 5), (float)(startX + startX - 5), (float)(startY - startY + 25), 14.0f, isOverExit ? new Color(150, 150, 150, 100).getRGB() : new Color(100, 100, 100, 100).getRGB());
        RoundedUtils.drawRoundedGradientOutlineCorner((float)(startX + startX - 25 + 1), (float)(startY - startY + 5 + 1), (float)(startX + startX - 5 - 1), (float)(startY - startY + 25 - 1), 3.0f, 12.0f, ColorUtil.getClientColor(0, 255).getRGB(), ColorUtil.getClientColor(90, 255).getRGB(), ColorUtil.getClientColor(180, 255).getRGB(), ColorUtil.getClientColor(270, 255).getRGB());
        GlStateManager.popMatrix();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
    
    @Override
    protected void mouseClicked(final int mouseX, final int mouseY, final int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        final ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        final int[] size = InputUtils.getWindowsSize();
        final int startX = size[0] / 2;
        final int startY = size[1] / 2;
        final boolean isOverExit = mouseX >= startX + startX - 25 && mouseX <= startX + startX - 5 && mouseY >= startY - startY + 5 && mouseY <= startY - startY + 25;
        final boolean isOverAccountManager = mouseX >= startX + startX - 50 && mouseX <= startX + startX - 30 && mouseY >= startY - startY + 5 && mouseY <= startY - startY + 25;
        if (isOverExit) {
            this.mc.shutdown();
        }
        if (isOverAccountManager) {
            this.mc.displayGuiScreen(new GuiAccountManager(this));
        }
        synchronized (this.threadLock) {
            if (this.openGLWarning1.length() > 0 && mouseX >= this.field_92022_t && mouseX <= this.field_92020_v && mouseY >= this.field_92021_u && mouseY <= this.field_92019_w) {
                final GuiConfirmOpenLink guiconfirmopenlink = new GuiConfirmOpenLink(this, this.openGLWarningLink, 13, true);
                guiconfirmopenlink.disableSecurityWarning();
                this.mc.displayGuiScreen(guiconfirmopenlink);
            }
        }
    }
    
    @Override
    public void onGuiClosed() {
        if (this.field_183503_M != null) {
            this.field_183503_M.onGuiClosed();
        }
    }
    
    static {
        field_175373_f = new AtomicInteger(0);
        logger = LogManager.getLogger();
        RANDOM = new Random();
        splashTexts = new ResourceLocation("texts/splashes.txt");
        minecraftTitleTextures = new ResourceLocation("textures/gui/title/minecraft.png");
        titlePanoramaPaths = new ResourceLocation[] { new ResourceLocation("Athena/menu/panorama_0.png"), new ResourceLocation("Athena/menu/panorama_1.png"), new ResourceLocation("Athena/menu/panorama_2.png"), new ResourceLocation("Athena/menu/panorama_3.png"), new ResourceLocation("Athena/menu/panorama_4.png"), new ResourceLocation("Athena/menu/panorama_5.png") };
        field_96138_a = "Please click " + EnumChatFormatting.UNDERLINE + "here" + EnumChatFormatting.RESET + " for more information.";
    }
}
