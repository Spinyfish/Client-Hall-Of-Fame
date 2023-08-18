/* November.lol © 2023 */
package lol.november.feature.module.impl.player;

import lol.november.November;
import lol.november.feature.module.Category;
import lol.november.feature.module.Module;
import lol.november.feature.module.Register;
import lol.november.listener.bus.Listener;
import lol.november.listener.bus.Subscribe;
import lol.november.listener.event.net.EventPacket;
import lol.november.utility.chat.Printer;
import net.minecraft.network.play.client.C01PacketChatMessage;

/**
 * @author Gavin
 * @since 2.0.0
 */
@Register(
  name = "IRCChat",
  description = "lets you talk to other retarded november users",
  category = Category.PLAYER
)
public class IRCChatModule extends Module {

  /**
   * The IRC chat prefix
   */
  private static final String IRC_PREFIX = "#";

  @Override
  public void enable() {
    super.enable();
    Printer.irc("Talk in IRC chat with the prefix " + IRC_PREFIX);
  }

  @Subscribe
  private final Listener<EventPacket.Out> packetOut = event -> {
    if (event.get() instanceof C01PacketChatMessage packet) {
      if (!packet.getMessage().startsWith(IRC_PREFIX)) return;

      event.setCanceled(true);

      November
        .socket()
        .sendIrcChat(packet.getMessage().substring(IRC_PREFIX.length()));
    }
  };
}
