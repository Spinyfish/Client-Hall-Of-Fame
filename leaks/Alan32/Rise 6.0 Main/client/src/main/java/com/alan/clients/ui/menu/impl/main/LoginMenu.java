package com.alan.clients.ui.menu.impl.main;

import com.alan.clients.Client;
import com.alan.clients.newevent.Listener;
import com.alan.clients.newevent.annotations.EventLink;
import com.alan.clients.newevent.impl.other.BackendPacketEvent;
import com.alan.clients.ui.menu.Menu;
import com.alan.clients.ui.menu.component.button.MenuButton;
import com.alan.clients.ui.menu.component.button.impl.MenuTextButton;
import com.alan.clients.util.MouseUtil;
import com.alan.clients.util.animation.Animation;
import com.alan.clients.util.animation.Easing;
import com.alan.clients.util.font.Font;
import com.alan.clients.util.font.FontManager;
import com.alan.clients.util.gui.textbox.TextAlign;
import com.alan.clients.util.gui.textbox.TextBox;
import com.alan.clients.util.interfaces.InstanceAccess;
import com.alan.clients.util.render.ColorUtil;
import com.alan.clients.util.render.RenderUtil;
import com.alan.clients.util.shader.RiseShaders;
import com.alan.clients.util.shader.base.ShaderRenderType;
import com.alan.clients.util.vantage.HWIDUtil;
import com.alan.clients.util.vector.Vector2d;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;
import packet.Packet;
import packet.impl.server.login.ServerLoginPacket;
import util.time.StopWatch;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.IOException;

public final class LoginMenu extends Menu {

    private static final ResourceLocation SETTINGS_ICON = new ResourceLocation("rise/icons/main_menu/SettingsIcon.png");
    private static final ResourceLocation LANGUAGES_ICON = new ResourceLocation("rise/icons/main_menu/LanguagesIcon.png");

    // "Logo" animation
    private final Font fontRenderer = FontManager.getProductSansRegular(64);
    private Animation animation = new Animation(Easing.EASE_OUT_QUINT, 600);
    private final Animation fadeAnimation = new Animation(Easing.EASE_IN_OUT_CUBIC, 3000);

    private MenuTextButton emailButton;
    private MenuTextButton loginButton;
    private TextBox emailBox;

    private MenuButton[] menuButtons;

    private StopWatch lastLoginAttempt = new StopWatch();

    public LoginMenu() {
        Client.INSTANCE.getEventBus().register(this);
        lastLoginAttempt.setMillis(0);
    }

    public void onGuiClosed() {
        Client.INSTANCE.getEventBus().unregister(this);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        // Renders the background
        RiseShaders.MAIN_MENU_SHADER.run(ShaderRenderType.OVERLAY, partialTicks, null);

        ScaledResolution scaledResolution = new ScaledResolution(mc);

        NORMAL_BLUR_RUNNABLES.add(() -> RenderUtil.rectangle(0, 0, scaledResolution.getScaledWidth(), scaledResolution.getScaledHeight(), Color.BLACK));

        // Run blur
        RiseShaders.GAUSSIAN_BLUR_SHADER.update();
        RiseShaders.GAUSSIAN_BLUR_SHADER.run(ShaderRenderType.OVERLAY, partialTicks, InstanceAccess.NORMAL_BLUR_RUNNABLES);

        // Run bloom
        RiseShaders.POST_BLOOM_SHADER.update();
        RiseShaders.POST_BLOOM_SHADER.run(ShaderRenderType.OVERLAY, partialTicks, InstanceAccess.NORMAL_POST_BLOOM_RUNNABLES);

        // FPS counter on dev builds
        if (Client.DEVELOPMENT_SWITCH) mc.fontRendererObj.drawStringWithShadow(Minecraft.getDebugFPS() + "", 0, 0, -1);

        // Run post shader things
        InstanceAccess.clearRunnables();

        // Renders the buttons
        this.emailButton.draw(mouseX, mouseY, partialTicks);
        this.loginButton.draw(mouseX, mouseY, partialTicks);

        this.emailBox.draw();

        // Update the animation
        final double destination = this.emailButton.getY() - this.fontRenderer.height();
        this.animation.run(destination);

        // Render the rise "logo"
        final double value = this.animation.getValue();
        final Color color = ColorUtil.withAlpha(Color.WHITE, (int) (value / destination * 200));
        this.fontRenderer.drawCenteredString("Welcome", width / 2.0F, value - 10, color.getRGB());

        // Draws failed login info
        String errorMessage = Client.INSTANCE.getNetworkManager().message;
        if (errorMessage != null && !errorMessage.equals("")) {
            if (errorMessage.contains("INVALID_HWID")) {
                StringSelection selection = new StringSelection(HWIDUtil.getHWID());
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                clipboard.setContents(selection, selection);

                Client.INSTANCE.getNetworkManager().message = "Your HWID is invalid, please update your HWID on VantageClients.com, it has been copied to your clipboard";
            } else if (errorMessage.contains("INVALID_USER")) {
                Client.INSTANCE.getNetworkManager().message = "That user does not exist!";
            }

            FontManager.getProductSansLight(18).drawCenteredString(errorMessage, width / 2.0F, value + 26, Color.RED.getRGB());
        }

        // Draw bottom right text
        Font watermarkLarge = FontManager.getProductSansRegular(24);
        String watermarkMainText = Client.NAME + " " + Client.VERSION_FULL + " (" + Client.VERSION_DATE + ")";

        watermarkLarge.drawRightString(watermarkMainText, scaledResolution.getScaledWidth() - 5,
                scaledResolution.getScaledHeight() - watermarkLarge.height() - 2,
                ColorUtil.withAlpha(TEXT_SUBTEXT, 128).getRGB());

        FontManager.getProductSansRegular(16).drawRightString("Designed and built by Alan and Syphillis",
                scaledResolution.getScaledWidth() - 5, scaledResolution.getScaledHeight() - 40,
                ColorUtil.withAlpha(TEXT_SUBTEXT, 100).getRGB());

        FontManager.getProductSansRegular(12).drawRightString("© Rise Client 2022. All Rights Reserved",
                scaledResolution.getScaledWidth() - 5, scaledResolution.getScaledHeight() - 30,
                ColorUtil.withAlpha(TEXT_SUBTEXT, 100).getRGB());

        UI_BLOOM_RUNNABLES.forEach(Runnable::run);
        UI_BLOOM_RUNNABLES.clear();

        this.fadeAnimation.run(0);
        RenderUtil.rectangle(0, 0, mc.displayWidth, mc.displayHeight, new Color(0, 0, 0, (int) fadeAnimation.getValue()));
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {

        if (this.menuButtons == null) return;

        // If doing a left click and the mouse is hovered over a button, execute the buttons action (runnable)
        if (mouseButton == 0) {
            for (MenuButton menuButton : this.menuButtons) {
                if (MouseUtil.isHovered(menuButton.getX(), menuButton.getY(), menuButton.getWidth(), menuButton.getHeight(), mouseX, mouseY)) {
                    menuButton.runAction();
                    break;
                }
            }

            this.emailBox.click(mouseX, mouseY, mouseButton);
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        this.emailBox.key(typedChar, keyCode);

        // Tab to go between text boxes
        if (keyCode == Keyboard.KEY_TAB) {
            this.emailBox.setSelected(!this.emailBox.isSelected());
        }

        // Enter to log in
        else if (keyCode == Keyboard.KEY_RETURN && !this.emailBox.getText().isEmpty()) {
            this.loginButton.runAction();
        }
    }

    public void initNetworkManager(String username) {
        if (!lastLoginAttempt.finished(4000)) return;

        lastLoginAttempt.reset();
        Client.INSTANCE.getNetworkManager().init(username);
    }

    @Override
    public void initGui() {
        int centerX = this.width / 2;
        int centerY = this.height / 2;
        int buttonWidth = 180;
        int buttonHeight = 24;
        int buttonSpacing = 6;
        int iconSize = 24;
        int buttonX = centerX - buttonWidth / 2;
        int buttonY = centerY - buttonHeight / 2 - buttonSpacing / 2 - buttonHeight / 2;

        // Re-creates the buttons for not having to care about the animation reset
        this.emailButton = new MenuTextButton(buttonX, buttonY, buttonWidth, buttonHeight, () -> {
        }, "");

        this.loginButton = new MenuTextButton(buttonX, buttonY + buttonHeight + buttonSpacing, buttonWidth, buttonHeight, () -> {
            this.initNetworkManager(this.emailBox.getText());
        }, "Login");

        this.emailBox = new TextBox(new Vector2d(centerX, buttonY + 9), FontManager.getProductSansBold(24), Color.WHITE, TextAlign.CENTER, "Username", buttonWidth * 5);
        // Re-create the logo animation for not having to care about its reset
        this.animation = new Animation(Easing.EASE_OUT_QUINT, 600);

        // Putting all buttons in an array for handling mouse clicks
        this.menuButtons = new MenuButton[]{this.emailButton, this.loginButton};

        this.fadeAnimation.setValue(255);
        this.fadeAnimation.reset();
    }

    @EventLink()
    public final Listener<BackendPacketEvent> onBackend = event -> {
        Packet<?> packet = event.getPacket();

        if (packet instanceof ServerLoginPacket) {
            ServerLoginPacket sPacketLoginResponse = ((ServerLoginPacket) packet);

            if (sPacketLoginResponse.isSuccess()) {
                mc.displayGuiScreen(new MainMenu());
            }
        }
    };
}
