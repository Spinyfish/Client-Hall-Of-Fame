package store.femboy.spring.custommenus;

import fr.litarvan.openauth.microsoft.MicrosoftAuthResult;
import fr.litarvan.openauth.microsoft.MicrosoftAuthenticationException;
import fr.litarvan.openauth.microsoft.MicrosoftAuthenticator;
import gay.sukumi.irc.ChatClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.Session;
import store.femboy.spring.impl.util.FontUtil;

import java.io.IOException;

public class GuiAltManager extends GuiScreen {

    public GuiTextField setUsername;

    public void initGui() {
        super.initGui();
        this.buttonList.add(new GuiButton(0, this.width / 2 - 85, this.height / 2 - 20, 160, 20, "Login"));
        this.buttonList.add(new GuiButton(1, this.width / 2 - 85, this.height / 2, 160, 20, "Back"));
        this.buttonList.add(new GuiButton(3, this.width / 2 - 85, this.height / 2 - 50, 160, 20, "Login with Microsoft"));
        setUsername = new GuiTextField(2, mc.fontRendererObj, this.width / 2 - 85, this.height / 2 - 80, 160, 20);
    }
    MicrosoftAuthResult result;

    private final MicrosoftAuthenticator authenticator = new MicrosoftAuthenticator();


    public void actionPerformed(GuiButton button) {
        switch (button.id) {
            case 0:
                this.setUserOffline(setUsername.getText());
                break;
            case 1:
                this.mc.displayGuiScreen(new MainMenu());
                break;
            case 3:
                try {
                    result = authenticator.loginWithWebview();
                    System.out.printf("Logged in with '%s'%n", result.getProfile().getName());
                } catch (MicrosoftAuthenticationException e) {
                    throw new RuntimeException(e);
                }
                setUserOnline();
                break;
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawDefaultBackground();
        setUsername.drawTextBox();
        FontUtil.normal.drawCenteredString( "Enter Username", (this.width / 2) - (mc.fontRendererObj.getStringWidth("Enter Username") / 2) - 5, this.height / 2 - 100, -1);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        setUsername.textboxKeyTyped(typedChar, keyCode);
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        setUsername.mouseClicked(mouseX, mouseY, mouseButton);
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    private static void setSession(Session session) {
        Minecraft.getMinecraft().session = session;
        ChatClient.INSTANCE.updateUsername(session.getUsername());
    }

    public void setUserOffline(String username) {
        Session session = new Session(username, username, "0", "legacy");
        setSession(session);
        ChatClient.INSTANCE.updateUsername(session.getUsername());
    }

    public void setUserOnline(){
        Session session = new Session(result.getProfile().getName(), result.getProfile().getId(), result.getAccessToken(), "microsoft");
        setSession(session);
        ChatClient.INSTANCE.updateUsername(session.getUsername());
    }
}