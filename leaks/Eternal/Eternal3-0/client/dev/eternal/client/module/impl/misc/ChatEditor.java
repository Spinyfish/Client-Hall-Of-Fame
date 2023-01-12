package dev.eternal.client.module.impl.misc;

import com.google.common.collect.ImmutableMap;
import dev.eternal.client.event.Subscribe;;
import dev.eternal.client.Client;
import dev.eternal.client.event.events.EventChat;
import dev.eternal.client.module.Module;
import dev.eternal.client.module.api.ModuleInfo;
import dev.eternal.client.property.impl.BooleanSetting;
import dev.eternal.client.property.impl.EnumSetting;
import dev.eternal.client.property.impl.interfaces.INameable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.util.ChatComponentText;
import org.apache.commons.lang3.RandomUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@ModuleInfo(name = "ChatEditor", description = "Bypass Chat Filters", category = Module.Category.MISC)
public class ChatEditor extends Module {

  private final EnumSetting<ChatMode> enumSetting = new EnumSetting<>(this, "Mode", ChatMode.values());

  private final ImmutableMap<String, String> autoCorrect =
      new ImmutableMap.Builder<String, String>()
          .put("wtf", "what the fuck")
          .put("da", "the")
          .put("dont", "don't")
          .put("cant", "can't")
          .put("ty", "thank you")
          .put("ok", "okay")
          .build();

  @Subscribe
  public void handleChat(EventChat eventChat) {
    String text = eventChat.chatComponent().getUnformattedText();
    if (text.startsWith("/") || text.startsWith("."))
      return;
    switch (enumSetting.value()) {
      case DORT -> {
        String[] words = text.split(" ");
        StringBuilder sent = new StringBuilder();
        for (String word : words) {
          sent.append(word.length() <= 3 ? word : word + (word.endsWith("e") ? "rton" : "erton")).append(" ");
        }
        setChat(eventChat, sent + (RandomUtils.nextInt(0, 2) == 0 ? " sir." : " of."));
      }
      case BYPASS -> {
        ArrayList<String> prefixList = new ArrayList<>(Arrays.asList(",", ".", ":", "-", "_", ";"));
        String set = "";
        for (String s : text.split("")) {
          set += s + prefixList.get(RandomUtils.nextInt(0, prefixList.size()));
        }
        setChat(eventChat, prefixList.get(RandomUtils.nextInt(0, prefixList.size())) + set);
      }

      case FUNNY -> {
        //here we fucking go
        String replacement = text
            .replaceAll("[aA]", "@")
            .replaceAll("[cC]", "(")
            .replaceAll("[eE]", "3")
            .replaceAll("[hH]", "#")
            .replaceAll("[jJ]", ";")
            .replaceAll("[lL]", "1")
            .replaceAll("[oO]", "0")
            .replaceAll("[sS]", "5")
            .replaceAll("[zZ]", "7");
        setChat(eventChat, replacement);
      }
      case PROPER -> {
        text = autoCorrect(text);
        String set = String.valueOf(text.charAt(0)).toUpperCase() + text.substring(1);
        String endChar = String.valueOf(set.charAt(set.length() - 1));
        String regex = "[,.<>/?;:-{}=_+]";
        setChat(eventChat, endChar.matches(regex) ? set + "." : set);
      }
    }
  }

  private void setChat(EventChat eventChat, String s) {
    eventChat.chatComponent(new ChatComponentText(s));
  }

  private String autoCorrect(String str) {
    return Arrays.stream(str.split(" "))
        .map(s -> autoCorrect.getOrDefault(s, s))
        .collect(Collectors.joining(" "));
  }

  @Getter
  @AllArgsConstructor
  public enum ChatMode implements INameable {
    BYPASS("Bypass"),
    PROPER("Proper"),
    FUNNY("Funny"),
    DORT("Dort Talk");
    private final String getName;
  }

}
