package frapppyz.cutefurry.pics.guis;

import frapppyz.cutefurry.pics.Wrapper;
import frapppyz.cutefurry.pics.alts.AltLoginThread;
import frapppyz.cutefurry.pics.util.HWIDUtil;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.EnumChatFormatting;
import org.lwjgl.input.Keyboard;

import java.io.IOException;

public final class UidLogin
        extends GuiScreen {
    private GuiTextField uidField;

    public void login() {
        HWIDUtil.UID= uidField.getText();
        HWIDUtil.checkHWID();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        mc.displayGuiScreen(new GuiMainMenu());
        Wrapper.getAstra().startup();
    }

    @Override
    public void drawScreen(int x2, int y2, float z2) {
        this.uidField.drawTextBox();
        if (this.uidField.getText().isEmpty()) {
            this.drawString(this.mc.fontRendererObj, "UID", width / 2 - 8,  (int) (mc.displayHeight/4 - 4), -7829368);
        }
        super.drawScreen(x2, y2, z2);
    }

    @Override
    public void initGui() {
        int var3 = height / 4 + 24;

        this.uidField = new GuiTextField(var3, this.mc.fontRendererObj, width / 2 - 16, (int) (mc.displayHeight/4 - 10), 32, 20);
        this.uidField.setFocused(true);
        Keyboard.enableRepeatEvents(true);
    }

    @Override
    protected void keyTyped(char character, int key) {
        try {
            super.keyTyped(character, key);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        if (character == '\t') {
            if (!this.uidField.isFocused()) {
                this.uidField.setFocused(true);
            }
        }
        if (character == '\r') {
            login();
        }
        this.uidField.textboxKeyTyped(character, key);
    }

    @Override
    public void mouseClicked(int x2, int y2, int button) {
        try {
            super.mouseClicked(x2, y2, button);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        this.uidField.mouseClicked(x2, y2, button);
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
    }

    @Override
    public void updateScreen() {
        this.uidField.updateCursorCounter();
    }
}
