package dev.eternal.client.command.impl;

import dev.eternal.client.Client;
import dev.eternal.client.command.Command;
import dev.eternal.client.module.Module;
import dev.eternal.client.property.impl.ModeSetting;

import java.util.Arrays;
import java.util.Locale;

public class ModeCommand extends Command {

  public ModeCommand() {
    super("mode", "Sets the mode of a module to the one supplied, if it exists.", "m");
  }

  @Override
  public void run(String... args) {
    try {
      Module module = Client.singleton().moduleManager().getByName(args[0]);
      Arrays.stream(Client.singleton().propertyManager().get(module))
          .filter(property -> property instanceof ModeSetting)
          .map(property -> (ModeSetting) property)
          .forEach(property -> property.setValue(args[1].toUpperCase(Locale.ROOT)));
    } catch (Exception e) {
      error("<Module> <Mode>");
    }
  }
}
