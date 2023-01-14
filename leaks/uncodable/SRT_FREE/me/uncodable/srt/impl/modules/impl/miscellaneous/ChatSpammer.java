package me.uncodable.srt.impl.modules.impl.miscellaneous;

import me.uncodable.srt.Ries;
import me.uncodable.srt.impl.events.api.EventTarget;
import me.uncodable.srt.impl.events.events.entity.EventUpdate;
import me.uncodable.srt.impl.modules.api.Module;
import me.uncodable.srt.impl.modules.api.ModuleInfo;
import me.uncodable.srt.impl.modules.api.settings.Setting;
import org.apache.commons.lang3.RandomStringUtils;

@ModuleInfo(
   internalName = "ChatSpammer",
   name = "Chat Spammer",
   desc = "Allows you to spam the chat, like you're a sweat on MinemenClub.",
   category = Module.Category.MISCELLANEOUS
)
public class ChatSpammer extends Module {
   private static final String INTERNAL_DELAY_VALUE = "INTERNAL_DELAY_VALUE";
   private static final String DELAY_VALUE_SETTING_NAME = "Delay";
   private final me.uncodable.srt.impl.utils.Timer timer = new me.uncodable.srt.impl.utils.Timer();

   public ChatSpammer(int key, boolean enabled) {
      super(key, enabled);
      Ries.INSTANCE.getSettingManager().addSlider(this, "INTERNAL_DELAY_VALUE", "Delay", 500.0, 0.0, 5000.0, true);
   }

   @Override
   public void onDisable() {
      this.timer.reset();
   }

   @EventTarget(
      target = EventUpdate.class
   )
   public void onUpdate(EventUpdate e) {
      if (e.getState() == EventUpdate.State.PRE
         && this.timer.elapsed((long)Ries.INSTANCE.getSettingManager().getSetting(this, "INTERNAL_DELAY_VALUE", Setting.Type.SLIDER).getCurrentValue())) {
         MC.thePlayer
            .sendChatMessage(
               String.format("SRT Hellcat %s written by uncodable. [%s]", Ries.INSTANCE.getBuild(), RandomStringUtils.random(16, "1234567890abcdef"))
            );
         this.timer.reset();
      }
   }
}
