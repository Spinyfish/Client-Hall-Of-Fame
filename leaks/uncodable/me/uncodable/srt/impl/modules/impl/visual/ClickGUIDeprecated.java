package me.uncodable.srt.impl.modules.impl.visual;

import me.uncodable.srt.impl.events.api.EventTarget;
import me.uncodable.srt.impl.events.events.entity.EventMotionUpdate;
import me.uncodable.srt.impl.events.events.packet.EventPacket;
import me.uncodable.srt.impl.gui.clickgui.ClickGUIScreen;
import me.uncodable.srt.impl.modules.api.Module;
import me.uncodable.srt.impl.modules.api.ModuleInfo;
import net.minecraft.network.play.client.C0DPacketCloseWindow;
import net.minecraft.network.play.client.C0EPacketClickWindow;
import org.apache.commons.lang3.RandomUtils;

@Deprecated
@ModuleInfo(
   internalName = "ClickGUI",
   name = "Click GUI",
   desc = "Allows you to view a clickable GUI that you can click on.\nWhat else is there to say?",
   category = Module.Category.VISUAL,
   legit = true
)
public class ClickGUIDeprecated extends Module {
   public ClickGUIDeprecated(int key, boolean enabled) {
      super(key, enabled);
   }

   @Override
   public void onEnable() {
      MC.displayGuiScreen(new ClickGUIScreen(RandomUtils.nextInt(0, 100)));
   }

   @EventTarget(
      target = EventMotionUpdate.class
   )
   public void onMotion(EventMotionUpdate e) {
      this.toggle();
   }

   @EventTarget(
      target = EventPacket.class
   )
   public void onPacket(EventPacket e) {
      e.setCancelled(e.getPacket() instanceof C0EPacketClickWindow || e.getPacket() instanceof C0DPacketCloseWindow);
   }
}
