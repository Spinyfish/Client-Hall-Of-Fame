package dev.eternal.client.discord.command.impl;

import dev.eternal.client.Client;
import dev.eternal.client.discord.DiscordCommandException;
import dev.eternal.client.discord.DiscordEventListener;
import dev.eternal.client.discord.command.DiscordCommand;
import dev.eternal.client.discord.command.DiscordCommandInfo;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.List;

@DiscordCommandInfo(name = "help", usage = ".help", desc = "Sends a list of all commands along with their usages.")
public class DiscordHelpCommand extends DiscordCommand {

  @Override
  public void runCommand(MessageReceivedEvent messageReceivedEvent) throws DiscordCommandException {
    EmbedBuilder embedBuilder = new EmbedBuilder();
    embedBuilder.setColor(client.colour());
    List<DiscordCommand> commands = DiscordEventListener.getInstance().commands();
    commands.forEach(discordCommand -> {
      embedBuilder.addField(new MessageEmbed.Field(discordCommand.usage(), discordCommand.description(), false));
    });
    messageReceivedEvent.getChannel().sendMessageEmbeds(embedBuilder.build()).queue();
  }
}