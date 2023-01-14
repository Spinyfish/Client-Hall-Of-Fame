package me.uncodable.srt.impl.modules.impl.visual;

import me.uncodable.srt.Ries;
import me.uncodable.srt.impl.events.api.EventTarget;
import me.uncodable.srt.impl.events.events.render.Event2DRender;
import me.uncodable.srt.impl.modules.api.Module;
import me.uncodable.srt.impl.modules.api.ModuleInfo;
import me.uncodable.srt.impl.modules.api.settings.Setting;
import net.minecraft.block.BlockBarrier;

@ModuleInfo(
   internalName = "AntiInvisibles",
   name = "Anti-Invisibles",
   desc = "Allows you to see invisible entities or barriers.\nAlso allows you to see staff members with terrible staff plugins!",
   category = Module.Category.VISUAL
)
public class AntiInvisibles extends Module {
   private static final String INTERNAL_INVISIBLES = "INTERNAL_INVISIBLES";
   private static final String INTERNAL_BARRIERS = "INTERNAL_BARRIERS";
   private static final String INVISIBLES_SETTING_NAME = "Invisibles";
   private static final String BARRIERS_SETTING_NAME = "Barriers";
   private boolean doRender;

   public AntiInvisibles(int key, boolean enabled) {
      super(key, enabled);
      Ries.INSTANCE.getSettingManager().addCheckbox(this, "INTERNAL_INVISIBLES", "Invisibles", true);
      Ries.INSTANCE.getSettingManager().addCheckbox(this, "INTERNAL_BARRIERS", "Barriers", true);
   }

   @Override
   public void onEnable() {
      if (MC.renderGlobal != null) {
         MC.renderGlobal.loadRenderers();
      }
   }

   @Override
   public void onDisable() {
      BlockBarrier.setRender(this.doRender = false);
      MC.renderGlobal.loadRenderers();
   }

   @EventTarget(
      target = Event2DRender.class
   )
   public void onRender(Event2DRender e) {
      if (Ries.INSTANCE.getSettingManager().getSetting(this, "INTERNAL_BARRIERS", Setting.Type.CHECKBOX).isTicked()) {
         if (!this.doRender) {
            BlockBarrier.setRender(this.doRender = true);
         }
      } else if (this.doRender) {
         BlockBarrier.setRender(this.doRender = false);
      }
   }
}
