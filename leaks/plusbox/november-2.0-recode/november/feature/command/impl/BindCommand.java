/* November.lol © 2023 */
package lol.november.feature.command.impl;

import lol.november.November;
import lol.november.feature.command.Command;
import lol.november.feature.command.Register;
import lol.november.feature.command.exceptions.CommandInvalidArgumentException;
import lol.november.feature.command.exceptions.CommandInvalidSyntaxException;
import lol.november.feature.keybind.KeyBindType;
import lol.november.feature.module.Module;
import lol.november.utility.chat.Printer;
import org.lwjgl.input.Keyboard;

/**
 * @author Gavin
 * @since 2.0.0
 */
@Register(
  aliases = { "bind", "setbind", "keybind", "key" },
  description = "Binds a module to a key",
  syntax = "[module name] [key code] <device type>"
)
public class BindCommand extends Command {

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

    if (args.length == 1) throw new CommandInvalidSyntaxException("key code");

    int keyCode = Keyboard.getKeyIndex(args[1].toUpperCase());
    KeyBindType type = KeyBindType.KEYBOARD;

    if (args.length == 3) {
      String last = args[2];
      type = KeyBindType.from(args[2]);
      if (type == null) throw new CommandInvalidArgumentException(
        "device type",
        "\"" + last + "\" is not a valid device"
      );
    }

    parsedModule.getKeyBind().getValue().setType(type);
    parsedModule.getKeyBind().getValue().setKey(keyCode);

    Printer.print("Set " + parsedModule.name() + "'s bind to " + Keyboard.getKeyName(keyCode));
  }
}
