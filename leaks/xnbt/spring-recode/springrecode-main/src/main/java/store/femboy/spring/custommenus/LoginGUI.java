package store.femboy.spring.custommenus;

import de.datasecs.hydra.shared.handler.Session;
import gay.sukumi.irc.ChatClient;
import gay.sukumi.irc.packet.packet.impl.login.LoginSuccessPacket;
import gay.sukumi.irc.profile.UserProfile;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import store.femboy.spring.Spring;
import store.femboy.spring.impl.util.RenderUtil;

import java.awt.*;
import java.io.IOException;

public class LoginGUI extends GuiScreen {

    public GuiTextField clientUsername;
    public GuiTextField password;

    public static String ircUser, ircPass;

    @Override
    public void initGui() {
        super.initGui();
        this.buttonList.add(new GuiButton(0, this.width / 2 - 50, this.height / 2 - 22, 100, 20, "Login"));
        this.buttonList.add(new GuiButton(1, this.width / 2 - 50, this.height / 2 - 0, 100, 20, "Quit"));
        clientUsername = new GuiTextField(2, mc.fontRendererObj, this.width / 2 - 85, this.height / 2 - 110, 160, 20);
        password = new GuiTextField(3, mc.fontRendererObj, this.width / 2 - 85, this.height / 2 - 80, 160, 20);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        RenderUtil.drawRect(0,0,this.width,this.height,new Color(20,20,20).getRGB());
        clientUsername.drawTextBox();
        password.drawTextBox();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
    protected void actionPerformed(GuiButton button) {
        switch(button.id){
            case 0:
                ircUser = clientUsername.getText();
                ircPass = password.getText();
                mc.displayGuiScreen(new LoadingScreen());
                break;
            case 1:
                this.mc.shutdown();
                break;
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        clientUsername.textboxKeyTyped(typedChar, keyCode);
        password.textboxKeyTyped(typedChar, keyCode);
        super.keyTyped(typedChar, keyCode);
    }
    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        clientUsername.mouseClicked(mouseX, mouseY, mouseButton);
        password.mouseClicked(mouseX, mouseY, mouseButton);
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }
}
