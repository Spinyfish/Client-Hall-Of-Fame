package dev.eternal.client.command.impl;

import dev.eternal.client.Client;
import dev.eternal.client.command.Command;
import dev.eternal.client.module.Module;

import java.util.Optional;

public class ToggleCommand extends Command {

  public ToggleCommand() {
    super("toggle", "Toggles a said module", "t");
  }

  @Override
  public void run(String... args) {
    try {
      Optional<Module> module = client.moduleManager().stream().filter(module1 -> module1.moduleInfo().name().equalsIgnoreCase(args[0])).findFirst();
      client.moduleManager().stream().filter(module1 -> module1.moduleInfo().name().equalsIgnoreCase(args[0])).findFirst().ifPresent(Module::toggle);
      assert module.isPresent();
      client.displayMessage(String.format("Toggled %s.", module.orElse(null).moduleInfo().name()));
    } catch (Exception e) {
      error("<Module>");
    }
  }
}
