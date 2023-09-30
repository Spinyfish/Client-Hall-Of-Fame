package net.minecraft.client.gui;

import net.minecraft.client.resources.I18n;
import net.minecraft.util.IChatComponent;

import java.io.IOException;
import java.util.ArrayList;

public class GuiDisconnected extends GuiScreen {
    private final String reason;
    private final IChatComponent message;
    private ArrayList<String> multilineMessage;
    private final GuiScreen parentScreen;
    private int field_175353_i;
    private boolean triedToReconnect;

    public GuiDisconnected(GuiScreen screen, String reasonLocalizationKey, IChatComponent chatComp) {
        this.parentScreen = screen;
        this.reason = I18n.format(reasonLocalizationKey);
        this.message = chatComp;
//        this.message.appendText("\n\nYour session lasted " + DateUtil.getFormattedTime(banTime - SessionInfoModule.instance().startPlayTime()));
//        if (reason != null && (reason.equalsIgnoreCase("Connection Lost") || reason.equalsIgnoreCase("Failed to connect to the server")) && Client.INSTANCE.getAccountRepository().currentAccount() != null) {
//            if (message != null && message.getFormattedText().contains("banned")) {
//                if (ServerUtil.onServer("Hypixel")) {
//                    SessionInfoModule sessionInfo = SessionInfoModule.instance();
//                    String messageclone = EnumChatFormatting.getTextWithoutFormattingCodes(message.getFormattedText());
//                    if (!messageclone.contains("security"))
//                        sessionInfo.addBan();
//                    sessionInfo.kills(0);
//                    sessionInfo.wins(0);
//                    sessionInfo.games(0);
//                    Client.INSTANCE.getModuleRepository().moduleBy(StaffAnalyzerModule.class).recievedAPIKey(false);
////                    String banID = messageclone.substring(messageclone.indexOf("Ban ID: ") + "Ban ID: ".length()).replace("#", "").substring(0, 8);
//                    if (message.getFormattedText().contains("permanently")) {
//                        Client.INSTANCE.getAccountRepository().currentAccount().info().hypixelBanned(AccountStatus.BANNED);
//                        return;
//                    }
//                    if (Client.INSTANCE.getAccountRepository().currentAccount().info().hypixelBanned() == AccountStatus.UNKNOWN)
//                        Client.INSTANCE.getAccountRepository().currentAccount().info().hypixelBanMillis(System.currentTimeMillis());
////                    messageclone = messageclone.substring(messageclone.indexOf("banned for") + "banned for".length(), messageclone.indexOf("from")).trim();
////                    messageclone = messageclone.replaceAll("d", "").replaceAll("h", "").replaceAll("m", "").replaceAll("s", "");
//                    Client.INSTANCE.getAccountRepository().currentAccount().info().hypixelBanned(AccountStatus.TEMPORARILY_BANNED);
//                    Client.INSTANCE.getAccountRepository().currentAccount().info().hypixelBanMillis(System.currentTimeMillis());
//                } else if (ServerUtil.onServer("Mineplex")) {
//                    Client.INSTANCE.getAccountRepository().currentAccount().info().mineplexBanMillis(System.currentTimeMillis());
//                    Client.INSTANCE.getAccountRepository().currentAccount().info().mineplexBanned(AccountStatus.TEMPORARILY_BANNED);
////                    Client.INSTANCE.getAccountRepository().currentAccount().info().mineplexBanMillis(time);
//                }
//            }
//        }
    }

    /**
     * Fired when a key is typed (except F11 which toggles full screen). This is the equivalent of
     * KeyListener.keyTyped(KeyEvent e). Args : character (character on the key), keyCode (lwjgl Keyboard key code)
     */
    protected void keyTyped(char typedChar, int keyCode) throws IOException
    {
    }

    /**
     * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
     * window resizes, the buttonList is cleared beforehand.
     */
//    private long banTime;
    public void initGui()
    {
        this.buttonList.clear();
        this.multilineMessage = new ArrayList<>(this.fontRendererObj.listFormattedStringToWidth(this.message.getFormattedText(), this.width - 50));
        this.field_175353_i = this.multilineMessage.size() * this.fontRendererObj.FONT_HEIGHT;
        this.buttonList.add(new GuiButton(0, this.width / 2 - 100, this.height / 2 + this.field_175353_i / 2 + this.fontRendererObj.FONT_HEIGHT, I18n.format("gui.toMenu")));
//        banTime = System.currentTimeMillis();
    }

    private String getBanId() {
        for (String s : multilineMessage) {
            if (s.contains("Ban ID")) return s.split("�r�f")[1].replace("#", "");
        }
        return "";
    }

    /**
     * Called by the controls from the buttonList when activated. (Mouse pressed for buttons)
     */
    protected void actionPerformed(GuiButton button) throws IOException
    {
        if (button.id == 0)
        {
            this.mc.displayGuiScreen(this.parentScreen);
        }
    }

    /**
     * Draws the screen and all the components in it. Args : mouseX, mouseY, renderPartialTicks
     */
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRendererObj, this.reason, this.width / 2, this.height / 2 - this.field_175353_i / 2 - this.fontRendererObj.FONT_HEIGHT * 2, 11184810);
        int i = this.height / 2 - this.field_175353_i / 2;

        try {
            if (this.multilineMessage != null)
            {
                for (String s : this.multilineMessage)
                {
                    this.drawCenteredString(this.fontRendererObj, s, this.width / 2, i, 16777215);
                    i += this.fontRendererObj.FONT_HEIGHT;
                }
            }
        } catch (Exception ignored) {}

        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}