/* November.lol © 2023 */
package lol.november.utility.net;

import net.minecraft.client.Minecraft;
import net.minecraft.network.Packet;

/**
 * @author Gavin
 * @since 2.0.0
 */
public class PacketUtils {

  /**
   * The {@link Minecraft} game instance
   */
  private static final Minecraft mc = Minecraft.getMinecraft();

  /**
   * Sends a packet a set number of times
   *
   * @param packet the {@link Packet} to send
   * @param times  the amount of times to add the packet to the send queue
   */
  public static void repeated(Packet<?> packet, int times) {
    for (int i = 0; i < times; ++i) {
      mc.thePlayer.sendQueue.addToSendQueue(packet);
    }
  }
}
