package dev.eternal.client.discord.command.impl;

import dev.eternal.client.discord.command.DiscordCommand;
import dev.eternal.client.discord.command.DiscordCommandInfo;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

@DiscordCommandInfo(name = "text", usage = ".text <message>", desc = "Sends a chat message in Minecraft.")
public class DiscordTextCommand extends DiscordCommand {

  @Override
  public void runCommand(MessageReceivedEvent messageReceivedEvent) {
    final String args = messageReceivedEvent.getMessage().getContentRaw().split(".text ")[1];
    mc.thePlayer.sendChatMessage("[BOT:" + messageReceivedEvent.getAuthor().getAsTag() + "] - " + args);
    messageReceivedEvent.getChannel().sendMessage("Sent message.").queue();
  }
}
