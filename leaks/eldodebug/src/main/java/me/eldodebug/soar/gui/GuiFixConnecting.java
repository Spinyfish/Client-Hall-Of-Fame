package me.eldodebug.soar.gui;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import me.eldodebug.soar.Soar;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiDisconnected;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.ServerAddress;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.network.NetHandlerLoginClient;
import net.minecraft.client.resources.I18n;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.handshake.client.C00Handshake;
import net.minecraft.network.login.client.C00PacketLoginStart;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;

public class GuiFixConnecting extends GuiScreen
{
    private static final AtomicInteger CONNECTION_ID = new AtomicInteger(0);
    private static final Logger logger = LogManager.getLogger();
    private NetworkManager networkManager;
    private boolean cancel;
    private final GuiScreen previousGuiScreen;

    public GuiFixConnecting(GuiScreen p_i1181_1_, Minecraft mcIn, ServerData p_i1181_3_)
    {
        this.mc = mcIn;
        this.previousGuiScreen = p_i1181_1_;
        mcIn.loadWorld(null);
        mcIn.setServerData(p_i1181_3_);
        this.connectServerData(p_i1181_3_);
    }

    public GuiFixConnecting(GuiScreen p_i1182_1_, Minecraft mcIn, String hostName, int port)
    {
        this.mc = mcIn;
        this.previousGuiScreen = p_i1182_1_;
        mcIn.loadWorld(null);
        this.connect(hostName, port);
    }

    private void connectServerData(final ServerData serverData)
    {
        (new Thread("Server Connector #" + CONNECTION_ID.incrementAndGet())
        {
            public void run()
            {
                InetAddress inetaddress = null;
                ServerAddress serveraddress = ServerAddress.fromString(serverData.serverIP);
                String ip = serveraddress.getIP();
                int port = serveraddress.getPort();
                
                try
                {
                    if (GuiFixConnecting.this.cancel)
                    {
                        return;
                    }

                    inetaddress = InetAddress.getByName(ip);
                    GuiFixConnecting.this.networkManager = NetworkManager.createNetworkManagerAndConnect(inetaddress, port, GuiFixConnecting.this.mc.gameSettings.isUsingNativeTransport());
                    GuiFixConnecting.this.networkManager.setNetHandler(new NetHandlerLoginClient(GuiFixConnecting.this.networkManager, GuiFixConnecting.this.mc, GuiFixConnecting.this.previousGuiScreen));
                    GuiFixConnecting.this.networkManager.sendPacket(new C00Handshake(47, ip, port, EnumConnectionState.LOGIN));
                    GuiFixConnecting.this.networkManager.sendPacket(new C00PacketLoginStart(GuiFixConnecting.this.mc.getSession().getProfile()));
                    
                    Soar.instance.setPlayTime(System.currentTimeMillis());
                }
                catch (UnknownHostException unknownhostexception)
                {
                    if (GuiFixConnecting.this.cancel)
                    {
                        return;
                    }

                    GuiFixConnecting.logger.error((String)"Couldn\'t connect to server", (Throwable)unknownhostexception);
                    GuiFixConnecting.this.mc.displayGuiScreen(new GuiDisconnected(GuiFixConnecting.this.previousGuiScreen, "connect.failed", new ChatComponentTranslation("disconnect.genericReason", new Object[] {"Unknown host"})));
                }
                catch (Exception exception)
                {
                    if (GuiFixConnecting.this.cancel)
                    {
                        return;
                    }

                    GuiFixConnecting.logger.error((String)"Couldn\'t connect to server", (Throwable)exception);
                    String s = exception.toString();

                    if (inetaddress != null)
                    {
                        String s1 = inetaddress.toString() + ":" + port;
                        s = s.replaceAll(s1, "");
                    }

                    GuiFixConnecting.this.mc.displayGuiScreen(new GuiDisconnected(GuiFixConnecting.this.previousGuiScreen, "connect.failed", new ChatComponentTranslation("disconnect.genericReason", new Object[] {s})));
                }
            }
        }).start();
    }
    
    private void connect(final String ip, final int port)
    {
        logger.info("Connecting to " + ip + ", " + port);
        (new Thread("Server Connector #" + CONNECTION_ID.incrementAndGet())
        {
            public void run()
            {
                InetAddress inetaddress = null;

                try
                {
                    if (GuiFixConnecting.this.cancel)
                    {
                        return;
                    }

                    inetaddress = InetAddress.getByName(ip);
                    GuiFixConnecting.this.networkManager = NetworkManager.createNetworkManagerAndConnect(inetaddress, port, GuiFixConnecting.this.mc.gameSettings.isUsingNativeTransport());
                    GuiFixConnecting.this.networkManager.setNetHandler(new NetHandlerLoginClient(GuiFixConnecting.this.networkManager, GuiFixConnecting.this.mc, GuiFixConnecting.this.previousGuiScreen));
                    GuiFixConnecting.this.networkManager.sendPacket(new C00Handshake(47, ip, port, EnumConnectionState.LOGIN));
                    GuiFixConnecting.this.networkManager.sendPacket(new C00PacketLoginStart(GuiFixConnecting.this.mc.getSession().getProfile()));
                }
                catch (UnknownHostException unknownhostexception)
                {
                    if (GuiFixConnecting.this.cancel)
                    {
                        return;
                    }

                    GuiFixConnecting.logger.error((String)"Couldn\'t connect to server", (Throwable)unknownhostexception);
                    GuiFixConnecting.this.mc.displayGuiScreen(new GuiDisconnected(GuiFixConnecting.this.previousGuiScreen, "connect.failed", new ChatComponentTranslation("disconnect.genericReason", new Object[] {"Unknown host"})));
                }
                catch (Exception exception)
                {
                    if (GuiFixConnecting.this.cancel)
                    {
                        return;
                    }

                    GuiFixConnecting.logger.error((String)"Couldn\'t connect to server", (Throwable)exception);
                    String s = exception.toString();

                    if (inetaddress != null)
                    {
                        String s1 = inetaddress.toString() + ":" + port;
                        s = s.replaceAll(s1, "");
                    }

                    GuiFixConnecting.this.mc.displayGuiScreen(new GuiDisconnected(GuiFixConnecting.this.previousGuiScreen, "connect.failed", new ChatComponentTranslation("disconnect.genericReason", new Object[] {s})));
                }
            }
        }).start();
    }

    /**
     * Called from the main game loop to update the screen.
     */
    public void updateScreen()
    {
        if (this.networkManager != null)
        {
            if (this.networkManager.isChannelOpen())
            {
                this.networkManager.processReceivedPackets();
            }
            else
            {
                this.networkManager.checkDisconnected();
            }
        }
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
    public void initGui()
    {
        this.buttonList.clear();
        this.buttonList.add(new GuiButton(0, this.width / 2 - 100, this.height / 4 + 120 + 12, I18n.format("gui.cancel")));
    }

    /**
     * Called by the controls from the buttonList when activated. (Mouse pressed for buttons)
     */
    protected void actionPerformed(GuiButton button) throws IOException
    {
        if (button.id == 0)
        {
            this.cancel = true;

            if (this.networkManager != null)
            {
                this.networkManager.closeChannel(new ChatComponentText("Aborted"));
            }

            this.mc.displayGuiScreen(this.previousGuiScreen);
        }
    }

    /**
     * Draws the screen and all the components in it. Args : mouseX, mouseY, renderPartialTicks
     */
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();

        if (this.networkManager == null)
        {
            this.drawCenteredString(this.fontRendererObj, I18n.format("connect.connecting"), this.width / 2, this.height / 2 - 50, 16777215);
        }
        else
        {
            this.drawCenteredString(this.fontRendererObj, I18n.format("connect.authorizing"), this.width / 2, this.height / 2 - 50, 16777215);
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
