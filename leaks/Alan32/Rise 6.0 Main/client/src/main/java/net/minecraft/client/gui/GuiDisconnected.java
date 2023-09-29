package net.minecraft.client.gui;

import com.alan.clients.Client;
import com.alan.clients.component.impl.player.LastConnectionComponent;
import com.alan.clients.newevent.impl.other.ServerKickEvent;
import com.alan.clients.ui.menu.impl.main.MainMenu;
import com.alan.clients.util.font.impl.minecraft.FontRenderer;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.IChatComponent;

import java.io.IOException;
import java.util.List;

public class GuiDisconnected extends GuiScreen {
    private final String reason;
    private final IChatComponent message;
    private List<String> multilineMessage;
    private final GuiScreen parentScreen;
    private int field_175353_i;

    public GuiDisconnected(final GuiScreen screen, final String reasonLocalizationKey, final IChatComponent chatComp) {
        this.parentScreen = screen;
        this.reason = I18n.format(reasonLocalizationKey);
        this.message = chatComp;
    }

    /**
     * Fired when a key is typed (except F11 which toggles full screen). This is the equivalent of
     * KeyListener.keyTyped(KeyEvent e). Args : character (character on the key), keyCode (lwjgl Keyboard key code)
     */
    protected void keyTyped(final char typedChar, final int keyCode) throws IOException {
        if (keyCode == 1) {
            this.mc.displayGuiScreen(null);

            if (this.mc.currentScreen == null) {
                this.mc.setIngameFocus();
            }
        }
    }

    /**
     * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
     * window resizes, the buttonList is cleared beforehand.
     */
    public void initGui() {
        this.buttonList.clear();
        this.multilineMessage = this.fontRendererObj.listFormattedStringToWidth(this.message.getFormattedText(), this.width - 50);
        this.field_175353_i = this.multilineMessage.size() * FontRenderer.FONT_HEIGHT;
        int padding = 3;

        this.buttonList.add(new GuiButton(0, this.width / 2 - 100, this.height / 2 + this.field_175353_i / 2 + FontRenderer.FONT_HEIGHT, 100 - padding, 20, "Leave"));
        this.buttonList.add(new GuiButton(1, this.width / 2 + padding, this.height / 2 + this.field_175353_i / 2 + FontRenderer.FONT_HEIGHT, 100 - padding, 20, "Reconnect"));

        Client.INSTANCE.getEventBus().handle(new ServerKickEvent(multilineMessage));
    }

    /**
     * Called by the controls from the buttonList when activated. (Mouse pressed for buttons)
     */
    protected void actionPerformed(final GuiButton button) throws IOException {
        if (button.id == 0) {
            this.mc.displayGuiScreen(this.parentScreen);
        }

        if (button.id == 1) {
            this.mc.displayGuiScreen(new GuiConnecting(new GuiMultiplayer(new MainMenu()), this.mc, new ServerData("", LastConnectionComponent.ip, false)));
        }
    }

    /**
     * Draws the screen and all the components in it. Args : mouseX, mouseY, renderPartialTicks
     */
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRendererObj, this.reason, this.width / 2, this.height / 2 - this.field_175353_i / 2 - FontRenderer.FONT_HEIGHT * 2, 11184810);
        int i = this.height / 2 - this.field_175353_i / 2;

        if (this.multilineMessage != null) {
            for (final String s : this.multilineMessage) {
                this.drawCenteredString(this.fontRendererObj, s, this.width / 2, i, 16777215);
                i += FontRenderer.FONT_HEIGHT;
            }
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
