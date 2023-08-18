/* November.lol © 2023 */
package lol.november.feature.command.exec;

import java.util.Arrays;
import lol.november.feature.command.Command;
import lol.november.feature.command.CommandRegistry;
import lol.november.feature.command.exceptions.CommandInvalidArgumentException;
import lol.november.feature.command.exceptions.CommandInvalidSyntaxException;
import lol.november.listener.bus.Listener;
import lol.november.listener.bus.Subscribe;
import lol.november.listener.event.net.EventPacket;
import lol.november.utility.chat.Printer;
import lombok.extern.log4j.Log4j2;
import net.minecraft.network.play.client.C01PacketChatMessage;

/**
 * @author Gavin
 * @since 2.0.0
 */
@Log4j2
public class CommandExecutor {

  /**
   * The prefix used to execute commands
   */
  private static final String COMMAND_PREFIX = ".";

  private CommandRegistry registry;

  public CommandExecutor(CommandRegistry registry) {
    this.registry = registry;
  }

  @Subscribe
  private final Listener<EventPacket.Out> packetOut = event -> {
    if (event.get() instanceof C01PacketChatMessage packet) {
      String message = packet.getMessage();
      if (!message.startsWith(COMMAND_PREFIX)) return;

      event.setCanceled(true);

      String[] args = message
        .substring(COMMAND_PREFIX.length())
        .trim()
        .split("\\s+");
      if (args.length == 0) return;

      Command command = registry.get(args[0].toLowerCase());
      if (command == null) {
        Printer.print("No command was found with the name \"" + args[0] + "\"");
        return;
      }

      try {
        log.info("Dispatching command {}", command.getAliases()[0]);
        command.dispatch(Arrays.copyOfRange(args, 1, args.length));
      } catch (CommandInvalidSyntaxException e) {
        Printer.print("Invalid argument \"" + e.getArgument() + "\".");
        Printer.print("Valid syntax: " + command.getSyntax());
      } catch (CommandInvalidArgumentException e) {
        Printer.print("Invalid argument \"" + e.getArgument() + "\"");
        Printer.print("Details: " + e.getDetails());
      } catch (Exception e) {
        log.error("Failed to dispatch command {}", command.getAliases()[0]);
        e.printStackTrace();
        Printer.print("&cSomething went wrong, report this to the developers!");
      }
    }
  };
}
