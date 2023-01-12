package dev.eternal.client.discord.command.impl;

import dev.eternal.client.discord.DiscordCommandException;
import dev.eternal.client.discord.command.DiscordCommand;
import dev.eternal.client.discord.command.DiscordCommandInfo;
import dev.eternal.client.util.pathfinder.PathInfo;
import dev.eternal.client.util.pathfinder.PathUtil;
import dev.eternal.client.util.pathfinder.Pather;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.minecraft.entity.Entity;
import net.minecraft.util.BlockPos;

import java.util.Optional;

@DiscordCommandInfo(name = "gotoplayer", usage = ".gotoplayer <player>", desc = "Paths to the given player.")
public class DiscordGotoplayerCommand extends DiscordCommand {

  @Override
  public void runCommand(MessageReceivedEvent messageReceivedEvent) throws DiscordCommandException {
    final String message = messageReceivedEvent.getMessage().getContentRaw();
    final MessageChannelUnion channel = messageReceivedEvent.getChannel();

    final String args = message.split(".gotoplayer ")[1];
    Optional<Entity> entityOptional = mc.theWorld.loadedEntityList.stream()
        .filter(entity -> entity.getName().equalsIgnoreCase(args))
        .findFirst();

    if (entityOptional.isEmpty()) {
      throw new DiscordCommandException("Entity not found");
    }

    BlockPos blockPos = new BlockPos(entityOptional.get());

    PathInfo pathInfo = Pather.getPath(blockPos);

    if (pathInfo.pathList().isEmpty()) {
      throw new DiscordCommandException("Unable to path");
    }

    PathUtil.getSingleton().doPath(pathInfo);
  }
}