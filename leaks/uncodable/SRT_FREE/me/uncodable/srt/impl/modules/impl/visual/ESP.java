package me.uncodable.srt.impl.modules.impl.visual;

import me.uncodable.srt.Ries;
import me.uncodable.srt.impl.events.api.EventTarget;
import me.uncodable.srt.impl.events.events.render.Event3DRender;
import me.uncodable.srt.impl.modules.api.Module;
import me.uncodable.srt.impl.modules.api.ModuleInfo;
import me.uncodable.srt.impl.utils.RenderUtils;
import net.minecraft.entity.Entity;

@ModuleInfo(
   internalName = "ESP",
   name = "ESP",
   desc = "Allows you to see entities through walls.\nPlease, don't be Kant and stalk fappers through walls.",
   category = Module.Category.VISUAL,
   exp = true
)
public class ESP extends Module {
   public ESP(int key, boolean enabled) {
      super(key, enabled);
   }

   @Override
   public void onEnable() {
      Ries.INSTANCE.msg("This module will be finished at a later date.");
   }

   @EventTarget(
      target = Event3DRender.class
   )
   public void onRender(Event3DRender e) {
      for(Entity entity : MC.theWorld.loadedEntityList) {
         double d0 = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double)MC.timer.getRenderPartialTicks();
         double d1 = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double)MC.timer.getRenderPartialTicks();
         double d2 = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double)MC.timer.getRenderPartialTicks();
         RenderUtils.renderBox(entity, d0, d1, d2);
      }
   }
}
