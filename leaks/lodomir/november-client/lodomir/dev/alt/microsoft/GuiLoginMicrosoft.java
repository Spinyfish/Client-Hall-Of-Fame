/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.input.Keyboard
 */
package lodomir.dev.alt.microsoft;

import java.io.IOException;
import lodomir.dev.alt.AltLoginThread;
import lodomir.dev.alt.GuiAltManager;
import lodomir.dev.alt.PasswordField;
import lodomir.dev.alt.microsoft.SessionChanger;
import lodomir.dev.ui.menu.MainMenu;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.EnumChatFormatting;
import org.lwjgl.input.Keyboard;

public class GuiLoginMicrosoft
extends GuiScreen {
    private GuiTextField username;
    private PasswordField password;
    private AltLoginThread thread;
    private String status = (Object)((Object)EnumChatFormatting.GRAY) + "Waiting...";
    private final GuiAltManager manager;

    public GuiLoginMicrosoft(GuiAltManager manager) {
        this.manager = manager;
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        switch (button.id) {
            case 0: {
                this.thread = new AltLoginThread(this.username.getText(), this.password.getText());
                this.thread.start();
            }
            case 2: {
                this.status = (Object)((Object)EnumChatFormatting.YELLOW) + "Logging in...";
                if (this.username.getText().equals("")) {
                    this.mc.displayGuiScreen(new GuiLoginMicrosoft(this.manager));
                    break;
                }
                if (!this.username.getText().equals("")) {
                    SessionChanger.getInstance().setUserMicrosoft(this.username.getText(), this.password.getText());
                    this.status = (Object)((Object)EnumChatFormatting.GREEN) + "Logged in (" + this.mc.session.getUsername() + ")";
                    break;
                }
                this.status = (Object)((Object)EnumChatFormatting.RED) + "Login failed!";
                break;
            }
            case 1: {
                this.mc.displayGuiScreen(new MainMenu());
            }
        }
    }

    @Override
    public void drawScreen(int x2, int y2, float z2) {
        this.drawDefaultBackground();
        ScaledResolution sr = new ScaledResolution(this.mc);
        this.username.drawTextBox();
        this.password.drawTextBox();
        GuiLoginMicrosoft.drawCenteredString(this.mc.fontRendererObj, "Alt Login", width / 2, 20, -1);
        GuiLoginMicrosoft.drawCenteredString(this.mc.fontRendererObj, this.status, width / 2, 29, -1);
        if (this.username.getText().isEmpty()) {
            this.drawString(this.mc.fontRendererObj, "E-Mail", width / 2 - 96, 66.0f, -7829368);
        }
        if (this.password.getText().isEmpty()) {
            this.drawString(this.mc.fontRendererObj, "Password", width / 2 - 96, 106.0f, -7829368);
        }
        super.drawScreen(x2, y2, z2);
    }

    @Override
    public void initGui() {
        int var3 = height / 4 + 24;
        this.buttonList.add(new GuiButton(0, width / 2 - 100, var3 + 72 + 12, "Login"));
        this.buttonList.add(new GuiButton(1, width / 2 - 100, var3 + 72 + 12 + 24, "Back"));
        this.username = new GuiTextField(var3, this.mc.fontRendererObj, width / 2 - 100, 60, 200, 20);
        this.password = new PasswordField(this.mc.fontRendererObj, width / 2 - 100, 100, 200, 20);
        this.username.setFocused(true);
        Keyboard.enableRepeatEvents((boolean)true);
    }

    @Override
    protected void keyTyped(char character, int key) {
        try {
            super.keyTyped(character, key);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        if (character == '\t' && !this.username.isFocused()) {
            this.username.setFocused(true);
            this.password.setFocused(false);
        }
        if (character == '\t' && !this.password.isFocused()) {
            this.password.setFocused(true);
            this.username.setFocused(false);
        }
        if (character == '\r') {
            this.actionPerformed((GuiButton)this.buttonList.get(0));
        }
        this.username.textboxKeyTyped(character, key);
        this.password.textboxKeyTyped(character, key);
    }

    @Override
    protected void mouseClicked(int x2, int y2, int button) {
        try {
            super.mouseClicked(x2, y2, button);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        this.username.mouseClicked(x2, y2, button);
        this.password.mouseClicked(x2, y2, button);
    }

    @Override
    public void onGuiClosed() {
        this.mc.entityRenderer.loadEntityShader(null);
        Keyboard.enableRepeatEvents((boolean)false);
    }

    @Override
    public void updateScreen() {
        this.username.updateCursorCounter();
        this.password.updateCursorCounter();
    }
}

