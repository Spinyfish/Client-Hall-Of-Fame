package wtf.automn.utils.minecraft;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import wtf.automn.Automn;

public final class ChatUtil {

  public static void sendChatMessage(final String message) {
    Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(message));
  }

  public static void sendChatMessageWithPrefix(final String message) {
    Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("§7[§f" + Automn.NAME + "§7]§f " + message));
  }

  public static void messageWithoutPrefix(final String message) {
    Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(message));
  }

  public static void sendChatInfo(final String string) {
    Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("§7[§f" + Automn.NAME + "§7]§a " + string));
  }

  public static void sendChatError(final String message) {
    Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("§7[§f" + Automn.NAME + "§7]§c " + message));
  }
}