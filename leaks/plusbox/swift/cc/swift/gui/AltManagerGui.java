package cc.swift.gui;

import cc.swift.util.ChatUtil;
import cc.swift.util.CookieLoginUtil;
import com.github.javafaker.Faker;
import com.sun.java.swing.plaf.windows.WindowsLookAndFeel;
import fr.litarvan.openauth.microsoft.MicrosoftAuthResult;
import fr.litarvan.openauth.microsoft.MicrosoftAuthenticator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.Session;
import org.lwjgl.input.Keyboard;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.IOException;

public class AltManagerGui extends GuiScreen {

    private GuiTextField usernameField;
    private GuiTextField passwordField;
    private String status;

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        drawCenteredString(mc.fontRendererObj, status, width / 2, height / 2 - 50, Color.WHITE.getRGB());
        usernameField.drawTextBox();
        passwordField.drawTextBox();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public void initGui() {

        this.buttonList.add(new GuiButton(0, width / 2 - 100, height / 2 + 24, "Login"));
        this.buttonList.add(new GuiButton(2, width / 2 - 100, height / 2 + 24 * 2, "Clipboard"));
        this.buttonList.add(new GuiButton(3, width / 2 - 100, height / 2 + 24 * 3, "Fake Cracked Name"));
        this.buttonList.add(new GuiButton(4, width / 2 - 100, height / 2 + 24 * 4, "Use Cookie"));
        this.buttonList.add(new GuiButton(5, width / 2 - 100, height / 2 + 24 * 5, "Back"));
        this.usernameField = new GuiTextField(6, mc.fontRendererObj, width / 2 - 100, height / 2 - 28, 200, 20);
        this.passwordField = new GuiPasswordField(7, mc.fontRendererObj, width / 2 - 100, height / 2 - 2, 200, 20);
        super.initGui();
        usernameField.setFocused(true);
        Keyboard.enableRepeatEvents(true);
    }

    @Override
    protected void mouseClicked(int x2, int y2, int button) {
        try {
            super.mouseClicked(x2, y2, button);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.usernameField.mouseClicked(x2, y2, button);
        this.passwordField.mouseClicked(x2, y2, button);
    }

    @Override
    protected void keyTyped(char character, int key) {
        try {
            super.keyTyped(character, key);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (character == '\t') {
            if (!this.usernameField.isFocused() && !this.passwordField.isFocused()) {
                this.usernameField.setFocused(true);
            } else {
                this.usernameField.setFocused(this.passwordField.isFocused());
                this.passwordField.setFocused(!this.usernameField.isFocused());
            }
        }
        if (character == '\r') {
            this.actionPerformed(this.buttonList.get(0));
        }
        this.usernameField.textboxKeyTyped(character, key);
        this.passwordField.textboxKeyTyped(character, key);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        switch (button.id) {
            case 0: {
                if (passwordField.getText().isEmpty()) {
                    mc.session = new Session(usernameField.getText(), "", "", "mojang");
                    status = EnumChatFormatting.GREEN + "Logged in to " + mc.session.getUsername() + " (offline).";
                } else {
                    new Thread(() -> {
                        status = "Logging in...";
                        MicrosoftAuthenticator authenticator = new MicrosoftAuthenticator();
                        try {
                            MicrosoftAuthResult result = authenticator.loginWithCredentials(usernameField.getText(), passwordField.getText());
                            mc.session = new Session(result.getProfile().getName(), result.getProfile().getId(), result.getAccessToken(), "mojang");
                            status = EnumChatFormatting.GREEN + "Logged in to " + mc.session.getUsername() + ".";
                        } catch (Exception e) {
                            status = EnumChatFormatting.RED + "Failed to login.";
                            e.printStackTrace();
                        }
                    }).start();
                }
            }
            case 2: {
                if (getClipboardString().contains(":")) {
                    String[] split = getClipboardString().split(":");
                    if (split.length < 2) return;
                    usernameField.setText(split[0]);
                    passwordField.setText(split[1]);
                }
                break;
            }
            case 3: {

                Faker faker = new Faker();
                String username = faker.funnyName().name();
                Number num = faker.number().numberBetween(0, 9999);
                username = username + num;
                if (username.length() > 16)
                    username = username.substring(0, 16);
                mc.session = new Session(username.replace(" ", "").replaceAll("\\.", ""), "", "", "mojang");
                status = EnumChatFormatting.GREEN + "Logged in to " + mc.session.getUsername() + " (offline).";
                break;
            }
            case 4: {
                boolean fullscreen;
                if (mc.gameSettings.fullScreen) {
                    mc.toggleFullscreen();
                    fullscreen = true;
                } else {
                    fullscreen = false;
                }
                new Thread(() -> {

                    try {
                        System.out.println("setting look and feel");
                        UIManager.setLookAndFeel(new WindowsLookAndFeel());
                    } catch (UnsupportedLookAndFeelException e) {
                        e.printStackTrace();
                        System.out.println("look and feel no work using swing's dogshit default look");
                    }
                    JFileChooser chooser = new JFileChooser();
                    FileNameExtensionFilter filter = new FileNameExtensionFilter("Text Files", "txt");
                    chooser.setFileFilter(filter);
                    int returnVal = chooser.showOpenDialog(null);
                    System.out.println(returnVal);
                    if (returnVal == JFileChooser.APPROVE_OPTION) {
                        System.out.println("Alt " + chooser.getSelectedFile().getName());
                        if (fullscreen) {
                            mc.toggleFullscreen();
                        }
                        try {
                            CookieLoginUtil.LoginData loginData = CookieLoginUtil.loginWithCookie(chooser.getSelectedFile());

                            if (loginData == null) {
                                status = EnumChatFormatting.RED + "Failed to login with cookie!";
                                return;
                            }

                            status = EnumChatFormatting.GREEN + "Logged in to " + loginData.username + ".";
                            Minecraft.getMinecraft().session = new Session(loginData.username, loginData.uuid, loginData.mcToken, "legacy");
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                }).start();
                break;
            }
            case 5: {
                mc.displayGuiScreen(new GuiMainMenu());
                break;
            }
        }
    }
}
