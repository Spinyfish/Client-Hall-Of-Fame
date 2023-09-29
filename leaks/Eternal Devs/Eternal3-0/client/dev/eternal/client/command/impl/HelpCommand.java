package dev.eternal.client.command.impl;

import dev.eternal.client.Client;
import dev.eternal.client.command.Command;
import dev.eternal.client.command.CommandManager;

public class HelpCommand extends Command {
  public HelpCommand() {
    super("help", "Outputs the client commands", "commands", "h", "?", "cmds", "command", "cmd");
  }

  private final Client client = Client.singleton();

  @Override
  public void run(String... args) {
    CommandManager manager = client.commandManager();
    client.displayMessage("Currently available commands: " + manager.size());
    for (Command command : manager) {
      client.displayMessage(String.format("%s - %s", command.name(), command.description()));
    }
  }
}
