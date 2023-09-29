package dev.eternal.client.discord.command.impl;

import dev.eternal.client.Client;
import dev.eternal.client.discord.DiscordCommandException;
import dev.eternal.client.discord.command.DiscordCommand;
import dev.eternal.client.discord.command.DiscordCommandInfo;
import dev.eternal.client.module.Module;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

@DiscordCommandInfo(name = "togglemod", usage = ".togglemod <module>", desc = "Toggled a module in the client.")
public class DiscordTogglemodCommand extends DiscordCommand {

  @Override
  public void runCommand(MessageReceivedEvent messageReceivedEvent) throws DiscordCommandException {
    final String message = messageReceivedEvent.getMessage().getContentRaw();
    final MessageChannelUnion channel = messageReceivedEvent.getChannel();

    Module module = client.moduleManager().getByName(message.split(".togglemod ")[1]);
    if (module != null) {
      module.toggle();
      channel.sendMessage("Toggled " + message.split(".togglemod ")[1] + ".").queue();
    } else {
      throw new DiscordCommandException(String.format("Module %s does not exist!",
          message.split(".togglemod ")[1]));
    }
  }
}