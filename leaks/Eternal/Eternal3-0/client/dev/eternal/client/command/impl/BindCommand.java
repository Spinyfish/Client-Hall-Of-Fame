package dev.eternal.client.command.impl;

import dev.eternal.client.Client;
import dev.eternal.client.command.Command;
import org.lwjgl.input.Keyboard;

public class BindCommand extends Command {

  public BindCommand() {
    super("bind", "Binds a module to the specified key", "b", "setbind");
  }

  @Override
  public void run(String... args) {
    try {
      final Client client = Client.singleton();
      client.moduleManager().getByName(args[0]).keyBind(Keyboard.getKeyIndex(args[1].toUpperCase()));
      client.displayMessage(String.format("Successfully bound %s to %s", args[0], Keyboard.getKeyName(Keyboard.getKeyIndex(args[1].toUpperCase()))));
    } catch (Exception e) {
      client.displayMessage("Usage: <p> bind [module] [key]");
    }
  }
}
