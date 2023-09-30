package dev.eternal.client.module.impl.misc;

import dev.eternal.client.event.Subscribe;
import dev.eternal.client.event.events.EventUpdate;
import dev.eternal.client.module.Module;
import dev.eternal.client.module.api.ModuleInfo;
import dev.eternal.client.property.impl.NumberSetting;
import dev.eternal.client.property.impl.TextSetting;
import dev.eternal.client.util.time.Stopwatch;

@ModuleInfo(name = "ChatBot", description = "Types something every once in a while.", category = Module.Category.MISC)
public class ChatBot extends Module {

  private final TextSetting text = new TextSetting(this, "Text", "Basic Text");
  private final NumberSetting delay = new NumberSetting(this, "Delay", "The delay between sent messages. (MS) ", 1000, 100, 10000, 1);
  private final Stopwatch stopwatch = new Stopwatch();

  @Subscribe
  public void handleUpdate(EventUpdate eventUpdate) {
    if(stopwatch.hasElapsed(delay.value().longValue(), true))
      mc.thePlayer.sendChatMessage(text.value());
  }

}
