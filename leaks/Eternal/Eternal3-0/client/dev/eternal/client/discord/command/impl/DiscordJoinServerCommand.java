package dev.eternal.client.discord.command.impl;

import dev.eternal.client.discord.DiscordCommandException;
import dev.eternal.client.discord.command.DiscordCommand;
import dev.eternal.client.discord.command.DiscordCommandInfo;
import dev.eternal.client.ui.mainmenu.GUIMainMenu;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.client.multiplayer.ServerData;

@DiscordCommandInfo(name = "joinserver", usage = ".joinserver <server ip>", desc = "Allows you to connect to servers through Discord.")
public class DiscordJoinServerCommand extends DiscordCommand {

  @Override
  public void runCommand(MessageReceivedEvent messageReceivedEvent) throws DiscordCommandException {
    final String message = messageReceivedEvent.getMessage().getContentRaw();

    if (!message.contains(" "))
      throw new DiscordCommandException("No server IP?");

    final String serverIP = message.split(" ")[1];

    mc.addScheduledTask(() -> mc.displayGuiScreen(new GuiConnecting(new GUIMainMenu(), mc, new ServerData("server", serverIP, false))));
  }
}