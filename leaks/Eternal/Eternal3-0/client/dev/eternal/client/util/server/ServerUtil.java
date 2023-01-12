package dev.eternal.client.util.server;

import dev.eternal.client.util.IMinecraft;

public class ServerUtil implements IMinecraft {

  public static boolean isOnServer(String ip) {
    return !mc.isSingleplayer() && mc.getCurrentServerData().serverIP.contains(ip);
  }

}
