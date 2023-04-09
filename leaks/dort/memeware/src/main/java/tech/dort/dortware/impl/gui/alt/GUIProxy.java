package tech.dort.dortware.impl.gui.alt;

import net.minecraft.client.gui.*;
import net.minecraft.client.resources.I18n;
import org.apache.commons.lang3.RandomStringUtils;
import org.lwjgl.input.Keyboard;
import tech.dort.dortware.Client;
import tech.dort.dortware.api.font.CustomFontRenderer;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.io.IOException;
import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;

public final class GUIProxy
        extends GuiScreen {
    public static String sessionID = "";
    private final GuiScreen previousScreen;
    private static LastProxy lastSession;
    private GuiTextField ip, port, username, password, sessionIDField;

    final CustomFontRenderer font = Client.INSTANCE.getFontManager().getFont("Large").getRenderer();

    public GUIProxy(GuiScreen previousScreen) {
        this.previousScreen = previousScreen;
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        try {
            switch (button.id) {
                case 1:
                    mc.displayGuiScreen(previousScreen);
                    break;
                case 2:
                    String data = (String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
                    if (!data.contains(":")) break;
                    String[] credentials = data.split(":");
                    if (credentials.length >= 2) {
                        ip.setText(credentials[0]);
                        port.setText(credentials[1]);
                    }
                    if (credentials.length >= 3) {
                        username.setText(credentials[2]);
                        password.setText(credentials[3]);
                    }
                    if (credentials.length >= 5) {
                        sessionIDField.setText(credentials[4]);
                    }
                    break;
                case 0:
                    sessionID = sessionIDField.getText();
                    System.setProperty("jdk.http.auth.tunneling.disabledSchemes", "");
                    if (!username.getText().isEmpty() && !password.getText().isEmpty()) {
                        Authenticator.setDefault(new Authenticator() {
                            @Override
                            protected PasswordAuthentication getPasswordAuthentication() {
                                PasswordAuthentication passwordAuthentication;
                                if (sessionIDField.getText().isEmpty()) {
                                    passwordAuthentication = new PasswordAuthentication(username.getText(), password.getText().toCharArray());
                                } else {
                                    passwordAuthentication = new PasswordAuthentication(username.getText(), (password.getText() + ':' + sessionIDField.getText() + ':' + 130000).toCharArray());
                                }
                                return passwordAuthentication;
                            }
                        });
                    }
                    Client.INSTANCE.setProxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress(ip.getText(), Integer.parseInt(port.getText()))));
                    lastSession = new LastProxy(username.getText(), password.getText(), ip.getText(), port.getText());
                    break;
                case 3:
                    Client.INSTANCE.setProxy(Proxy.NO_PROXY);
                    break;
                case 1337:
                    if (lastSession == null)
                        break;
                    sessionID = RandomStringUtils.randomAlphabetic(25);
                    if (!username.getText().isEmpty() && !password.getText().isEmpty()) {
                        Authenticator.setDefault(new Authenticator() {
                            @Override
                            protected PasswordAuthentication getPasswordAuthentication() {
                                PasswordAuthentication passwordAuthentication;
                                if (sessionIDField.getText().isEmpty()) {
                                    passwordAuthentication = new PasswordAuthentication(lastSession.getUsername(), lastSession.getPassword().toCharArray());
                                } else {
                                    passwordAuthentication = new PasswordAuthentication(lastSession.getUsername(), (lastSession.getPassword() + ':' + sessionID + ':' + 30000).toCharArray());
                                }
                                return passwordAuthentication;
                            }
                        });
                        System.out.println(lastSession.getIp());
                    }
                    Client.INSTANCE.setProxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress(lastSession.getIp(), Integer.parseInt(lastSession.getPort()))));
                    break;
                default:
                    break;
            }
        } catch (Throwable ignored) {
        }
    }

    @Override
    public void drawScreen(int x, int y2, float z) {
        ScaledResolution scaledResolution = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);

        int startColor = new Color(0, 81, 158).getRGB();
        int endColor = new Color(3, 45, 150).getRGB();
        Gui.drawGradientRectDiagonal(0, 0, scaledResolution.getScaledWidth(), scaledResolution.getScaledHeight(), startColor, endColor);
        ip.drawTextBox();
        port.drawTextBox();
        username.drawTextBox();
        password.drawTextBox();
        sessionIDField.drawTextBox();
        font.drawCenteredStringWithShadow("Proxy", width / 2F, 20, -1);
        if (ip.getText().isEmpty()) {
            font.drawStringWithShadow("IP", width / 2F - 96, 66, -7829368);
        }
        if (port.getText().isEmpty()) {
            font.drawStringWithShadow("Port", width / 2F - 96, 106, -7829368);
        }
        if (username.getText().isEmpty()) {
            font.drawStringWithShadow("Username", width / 2F - 96, 146, -7829368);
        }
        if (password.getText().isEmpty()) {
            font.drawStringWithShadow("Password", width / 2F - 96, 186, -7829368);
        }
        if (sessionIDField.getText().isEmpty()) {
            font.drawStringWithShadow("Session ID (proxyland.io only!)", width / 2F - 96, 226, -7829368);
        }
        super.drawScreen(x, y2, z);
    }

    @Override
    public void initGui() {
        int var3 = 240;
        buttonList.add(new GuiButton(0, width / 2 - 100, var3 + 12, "Connect"));
        buttonList.add(new GuiButton(1, width / 2 - 100, var3 + 12 + 24, I18n.format("gui.cancel")));
        buttonList.add(new GuiButton(2, width / 2 - 100, var3 + 12 + 48, "Import IP:Port"));
        buttonList.add(new GuiButton(1337, width / 2 - 100, var3 + 12 + 48 + 48 + 24, "New Proxyland Session"));
        buttonList.add(new GuiButton(3, width / 2 - 100, var3 + 12 + 48 + 48, "Reset"));
        ip = new GuiTextField(var3, mc.fontRendererObj, width / 2 - 100, 60, 200, 20);
        port = new GuiTextField(var3, mc.fontRendererObj, width / 2 - 100, 100, 200, 20);
        username = new GuiTextField(var3, mc.fontRendererObj, width / 2 - 100, 140, 200, 20);
        password = new GuiTextField(var3, mc.fontRendererObj, width / 2 - 100, 180, 200, 20);
        sessionIDField = new GuiTextField(var3, mc.fontRendererObj, width / 2 - 100, 220, 200, 20);
        ip.setFocused(true);
        Keyboard.enableRepeatEvents(true);
    }

    @Override
    protected void keyTyped(char character, int key) {
        try {
            super.keyTyped(character, key);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (character == '\t') {
            if (!ip.isFocused() && !port.isFocused()) {
                ip.setFocused(true);
            } else {
                ip.setFocused(port.isFocused());
                port.setFocused(!ip.isFocused());
            }
        }
        if (character == '\r') {
            actionPerformed(buttonList.get(0));
        }
        ip.textboxKeyTyped(character, key);
        port.textboxKeyTyped(character, key);
        username.textboxKeyTyped(character, key);
        password.textboxKeyTyped(character, key);
        sessionIDField.textboxKeyTyped(character, key);
    }

    @Override
    protected void mouseClicked(int x, int y2, int button) {
        try {
            super.mouseClicked(x, y2, button);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ip.mouseClicked(x, y2, button);
        port.mouseClicked(x, y2, button);
        username.mouseClicked(x, y2, button);
        password.mouseClicked(x, y2, button);
        sessionIDField.mouseClicked(x, y2, button);
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
    }

    @Override
    public void updateScreen() {
        ip.updateCursorCounter();
        port.updateCursorCounter();
        username.updateCursorCounter();
        password.updateCursorCounter();
        sessionIDField.updateCursorCounter();
    }
}
