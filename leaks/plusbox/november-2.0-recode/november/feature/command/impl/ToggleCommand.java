/* November.lol © 2023 */
package lol.november.feature.command.impl;

import lol.november.November;
import lol.november.feature.command.Command;
import lol.november.feature.command.Register;
import lol.november.feature.command.exceptions.CommandInvalidArgumentException;
import lol.november.feature.command.exceptions.CommandInvalidSyntaxException;
import lol.november.feature.module.Module;
import lol.november.utility.chat.Printer;

/**
 * @author Gavin
 * @since 2.0.0
 */
@Register(
  aliases = { "toggle", "t" },
  description = "Toggles a module on or off",
  syntax = "[module name]"
)
public class ToggleCommand extends Command {

  @Override
  public void dispatch(String[] args) throws Exception {
    if (args.length == 0) throw new CommandInvalidSyntaxException(
      "module name"
    );

    Module parsedModule = null;
    for (Module module : November.instance().modules().values()) {
      if (module.name().toLowerCase().startsWith(args[0].toLowerCase())) {
        parsedModule = module;
        break;
      }
    }

    if (parsedModule == null) throw new CommandInvalidArgumentException(
      "module name",
      "No command with name \"" + args[0] + "\" could be found"
    );

    parsedModule.setState(!parsedModule.toggled());
    Printer.print(
      parsedModule.name() +
      " toggled " +
      (parsedModule.toggled() ? "&aon" : "&coff")
    );
  }

  @Override
  public String suggest(String argument, int argumentIndex) {
    if (argumentIndex != 0) return null;

    for (Module module : November.instance().modules().values()) {
      if (module.name().toLowerCase().startsWith(argument.toLowerCase())) {
        return module.name();
      }
    }

    return null;
  }
}
