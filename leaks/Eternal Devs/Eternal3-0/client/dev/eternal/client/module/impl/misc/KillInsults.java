package dev.eternal.client.module.impl.misc;

import dev.eternal.client.event.Subscribe;
import dev.eternal.client.event.events.EventPacket;
import dev.eternal.client.module.Module;
import dev.eternal.client.module.api.ModuleInfo;
import dev.eternal.client.ui.notification.Notification;
import dev.eternal.client.ui.notification.NotificationManager;
import dev.eternal.client.ui.notification.NotificationType;
import dev.eternal.client.util.files.FileUtils;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.server.S02PacketChat;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

@ModuleInfo(name = "Kill Insults", category = Module.Category.MISC, description = "Insult players when you kill them.")
public class KillInsults extends Module {

  @Subscribe
  public void handleS02(EventPacket eventPacket) {
    if (!(eventPacket.getPacket() instanceof S02PacketChat s02PacketChat)) return;
    final String message = s02PacketChat.getChatComponent().getUnformattedText();

    if(!message.contains(" ")) return;
    final List<String> strings = Arrays.stream(message.split(" ")).toList();
    if(strings.size() < 2) return; // very unlikely for the message to be a kill notification if less that 2

    if(strings.get(strings.size()-1).equals(mc.thePlayer.getName()) && isName(strings.get(0)))
      insult();
  }

  public void insult() {
    List<String> args = FileUtils.readFromFile(FileUtils.getFileFromFolder("modules", "insults.txt"));
    if(args.isEmpty()) {
      NotificationManager.pushNotification(new Notification("Kills Insults", "Insult file is empty!", 5000, NotificationType.WARNING));
      return;
    }
    mc.thePlayer.sendChatMessage(args.get(ThreadLocalRandom.current().nextInt(0, args.size())));
  }

  private boolean isName(String name) {
    return mc.theWorld.loadedEntityList.stream()
        .map(Entity::getName) //map to a list of names
        .anyMatch(s -> Objects.equals(s, name)); // return if the given word is the name of an entity
  }

}