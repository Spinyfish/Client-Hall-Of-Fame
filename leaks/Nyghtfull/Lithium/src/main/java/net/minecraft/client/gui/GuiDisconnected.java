package net.minecraft.client.gui;

import dev.tenacity.Tenacity;
import dev.tenacity.module.impl.render.NotificationsMod;
import dev.tenacity.module.impl.render.Statistics;
import dev.tenacity.ui.altmanager.helpers.Alt;
import dev.tenacity.ui.mainmenu.CustomMainMenu;
import dev.tenacity.utils.server.ServerUtils;
import dev.tenacity.utils.server.ban.BanUtils;
import net.minecraft.client.multiplayer.GuiConnecting;
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

    public GuiDisconnected(GuiScreen screen, String reasonLocalizationKey, IChatComponent chatComp) {
        this.parentScreen = screen;
        this.reason = I18n.format(reasonLocalizationKey);
        this.message = chatComp;
        Alt activeAlt = Tenacity.INSTANCE.getAltManager().currentSessionAlt;
        if (activeAlt != null) {
            BanUtils.processDisconnect(activeAlt, chatComp);
        }
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
    public void initGui() {
        this.buttonList.clear();
        this.multilineMessage = this.fontRendererObj.listFormattedStringToWidth(this.message.getFormattedText(), this.width - 50);
        this.field_175353_i = this.multilineMessage.size() * this.fontRendererObj.FONT_HEIGHT;
        this.buttonList.add(new GuiButton(0, this.width / 2 - 100, this.height / 2 + this.field_175353_i / 2 + this.fontRendererObj.FONT_HEIGHT, I18n.format("gui.toMenu")));
        this.buttonList.add(new GuiButton(1, this.width / 2 - 100, this.height / 2 + this.field_175353_i / 2 + this.fontRendererObj.FONT_HEIGHT + 25, 99, 20, "Alt Manager"));
        this.buttonList.add(new GuiButton(2, this.width / 2 + 1, this.height / 2 + this.field_175353_i / 2 + this.fontRendererObj.FONT_HEIGHT + 25, 99, 20, "Reconnect"));
    }

    /**
     * Called by the controls from the buttonList when activated. (Mouse pressed for buttons)
     */
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == 0) {
            mc2.displayGuiScreen(this.parentScreen);
        } else if (button.id == 1) {
            mc2.displayGuiScreen(Tenacity.INSTANCE.getAltManager());
        } else if (button.id == 2) {
            if (ServerUtils.lastServer != null) {
                mc2.displayGuiScreen(new GuiConnecting(new GuiMultiplayer(new CustomMainMenu()), mc2, ServerUtils.lastServer));
            }
        }
    }

    /**
     * Draws the screen and all the components in it. Args : mouseX, mouseY, renderPartialTicks
     */
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRendererObj, this.reason, this.width / 2, this.height / 2 - this.field_175353_i / 2 - this.fontRendererObj.FONT_HEIGHT * 2, 11184810);
        int i = this.height / 2 - this.field_175353_i / 2;

        if (this.multilineMessage != null) {
            for (String s : this.multilineMessage) {
                this.drawCenteredString(this.fontRendererObj, s, this.width / 2, i, 16777215);
                i += this.fontRendererObj.FONT_HEIGHT;
            }
        }

        int offset = 60;

        if (mc2.getSession() != null && mc2.getSession().getUsername() != null) {
            this.drawCenteredString(this.fontRendererObj, "Username: §7" + mc2.getSession().getUsername(), this.width / 2, i + offset, 0xFFE3E3E3);
            offset += 10;
        }

        String serverIP = ServerUtils.lastServer != null && ServerUtils.lastServer.serverIP != null ? ServerUtils.lastServer.serverIP : null;
        if (serverIP != null) {
            this.drawCenteredString(this.fontRendererObj, "Server: §7" + serverIP, this.width / 2, i + offset, 0xFFE3E3E3);
            offset += 10;
        }

        if (Statistics.getTimeDiff() > 2000) {
            int[] playTime = Statistics.getPlayTime();
            String str = playTime[2] + "s";
            if (playTime[1] > 0) str = playTime[1] + "m " + str;
            if (playTime[0] > 0) str = playTime[0] + "h " + str;
            this.drawCenteredString(this.fontRendererObj, "Play time: §7" + str, this.width / 2, i + offset, 0xFFE3E3E3);
        }

        Tenacity.INSTANCE.getModuleCollection().getModule(NotificationsMod.class).render();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
