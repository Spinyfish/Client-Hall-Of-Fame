package net.minecraft.client.gui;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import intentions.modules.render.TabGUI;
import intentions.ui.MainMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.Session;

public class GuiDisconnected extends GuiScreen
{
    private String reason;
    private IChatComponent message;
    private List multilineMessage;
    private final GuiScreen parentScreen;
    private int field_175353_i;
    private static final String __OBFID = "CL_00000693";

    public GuiDisconnected(GuiScreen p_i45020_1_, String p_i45020_2_, IChatComponent p_i45020_3_)
    {
        this.parentScreen = p_i45020_1_;
        this.reason = I18n.format(p_i45020_2_, new Object[0]);
        this.message = p_i45020_3_;
    }

    /**
     * Fired when a key is typed (except F11 who toggle full screen). This is the equivalent of
     * KeyListener.keyTyped(KeyEvent e). Args : character (character on the key), keyCode (lwjgl Keyboard key code)
     */
    protected void keyTyped(char typedChar, int keyCode) throws IOException {}

    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    public void initGui()
    {
        this.buttonList.clear();
        this.multilineMessage = this.fontRendererObj.listFormattedStringToWidth(this.message.getFormattedText(), this.width - 50);
        this.field_175353_i = this.multilineMessage.size() * this.fontRendererObj.FONT_HEIGHT;
        this.buttonList.add(new GuiButton(0, this.width / 2 - 100, this.height / 2 + this.field_175353_i / 2 + this.fontRendererObj.FONT_HEIGHT, I18n.format("gui.toMenu", new Object[0])));
        if(!TabGUI.openTabGUI) return;
        this.buttonList.add(new GuiButton(1, this.width / 2 - 100, this.height / 2 + 20 + this.field_175353_i / 2 + this.fontRendererObj.FONT_HEIGHT, 100, 20, "Reconnect"));
        this.buttonList.add(new GuiButton(2, this.width / 2, this.height / 2 + 20 + this.field_175353_i / 2 + this.fontRendererObj.FONT_HEIGHT, 100, 20, "Random Alt"));
    }

    protected void actionPerformed(GuiButton button) throws IOException
    {
        if (button.id == 0)
        {
            this.mc.displayGuiScreen(new GuiMultiplayer(new MainMenu()));
        }
        else if(button.id == 1)
        {
        	this.mc.displayGuiScreen(new GuiConnecting(this, this.mc, GuiConnecting.serverData));
        }
        else if (button.id == 2)
        {
        	String randomName = random(rng(3, 16));
        	Minecraft.getMinecraft().session = new Session(randomName, "", "", "mojang");

        	this.mc.displayGuiScreen(new GuiConnecting(this, this.mc, GuiConnecting.serverData));
        }
    }
    private char[] validCharacters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ_".toCharArray();
    private String random(int length) {
    	String output = "";
    	for(int i=0;i<length;i++) output += validCharacters[rng(0, validCharacters.length)];
    	return output;
    }
    private int rng(int min, int max) {
    	return (int) Math.floor(Math.random() * (max - min)) + min;
    }

    /**
     * Draws the screen and all the components in it. Args : mouseX, mouseY, renderPartialTicks
     */
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRendererObj, this.reason, this.width / 2, this.height / 2 - this.field_175353_i / 2 - this.fontRendererObj.FONT_HEIGHT * 2, 11184810);
        int var4 = this.height / 2 - this.field_175353_i / 2;

        if (this.multilineMessage != null)
        {
            for (Iterator var5 = this.multilineMessage.iterator(); var5.hasNext(); var4 += this.fontRendererObj.FONT_HEIGHT)
            {
                String var6 = (String)var5.next();
                this.drawCenteredString(this.fontRendererObj, var6, this.width / 2, var4, 16777215);
            }
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
