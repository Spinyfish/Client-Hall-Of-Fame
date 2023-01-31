package intent.AquaDev.aqua.discord;

import net.aql.Lib;
import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL11;

public class DiscordRPCHandler {
   public static final DiscordRPCHandler instance = new DiscordRPCHandler();
   private final DiscordRPC discordRPC = new DiscordRPC();
   String serverIP;
   public static String second = "Client  " + Lib.getUID() + "";

   public DiscordRPCHandler() {
      this.serverIP = Minecraft.getMinecraft().isSingleplayer() ? "Singleplayer" : Minecraft.getMinecraft().getCurrentServerData().serverIP;
   }

   public void init() {
      if (GL11.glGetString(7937).contains("NVIDIA") || GL11.glGetString(7937).contains("AMD") || GL11.glGetString(7937).contains("RX")) {
         this.discordRPC.start();
      }
   }

   public void shutdown() {
      this.discordRPC.shutdown();
   }

   public DiscordRPC getDiscordRPC() {
      return this.discordRPC;
   }
}
