package net.minecraft.client.gui;

import java.io.IOException;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.optifine.CustomLoadingScreen;
import net.optifine.CustomLoadingScreens;
import store.femboy.spring.impl.util.PacketUtil;

public class GuiDownloadTerrain extends GuiScreen
{
    private NetHandlerPlayClient netHandlerPlayClient;
    private int progress;
    private CustomLoadingScreen customLoadingScreen = CustomLoadingScreens.getCustomLoadingScreen();

    public GuiDownloadTerrain(NetHandlerPlayClient netHandler)
    {
        this.netHandlerPlayClient = netHandler;
    }

    protected void keyTyped(char typedChar, int keyCode) throws IOException
    {
    }

    public void initGui()
    {
        this.buttonList.clear();
    }

    public void updateScreen()
    {
        if (this.progress % 20 == 0)
        {
            PacketUtil.sendSilentPacket(new C00PacketKeepAlive());
            mc.displayGuiScreen(new GuiGameOver());
        }
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    public boolean doesGuiPauseGame()
    {
        return false;
    }
}
