package dev.eternal.client.module.impl.misc;

import dev.eternal.client.event.Subscribe;;
import dev.eternal.client.event.events.EventPacket;
import dev.eternal.client.event.events.EventUpdate;
import dev.eternal.client.module.Module;
import dev.eternal.client.module.api.ModuleInfo;
import dev.eternal.client.ui.notification.Notification;
import dev.eternal.client.ui.notification.NotificationManager;
import dev.eternal.client.ui.notification.NotificationType;
import dev.eternal.client.util.time.Stopwatch;
import net.minecraft.client.gui.GuiUtilRenderComponents;
import net.minecraft.event.HoverEvent;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;

import java.util.Arrays;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@ModuleInfo(name = "AutoReact", description = "Automatically reacts to chat minigames.", category = Module.Category.MISC)
public class AutoReact extends Module {

  private final Stopwatch stopwatch = new Stopwatch();
  private final Queue<String> textQueue = new ConcurrentLinkedQueue<>();

  @Subscribe
  public void onChat(EventPacket eventPacket) {
    if (eventPacket.getPacket() instanceof S02PacketChat s02PacketChat) {
      final IChatComponent component = s02PacketChat.getChatComponent();
      HoverEvent hoverevent = component.getChatStyle().getChatHoverEvent();
      final String text = component.getUnformattedText();
      if (hoverevent != null && text.contains("TypeIt")) {
        if (hoverevent.getAction() == HoverEvent.Action.SHOW_TEXT) {
          stopwatch.reset();
          textQueue.add(hoverevent.getValue().getUnformattedText());
        }
      }
    }
  }

  @Subscribe
  public void onUpdate(EventUpdate eventUpdate) {
    if (eventUpdate.pre()) {
      if (!textQueue.isEmpty() && stopwatch.hasElapsed(3000L)) {
        NotificationManager.pushNotification(new Notification("AutoReact response", "", 5000, NotificationType.INFO));
        mc.thePlayer.sendChatMessage(textQueue.poll());
      }
    }
  }

}
