package me.uncodable.srt.impl.modules.impl.visual;

import java.util.ArrayList;
import me.uncodable.srt.impl.events.api.EventTarget;
import me.uncodable.srt.impl.events.events.packet.EventPacket;
import me.uncodable.srt.impl.gui.clickgui2.ClickGUIScreen;
import me.uncodable.srt.impl.gui.clickgui2.components.RenderTab;
import me.uncodable.srt.impl.modules.api.Module;
import me.uncodable.srt.impl.modules.api.ModuleInfo;
import net.minecraft.network.play.client.C0DPacketCloseWindow;
import net.minecraft.network.play.client.C0EPacketClickWindow;

@ModuleInfo(
   internalName = "ClickGUIEXPERIMENTAL",
   name = "Click GUI New",
   desc = "Allows you to view a clickable GUI that you can click on.\nWhat else is there to say?",
   category = Module.Category.VISUAL,
   legit = true
)
public class ClickGUI extends Module {
   private final ArrayList<RenderTab> renderTabs = new ArrayList<>();

   public ClickGUI(int primaryKey, boolean enabled) {
      super(primaryKey, enabled);
   }

   @Override
   public void onEnable() {
      int halfCategoryValues = Module.Category.values().length / 2;
      if (this.renderTabs.isEmpty()) {
         for(int i = 0; i < halfCategoryValues; ++i) {
            this.renderTabs.add(new RenderTab(Module.Category.values()[i], 10 + 94 * i, 10, 100 + 94 * i, 30));
         }

         for(int i = halfCategoryValues; i < Module.Category.values().length; ++i) {
            this.renderTabs.add(new RenderTab(Module.Category.values()[i], 10 + 94 * (i - halfCategoryValues), 50, 100 + 94 * (i - halfCategoryValues), 70));
         }
      }

      MC.displayGuiScreen(new ClickGUIScreen(this.renderTabs));
      this.toggle();
   }

   @EventTarget(
      target = EventPacket.class
   )
   public void onPacket(EventPacket e) {
      e.setCancelled(e.getPacket() instanceof C0EPacketClickWindow || e.getPacket() instanceof C0DPacketCloseWindow);
   }
}
