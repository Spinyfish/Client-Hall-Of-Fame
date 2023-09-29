package dev.eternal.client.module.impl.server;

import com.google.gson.Gson;
import dev.eternal.client.event.Subscribe;;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.eternal.client.event.events.EventPacket;
import dev.eternal.client.event.events.EventPostRenderGui;
import dev.eternal.client.event.events.EventUpdate;
import dev.eternal.client.font.FontManager;
import dev.eternal.client.font.FontType;
import dev.eternal.client.module.Module;
import dev.eternal.client.module.api.ModuleInfo;
import dev.eternal.client.ui.notification.Notification;
import dev.eternal.client.ui.notification.NotificationManager;
import dev.eternal.client.ui.notification.NotificationType;
import dev.eternal.client.util.server.ServerUtil;
import net.minecraft.client.gui.Gui;
import net.minecraft.network.play.server.S02PacketChat;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@ModuleInfo(name = "VortexInfo", description = "Displays info about vortexHvH.", category = Module.Category.SERVER)
public class VortexInfo extends Module {

  private final Gson gson = new Gson();

  private final Map<String, String> hashMap = new HashMap<>();
  private final List<String> commands = Arrays.asList(
      "/api minify DiceOfGodTime ",
      "/api minify Mask ",
      "/api minify PlayTime ",
      "/api minify TreasureTotem ");

  @Subscribe
  public void onPacket(EventPacket eventPacket) {
    if (eventPacket.getPacket() instanceof S02PacketChat s02PacketChat) {
      final String chatMessage = s02PacketChat.getChatComponent().getUnformattedText();
      if (chatMessage.startsWith("API")) {
        eventPacket.cancelled(true);
        JsonElement element = JsonParser.parseString(chatMessage.split("API ")[1].trim());
        final JsonObject jsonObject = element.getAsJsonObject();
        if (chatMessage.contains("DiceOfGodTime")) {

          hashMap.put("Dice Of God",
              String.format("%dm %ds",
                  TimeUnit.MILLISECONDS.toMinutes(jsonObject.get("DiceOfGodTime").getAsInt() / 2),
                  TimeUnit.MILLISECONDS.toSeconds(jsonObject.get("DiceOfGodTime").getAsInt() / 2) % 60
              ));
        } else if (chatMessage.contains("Mask")) {
          hashMap.put("Mask", jsonObject.get("Mask").getAsString());
        } else if (chatMessage.contains("TreasureTotem")) {
          hashMap.put("Treasure for Mask", String.format("%s/100", jsonObject.get("TreasureTotem").getAsString()));
        } else if (chatMessage.contains("PlayTime")) {
          hashMap.put("Endurance",
              String.format("%dh %dm %ds",
                  TimeUnit.MILLISECONDS.toHours((int) (jsonObject.get("PlayTime").getAsInt() * 2.5)),
                  TimeUnit.MILLISECONDS.toMinutes((int) (jsonObject.get("PlayTime").getAsInt() * 2.5)) % 60,
                  TimeUnit.MILLISECONDS.toSeconds((int) (jsonObject.get("PlayTime").getAsInt() * 2.5)) % 60
              ));
        }
      }
    }
  }

  @Subscribe
  public void onUpdate(EventUpdate eventUpdate) {
    if (!ServerUtil.isOnServer("vortexhvh.com")) {
      NotificationManager.pushNotification(new Notification(
          "Server not supported!",
          "This module is made for the server 'play.vortexhvh.com'",
          5000,
          NotificationType.INFO
      ));
      toggle();
      return;
    }
    if (eventUpdate.pre() && mc.thePlayer.ticksExisted % 100 == 0) {
      commands.stream()
          .map(s -> s + mc.thePlayer.getName())
          .forEach(mc.thePlayer::sendChatMessage);
    }
  }

  @Subscribe
  public void onRender2D(EventPostRenderGui eventPostRenderGui) {
    Gui.drawRect(1, 21, 201, 26 + (hashMap.size() * 10), 0x66000000);
    Gui.drawRect(1, 21, 201, 22, client.scheme().getPrimary());

    AtomicInteger atomicInteger = new AtomicInteger();
    hashMap.forEach((s, s2) -> {
      final int iteration = atomicInteger.getAndIncrement();
      FontManager.getFontRenderer(FontType.ICIEL, 18).drawStringWithShadow(String.format("%s: %s", s, s2), 5, 25 + (10 * iteration), -1);
    });
  }

}