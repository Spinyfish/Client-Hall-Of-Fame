package dev.eternal.client.command.impl;

import dev.eternal.client.Client;
import dev.eternal.client.command.Command;
import dev.eternal.client.util.movement.MovementUtil;

public class HClipCommand extends Command {
  public HClipCommand() {
    super("hclip", "Teleports forward a set amount of blocks.", "hc");
  }

  @Override
  public void run(String... args) {
    try {
      MovementUtil.setHClip(Double.parseDouble(args[0]));
    } catch (Exception e) {
      error("<Amount>");
    }
  }
}
