package dev.eternal.client.command.impl;

import dev.eternal.client.Client;
import dev.eternal.client.command.Command;
import dev.eternal.client.util.movement.MovementUtil;

public class VClipCommand extends Command {
  public VClipCommand() {
    super("vclip", "Teleports up a set amount of blocks.", "vc");
  }

  @Override
  public void run(String... args) {
    try {
      MovementUtil.setVClip(Double.parseDouble(args[0]));
    } catch (Exception e) {
      error("<Amount>");
    }
  }
}
