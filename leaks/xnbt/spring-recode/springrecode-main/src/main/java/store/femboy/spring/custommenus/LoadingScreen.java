package store.femboy.spring.custommenus;

import gay.sukumi.irc.ChatClient;
import gay.sukumi.irc.profile.UserProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import store.femboy.spring.Spring;
import store.femboy.spring.impl.util.FontUtil;
import store.femboy.spring.impl.util.RenderUtil;

import java.awt.*;
import java.net.InetSocketAddress;

public class LoadingScreen extends GuiScreen {

    public static boolean startInit = false;

    @Override
    public void initGui() {
        super.initGui();
    }

    @Override
    public void updateScreen() {
        ChatClient.INSTANCE.connect(new InetSocketAddress("irc.sukumi.gay", 8888), LoginGUI.ircUser, LoginGUI.ircPass, Minecraft.getMinecraft().session.getUsername(), UserProfile.Client.SPRING);
        try{
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        if(ChatClient.INSTANCE.isConnected()){
            Spring.INSTANCE.init.run();
            mc.displayGuiScreen(new MainMenu());
        }
        super.updateScreen();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        RenderUtil.drawRect(0,0, this.width, this.height, new Color(20,20,20).getRGB());
        FontUtil.nunitobold22.drawString("Loading, Please wait. (If the client gets stuck here, try restarting. If that doesn't work please contact support)", 3, 3, -1);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
