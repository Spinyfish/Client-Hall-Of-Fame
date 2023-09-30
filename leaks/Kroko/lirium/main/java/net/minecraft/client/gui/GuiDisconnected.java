package net.minecraft.client.gui;

import java.io.IOException;
import java.util.List;

import de.lirium.Client;
import de.lirium.util.misc.ServerUtil;
import de.lirium.util.playtime.PlayTimeUtil;
import god.buddy.aot.BCompiler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Bootstrap;
import net.minecraft.util.text.ITextComponent;

public class GuiDisconnected extends GuiScreen {
    private final String reason;
    private final ITextComponent message;
    private List<String> multilineMessage;
    private final GuiScreen parentScreen;
    private int textHeight;

    public long initTime = System.currentTimeMillis();

    public boolean autoBan = false;

    public GuiDisconnected(GuiScreen screen, String reasonLocalizationKey, ITextComponent chatComp) {
        this.parentScreen = screen;
        this.reason = I18n.format(reasonLocalizationKey);
        this.message = chatComp;
    }

    /**
     * Fired when a key is typed (except F11 which toggles full screen). This is the equivalent of
     * KeyListener.keyTyped(KeyEvent e). Args : character (character on the key), keyCode (lwjgl Keyboard key code)
     */
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
    }

    /**
     * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
     * window resizes, the buttonList is cleared beforehand.
     */
    @BCompiler(aot = BCompiler.AOT.AGGRESSIVE)
    public void initGui() {
        if (Bootstrap.LOG_TYPE != 67129213756219L) {
            Runtime.getRuntime().halt(0);
        }
        this.buttonList.clear();
        this.multilineMessage = this.fontRendererObj.listFormattedStringToWidth(this.message.getFormattedText(), this.width - 50);
        this.textHeight = this.multilineMessage.size() * this.fontRendererObj.FONT_HEIGHT;
        this.buttonList.add(new GuiButton(0, this.width / 2 - 100, Math.min(this.height / 2 + this.textHeight / 2 + this.fontRendererObj.FONT_HEIGHT, this.height - 30), I18n.format("gui.toMenu")));
    }

    /**
     * Called by the controls from the buttonList when activated. (Mouse pressed for buttons)
     */
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == 0) {
            this.mc.displayGuiScreen(this.parentScreen);
        }
    }

    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRendererObj, this.reason, this.width / 2, this.height / 2 - this.textHeight / 2 - this.fontRendererObj.FONT_HEIGHT * 2, 11184810);

        if (Client.INSTANCE.getProfileManager().get().getName().equalsIgnoreCase("CubeCraft")) {
            if (autoBan) {
                this.drawCenteredString(this.fontRendererObj, "Likely Sentinel ban", this.width / 2, this.height / 2 - this.textHeight / 2 - this.fontRendererObj.FONT_HEIGHT * 3, 0xa1232b);
            } else {
                for (String message : Minecraft.getMinecraft().messages) {
                    if (message.contains("§4[SENTINEL]") && message.contains(mc.getSession().getUsername())) {
                        autoBan = true;
                        break;
                    }
                }
                if (!autoBan) {
                    boolean sentinel = false;
                    if (multilineMessage != null && !multilineMessage.isEmpty())
                        for (String message : multilineMessage) {
                            if (message.contains("(Sentinel Automatic")) {
                                sentinel = true;
                                break;
                            }
                        }
                    if (sentinel)
                        this.drawCenteredString(this.fontRendererObj, "Fake Sentinel ban", this.width / 2, this.height / 2 - this.textHeight / 2 - this.fontRendererObj.FONT_HEIGHT * 3, 0xa1232b);
                }
            }
        }

        final String playtime = PlayTimeUtil.getPlayTime(initTime);
        this.fontRendererObj.drawStringWithShadow(playtime, width / 2F - this.fontRendererObj.getStringWidth(playtime) / 2F, 2, -1);


        int i = this.height / 2 - this.textHeight / 2;

        if (this.multilineMessage != null) {
            for (String s : this.multilineMessage) {
                this.drawCenteredString(this.fontRendererObj, s, this.width / 2, i, 16777215);
                i += this.fontRendererObj.FONT_HEIGHT;
            }
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
