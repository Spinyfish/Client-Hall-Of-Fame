package com.alan.clients.ui.menu.impl.alt;

import com.alan.clients.Client;
import com.alan.clients.ui.menu.Menu;
import com.alan.clients.ui.menu.component.button.MenuButton;
import com.alan.clients.ui.menu.component.button.impl.MenuTextButton;
import com.alan.clients.ui.menu.impl.alt.impl.AltDisplay;
import com.alan.clients.util.MouseUtil;
import com.alan.clients.util.account.Account;
import com.alan.clients.util.account.microsoft.MicrosoftLogin;
import com.alan.clients.util.animation.Animation;
import com.alan.clients.util.animation.Easing;
import com.alan.clients.util.gui.ScrollUtil;
import com.alan.clients.util.interfaces.InstanceAccess;
import com.alan.clients.util.render.RenderUtil;
import com.alan.clients.util.render.ScissorUtil;
import com.alan.clients.util.shader.RiseShaders;
import com.alan.clients.util.shader.base.ShaderRenderType;
import util.time.StopWatch;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.Session;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class AltManagerMenu extends Menu {

    private static final double ACCOUNT_WIDTH = 180;
    private static final double ACCOUNT_HEIGHT = 32;
    private static final int ACCOUNT_SPACING = 6;

    private static final int BOX_SPACING = 10;
    private static final int BUTTON_WIDTH = 180;
    private static final int BUTTON_HEIGHT = 24;
    private static final int BUTTON_SPACING = 6;

    public final List<AltDisplay> altDisplays = new ArrayList<>();
    private final ScrollUtil scrollUtil = new ScrollUtil();

    private MenuTextButton loginThroughBrowserButton;
    private MenuButton[] menuButtons;

    private Animation animation = new Animation(Easing.EASE_OUT_QUINT, 500);

    private StopWatch click = new StopWatch();
    private String loginText = "Login through browser";

    @Override
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
        // Renders the background
        RiseShaders.MAIN_MENU_SHADER.run(ShaderRenderType.OVERLAY, partialTicks, null);

        // Update animations
        animation.run(this.height);

        // Handles scrolling
        int visibleHeight = this.height - 20 - BUTTON_HEIGHT - BUTTON_SPACING * 2;
        int listHeight = (int) (Math.ceil(altDisplays.size() / 2f) * (32 + ACCOUNT_SPACING));
        scrollUtil.setMax(-listHeight + visibleHeight - (BOX_SPACING - ACCOUNT_SPACING));
        scrollUtil.onRender();

        // Renders the buttons
        this.loginThroughBrowserButton.draw(mouseX, mouseY, partialTicks);

        // Blur and bloom box
        double boxX = this.width / 2 - ACCOUNT_WIDTH - ACCOUNT_SPACING / 2 - BOX_SPACING;
        double boxY = (altDisplays.isEmpty() ? 20 : altDisplays.get(0).getY()) + this.height - animation.getValue() - BOX_SPACING + scrollUtil.getScroll();
        double boxWidth = ACCOUNT_WIDTH * 2 + ACCOUNT_SPACING + BOX_SPACING * 2;
        double boxHeight = (altDisplays.isEmpty() ? 0 /* add an alt*/ : (ACCOUNT_HEIGHT + ACCOUNT_SPACING) * altDisplays.size() - ACCOUNT_SPACING + BOX_SPACING * 2);
        double finalBoxHeight = Math.min(this.height - BUTTON_HEIGHT - BUTTON_SPACING * 2 - 10 - this.scrollUtil.scroll, boxHeight);

        // Rendering the background box
        NORMAL_BLUR_RUNNABLES.add(() -> RenderUtil.roundedRectangle(boxX, boxY, boxWidth, finalBoxHeight, 10, Color.WHITE));
        NORMAL_POST_BLOOM_RUNNABLES.add(() -> RenderUtil.roundedRectangle(boxX, boxY, boxWidth, finalBoxHeight, 10, new Color(0, 0, 0, 100)));

        double offsetY = this.height - this.animation.getValue() + scrollUtil.getScroll();
        for (AltDisplay altDisplay : altDisplays) {
            altDisplay.draw(offsetY, mouseX, mouseY);
        }

        ScaledResolution scaledResolution = new ScaledResolution(mc);

        NORMAL_BLUR_RUNNABLES.add(() -> RenderUtil.rectangle(0, 0, scaledResolution.getScaledWidth(), scaledResolution.getScaledHeight(), Color.BLACK));

        // Run blur
        RiseShaders.GAUSSIAN_BLUR_SHADER.update();
        RiseShaders.GAUSSIAN_BLUR_SHADER.run(ShaderRenderType.OVERLAY, mc.timer.renderPartialTicks, InstanceAccess.NORMAL_BLUR_RUNNABLES);

        // Run bloom
        RiseShaders.POST_BLOOM_SHADER.update();
        RiseShaders.POST_BLOOM_SHADER.run(ShaderRenderType.OVERLAY, partialTicks, InstanceAccess.NORMAL_POST_BLOOM_RUNNABLES);

        // Run post shader things
        NORMAL_BLUR_RUNNABLES.clear();
        NORMAL_POST_BLOOM_RUNNABLES.clear();

        // Remove the area where no accounts should be displayed
        GL11.glPushMatrix();
        ScissorUtil.enable();
        NORMAL_RENDER_RUNNABLES.forEach(Runnable::run);
        NORMAL_RENDER_RUNNABLES.clear();
        ScissorUtil.scissor(new ScaledResolution(mc), 0, 0, this.width, this.height - BUTTON_HEIGHT - BUTTON_SPACING - ACCOUNT_SPACING);
        ScissorUtil.disable();
        GL11.glPopMatrix();

        NORMAL_UI_BLOOM_RUNNABLES.forEach(Runnable::run);
        NORMAL_UI_BLOOM_RUNNABLES.clear();

        if (this.click.finished(2000)) {
            this.loginThroughBrowserButton.name = loginText;
        }

        // TODO: Don't forget to NOT render the displays out of the screen to save performance
    }

    @Override
    protected void mouseClicked(final int mouseX, final int mouseY, final int mouseButton) throws IOException {
        if (this.menuButtons == null) return;

        // If doing a left click and the mouse is hovered over a button, execute the buttons action (runnable)
        if (mouseButton == 0) {
            for (MenuButton menuButton : this.menuButtons) {
                if (MouseUtil.isHovered(menuButton.getX(), menuButton.getY(), menuButton.getWidth(), menuButton.getHeight(), mouseX, mouseY)) {
                    menuButton.runAction();
                    break;
                }
            }

            boolean resetAltDisplays = false;

            for (int i = 0; i < altDisplays.size(); i++) {
                AltDisplay altDisplay = altDisplays.get(i);
                if (MouseUtil.isHovered(altDisplay.getX(), altDisplay.getY() + scrollUtil.getScroll(), altDisplay.getWidth(), altDisplay.getHeight(), mouseX, mouseY)) {

                    double deleteX = altDisplay.getX() + altDisplay.getWidth() - 16;
                    double deleteY = altDisplay.getY() + 4;
                    if (MouseUtil.isHovered(deleteX, deleteY + scrollUtil.scroll, 12, 12, mouseX, mouseY)) {
                        System.out.println("clicked");
                        Account account = altDisplay.getAccount();
                        Client.INSTANCE.getAltManager().getAccounts().remove(account);
                        this.altDisplays.remove(altDisplay);
                        resetAltDisplays = true;
                        break;
                    }

                    Account account = altDisplay.getAccount();
                    String refreshToken = account.getRefreshToken();
                    if (refreshToken != null) {
                        new Thread(() -> {
                            MicrosoftLogin.LoginData loginData = loginWithRefreshToken(refreshToken);
                            account.setUsername(loginData.username);
                            account.setRefreshToken(loginData.newRefreshToken);
                            for (AltDisplay d : altDisplays) if (d.isSelected()) d.setSelected(false);
                            altDisplay.setSelected(true);

                        }).start();
                    }
                    break;
                }
            }

            if (resetAltDisplays) {
                Client.INSTANCE.getAltManager().set("alts");
                this.loadAltDisplays();
            }
        }
    }

    @Override
    public void initGui() {
        int centerX = this.width / 2;
        int buttonX = centerX - BUTTON_WIDTH / 2;
        int buttonY = this.height - BUTTON_HEIGHT - BUTTON_SPACING;
        scrollUtil.reset();

        // Re-creates the buttons for not having to care about the animation reset
        this.loginThroughBrowserButton = new MenuTextButton(buttonX, buttonY, BUTTON_WIDTH, BUTTON_HEIGHT, () -> {
            final String clipboardContent = getClipboardString();
            mc.session = new Session(clipboardContent, "", "", "mojang");
            MicrosoftLogin.getRefreshToken(refreshToken -> {
                if (refreshToken != null) {
                    new Thread(() -> {
                        MicrosoftLogin.LoginData loginData = loginWithRefreshToken(refreshToken);
                        Account account = new Account(loginData.username, "************");
                        account.setUsername(loginData.username);
                        account.setRefreshToken(loginData.newRefreshToken); // TODO: THIS IS IMPORTANT
                        Client.INSTANCE.getAltManager().getAccounts().add(account);

                        AltDisplay display;
                        if (!altDisplays.isEmpty()) {
                            AltDisplay prevDisplay = altDisplays.get(altDisplays.size() - 1);
                            boolean newRow = altDisplays.size() % 2 == 0;
                            display = new AltDisplay(centerX + (newRow ? -ACCOUNT_WIDTH - ACCOUNT_SPACING / 2.0 : ACCOUNT_SPACING / 2.0), (newRow ? prevDisplay.getY() + prevDisplay.getHeight() + ACCOUNT_SPACING : prevDisplay.getY()), ACCOUNT_WIDTH, ACCOUNT_HEIGHT, account);
                        } else {
                            display = new AltDisplay(centerX - ACCOUNT_SPACING / 2.0 - ACCOUNT_WIDTH, 20, ACCOUNT_WIDTH, ACCOUNT_HEIGHT, account);
                        }

                        display.setSelected(true);
                        this.unselectDisplays();
                        this.altDisplays.add(display);
                        Client.INSTANCE.getAltManager().set("alts");
                    }).start();
                }
            });
        }, loginText);

        // Re-create the logo animation for not having to care about its reset
        this.animation = new Animation(Easing.EASE_OUT_QUINT, 500);

        // Putting all buttons in an array for handling mouse clicks
        this.menuButtons = new MenuButton[]{this.loginThroughBrowserButton};

        // TODO: Load saved account information from file
        // TODO: Maybe run skin-gathering on external thread

        this.loadAltDisplays();
    }

    private MicrosoftLogin.LoginData loginWithRefreshToken(String refreshToken) {
        final MicrosoftLogin.LoginData loginData = MicrosoftLogin.login(refreshToken);
        mc.session = new Session(loginData.username, loginData.uuid, loginData.mcToken, "microsoft");
        return loginData;
    }

    private void loadAltDisplays() {
        double centerX = width / 2.0;
        double accountY = 20;
        this.altDisplays.clear();
        List<Account> accounts = Client.INSTANCE.getAltManager().getAccounts();
        for (int i = 0; i < accounts.size(); i++) {
            Account account = accounts.get(i);
            AltDisplay display = new AltDisplay(centerX + (i % 2 == 0 ? -ACCOUNT_WIDTH - ACCOUNT_SPACING / 2.0 : ACCOUNT_SPACING / 2.0), accountY, ACCOUNT_WIDTH, ACCOUNT_HEIGHT, account);
            if (mc.session != null && account != null && account.getUsername() != null && mc.session.getUsername() != null &&
                    account.getUsername().equals(mc.session.getUsername())) display.setSelected(true);
            altDisplays.add(display);
            if (i % 2 == 1) accountY += ACCOUNT_HEIGHT + ACCOUNT_SPACING;
        }
    }

    private void unselectDisplays() {
        for (AltDisplay d : altDisplays) if (d.isSelected()) d.setSelected(false);
    }

    public AltManagerMenu() {
        Client.INSTANCE.getAltManager().get("alts").read();
    }
}
