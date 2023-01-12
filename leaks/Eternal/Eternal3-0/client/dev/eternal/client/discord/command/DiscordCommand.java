package dev.eternal.client.discord.command;

import dev.eternal.client.Client;
import dev.eternal.client.discord.DiscordCommandException;
import lombok.Getter;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.minecraft.client.Minecraft;

@Getter
public abstract class DiscordCommand {

  private final String name;
  private final String usage;
  private final String description;
  protected final Minecraft mc = Minecraft.getMinecraft();
  protected final Client client = Client.singleton();

  public DiscordCommand() {
    DiscordCommandInfo commandInfo = getClass().getAnnotation(DiscordCommandInfo.class);
    this.name = commandInfo.name();
    this.usage = commandInfo.usage();
    this.description = commandInfo.desc();
  }

  public abstract void runCommand(MessageReceivedEvent messageReceivedEvent) throws DiscordCommandException;

}
