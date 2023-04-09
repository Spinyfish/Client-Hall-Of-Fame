package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.IChatComponent;
import tech.dort.dortware.impl.gui.alt.LoginThread;
import tech.dort.dortware.impl.utils.AltUtils;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

public class GuiDisconnected extends GuiScreen {
    private final String reason;
    private final IChatComponent message;
    private List multilineMessage;
    private final GuiScreen parentScreen;
    private int field_175353_i;
    // private static final String __OBFID = "CL_00000693";

    public GuiDisconnected(GuiScreen p_i45020_1_, String p_i45020_2_, IChatComponent p_i45020_3_) {
        this.parentScreen = p_i45020_1_;
        this.reason = I18n.format(p_i45020_2_);
        this.message = p_i45020_3_;
    }

    /**
     * Fired when a key is typed (except F11 who toggle full screen). This is the equivalent of
     * KeyListener.keyTyped(KeyEvent e). Args : character (character on the key), keyCode (lwjgl Keyboard key code)
     */
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == 1) {
            this.mc.displayGuiScreen(this.parentScreen);
        }
    }

    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    public void initGui() {
        this.buttonList.clear();
        this.multilineMessage = this.fontRendererObj.listFormattedStringToWidth(this.message.getFormattedText(), this.width - 50);
        this.field_175353_i = this.multilineMessage.size() * this.fontRendererObj.FONT_HEIGHT;
        this.buttonList.add(new GuiButton(0, this.width / 2 - 100, this.height / 2 + this.field_175353_i / 2 + this.fontRendererObj.FONT_HEIGHT, I18n.format("gui.toMenu")));
        this.buttonList.add(new GuiButton(1, this.width / 2 - 100, this.height / 2 + this.field_175353_i / 2 + this.fontRendererObj.FONT_HEIGHT + 25, "Reconnect"));
        this.buttonList.add(new GuiButton(2, this.width / 2 - 100, this.height / 2 + this.field_175353_i / 2 + this.fontRendererObj.FONT_HEIGHT + 50, "Generate Cracked Account"));
        this.buttonList.add(new GuiButton(3, this.width / 2 - 100, this.height / 2 + this.field_175353_i / 2 + this.fontRendererObj.FONT_HEIGHT + 75, "Login from Clipboard"));
    }

    protected void actionPerformed(GuiButton button) throws IOException {
        switch (button.id) {
            case 0:
                this.mc.displayGuiScreen(this.parentScreen);
                break;
            case 1:
                Minecraft.getMinecraft().displayGuiScreen(new GuiConnecting(new GuiMultiplayer(new GuiMainMenu()), Minecraft.getMinecraft(), GuiConnecting.lastIP));
                break;
            case 2:
                AltUtils.genCracked();
                Minecraft.getMinecraft().displayGuiScreen(new GuiConnecting(new GuiMultiplayer(new GuiMainMenu()), Minecraft.getMinecraft(), GuiConnecting.lastIP));
                break;
            case 3:
                String data = null;
                try {
                    data = (String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
                } catch (UnsupportedFlavorException e) {
                    e.printStackTrace();
                }
                assert data != null;
                if (!data.contains(":")) break;
                String[] credentials = data.split(":");
                LoginThread thread = new LoginThread(credentials[0], credentials[1]);
                thread.start();
                Minecraft.getMinecraft().displayGuiScreen(new GuiConnecting(new GuiMultiplayer(new GuiMainMenu()), Minecraft.getMinecraft(), GuiConnecting.lastIP));
                break;
        }
    }

    /**
     * Draws the screen and all the components in it. Args : mouseX, mouseY, renderPartialTicks
     */
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRendererObj, this.reason, this.width / 2, this.height / 2 - this.field_175353_i / 2 - this.fontRendererObj.FONT_HEIGHT * 2, 11184810);
        int var4 = this.height / 2 - this.field_175353_i / 2;

        if (this.multilineMessage != null) {
            for (Iterator var5 = this.multilineMessage.iterator(); var5.hasNext(); var4 += this.fontRendererObj.FONT_HEIGHT) {
                String var6 = (String) var5.next();
                this.drawCenteredString(this.fontRendererObj, var6, this.width / 2, var4, 16777215);
            }
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
