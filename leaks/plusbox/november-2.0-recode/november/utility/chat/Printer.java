/* November.lol © 2023 */
package lol.november.utility.chat;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;

/**
 * @author Gavin
 * @since 2.0.0
 */
public class Printer {

  /**
   * The {@link Minecraft} game instance
   */
  private static final Minecraft mc = Minecraft.getMinecraft();

  /**
   * Prints a prefixed IRC client chat message
   *
   * @param message the message from the IRC chat
   */
  public static void irc(String message) {
    sendPrefixed("&cIRC", message);
  }

  /**
   * Prints a prefixed november client chat message
   *
   * @param message the message
   */
  public static void print(String message) {
    sendPrefixed("&9November", message);
  }

  /**
   * Sends a prefixed client chat message
   *
   * @param prefix  the prefix of the message
   * @param content the content of the message
   */
  private static void sendPrefixed(String prefix, String content) {
    mc.ingameGUI
      .getChatGUI()
      .printChatMessage(
        new ChatComponentText("")
          .appendText(replaceColorCodes(prefix))
          .setChatStyle(new ChatStyle().setColor(EnumChatFormatting.GRAY))
          .appendText(" > ")
          .appendText(replaceColorCodes(content))
      );
  }

  /**
   * Replaces all &<color code> with the SECTION symbol
   *
   * @param text the text to replace
   * @return the parsed & "fixed" text
   */
  private static String replaceColorCodes(String text) {
    return text.replaceAll("&", "\u00A7");
  }
}
