// 
// Decompiled by Procyon v0.5.30
// 

package felix.gui.alt.gui.impl;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.input.Keyboard;

import felix.Client;
import felix.gui.alt.gui.components.GuiPasswordField;
import felix.gui.alt.gui.thread.AccountLoginThread;
import felix.gui.alt.system.Account;
import felix.util.font.FontRenderer;
import felix.util.visual.RenderUtil;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.GuiScreen;

public final class GuiAltLogin extends GuiScreen {
    private GuiPasswordField password;
    private final GuiScreen previousScreen;
    private AccountLoginThread thread;
    private GuiTextField username;

    public GuiAltLogin(final GuiScreen previousScreen) {
        this.previousScreen = previousScreen;
    }

    @Override
    protected void actionPerformed(final GuiButton button) {
        switch (button.id) {
            case 1: {
                this.mc.displayGuiScreen(this.previousScreen);
                break;
            }
            case 0: {
                (this.thread = new AccountLoginThread(username.getText(), password.getText())).start();
                break;
            }
            case 2: {
                String data = null;
                try {
                    data = (String) Toolkit.getDefaultToolkit()
                            .getSystemClipboard().getData(DataFlavor.stringFlavor);
                    if (data.contains(":")) {
                        String[] credentials = data.split(":");
                        username.setText(credentials[0]);
                        password.setText(credentials[1]);
                    }
                } catch (UnsupportedFlavorException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void drawScreen(final int x, final int y, final float z) {
    	this.drawDefaultBackground();
        this.username.drawTextBox();
        this.password.drawTextBox();
        final FontRenderer fr = Client.INSTANCE.getFontManager().getFont("Display 20", false);
        fr.drawCenteredString("Alt Login", this.width / 2, 20, -1);
        fr.drawCenteredString((this.thread == null) ? (EnumChatFormatting.GRAY + "Idle...") : this.thread.getStatus(), this.width / 2, 29, -1);
        if (this.username.getText().isEmpty()) {
        	mc.fontRendererObj.drawString("Username / E-Mail", this.width / 2 - 96, 66, -7829368);
        }
        if (this.password.getText().isEmpty()) {
        	mc.fontRendererObj.drawString("Password", this.width / 2 - 96, 106, -7829368);
        }
        super.drawScreen(x, y, z);
    }

    @Override
    public void initGui() {
        final int var3 = this.height / 4 + 24;
        this.buttonList.add(new GuiButton(0, this.width / 2 - 100, var3 + 72 + 12, "Login"));
        this.buttonList.add(new GuiButton(1, this.width / 2 - 100, var3 + 72 + 12 + 24, "Back"));
        buttonList.add(new GuiButton(2, this.width / 2 - 100, var3 + 72 + 12 + 48, "Import user:pass"));
        this.username = new GuiTextField(var3, this.mc.fontRendererObj, this.width / 2 - 100, 60, 200, 20);
        this.password = new GuiPasswordField(this.mc.fontRendererObj, this.width / 2 - 100, 100, 200, 20);
        this.username.setFocused(true);
        Keyboard.enableRepeatEvents(true);
    }

    @Override
    protected void keyTyped(final char character, final int key) {
        try {
            super.keyTyped(character, key);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (character == '\t') {
            if (!this.username.isFocused() && !this.password.isFocused()) {
                this.username.setFocused(true);
            } else {
                this.username.setFocused(this.password.isFocused());
                this.password.setFocused(!this.username.isFocused());
            }
        }
        if (character == '\r') {
            this.actionPerformed((GuiButton) this.buttonList.get(0));
        }
        this.username.textboxKeyTyped(character, key);
        this.password.textboxKeyTyped(character, key);
    }

    @Override
    protected void mouseClicked(final int x, final int y, final int button) {
        try {
            super.mouseClicked(x, y, button);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.username.mouseClicked(x, y, button);
        this.password.mouseClicked(x, y, button);
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
    }

    @Override
    public void updateScreen() {
        this.username.updateCursorCounter();
        this.password.updateCursorCounter();
    }
    
    
}
