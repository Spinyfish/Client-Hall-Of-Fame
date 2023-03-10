/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.input.Keyboard
 */
package lodomir.dev.alt.microsoft;

import java.io.IOException;
import lodomir.dev.alt.microsoft.SessionChanger;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.resources.I18n;
import org.lwjgl.input.Keyboard;

public class GuiLogin
extends GuiScreen {
    private GuiTextField username;

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == 0) {
            if (this.username.getText().equals("")) {
                this.mc.displayGuiScreen(new GuiLogin());
            } else {
                SessionChanger.getInstance().setUserOffline(this.username.getText());
            }
        }
    }

    @Override
    public void drawScreen(int x2, int y2, float z2) {
        ScaledResolution sr = new ScaledResolution(this.mc);
        this.username.drawTextBox();
        Gui.drawCenteredString(this.mc.fontRendererObj, "Username", width / 2, sr.getScaledHeight() / 2 - 65, -1);
        super.drawScreen(x2, y2, z2);
    }

    @Override
    public void initGui() {
        ScaledResolution sr = new ScaledResolution(this.mc);
        this.buttonList.clear();
        this.buttonList.add(new GuiButton(0, width / 2 - 50 - 10, height / 2 - 20, 120, 20, I18n.format("Login (Cracked)", new Object[0])));
        this.username = new GuiTextField(100, this.fontRendererObj, width / 2 - 50 - 10, sr.getScaledHeight() / 2 - 50, 120, 20);
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
        }
        if (character == '\r') {
            this.actionPerformed((GuiButton)this.buttonList.get(0));
        }
        this.username.textboxKeyTyped(character, key);
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
    }

    @Override
    public void onGuiClosed() {
        this.mc.entityRenderer.loadEntityShader(null);
        Keyboard.enableRepeatEvents((boolean)false);
    }

    @Override
    public void updateScreen() {
        this.username.updateCursorCounter();
    }
}

