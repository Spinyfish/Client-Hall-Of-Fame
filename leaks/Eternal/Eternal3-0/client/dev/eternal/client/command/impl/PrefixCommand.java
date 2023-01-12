package dev.eternal.client.command.impl;

import dev.eternal.client.Client;
import dev.eternal.client.command.Command;

public class PrefixCommand extends Command {
  public PrefixCommand() {
    super("prefix", "Sets the command prefix", "p", "pref", "pf", "pfix");
  }

  @Override
  public void run(String... args) {
    Client.singleton().commandManager().prefix(args[0]);
    Client.singleton().displayMessage("Successfully changed the prefix!");
  }
}
