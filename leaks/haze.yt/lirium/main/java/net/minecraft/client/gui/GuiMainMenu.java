package net.minecraft.client.gui;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.Runnables;

import java.awt.*;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Proxy;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import com.google.gson.JsonObject;
import com.mojang.authlib.Agent;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication;
import de.lirium.Client;
import de.lirium.base.profile.Profile;
import de.lirium.util.json.JsonUtil;
import de.lirium.util.render.FontRenderer;
import de.lirium.util.render.RenderUtil;
import de.lirium.util.service.ServiceUtil;
import fr.litarvan.openauth.microsoft.MicrosoftAuthResult;
import fr.litarvan.openauth.microsoft.MicrosoftAuthenticationException;
import fr.litarvan.openauth.microsoft.MicrosoftAuthenticator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.IResource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Session;
import net.minecraft.util.StringUtils;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.WorldServerDemo;
import net.minecraft.world.storage.ISaveFormat;
import net.minecraft.world.storage.WorldInfo;
import optifine.Reflector;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;

public class GuiMainMenu extends GuiScreen {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Random RANDOM = new Random();

    /**
     * Counts the number of screen updates.
     */
    private final float updateCounter;

    /**
     * The splash message.
     */
    private String splashText;
    private GuiButton buttonResetDemo;

    /**
     * Timer used to rotate the panorama, increases every tick.
     */
    private float panoramaTimer;

    /**
     * Texture allocated for the current viewport of the main menu's panorama background.
     */
    private DynamicTexture viewportTexture;

    /**
     * The Object object utilized as a thread lock when performing non thread-safe operations
     */
    private final Object threadLock = new Object();
    public static final String MORE_INFO_TEXT = "Please click " + TextFormatting.UNDERLINE + "here" + TextFormatting.RESET + " for more information.";

    /**
     * Width of openGLWarning2
     */
    private int openGLWarning2Width;

    /**
     * Width of openGLWarning1
     */
    private int openGLWarning1Width;

    /**
     * Left x coordinate of the OpenGL warning
     */
    private int openGLWarningX1;

    /**
     * Top y coordinate of the OpenGL warning
     */
    private int openGLWarningY1;

    /**
     * Right x coordinate of the OpenGL warning
     */
    private int openGLWarningX2;

    /**
     * Bottom y coordinate of the OpenGL warning
     */
    private int openGLWarningY2;

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

    private String loginStatus = "waiting...";

    private static final ResourceLocation SPLASH_TEXTS = new ResourceLocation("texts/splashes.txt");
    private static final ResourceLocation MINECRAFT_TITLE_TEXTURES = new ResourceLocation("textures/gui/title/minecraft.png");
    private static final ResourceLocation field_194400_H = new ResourceLocation("textures/gui/title/edition.png");

    /**
     * An array of all the paths to the panorama pictures.
     */
    private static final ResourceLocation[] TITLE_PANORAMA_PATHS = new ResourceLocation[]{new ResourceLocation("textures/gui/title/background/panorama_0.png"), new ResourceLocation("textures/gui/title/background/panorama_1.png"), new ResourceLocation("textures/gui/title/background/panorama_2.png"), new ResourceLocation("textures/gui/title/background/panorama_3.png"), new ResourceLocation("textures/gui/title/background/panorama_4.png"), new ResourceLocation("textures/gui/title/background/panorama_5.png")};
    private ResourceLocation backgroundTexture;

    private int field_193978_M;
    private int field_193979_N;
    private GuiButton modButton;
    private GuiScreen modUpdateNotification;

    private boolean expandProfiles;

    public GuiMainMenu() {
        this.openGLWarning2 = MORE_INFO_TEXT;
        this.splashText = "missingno";
        IResource iresource = null;

        try {
            List<String> list = Lists.<String>newArrayList();
            iresource = Minecraft.getMinecraft().getResourceManager().getResource(SPLASH_TEXTS);
            BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(iresource.getInputStream(), StandardCharsets.UTF_8));
            String s;

            while ((s = bufferedreader.readLine()) != null) {
                s = s.trim();

                if (!s.isEmpty()) {
                    list.add(s);
                }
            }

            if (!list.isEmpty()) {
                while (true) {
                    this.splashText = list.get(RANDOM.nextInt(list.size()));

                    if (this.splashText.hashCode() != 125780783) {
                        break;
                    }
                }
            }
        } catch (IOException var8) {
            ;
        } finally {
            IOUtils.closeQuietly((Closeable) iresource);
        }

        this.updateCounter = RANDOM.nextFloat();
        this.openGLWarning1 = "";

        if (!GLContext.getCapabilities().OpenGL20 && !OpenGlHelper.areShadersSupported()) {
            this.openGLWarning1 = I18n.format("title.oldgl1");
            this.openGLWarning2 = I18n.format("title.oldgl2");
            this.openGLWarningLink = "https://help.mojang.com/customer/portal/articles/325948?ref=game";
        }
    }

    /**
     * Called from the main game loop to update the screen.
     */
    public void updateScreen() {
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
        expandProfiles = false;
        this.viewportTexture = new DynamicTexture(256, 256);
        this.backgroundTexture = this.mc.getTextureManager().getDynamicTextureLocation("background", this.viewportTexture);
        this.field_193978_M = this.fontRendererObj.getStringWidth("Copyright Mojang AB. Do not distribute!");
        this.field_193979_N = this.width - this.field_193978_M - 2;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());

        if (calendar.get(2) + 1 == 12 && calendar.get(5) == 24) {
            this.splashText = "Merry X-mas!";
        } else if (calendar.get(2) + 1 == 1 && calendar.get(5) == 1) {
            this.splashText = "Happy new year!";
        } else if (calendar.get(2) + 1 == 10 && calendar.get(5) == 31) {
            this.splashText = "OOoooOOOoooo! Spooky!";
        }

        int i = 24;
        int j = this.height / 4 + 48;

        if (this.mc.isDemo()) {
            this.addDemoButtons(j, 24);
        } else {
            this.addSingleplayerMultiplayerButtons(j, 24);
        }

        this.buttonList.add(new GuiButton(0, this.width / 2 - 100, j + 72 + 12, 98, 20, I18n.format("menu.options")));
        this.buttonList.add(new GuiButton(4, this.width / 2 + 2, j + 72 + 12, 98, 20, I18n.format("menu.quit")));
        this.buttonList.add(new GuiButtonLanguage(5, this.width / 2 - 124, j + 72 + 12));

        synchronized (this.threadLock) {
            this.openGLWarning1Width = this.fontRendererObj.getStringWidth(this.openGLWarning1);
            this.openGLWarning2Width = this.fontRendererObj.getStringWidth(this.openGLWarning2);
            int k = Math.max(this.openGLWarning1Width, this.openGLWarning2Width);
            this.openGLWarningX1 = (this.width - k) / 2;
            this.openGLWarningY1 = (this.buttonList.get(0)).yPosition - 24;
            this.openGLWarningX2 = this.openGLWarningX1 + k;
            this.openGLWarningY2 = this.openGLWarningY1 + 24;
        }

        if (Reflector.NotificationModUpdateScreen_init.exists()) {
            this.modUpdateNotification = (GuiScreen) Reflector.call(Reflector.NotificationModUpdateScreen_init, this, this.modButton);
        }
    }

    /**
     * Adds Singleplayer and Multiplayer buttons on Main Menu for players who have bought the game.
     */
    private void addSingleplayerMultiplayerButtons(int p_73969_1_, int p_73969_2_) {
        this.buttonList.add(new GuiButton(1, this.width / 2 - 100, p_73969_1_, I18n.format("menu.singleplayer")));
        this.buttonList.add(new GuiButton(2, this.width / 2 - 100, p_73969_1_ + p_73969_2_ * 1, I18n.format("menu.multiplayer")));
        this.buttonList.add(new GuiButton(1337, this.width - 65, this.height - 32, 60, 20, "Profiles"));

        if (Reflector.GuiModList_Constructor.exists()) {
            this.addButton(new GuiButton(14, this.width / 2 + 2, p_73969_1_ + p_73969_2_ * 2, 98, 20, "Alt Manager"));
            this.buttonList.add(this.modButton = new GuiButton(6, this.width / 2 - 100, p_73969_1_ + p_73969_2_ * 2, 98, 20, I18n.format("fml.menu.mods")));
        } else {
            this.addButton(new GuiButton(14, this.width / 2 - 100, p_73969_1_ + p_73969_2_ * 2, "Clipboard Login"));
        }
    }

    /**
     * Adds Demo buttons on Main Menu for players who are playing Demo.
     */
    private void addDemoButtons(int p_73972_1_, int p_73972_2_) {
        this.buttonList.add(new GuiButton(11, this.width / 2 - 100, p_73972_1_, I18n.format("menu.playdemo")));
        this.buttonResetDemo = this.addButton(new GuiButton(12, this.width / 2 - 100, p_73972_1_ + p_73972_2_ * 1, I18n.format("menu.resetdemo")));
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
        switch (button.id) {
            case 0:
                mc.displayGuiScreen(new GuiOptions(this, mc.gameSettings));
                break;
            case 5:
                mc.displayGuiScreen(new GuiLanguage(this, mc.gameSettings, mc.getLanguageManager()));
                break;
            case 1:
                mc.displayGuiScreen(new GuiWorldSelection(this));
                break;
            case 2:
                mc.displayGuiScreen(new GuiMultiplayer(this));
                break;
            case 14: {
                loginStatus = "waiting...";
                if (!getClipboardString().trim().contains(":")) {
                    if (!isShiftKeyDown()) {
                        if(ServiceUtil.switchService("Mojang")) {
                            final String name = getClipboardString().trim();
                            if (name.length() <= 16 && name.length() >= 3 && name.matches("[a-zA-Z0-9_]*")) {
                                Minecraft.getMinecraft().setSession(new Session(name, "", "", "LEGACY"));
                                loginStatus = "Logged in as §e§l" + name + " §7- §c§lCracked";
                            } else {
                                loginStatus = "§c§lInvalid username";
                            }
                        }
                    } else {
                        if (ServiceUtil.switchService("EasyMC")) {
                            loginStatus = "Logging in...";
                            final JsonObject jsonObject = JsonUtil.getEasyMCContent("https://api.easymc.io/v1/token/redeem", getClipboardString().trim());
                            if (jsonObject.has("error")) {
                                System.out.println("Error: " + jsonObject.get("error").getAsString());
                                loginStatus = "§cInvalid Alt Token";
                            } else {
                                final String session = jsonObject.get("session").getAsString();
                                final String name = jsonObject.get("mcName").getAsString();
                                final String uuid = jsonObject.get("uuid").getAsString();
                                mc.setSession(new Session(name, uuid, session, "mojang"));
                                loginStatus = "Logged in as §e§l" + name + " §7- §e§lEasyMC";
                            }
                        }
                    }
                    return;
                } else {
                    if(ServiceUtil.switchService("Mojang")) {
                        final String[] loginCredentials = getClipboardString().trim().split(":");
                        if (loginCredentials.length == 3) {
                            mc.setSession(new Session(loginCredentials[0], loginCredentials[1], loginCredentials[2], "mojang"));
                            loginStatus = "Logged in as §e§l" + loginCredentials[0] + " §7- §e§lSession";
                        } else {
                            try {
                                loginStatus = "Logging in...";
                                final MicrosoftAuthResult result = new MicrosoftAuthenticator().loginWithCredentials(loginCredentials[0], loginCredentials[1]);
                                mc.setSession(new Session(result.getProfile().getName(), result.getProfile().getId(), result.getAccessToken(), "mojang"));
                                loginStatus = "Logged in as §e§l" + result.getProfile().getName() + " §7- §a§lMicrosoft";
                            } catch (Exception ex) {
                                loginStatus = "§cLogin failed";
                                ex.printStackTrace();
                            }
                        }
                    }
                }
                break;
            }
            case 4:
                mc.shutdown();
                break;
        }

        if (button.id == 1337)
            expandProfiles = !expandProfiles;
    }

    public void confirmClicked(boolean result, int id) {
        if (result && id == 12) {
            ISaveFormat isaveformat = this.mc.getSaveLoader();
            isaveformat.flushCache();
            isaveformat.deleteWorldDirectory("Demo_World");
            this.mc.displayGuiScreen(this);
        } else if (id == 12) {
            this.mc.displayGuiScreen(this);
        } else if (id == 13) {
            if (result) {
                try {
                    Class<?> oclass = Class.forName("java.awt.Desktop");
                    Object object = oclass.getMethod("getDesktop").invoke((Object) null);
                    oclass.getMethod("browse", URI.class).invoke(object, new URI(this.openGLWarningLink));
                } catch (Throwable throwable1) {
                    LOGGER.error("Couldn't open link", throwable1);
                }
            }

            this.mc.displayGuiScreen(this);
        }
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        GlStateManager.disableAlpha();

        RenderUtil.backgroundShader.render();

        final Calendar calendar = Calendar.getInstance();
        if (calendar.get(Calendar.MONTH) == Calendar.APRIL && calendar.get(Calendar.DAY_OF_MONTH) == 1) {
            final ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
            RenderUtil.drawPicture(new ResourceLocation("lirium/images/exo.png"), 0, 0, sr.getScaledWidth(), sr.getScaledHeight());

            Gui.drawRect(sr.getScaledWidth() / 2 - 100, 75, sr.getScaledWidth() / 2 + 100, 150, new Color(0, 0, 0, 150).getRGB());

            GL11.glPushMatrix();
            GL11.glScaled(2.0D, 2.0D, 2.0D);
            fontRendererObj.drawStringWithShadow("Jungle Client", (sr.getScaledWidth() / 2.0F - fontRendererObj.getStringWidth("Jungle Client")) / 2.0F, 50, new Color(0, 255, 0).getRGB());
            GL11.glPopMatrix();
        }

        fontRendererObj.drawString(loginStatus, 2, 2, -1);

        String s = "Based on Minecraft 1.12.2";

        if (this.mc.isDemo()) {
            s = s + " Demo";
        } else {
            s = s + ("release".equalsIgnoreCase(this.mc.getVersionType()) ? "" : "/" + this.mc.getVersionType());
        }

        if (Reflector.FMLCommonHandler_getBrandings.exists()) {
            Object object = Reflector.call(Reflector.FMLCommonHandler_instance);
            List<String> list = Lists.<String>reverse((List) Reflector.call(object, Reflector.FMLCommonHandler_getBrandings, true));

            for (int l1 = 0; l1 < list.size(); ++l1) {
                String s1 = list.get(l1);

                if (!Strings.isNullOrEmpty(s1)) {
                    this.drawString(this.fontRendererObj, s1, 2, this.height - (10 + l1 * (this.fontRendererObj.FONT_HEIGHT + 1)), 16777215);
                }
            }
        } else {
            this.drawString(this.fontRendererObj, s, 2, this.height - 10, -1);
        }

        this.drawString(this.fontRendererObj, "Copyright Mojang AB. Do not distribute!", this.field_193979_N, this.height - 10, -1);

        if (mouseX > this.field_193979_N && mouseX < this.field_193979_N + this.field_193978_M && mouseY > this.height - 10 && mouseY < this.height && Mouse.isInsideWindow()) {
            drawRect(this.field_193979_N, this.height - 1, this.field_193979_N + this.field_193978_M, this.height, -1);
        }

        if (this.openGLWarning1 != null && !this.openGLWarning1.isEmpty()) {
            drawRect(this.openGLWarningX1 - 2, this.openGLWarningY1 - 2, this.openGLWarningX2 + 2, this.openGLWarningY2 - 1, 1428160512);
            this.drawString(this.fontRendererObj, this.openGLWarning1, this.openGLWarningX1, this.openGLWarningY1, -1);
            this.drawString(this.fontRendererObj, this.openGLWarning2, (this.width - this.openGLWarning2Width) / 2, (this.buttonList.get(0)).yPosition - 12, -1);
        }

        if (expandProfiles) {
            int y = 0;
            for (Profile profile : Client.INSTANCE.getProfileManager().getFeatures()) {
                final String name = (Client.INSTANCE.getProfileManager().get() == profile ? "§e§l" : "") + profile.getName();
                fontRendererObj.drawStringWithShadow(name, width - fontRendererObj.getStringWidth(name) / 2F - 65 / 2F, height - 42 - y, -1);
                y += fontRendererObj.FONT_HEIGHT;
            }
        }


        super.drawScreen(mouseX, mouseY, partialTicks);

        if (this.modUpdateNotification != null) {
            this.modUpdateNotification.drawScreen(mouseX, mouseY, partialTicks);
        }
    }

    /**
     * Called when the mouse is clicked. Args : mouseX, mouseY, clickedButton
     */
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        if (expandProfiles) {
            int y = 0;
            for (Profile profile : Client.INSTANCE.getProfileManager().getFeatures()) {
                final String name = (Client.INSTANCE.getProfileManager().get() == profile ? "§e§l" : "") + profile.getName();
                if (fontRendererObj.isMouseOver(name, width - fontRendererObj.getStringWidth(name) / 2F - 65 / 2F, height - 42 - y, mouseX, mouseY)) {
                    Client.INSTANCE.getProfileManager().get().save();
                    Client.INSTANCE.getProfileManager().setCurrent(profile);
                    profile.read();
                }
                y += fontRendererObj.FONT_HEIGHT;
            }
        }

        synchronized (this.threadLock) {
            if (!this.openGLWarning1.isEmpty() && !StringUtils.isNullOrEmpty(this.openGLWarningLink) && mouseX >= this.openGLWarningX1 && mouseX <= this.openGLWarningX2 && mouseY >= this.openGLWarningY1 && mouseY <= this.openGLWarningY2) {
                GuiConfirmOpenLink guiconfirmopenlink = new GuiConfirmOpenLink(this, this.openGLWarningLink, 13, true);
                guiconfirmopenlink.disableSecurityWarning();
                this.mc.displayGuiScreen(guiconfirmopenlink);
            }
        }

        if (mouseX > this.field_193979_N && mouseX < this.field_193979_N + this.field_193978_M && mouseY > this.height - 10 && mouseY < this.height) {
            mc.displayGuiScreen(new GuiWinGame(false, Runnables.doNothing()));
        }
    }

    /**
     * Called when the screen is unloaded. Used to disable keyboard repeat events
     */
    public void onGuiClosed() {
    }
}
