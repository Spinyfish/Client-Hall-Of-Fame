package dev.eternal.client.discord.command.impl;

import dev.eternal.client.discord.command.DiscordCommand;
import dev.eternal.client.discord.command.DiscordCommandInfo;
import dev.eternal.client.ui.notification.Notification;
import dev.eternal.client.ui.notification.NotificationManager;
import dev.eternal.client.ui.notification.NotificationType;
import dev.eternal.client.util.files.FileUtils;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.utils.FileUpload;
import net.minecraft.util.ScreenShotHelper;

import java.io.File;

@DiscordCommandInfo(name = "screenshot", usage = ".screenshot", desc = "Takes a screenshot of the game and sends it in Discord.")
public class DiscordScreenshotCommand extends DiscordCommand {

  @Override
  public void runCommand(MessageReceivedEvent messageReceivedEvent) {
    final File ssLocation = FileUtils.getEternalFolder();
    mc.addScheduledTask(() -> {
      ScreenShotHelper.saveScreenshot(ssLocation, "temp.png", mc.displayWidth, mc.displayHeight, mc.getFramebuffer());
      messageReceivedEvent.getChannel().sendFiles(FileUpload.fromData(FileUtils.getFileFromFolder("screenshots", "temp.png"))).queue();
      NotificationManager.pushNotification(new Notification("Screenshot Taken...", "Someone took a screenshot using the Eternal Discord bot.", 5000, NotificationType.SUCCESS));
    });
  }

}