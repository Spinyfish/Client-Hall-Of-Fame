/* November.lol © 2023 */
package lol.november.feature.command.impl;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;
import lol.november.November;
import lol.november.feature.command.Command;
import lol.november.feature.command.Register;
import lol.november.listener.bus.Listener;
import lol.november.listener.bus.Subscribe;
import lol.november.listener.event.net.EventPacket;
import lol.november.utility.chat.Printer;
import lol.november.utility.math.timer.Timer;
import net.minecraft.network.play.client.C14PacketTabComplete;
import net.minecraft.network.play.server.S3APacketTabComplete;

/**
 * @author Gavin
 * @since 2.0.0
 */
@Register(
  aliases = { "plugins", "serverplugins" },
  description = "Allows you to see the plugins a server is using",
  syntax = "<custom command>"
)
public class PluginsCommand extends Command {

  private static final Pattern PLUGIN_REGEX = Pattern.compile("[A-Z0-9]\\w+");

  private final Timer timeoutTimer = new Timer();
  private boolean listening;

  public PluginsCommand() {
    November.bus().subscribe(this);
  }

  @Subscribe
  private final Listener<EventPacket.In> packetIn = event -> {
    if (!listening) return;

    if (timeoutTimer.passed(10_000L)) {
      Printer.print("Server took too long to respond with a tab complete");
      listening = false;
      return;
    }

    if (event.get() instanceof S3APacketTabComplete packet) {
      String[] suggestions = packet.func_149630_c();

      listening = false;

      if (suggestions.length == 0) {
        Printer.print("This server has no visible plugins");
        return;
      }

      Set<String> plugins = new HashSet<>();
      for (String result : suggestions) {
        if (result.contains(":")) {
          String[] split = result.split(":");
          String pluginName = split[0].replaceAll("\\s*", "").replace("/", "");

          if (!pluginName.isEmpty() && !pluginName.equals("minecraft")) {
            plugins.add(pluginName);
          }
        } else {
          if (
            result.matches(PLUGIN_REGEX.pattern()) && !result.startsWith("/")
          ) {
            plugins.add(result);
          }
        }
      }

      if (plugins.isEmpty()) {
        Printer.print("No plugins were able to be parsed");
        return;
      }

      Printer.print("There was &9" + plugins.size() + "&7 found:");
      Printer.print(String.join(", ", plugins));
    }
  };

  @Override
  public void dispatch(String[] args) throws Exception {
    String value = "/";
    if (args.length != 0) {
      value += String.join(" ", args);
    }

    timeoutTimer.reset();
    listening = true;
    mc.thePlayer.sendQueue.addToSendQueue(new C14PacketTabComplete(value));
  }
}
