/* November.lol © 2023 */
package lol.november.feature.command.impl;

import java.util.Collection;
import java.util.StringJoiner;
import lol.november.November;
import lol.november.feature.command.Command;
import lol.november.feature.command.Register;
import lol.november.feature.command.exceptions.CommandInvalidArgumentException;
import lol.november.utility.chat.Printer;

/**
 * @author Gavin
 * @since 2.0.0
 */
@Register(
  aliases = { "help", "h", "commands", "cmds" },
  description = "Displays commands and their information",
  syntax = "<command alias>"
)
public class HelpCommand extends Command {

  @Override
  public void dispatch(String[] args) throws Exception {
    if (args.length == 0) {
      Collection<Command> commands = November.instance().commands().values();

      StringJoiner joiner = new StringJoiner(", ");
      for (Command command : commands) {
        joiner.add(command.getAliases()[0]);
      }

      Printer.print(
        "There are &9" + commands.size() + "&7 commands available:"
      );
      Printer.print(joiner.toString());
      return;
    }

    Command command = November.instance().commands().get(args[0]);
    if (command == null) throw new CommandInvalidArgumentException(
      "command alias",
      "No command found with alias \"" + args[0] + "\""
    );

    Printer.print("Aliases: " + String.join(", ", command.getAliases()));
    Printer.print("Description: " + command.getDescription());
    if (!command.getSyntax().isEmpty()) Printer.print(
      "Syntax: " + command.getSyntax()
    );
  }
}
