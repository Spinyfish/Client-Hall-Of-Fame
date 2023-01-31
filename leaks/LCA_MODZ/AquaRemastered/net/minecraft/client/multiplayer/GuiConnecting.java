package net.minecraft.client.multiplayer;

import intent.AquaDev.aqua.Aqua;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.atomic.AtomicInteger;
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
import org.lwjgl.opengl.GL11;
import viamcp.ViaMCP;

public class GuiConnecting extends GuiScreen {
   private static final AtomicInteger CONNECTION_ID = new AtomicInteger(0);
   private static final Logger logger = LogManager.getLogger();
   private NetworkManager networkManager;
   private boolean cancel;
   public static boolean sentinelChecker;
   private final GuiScreen previousGuiScreen;

   public GuiConnecting(GuiScreen p_i1181_1_, Minecraft mcIn, ServerData p_i1181_3_) {
      this.mc = mcIn;
      this.previousGuiScreen = p_i1181_1_;
      ServerAddress serveraddress = ServerAddress.fromString(p_i1181_3_.serverIP);
      mcIn.loadWorld((WorldClient)null);
      mcIn.setServerData(p_i1181_3_);
      this.connect(serveraddress.getIP(), serveraddress.getPort());
   }

   public GuiConnecting(GuiScreen p_i1182_1_, Minecraft mcIn, String hostName, int port) {
      this.mc = mcIn;
      this.previousGuiScreen = p_i1182_1_;
      mcIn.loadWorld((WorldClient)null);
      this.connect(hostName, port);
   }

   private void connect(final String ip, final int port) {
      Aqua.INSTANCE.ircClient.setIngameName(Minecraft.getMinecraft().session.getUsername());
      if (this.mc.getCurrentServerData().serverIP.equalsIgnoreCase("cubecraft.net")) {
         sentinelChecker = true;
         ViaMCP.getInstance().setVersion(340);
      } else {
         sentinelChecker = false;
      }

      if (this.mc.getCurrentServerData().serverIP.contains("hypixel.net")) {
         ViaMCP.getInstance().setVersion(47);
      }

      if (this.mc.getCurrentServerData().serverIP.contains("blocksmc.com")) {
         ViaMCP.getInstance().setVersion(47);
      }

      if (this.mc.getCurrentServerData().serverIP.contains("premium.blocksmc.com")) {
         ViaMCP.getInstance().setVersion(47);
      }

      Aqua.INSTANCE.lastConnection = System.currentTimeMillis();
      String Copy = getClipboardString();
      logger.info("Connecting to " + ip + ", " + port);
      (new Thread("Server Connector #" + CONNECTION_ID.incrementAndGet()) {
            @Override
            public void run() {
               InetAddress inetaddress = null;
   
               try {
                  if (GuiConnecting.this.cancel) {
                     return;
                  }
   
                  inetaddress = InetAddress.getByName(ip);
                  GuiConnecting.this.networkManager = NetworkManager.createNetworkManagerAndConnect(
                     inetaddress, port, GuiConnecting.this.mc.gameSettings.isUsingNativeTransport()
                  );
                  GuiConnecting.this.networkManager
                     .setNetHandler(new NetHandlerLoginClient(GuiConnecting.this.networkManager, GuiConnecting.this.mc, GuiConnecting.this.previousGuiScreen));
                  GuiConnecting.this.networkManager.sendPacket(new C00Handshake(47, ip, port, EnumConnectionState.LOGIN));
                  GuiConnecting.this.networkManager.sendPacket(new C00PacketLoginStart(GuiConnecting.this.mc.getSession().getProfile()));
               } catch (UnknownHostException var5) {
                  if (GuiConnecting.this.cancel) {
                     return;
                  }
   
                  GuiConnecting.logger.error("Couldn't connect to server", (Throwable)var5);
                  GuiConnecting.this.mc
                     .displayGuiScreen(
                        new GuiDisconnected(
                           GuiConnecting.this.previousGuiScreen, "connect.failed", new ChatComponentTranslation("disconnect.genericReason", "Unknown host")
                        )
                     );
               } catch (Exception var6) {
                  if (GuiConnecting.this.cancel) {
                     return;
                  }
   
                  GuiConnecting.logger.error("Couldn't connect to server", (Throwable)var6);
                  String s = var6.toString();
                  if (inetaddress != null) {
                     String s1 = inetaddress.toString() + ":" + port;
                     s = s.replaceAll(s1, "");
                  }
   
                  GuiConnecting.this.mc
                     .displayGuiScreen(
                        new GuiDisconnected(
                           GuiConnecting.this.previousGuiScreen, "connect.failed", new ChatComponentTranslation("disconnect.genericReason", s)
                        )
                     );
               }
            }
         })
         .start();
   }

   @Override
   public void updateScreen() {
      if (this.networkManager != null) {
         if (this.networkManager.isChannelOpen()) {
            this.networkManager.processReceivedPackets();
         } else {
            this.networkManager.checkDisconnected();
         }
      }
   }

   @Override
   protected void keyTyped(char typedChar, int keyCode) throws IOException {
   }

   @Override
   public void initGui() {
      this.buttonList.clear();
      this.buttonList.add(new GuiButton(0, width / 2 - 100, height / 4 + 120 + 12, I18n.format("gui.cancel")));
   }

   @Override
   protected void actionPerformed(GuiButton button) throws IOException {
      if (button.id == 0) {
         this.cancel = true;
         if (this.networkManager != null) {
            this.networkManager.closeChannel(new ChatComponentText("Aborted"));
         }

         this.mc.displayGuiScreen(this.previousGuiScreen);
      }
   }

   @Override
   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      if (!GL11.glGetString(7937).contains("AMD") && !GL11.glGetString(7937).contains("RX ")) {
         Aqua.INSTANCE.shaderBackgroundMM.renderShader();
      } else {
         this.drawDefaultBackground();
      }

      if (this.networkManager == null) {
         Aqua.INSTANCE.comfortaa3.drawCenteredString(I18n.format("connect.connecting"), (float)(width / 2), (float)(height / 2 - 50), 16777215);
      } else {
         Aqua.INSTANCE.comfortaa3.drawCenteredString(I18n.format("connect.authorizing"), (float)(width / 2), (float)(height / 2 - 50), 16777215);
      }

      super.drawScreen(mouseX, mouseY, partialTicks);
   }
}
