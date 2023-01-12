package dev.eternal.client.command.impl;

import dev.eternal.client.Client;
import dev.eternal.client.command.Command;
import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldInfo;

public class FixWorldCommand extends Command {
  public FixWorldCommand() {
    super("fixworld", "Fixes the World (Day Time, and Weather)", "fw", "wf");
  }

  @Override
  public void run(String... args) {
    World world = MinecraftServer.getServer().worldServers[0];
    WorldInfo worldinfo = world.getWorldInfo();
    if (Minecraft.getMinecraft().isSingleplayer()) {
      worldinfo.setWorldTime(0);
      worldinfo.setCleanWeatherTime(args == null ? 10000 : Integer.parseInt(args[1]));
      worldinfo.setRainTime(0);
      worldinfo.setThunderTime(0);
      worldinfo.setRaining(false);
      worldinfo.setThundering(false);
      Client.singleton().displayMessage("Set time to Day and Cleared the Weather.");
    } else {
      Client.singleton().displayMessage("You are not in Singleplayer.");
    }
  }
}
