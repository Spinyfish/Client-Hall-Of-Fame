package dev.eternal.client.command.impl;

import dev.eternal.client.Client;
import dev.eternal.client.command.Command;
import dev.eternal.client.module.Module;

public class ClearBindsCommand extends Command {

  public ClearBindsCommand() {
    super("clearbinds", "Clears Binds", "cb");
  }

  @Override
  public void run(String... args) {
    client.moduleManager().stream()
        .filter(module -> !module.moduleInfo().name().equalsIgnoreCase("clickgui"))
        .forEach(module -> module.keyBind(0));
  }
}
