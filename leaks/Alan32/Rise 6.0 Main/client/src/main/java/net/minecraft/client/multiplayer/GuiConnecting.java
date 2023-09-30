package net.minecraft.client.multiplayer;

import by.radioegor146.nativeobfuscator.Native;
import com.alan.clients.Client;
import com.alan.clients.component.impl.player.LastConnectionComponent;
import com.alan.clients.newevent.Listener;
import com.alan.clients.newevent.annotations.EventLink;
import com.alan.clients.newevent.impl.other.BackendPacketEvent;
import com.alan.clients.newevent.impl.other.ServerJoinEvent;
import com.alan.clients.util.player.ServerUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiDisconnected;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.network.NetHandlerLoginClient;
import net.minecraft.client.resources.I18n;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.handshake.client.C00Handshake;
import net.minecraft.network.login.client.C00PacketLoginStart;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import packet.Packet;
import packet.impl.client.protection.lIlIlIlIIIlllIIlIlIIIlllIIlllIlIIIlllIlI;
import packet.impl.server.protection.lIllIlIlIlIIIlllIIIlllIIIIlllI;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.atomic.AtomicInteger;

@Native
public class GuiConnecting extends GuiScreen {
    private static final AtomicInteger CONNECTION_ID = new AtomicInteger(0);
    private static final Logger logger = LogManager.getLogger();
    private NetworkManager networkManager;
    private boolean cancel;
    private final GuiScreen previousGuiScreen;
    private static boolean joinServer, registered = false;
    public static boolean a;

    public GuiConnecting(final GuiScreen p_i1181_1_, final Minecraft mcIn, final ServerData p_i1181_3_) {
        joinServer = true;
        this.previousGuiScreen = p_i1181_1_;
        final ServerAddress serveraddress = ServerAddress.func_78860_a(p_i1181_3_.serverIP);
        mcIn.loadWorld(null);
        mcIn.setServerData(p_i1181_3_);
        this.connect(serveraddress.getIP(), serveraddress.getPort());

        Client.INSTANCE.getEventBus().register(this);
    }

    public GuiConnecting(final GuiScreen p_i1182_1_, final Minecraft mcIn, final String hostName, final int port) {
        joinServer = true;
        this.previousGuiScreen = p_i1182_1_;
        mcIn.loadWorld(null);
        this.connect(hostName, port);

        Client.INSTANCE.getEventBus().register(this);
    }

    private void connect(String ip, int port) {

        final ServerJoinEvent event = new ServerJoinEvent(ip, port);
        Client.INSTANCE.getEventBus().handle(event);

        logger.info("Connecting to " + ip + ", " + port);

        try {
            Client.INSTANCE.getNetworkManager().getCommunication().write(new lIlIlIlIIIlllIIlIlIIIlllIIlllIlIIIlllIlI(ip, port));
        } catch (Exception exception) {
            System.out.println("Not connected to the server");
        }
    }

    @EventLink
    public final Listener<BackendPacketEvent> onBackend = event -> {
        Packet packet = event.getPacket();

        if (!a) {
            return;
        }

        if (!(packet instanceof lIllIlIlIlIIIlllIIIlllIIIIlllI)) return;

        if (!joinServer) {
            System.out.println("Cancelled Packet");
            return;
        }

        joinServer = false;

        final String finalIp = ((lIllIlIlIlIIIlllIIIlllIIIIlllI) packet).getLIlIlIIIlllIlIIIlllIlIlIIIllIlIIIlllIllI();
        final int finalPort = ((lIllIlIlIlIIIlllIIIlllIIIIlllI) packet).getLIlIlIIIlllIllIlIIIlllIIIIlIlIIIlllIlllI();

        LastConnectionComponent.ip = finalIp;
        LastConnectionComponent.port = finalPort;

        System.out.println("Received Packet IP: " + finalIp + " Port: " + finalPort);

        ServerUtil.cachedServers.clear();
        (new Thread("Server Connector #" + CONNECTION_ID.incrementAndGet()) {
            public void run() {
                InetAddress inetaddress = null;

                try {
                    if (GuiConnecting.this.cancel) {
                        return;
                    }

                    inetaddress = InetAddress.getByName(finalIp);
                    GuiConnecting.this.networkManager = NetworkManager.func_181124_a(inetaddress, finalPort, GuiConnecting.this.mc.gameSettings.func_181148_f());
                    GuiConnecting.this.networkManager.setNetHandler(new NetHandlerLoginClient(GuiConnecting.this.networkManager, GuiConnecting.this.mc, GuiConnecting.this.previousGuiScreen));
                    GuiConnecting.this.networkManager.sendPacket(new C00Handshake(47, finalIp, finalPort, EnumConnectionState.LOGIN));
                    GuiConnecting.this.networkManager.sendPacket(new C00PacketLoginStart(GuiConnecting.this.mc.getSession().getProfile()));
                } catch (final UnknownHostException unknownhostexception) {
                    if (GuiConnecting.this.cancel) {
                        return;
                    }

                    GuiConnecting.logger.error("Couldn't connect to server", unknownhostexception);
                    GuiConnecting.this.mc.displayGuiScreen(new GuiDisconnected(GuiConnecting.this.previousGuiScreen, "connect.failed", new ChatComponentTranslation("disconnect.genericReason", "Unknown host")));
                } catch (final Exception exception) {
                    if (GuiConnecting.this.cancel) {
                        return;
                    }

                    GuiConnecting.logger.error("Couldn't connect to server", exception);
                    String s = exception.toString();

                    if (inetaddress != null) {
                        final String s1 = inetaddress + ":" + finalPort;
                        s = s.replaceAll(s1, "");
                    }

                    GuiConnecting.this.mc.displayGuiScreen(new GuiDisconnected(GuiConnecting.this.previousGuiScreen, "connect.failed", new ChatComponentTranslation("disconnect.genericReason", s)));
                }
            }
        }).start();
    };

    /**
     * Called from the main game loop to update the screen.
     */
    public void updateScreen() {
        if (this.networkManager != null) {
            if (this.networkManager.isChannelOpen()) {
                this.networkManager.processReceivedPackets();
            } else {
                this.networkManager.checkDisconnected();
            }
        }
    }

    /**
     * Fired when a key is typed (except F11 which toggles full screen). This is the equivalent of
     * KeyListener.keyTyped(KeyEvent e). Args : character (character on the key), keyCode (lwjgl Keyboard key code)
     */
    protected void keyTyped(final char typedChar, final int keyCode) throws IOException {
    }

    /**
     * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
     * window resizes, the buttonList is cleared beforehand.
     */
    public void initGui() {
        this.buttonList.clear();
        this.buttonList.add(new GuiButton(0, this.width / 2 - 100, this.height / 4 + 120 + 12, I18n.format("gui.cancel")));
    }

    /**
     * Called by the controls from the buttonList when activated. (Mouse pressed for buttons)
     */
    protected void actionPerformed(final GuiButton button) throws IOException {
        if (button.id == 0) {
            this.cancel = true;

            if (this.networkManager != null) {
                this.networkManager.closeChannel(new ChatComponentText("Aborted"));
            }

            this.mc.displayGuiScreen(this.previousGuiScreen);
        }
    }

    @Override
    public void onGuiClosed() {
        Client.INSTANCE.getEventBus().unregister(this);
    }

    /**
     * Draws the screen and all the components in it. Args : mouseX, mouseY, renderPartialTicks
     */
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
        this.drawDefaultBackground();

        if (this.networkManager == null) {
            this.drawCenteredString(this.fontRendererObj, I18n.format("connect.connecting"), this.width / 2, this.height / 2 - 50, 16777215);
        } else {
            this.drawCenteredString(this.fontRendererObj, I18n.format("connect.authorizing"), this.width / 2, this.height / 2 - 50, 16777215);
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
