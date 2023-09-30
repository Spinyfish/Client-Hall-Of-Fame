package dev.eternal.client.discord;

import dev.eternal.client.event.Subscribe;
import dev.eternal.client.Client;
import dev.eternal.client.discord.command.DiscordCommand;
import dev.eternal.client.discord.command.DiscordCommandInfo;
import dev.eternal.client.event.events.EventPacket;
import dev.eternal.client.ui.notification.Notification;
import dev.eternal.client.ui.notification.NotificationManager;
import dev.eternal.client.ui.notification.NotificationType;
import lombok.Getter;
import lombok.SneakyThrows;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.server.S02PacketChat;
import org.jetbrains.annotations.NotNull;
import org.reflections.Reflections;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Getter
public class DiscordEventListener extends ListenerAdapter {

  private static DiscordEventListener instance;
  private final List<DiscordCommand> commands = new ArrayList<>();
  private MessageChannelUnion lastUsedChannel;
  private final List<String> messages = new ArrayList<>();

  public DiscordEventListener() {
    instance = this;
    new Reflections("dev")
        .getTypesAnnotatedWith(DiscordCommandInfo.class)
        .stream().map(this::newInstance)
        .forEach(commands::add);
    Client.singleton().eventBus().register(this);
  }

  @Subscribe
  public void onPacket(EventPacket eventPacket) {
    if (eventPacket.getPacket() instanceof S02PacketChat s02) {
      if (s02.isChat()) messages.add(s02.getChatComponent().getUnformattedText());
      if (lastUsedChannel != null && messages.size() >= 20) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(Client.singleton().colour());
        messages.stream().map(s -> new MessageEmbed.Field(s, "", false)).forEach(embedBuilder::addField);
        lastUsedChannel.sendMessageEmbeds(embedBuilder.build()).queue();
        messages.clear();
      }
    }
  }

  @SneakyThrows
  private DiscordCommand newInstance(Class<?> clazz) {
    return (DiscordCommand) clazz.getConstructor().newInstance();
  }

  @Override
  public void onMessageReceived(@NotNull MessageReceivedEvent event) {
    final String message = event.getMessage().getContentRaw();
    lastUsedChannel = event.getChannel();

    if (!message.startsWith(".")) return;

    String command;
    if (message.contains(" "))
      command = message.split(" ")[0].substring(1);
    else command = message.substring(1);

    System.out.printf("%s - %s%n", event.getAuthor().getAsTag(), message);

    Optional<DiscordCommand> commandOptional = commands.stream()
        .filter(discordCommand -> discordCommand.name().equalsIgnoreCase(command))
        .findFirst();

    if (commandOptional.isPresent()) {
      try {
        commandOptional.get().runCommand(event);
      } catch (DiscordCommandException e) {
        event.getChannel().sendMessage(e.failure()).queue();
      }
    } else {
      event.getChannel().sendMessage("Command not found! Try using .help").queue();
    }

    NotificationManager.pushNotification(new Notification("Discord Message Sent...", event.getAuthor().getAsTag() + " - " + message, 5000, NotificationType.SUCCESS));
  }

  public static DiscordEventListener getInstance() {
    return instance;
  }

}