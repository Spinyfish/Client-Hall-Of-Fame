/**
 * @project Myth
 * @author CodeMan
 * @at 05.08.22, 23:55
 */
package dev.myth.ui;

import dev.myth.main.ClientMain;
import dev.myth.managers.ShaderManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import org.lwjgl.input.Keyboard;

import java.io.IOException;

public class AltLoginScreen extends GuiScreen {

    private GuiScreen parent;
    private GuiPasswordField passwordField;
    private GuiTextField usernameField;
    private AccountLoginThread accountThread = null;

    public AltLoginScreen(GuiScreen parent) {
        this.parent = parent;
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        super.actionPerformed(button);

        switch (button.id) {
            case 0:
                accountThread = new AccountLoginThread(usernameField.getText(), passwordField.getText());
                accountThread.start();
                break;
            case 1:
                String clipboard = getClipboardString().trim();
                if(clipboard.contains("\n")) clipboard = clipboard.replaceAll("\n", "");

                if(clipboard.contains(":")) {
                    String[] split = clipboard.split(":");
                    usernameField.setText(split[0]);
                    passwordField.setText(split[1]);
                } else {
                    usernameField.setText(clipboard);
                }
                break;
            case 2:
                mc.displayGuiScreen(parent);
                break;
        }
    }

    @Override
    public void initGui() {
        super.initGui();

        usernameField = new GuiTextField(1, Minecraft.getMinecraft().fontRendererObj, width / 2 - 100, height / 2 - 50, 200, 20);
        passwordField = new GuiPasswordField(0, width / 2 - 100, height / 2 - 20, 200, 20);

        usernameField.setMaxStringLength(100);
        passwordField.setMaxStringLength(100);

        usernameField.setFocused(true);

        buttonList.clear();
        buttonList.add(new GuiButton(0, width / 2 - 100, height / 2 + 20, 200, 20, "Login"));
        buttonList.add(new GuiButton(1, width / 2 - 100, height / 2 + 40, 200, 20, "Copy from Clipboard"));
        buttonList.add(new GuiButton(2, width / 2 - 100, height / 2 + 60, 200, 20, "Back"));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        switch (GuiMainMenu.bgId) {
            case 1:
                ClientMain.INSTANCE.manager.getManager(ShaderManager.class).BACKGROUND_SHADER1.doRender();
                break;
            case 2:
                ClientMain.INSTANCE.manager.getManager(ShaderManager.class).BACKGROUND_SHADER2.doRender();
                break;
            case 3:
                ClientMain.INSTANCE.manager.getManager(ShaderManager.class).BACKGROUND_SHADER3.doRender();
                break;
            case 4:
                ClientMain.INSTANCE.manager.getManager(ShaderManager.class).BACKGROUND_SHADER4.doRender();
                break;
        }
        super.drawScreen(mouseX, mouseY, partialTicks);
        usernameField.drawTextBox();
        passwordField.drawTextBox();

        if(usernameField.getText().isEmpty() && !usernameField.isFocused()) {
            this.drawString(fontRendererObj, "Username", width / 2 - 100 + 5, height / 2 - 50 + 5, 0xFFFFFF);
        }
        if(passwordField.getText().isEmpty() && !passwordField.isFocused()) {
            this.drawString(fontRendererObj, "Password", width / 2 - 100 + 5, height / 2 - 20 + 5, 0xFFFFFF);
        }

        String current = accountThread == null ? "Current: " + Minecraft.getMinecraft().session.getUsername() : accountThread.status;

        this.drawString(fontRendererObj, current, (width - mc.fontRendererObj.getStringWidth(current)) / 2, height / 2 - 70, 0xFFFFFF);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        usernameField.mouseClicked(mouseX, mouseY, mouseButton);
        passwordField.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (typedChar == '\t') {
            if (!usernameField.isFocused() && !passwordField.isFocused()) {
                usernameField.setFocused(true);
            } else {
                usernameField.setFocused(passwordField.isFocused());
                usernameField.setFocused(!usernameField.isFocused());
            }
        }

        if(keyCode == Keyboard.KEY_ESCAPE) {
            mc.displayGuiScreen(parent);
        }

        usernameField.textboxKeyTyped(typedChar, keyCode);
        passwordField.textboxKeyTyped(typedChar, keyCode);

    }
}
