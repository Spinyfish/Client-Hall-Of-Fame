package dev.eternal.client.discord;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;

public class DiscordBot {

  private JDA jda;
  private final String token;

  //MTAwMTUxMTMyNTY4OTcxMjY0MA.G6gysr.s5P64cvIeQDrArX0NKvHcst3-sJU132JRIqono
  public DiscordBot(String botToken) {
    this.token = botToken;
  }

  public void build() {
    this.jda = JDABuilder
        .createLight(token, GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MEMBERS)
        .enableIntents(GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MEMBERS)
        .setActivity(Activity.playing("Eternal Client"))
        .addEventListeners(new DiscordEventListener())
        .build();
  }

}
