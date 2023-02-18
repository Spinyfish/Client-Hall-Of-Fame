/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiButton
 *  net.minecraft.client.gui.GuiScreen
 *  net.minecraft.client.gui.GuiTextField
 *  vip.astroline.client.service.config.preset.PresetManager
 */
package vip.astroline.client.layout.clickgui.config;

import java.io.IOException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import vip.astroline.client.service.config.preset.PresetManager;

public class SavePresetScreen
extends GuiScreen {
    private final GuiScreen parent;
    private boolean saveBinds;
    private GuiTextField nameField;

    public SavePresetScreen(GuiScreen parent) {
        this.parent = parent;
    }

    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
        this.nameField.textboxKeyTyped(typedChar, keyCode);
        if (keyCode == 1) {
            this.mc.displayGuiScreen(this.parent);
        }
        this.nameField.setText(this.nameField.getText().replace(" ", "").replace("#", "").replace("_NONE", ""));
    }

    public void initGui() {
        this.nameField = new GuiTextField(0, Minecraft.getMinecraft().fontRendererObj, this.width / 2 - 100, this.height / 6 + 20, 200, 20);
        this.buttonList.add(new GuiButton(2, this.width / 2 - 100, this.height / 6 + 55 + 44, "Save binds: No"));
        this.buttonList.add(new GuiButton(3, this.width / 2 - 100, this.height / 6 + 40 + 110, "Add"));
        this.buttonList.add(new GuiButton(4, this.width / 2 - 100, this.height / 6 + 40 + 132, "Cancel"));
    }

    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == 2) {
            boolean bl = this.saveBinds = !this.saveBinds;
        }
        if (button.id == 3) {
            PresetManager.addPreset((String)this.nameField.getText(), (boolean)this.saveBinds);
            this.mc.displayGuiScreen(this.parent);
        }
        if (button.id == 4) {
            this.mc.displayGuiScreen(this.parent);
        }
        ((GuiButton)this.buttonList.get((int)0)).displayString = "Save binds: " + (this.saveBinds ? "Yes" : "No");
    }

    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        this.nameField.mouseClicked(mouseX, mouseY, mouseButton);
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    public void updateScreen() {
        this.nameField.updateCursorCounter();
        super.updateScreen();
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRendererObj, "Name", this.width / 2 - 89, this.height / 6 + 10, 0xFFFFFF);
        this.nameField.drawTextBox();
        this.drawCenteredString(this.fontRendererObj, "Adding Preset", this.width / 2, 30, 0xFFFFFF);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
