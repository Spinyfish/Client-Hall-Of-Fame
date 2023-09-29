package dev.eternal.client.module.impl.player;

import dev.eternal.client.event.Subscribe;;
import dev.eternal.client.event.events.EventUpdate;
import dev.eternal.client.module.Module;
import dev.eternal.client.module.api.ModuleInfo;
import dev.eternal.client.util.pathfinder.PathInfo;
import dev.eternal.client.util.pathfinder.Pather;

@ModuleInfo(name = "AutoMinecraft", description = "Plays the game for you.", category = Module.Category.PLAYER)
public class AutoMinecraft extends Module {

  private PathInfo currentPath;

  @Subscribe
  public void onUpdate(EventUpdate eventUpdate) {

  }

}