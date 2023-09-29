package wtf.automn.discord;


import wtf.automn.Automn;

public class DiscordRPCHandler {

  public static final DiscordRPCHandler instance = new DiscordRPCHandler();
  public static String second = Automn.NAME + " " + Automn.BUILD;
  private final DiscordRPC discordRPC = new DiscordRPC();

  public void init() {
    this.discordRPC.start();
  }

  public void shutdown() {
    this.discordRPC.shutdown();
  }

  public DiscordRPC getDiscordRPC() {
    return this.discordRPC;
  }
}
