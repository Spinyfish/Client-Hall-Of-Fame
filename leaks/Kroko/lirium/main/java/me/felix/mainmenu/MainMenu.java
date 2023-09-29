/*
 * Copyright Felix Hans from MainMenu coded for haze.yt / Lirium . - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited
 */

package me.felix.mainmenu;

import de.lirium.util.cookie.*;
import com.eatthepath.uuid.FastUUID;
import com.google.gson.JsonObject;
import com.mojang.authlib.Agent;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication;
import de.lirium.Client;
import de.lirium.base.animation.Animation;
import de.lirium.base.animation.Easings;
import de.lirium.base.background.Background;
import de.lirium.base.background.BackgroundManager;
import de.lirium.base.changelog.ChangeType;
import de.lirium.base.helper.TimeHelper;
import de.lirium.base.profile.Profile;
import de.lirium.util.altening.AlteningUtil;
import de.lirium.util.altening.data.AlteningSession;
import de.lirium.util.json.JsonUtil;
import de.lirium.util.render.FontRenderer;
import de.lirium.util.render.RenderUtil;
import de.lirium.util.render.StencilUtil;
import de.lirium.util.render.shader.shaders.AcrylBlurShader;
import de.lirium.util.service.ServiceUtil;
import fr.litarvan.openauth.microsoft.AuthTokens;
import fr.litarvan.openauth.microsoft.MicrosoftAuthResult;
import fr.litarvan.openauth.microsoft.MicrosoftAuthenticationException;
import fr.litarvan.openauth.microsoft.MicrosoftAuthenticator;
import god.buddy.aot.BCompiler;
import me.felix.shader.access.ShaderAccess;
import me.felix.shader.render.ingame.RenderableShaders;
import me.felix.util.ScrollHandler;
import me.felix.util.dropshadow.JHLabsShaderRenderer;
import me.felix.util.math.ClockMath;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.Session;
import org.jsoup.Jsoup;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import static me.felix.util.dropshadow.JHLabsShaderRenderer.*;

import static org.lwjgl.opengl.GL11.*;

import java.awt.*;
import java.io.IOException;
import java.net.Proxy;
import java.net.URLDecoder;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class MainMenu extends GuiScreen {

    private int calcWidth;

    private boolean hoveringBar, profilesExtended, hoveringChangelog, dragBackground;

    private int barX, barY, profileWidth, dragX, draggedX, prevDragX, swipeSpeed;

    private int barWidth, barHeight;

    private final Animation barAnimation = new Animation(), roundnessAnimation = new Animation(), alphaAnimationClock = new Animation(), extendAnimation = new Animation();

    public static String loginStatus = "waiting...";

    private FontRenderer boldFont, font, dragPointFont;
    private final ScrollHandler scrollHandler = new ScrollHandler();

    @Override
    public void initGui() {
        this.dragBackground = false;
        this.draggedX = 0;
        super.initGui();
    }

    //TODO: Animations
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(mouseX, mouseY);
        this.updateBarConditions(mouseX, mouseY);
        this.renderBar(mouseX, mouseY);

        final String display = "(Changelog)";
        float x = width - font.getStringWidth(display) - 20;
        float y = 10;
        if (!Client.INSTANCE.getChangelogManager().getFeatures().isEmpty()) {
            if (hoveringChangelog) {
                x -= 150;
                final float finalX = x;
                final float finalY = y;
                final float width = this.width - x - 5;
                final float height = this.height - y;
                hoveringChangelog = isHovered((double) mouseX, (double) mouseY, (int) x, (int) y, (int) width, (int) height);
                //roundedRectOutlineShader.drawRound(x, y, width, height, 20, 1, Color.white);

                float finalX1 = x;
                ShaderAccess.blurShaderRunnables.add(() -> {
                    RenderUtil.drawRoundedRect(finalX1, y, width, height - 8, 4, Color.white);
                });
                StencilUtil.init();
                RenderUtil.drawRoundedRect(finalX1, y, width, height - 8, 4, Color.white);
                StencilUtil.readBuffer(1);
                scrollHandler.onScroll();

                AtomicReference<Float> changelogY = new AtomicReference<>(scrollHandler.getScroll() + 10);
                Client.INSTANCE.getChangelogManager().getFeatures().stream().sorted((o1, o2) -> -Integer.parseInt(o1.getName().replace(".", ""))).forEach(changelog -> {
                    font.drawString(changelog.getName(), finalX + width / 2 - font.getStringWidth(changelog.getName()) / 2F, changelogY.get(), -1);
                    changelogY.updateAndGet(v -> v + font.FONT_HEIGHT + 2);
                    Arrays.stream(ChangeType.values()).forEach(changeType -> {
                        if (!changelog.getChanges().get(changeType).isEmpty()) {
                            changelog.getChanges().get(changeType).forEach(change -> {
                                font.listFormattedStringToWidth(change, (int) width).forEach(formatted -> {
                                    font.drawString(formatted, finalX + 5, changelogY.get(), -1);
                                    changelogY.updateAndGet(v -> v + font.FONT_HEIGHT);
                                });

                            });
                            changelogY.updateAndGet(v -> v + font.FONT_HEIGHT / 2);
                        }
                    });
                });
                StencilUtil.uninit();
                //JHLabsShaderRenderer.renderShadowOnObject((int) finalX, (int) finalY, (int) width, (int) height, 12, Color.black, () -> RenderUtil.drawRoundedRect(finalX, finalY, width, height, 20, Color.white));
                scrollHandler.maxScroll = (changelogY.get() - scrollHandler.getScroll()) - mc.displayHeight + changelogY.get();
            } else {
                font.drawString(display, x, y, -1);
                hoveringChangelog = isHovered((double) mouseX, (double) mouseY, (int) x, (int) y, font.getStringWidth(display), font.FONT_HEIGHT);
            }
        } else {
            font.drawString("Failed to load changelog", x, y, -1);
        }


        this.barHeight = 30;
        this.barWidth = hoveringBar ? 200 : 30;

        this.calcWidth = (int) (barAnimation.getValue() * 2f / 6f);

        boldFont.drawString(loginStatus, calculateMiddle(loginStatus, boldFont, 0, width), 10, Integer.MAX_VALUE);

        //fontRendererObj.drawString(loginStatus, calculateMiddle(loginStatus, fontRendererObj, 0, width), 10, Integer.MAX_VALUE);

        renderProfileBar(20, 20);

        int profileHeight;
        if (profilesExtended) {
            final AtomicInteger yAdd = new AtomicInteger(20);
            for (Profile features : Client.INSTANCE.getProfileManager().getFeatures())
                yAdd.addAndGet(20);

            profileHeight = yAdd.get();
        } else
            profileHeight = 20;

        extendAnimation.update();
        extendAnimation.animate(profileHeight, .05, Easings.BOUNCE_IN);

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private void renderProfileBar(int x, int y) {

        renderShadow(x, y, profileWidth, (int) (extendAnimation.getValue()), 20, Color.magenta);
        Gui.drawRect(x, y, x + profileWidth, (int) (y + extendAnimation.getValue()), new Color(9, 9, 9).getRGB());

        font.drawString(Client.INSTANCE.getProfileManager().get().getName(), calculateMiddle(Client.INSTANCE.getProfileManager().get().getName(), font, x, profileWidth), y + 3, -1);

        StencilUtil.init();
        Gui.drawRect(x, y, x + profileWidth, (int) (y + extendAnimation.getValue()), new Color(9, 9, 9).getRGB());

        StencilUtil.readBuffer(1);
        final AtomicInteger yAdd = new AtomicInteger(y + 22);
        for (Profile features : Client.INSTANCE.getProfileManager().getFeatures()) {

            if (features == Client.INSTANCE.getProfileManager().get())
                Gui.drawRect(x, yAdd.get(), x + profileWidth, yAdd.get() + 17, new Color(66, 126, 94).getRGB());

            font.drawString(features.getName(), calculateMiddle(features.getName(), font, x, profileWidth), yAdd.get() + 1, -1);

            yAdd.addAndGet(20);
        }

        StencilUtil.uninit();
    }

    private void renderBar(int mouseX, int mouseY) {
        if (boldFont == null) {
            boldFont = new FontRenderer(new Font("arial", Font.BOLD, 30));
            font = new FontRenderer(new Font("arial", Font.PLAIN, 21));
            dragPointFont = new FontRenderer(new Font("arial", Font.PLAIN, 30));
        }
        RenderUtil.roundedRectShader.drawRound(barX, barY, barAnimation.getValue() * 2, barHeight * 2, roundnessAnimation.getValue(), new Color(9, 9, 9));

        if (!hoveringBar && barAnimation.getValue() <= 32) {
            renderClock(barX - 5, barY + barHeight - 1);

            boldFont.drawString("Hover over clock!", calculateMiddle("Hover over clock!", boldFont, barX, barAnimation.getValue() * 2), barY + barHeight + 30, new Color(50, 50, 50, 160).getRGB());

        } else {
            renderButtons(barX + 5, barY + 14, mouseX, mouseY);
        }
    }

    private void updateBarConditions(final int mouseX, final int mouseY) {
        this.barY = height / 2 - barHeight;
        this.barX = (int) (width / 2 - barAnimation.getValue());
        this.hoveringBar = isHovered(mouseX, mouseY, barX, barY, barWidth * 2, barHeight * 2);
        this.barAnimation.update();
        this.barAnimation.animate(barWidth, .02, Easings.BOUNCE_IN);

        this.roundnessAnimation.update();
        this.roundnessAnimation.animate(!hoveringBar ? 25 : 9, .2, Easings.BACK_IN4);

        this.alphaAnimationClock.update();
        this.alphaAnimationClock.animate(!hoveringBar ? 255 : 0, .1, Easings.NONE);

        this.profileWidth = 100;

    }

    private void renderLineToClockCoords(int x, int y, double percent, int color) {
        final double converted = ClockMath.convertPercentToRadians(percent);

        GlStateManager.pushMatrix();
        glLineWidth(1.0f);
        glTranslated(x, y, 0);
        boolean blend = GL11.glIsEnabled(GL11.GL_BLEND);
        boolean texture2D = GL11.glIsEnabled(GL11.GL_TEXTURE_2D);
        boolean lineSmooth = GL11.glIsEnabled(GL11.GL_LINE_SMOOTH);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        GL11.glLineWidth(1.5F);
        GL11.glBegin(1);

        GlStateManager.color((float) (color >> 16 & 255) / 255.0F, (float) (color >> 8 & 255) / 255.0F, (float) (color & 255) / 255.0F, (float) (color >> 24 & 255) / 255.0F);
        glVertex2d(0, 0);
        glVertex2d(-(double) 22 * Math.sin(converted), (double) 22 * Math.cos(converted));
        GL11.glEnd();
        if (!lineSmooth)
            GL11.glDisable(GL11.GL_LINE_SMOOTH);


        if (texture2D)
            GL11.glEnable(GL11.GL_TEXTURE_2D);


        if (!blend)
            GL11.glDisable(GL11.GL_BLEND);


        GL11.glPopMatrix();
    }

    private void renderClock(int x, int y) {
        final LocalDateTime now = LocalDateTime.now();
        final double seconds = now.getSecond() + System.currentTimeMillis() % 1000 / 1000.0;
        final double minutes = now.getMinute() + seconds / 60.0;
        final double hours = now.getHour() + minutes / 60.0;
        final int secondLength = 40;

        renderLineToClockCoords(x + barWidth - secondLength / 2 - barWidth / 2 + secondLength, y, seconds / 60.0 * 100.0, Color.RED.getRGB());

        renderLineToClockCoords(x + barWidth - secondLength / 2 - barWidth / 2 + secondLength, y, minutes / 60.0 * 100.0, Color.white.getRGB());
        renderLineToClockCoords(x + barWidth - secondLength / 2 - barWidth / 2 + secondLength, y, (hours % 12.0) / 12.0 * 100.0, Color.white.getRGB());

    }

    private Background second;

    private final Animation animation = new Animation();
    private boolean showTooltip;
    private final TimeHelper timeHelper = new TimeHelper();

    @BCompiler(aot = BCompiler.AOT.AGGRESSIVE)
    private void renderBackground(int mouseX, int mouseY) {
        final ScaledResolution resolution = new ScaledResolution(mc);

        if (dragBackground) {
            dragX = mouseX + draggedX;
            swipeSpeed = Math.abs(dragX - prevDragX);
            prevDragX = dragX;
            if (dragX > 0) {
                second = Client.INSTANCE.getBackgroundManager().previusBackground();
            } else {
                second = Client.INSTANCE.getBackgroundManager().nextBackground();
            }

            second.render();

        }

        drawRect(dragX, 0, dragX + width, height, Color.black.getRGB());

        StencilUtil.init();
        drawRect(dragX, 0, dragX + width, height, Color.black.getRGB());
        StencilUtil.readBuffer(1);
        if (dragBackground)
            Client.INSTANCE.getBackgroundManager().current.draggedX = draggedX + mouseX;
        Client.INSTANCE.getBackgroundManager().current.render();
        StencilUtil.uninit();
        //RenderUtil.backgroundShader.render();

        if (dragPointFont != null) {
            final BackgroundManager manager = Client.INSTANCE.getBackgroundManager();
            int size = Client.INSTANCE.getBackgroundManager().getFeatures().size();
            if (size > 1) {
                int x = resolution.getScaledWidth() / 2 - dragPointFont.getStringWidth(".") * size / 2;
                final int width = dragPointFont.getStringWidth(".") * size + 10;
                final int finalX = x;

                RenderableShaders.render();
                ShaderAccess.blurShaderRunnables.clear();
                ShaderAccess.blurShaderRunnables.add(() -> {
                    RenderUtil.drawRoundedRect(finalX - 5, resolution.getScaledHeight() - dragPointFont.FONT_HEIGHT * 2 + 5, width, dragPointFont.FONT_HEIGHT, 5, Color.GRAY);
                });

                /*StencilUtil.init();
                RenderUtil.drawRoundedRect(x - 5, resolution.getScaledHeight() - dragPointFont.FONT_HEIGHT * 2 + 5, width, dragPointFont.FONT_HEIGHT, 5, new Color(0, 0, 0, 0.7F));
                StencilUtil.readBuffer(1);
                blurShader.draw();
                StencilUtil.uninit();*/

                RenderUtil.drawRoundedRect(x - 5, resolution.getScaledHeight() - dragPointFont.FONT_HEIGHT * 2 + 5, dragPointFont.getStringWidth(".") * size + 10, dragPointFont.FONT_HEIGHT, 5, new Color(0, 0, 0, 0.7F));

                animation.update();
                animation.animate(showTooltip ? 1 : 0, 0.05, Easings.BOUNCE_IN);

                if (showTooltip) {
                    if (Math.round(animation.getValue() * 100) != 100)
                        timeHelper.reset();
                    if (timeHelper.hasReached(2500))
                        showTooltip = false;
                } else
                    timeHelper.reset();

                final float value = animation.getValue();
                if (manager.current != null) {
                    GL11.glPushMatrix();
                    GL11.glTranslated(x + dragPointFont.getStringWidth(".") * size / 2F - font.getStringWidth(manager.current.getName()) / 2F, resolution.getScaledHeight() - dragPointFont.FONT_HEIGHT * 2 + 3 - font.FONT_HEIGHT, 1);
                    GL11.glScaled(value, value, 1);
                    font.drawString(manager.current.getName(), 0, 0, Integer.MAX_VALUE, false);
                    GL11.glPopMatrix();
                }

                for (int i = 0; i < size; i++) {
                    final Background background = manager.getFeatures().get(i);
                    dragPointFont.drawString(".", x, resolution.getScaledHeight() - dragPointFont.FONT_HEIGHT * 2, background == manager.current ? Color.white.getRGB() : Color.darkGray.getRGB());
                    if (Mouse.isButtonDown(0)) {
                        if (mouseX >= x && mouseX <= x + dragPointFont.getStringWidth(".") && mouseY >= resolution.getScaledHeight() - dragPointFont.FONT_HEIGHT * 2 && mouseY <= resolution.getScaledHeight() - dragPointFont.FONT_HEIGHT) {
                            final Background lastBackground = manager.current;
                            manager.current = background;
                            manager.current.draggedX = 0;
                            if (lastBackground != manager.current) {
                                showTooltip = true;
                                animation.setValue(0);
                            }
                            dragX = 0;
                            prevDragX = 0;
                            draggedX = 0;
                            dragBackground = false;
                        }
                    }
                    x += dragPointFont.getStringWidth(".");
                }
            }
        }
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        this.dragBackground = false;
        final BackgroundManager manager = Client.INSTANCE.getBackgroundManager();
        final Background lastBackground = manager.current;
        final ScaledResolution resolution = new ScaledResolution(mc);
        if (swipeSpeed > 3 || Math.abs(dragX) > resolution.getScaledWidth() / 6)
            if (dragX < 0)
                manager.current = manager.nextBackground();
            else if (dragX > 0)
                manager.current = manager.previusBackground();
        dragX = 0;
        manager.current.draggedX = 0;
        if (lastBackground != manager.current) {
            animation.setValue(0);
            showTooltip = true;
        }

        super.mouseReleased(mouseX, mouseY, state);
    }

    public boolean isHovered(double mouseX, double mouseY, int x, int y, int width, int height) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }

    public int calculateMiddle(String text, de.lirium.util.render.FontRenderer fontRenderer, double x, double width) {
        return (int) ((float) (x + width) - (fontRenderer.getStringWidth(text) / 2f) - (float) width / 2);
    }

    public int calculateMiddle(String text, net.minecraft.client.gui.FontRenderer fontRenderer, double x, double width) {
        return (int) ((float) (x + width) - (fontRenderer.getStringWidth(text) / 2f) - (float) width / 2);
    }

    @Override
    public void handleMouseInput() throws IOException {
        if (font == null) return;

        final AtomicReference<Float> changelogY = new AtomicReference<>(0F);
        Client.INSTANCE.getChangelogManager().getFeatures().stream().sorted((o1, o2) -> -Integer.parseInt(o1.getName().replace(".", ""))).forEach(changelog -> {
            changelogY.updateAndGet(v -> v + font.FONT_HEIGHT + 2);

            Arrays.stream(ChangeType.values()).forEach(changeType -> {
                if (!changelog.getChanges().get(changeType).isEmpty()) {
                    changelogY.updateAndGet(v -> v + font.FONT_HEIGHT / 2);
                    changelog.getChanges().get(changeType).forEach(change -> {
                        changelogY.updateAndGet(v -> v + font.FONT_HEIGHT);
                    });
                }
            });
        });
        float y = 5;
        final float height = this.height - y - 5;
        changelogY.updateAndGet(v -> v - height + font.FONT_HEIGHT);


        super.handleMouseInput();
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        boolean hovered = false;
        if (isHovered(mouseX, mouseY, 20, 20, profileWidth, 20)) {
            hovered = true;
            if (mouseButton == 1 || mouseButton == 0) {
                mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 0.2F));
                this.profilesExtended = !profilesExtended;
            }
        }
        int y = 20;
        int x = 20;
        final AtomicInteger yAdd = new AtomicInteger(y + 22);
        for (Profile features : Client.INSTANCE.getProfileManager().getFeatures()) {

            if (isHovered(mouseX, mouseY, x, yAdd.get(), profileWidth, 16)) {
                mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                hovered = true;
                if (mouseButton == 0) {
                    Client.INSTANCE.getProfileManager().get().save();
                    Client.INSTANCE.getProfileManager().setCurrent(features);
                    features.read();
                }
            }

            yAdd.addAndGet(20);
        }

        final AtomicInteger xAdditional = new AtomicInteger(barX);
        for (Buttons button : Buttons.values()) {
            final boolean isHover = isHovered(mouseX, mouseY, xAdditional.get(), barY, calcWidth, barHeight * 2);
            if (isHover) {
                hovered = true;
                mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                switch (button) {
                    case QUIT:
                        mc.shutdown();
                        break;
                    case LANGUAGE:
                        mc.displayGuiScreen(new GuiLanguage(this, mc.gameSettings, mc.getLanguageManager()));
                        break;
                    case OPTIONS:
                        mc.displayGuiScreen(new GuiOptions(this, mc.gameSettings));
                        break;
                    case ALTMANAGER: {
                        loginStatus = "waiting...";
                        new Thread("clipboard login") {
                            @Override
                            public void run() {
                                final String clipBoard = getClipboardString().trim();
                                if (clipBoard.startsWith("https://login.live.com/oauth20_desktop.srf")) {
                                    if (ServiceUtil.switchService("Mojang")) {
                                        final String result = URLDecoder.decode(clipBoard);
                                        final String accessToken = result.split("#")[1].split("access_token=")[1].split("&")[0];
                                        final String refreshToken = result.split("#")[1].split("refresh_token=")[1].split("&")[0];
                                        final MicrosoftAuthenticator authenticator = new MicrosoftAuthenticator();
                                        try {
                                            final MicrosoftAuthResult authResult = authenticator.loginWithTokens(new AuthTokens(accessToken, refreshToken));
                                            mc.setSession(new Session(authResult.getProfile().getName(), authResult.getProfile().getId(), authResult.getAccessToken(), "microsoft"));
                                            loginStatus = "Logged in as §e§l" + authResult.getProfile().getName() + " §7- §a§lMicrosoft §7(§eCookie§7)";
                                        } catch (MicrosoftAuthenticationException e) {
                                            loginStatus = "§c§lInvalid Cookie";
                                            e.printStackTrace();
                                        }
                                    }
                                } else if (!clipBoard.contains(":")) {
                                    if (clipBoard.contains("@shadowgen.net")) {
                                        if (ServiceUtil.switchService("ShadowGen")) {
                                            loginStatus = "Logging in...";
                                            final YggdrasilUserAuthentication auth = (YggdrasilUserAuthentication)
                                                    new YggdrasilAuthenticationService(Proxy.NO_PROXY, "").createUserAuthentication(Agent.MINECRAFT);
                                            auth.setUsername(clipBoard);
                                            auth.setPassword(Client.NAME);
                                            try {
                                                auth.logIn();
                                                if (auth.getSelectedProfile() != null) {
                                                    final Session session = new Session(auth.getSelectedProfile().getName(), FastUUID.toString(auth.getSelectedProfile().getId()), auth.getAuthenticatedToken(), "mojang");
                                                    mc.setSession(session);
                                                    loginStatus = "Logged in as §e§l" + session.getUsername() + " §7- §5§lShadow-Gen";
                                                } else {
                                                    loginStatus = "§c§lInvalid Account";
                                                }
                                            } catch (Exception e) {
                                                loginStatus = "§c§lInvalid account";
                                            }
                                        }
                                    } else if (clipBoard.contains("@alt")) {
                                        if (ServiceUtil.switchService("Altening")) {
                                            loginStatus = "Logging in...";
                                            final AlteningSession session = AlteningUtil.login(clipBoard);
                                            mc.session = session.getSession();
                                            loginStatus = "Logged in as §e§l" + session.getName() + " §7- §a§lAltening";
                                        }
                                    } else if (clipBoard.startsWith("api-")) {
                                        if (ServiceUtil.switchService("Altening")) {
                                            loginStatus = "Generate...";
                                            try {
                                                final AlteningSession session = AlteningUtil.login(AlteningUtil.generate(clipBoard).token);
                                                if (session != null) {
                                                    mc.session = session.getSession();
                                                    loginStatus = "Logged in as §e§l" + session.getName() + " §7- §a§lAltening §7(§a§lGenerated§7)";
                                                }
                                            } catch (Exception e) {
                                                System.out.println(AlteningUtil.getLicense(clipBoard));
                                                loginStatus = "§c§lInvalid Token";
                                            }
                                        }
                                    } else if(isCtrlKeyDown()) {
                                        try {
                                            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
                                        } catch (Exception e) {
                                            loginStatus = "§cFailed to change look";
                                            e.printStackTrace();
                                            return;
                                        }
                                        final JFileChooser fileSearcher = new JFileChooser(System.getProperty("user.home") + "/Desktop");
                                        fileSearcher.setDialogTitle("Select account file");
                                        fileSearcher.setFileFilter(new FileNameExtensionFilter("Text files", "txt"));
                                        fileSearcher.showOpenDialog(null);
                                        if(ServiceUtil.switchService("Mojang")) {
                                            if (fileSearcher.getSelectedFile() != null) {
                                                try {
                                                    final Map<String, String> cookies = new HashMap<>();
                                                    final Cookie[] cocks = CookieParser.parseCookieFile(fileSearcher.getSelectedFile().getPath());
                                                    for (final Cookie cookie : cocks) {
                                                        cookies.put(cookie.getName(), cookie.getValue());
                                                    }

                                                    final String url = "https://login.live.com/oauth20_authorize.srf/?client_id=000000004C12AE6F&redirect_uri=https://login.live.com/oauth20_desktop.srf&scope=service::user.auth.xboxlive.com::MBI_SSL&response_type=token";
                                                    final String response = Jsoup.connect(url).cookies(cookies).userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:109.0) Gecko/20100101 Firefox/111.0").followRedirects(true).execute().url().toExternalForm();

                                                    if (response.equals(url)) {
                                                        loginStatus = "§cCookie expired";
                                                        return;
                                                    }

                                                    final String accessToken = response.split("#")[1].split("access_token=")[1].split("&")[0];
                                                    final String refreshToken = response.split("#")[1].split("refresh_token=")[1].split("&")[0];

                                                    try {
                                                        final MicrosoftAuthResult authenticator = new MicrosoftAuthenticator().loginWithTokens(new AuthTokens(accessToken, refreshToken));
                                                        Minecraft.getMinecraft().session = new Session(authenticator.getProfile().getName(), authenticator.getProfile().getId(), authenticator.getAccessToken(), "microsoft");
                                                        loginStatus = "§fLogged into §e" + Minecraft.getMinecraft().getSession().getUsername() + " §7(§eCookie§7)";
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                        loginStatus = "§cAccount not working";
                                                    }

                                                } catch (CookieParseException e) {
                                                    loginStatus = "§cCookie not working";
                                                } catch (IOException e) {
                                                    throw new RuntimeException(e);
                                                }
                                            } else {
                                                loginStatus = "waiting...";
                                            }
                                        }
                                    } else if (!isShiftKeyDown()) {
                                        if (ServiceUtil.switchService("Mojang")) {
                                            if (clipBoard.length() <= 16 && clipBoard.length() >= 3 && clipBoard.matches("[a-zA-Z0-9_]*")) {
                                                mc.setSession(new Session(clipBoard, "", "", "LEGACY"));
                                                loginStatus = "Logged in as §e§l" + clipBoard + " §7- §c§lCracked";
                                            } else {
                                                loginStatus = "§c§lInvalid username";
                                            }
                                        }
                                    } else {
                                        if (ServiceUtil.switchService("EasyMC")) {
                                            loginStatus = "Logging in...";
                                            try {
                                                final JsonObject jsonObject = JsonUtil.getEasyMCContent("https://api.easymc.io/v1/token/redeem", clipBoard);
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
                                            } catch (IOException e) {
                                                loginStatus = "§cLogin failed";
                                            }
                                        }
                                    }
                                    return;
                                } else {
                                    if (ServiceUtil.switchService("Mojang")) {
                                        final String[] loginCredentials = clipBoard.split(":");
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
                                super.run();
                            }
                        }.start();
                    }
                    break;
                    case MULTIPLAYER:
                        mc.displayGuiScreen(new GuiMultiplayer(this));
                        break;
                    case SINGLEPLAYER:
                        mc.displayGuiScreen(new GuiWorldSelection(this));
                        break;
                }
            }
            xAdditional.addAndGet(calcWidth);

            if (!hovered) {
                if (mouseButton == 0 && Client.INSTANCE.getBackgroundManager().getFeatures().size() > 1) {
                    dragBackground = true;
                    draggedX = dragX - mouseX;
                }
            }
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    private void renderButtons(final int x, final int y, int mouseX, int mouseY) {
        final AtomicInteger xAdditional = new AtomicInteger(x);
        for (Buttons button : Buttons.values()) {
            final boolean hover = isHovered(mouseX, mouseY, xAdditional.get(), y, calcWidth - 9, barHeight);
            RenderUtil.roundedRectShader.drawRound(xAdditional.get(), y, calcWidth - 9, barHeight, 12, hover ? new Color(20, 20, 20) : new Color(12, 12, 12));
            StencilUtil.init();
            RenderUtil.roundedRectShader.drawRound(xAdditional.get(), y, calcWidth - 9, barHeight, 12, new Color(12, 12, 12));
            StencilUtil.readBuffer(1);

            font.drawString(button.clearName, calculateMiddle(button.clearName, font, xAdditional.get(), calcWidth - 9), y + 8, -1);

            StencilUtil.uninit();
            xAdditional.addAndGet(calcWidth);
        }
    }

    enum Buttons {
        SINGLEPLAYER("Singleplayer"), MULTIPLAYER("Multiplayer"), ALTMANAGER("Clipboard"), LANGUAGE("Language"), OPTIONS("Options"), QUIT("Quit");

        final String clearName;

        Buttons(String clearName) {
            this.clearName = clearName;
        }

    }

}
