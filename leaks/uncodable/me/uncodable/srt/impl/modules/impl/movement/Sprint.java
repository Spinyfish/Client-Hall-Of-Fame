package me.uncodable.srt.impl.modules.impl.movement;

import me.uncodable.srt.Ries;
import me.uncodable.srt.impl.events.api.EventTarget;
import me.uncodable.srt.impl.events.events.entity.EventLivingUpdate;
import me.uncodable.srt.impl.modules.api.Module;
import me.uncodable.srt.impl.modules.api.ModuleInfo;
import me.uncodable.srt.impl.modules.api.settings.Setting;
import me.uncodable.srt.impl.modules.impl.combat.Aura;
import net.minecraft.potion.Potion;

@ModuleInfo(
   internalName = "Sprint",
   name = "Sprint",
   desc = "Allows you to have to never manually sprint again.\nPerfect for lazy asses like Uncodable!",
   category = Module.Category.MOVEMENT,
   legit = true
)
public class Sprint extends Module {
   private Aura auraMod;
   private NoSlowdown noSlowMod;
   private boolean setup;
   private boolean wait;

   public Sprint(int key, boolean enabled) {
      super(key, enabled);
   }

   @Override
   public void onEnable() {
      if (!this.setup) {
         this.auraMod = (Aura)Ries.INSTANCE.getModuleManager().getModuleByName("Aura");
         this.noSlowMod = (NoSlowdown)Ries.INSTANCE.getModuleManager().getModuleByName("NoSlowdown");
         this.setup = true;
      }
   }

   @EventTarget(
      target = EventLivingUpdate.class
   )
   public void onLiving(EventLivingUpdate e) {
      if (!this.wait) {
         MC.gameSettings
            .keyBindSprint
            .setKeyDown(
               (
                     MC.thePlayer.moveForward > 0.0F
                           && (MC.thePlayer.getFoodStats().getFoodLevel() > 6 || MC.thePlayer.capabilities.isCreativeMode)
                           && !MC.thePlayer.isPotionActive(Potion.blindness)
                           && !MC.thePlayer.isEating()
                        || this.noSlowMod.isEnabled()
                  )
                  && (
                     !this.auraMod.isEnabled()
                        || !Ries.INSTANCE.getSettingManager().getSetting(this.auraMod, "INTERNAL_NO_KEEP_SPRINT", Setting.Type.CHECKBOX).isTicked()
                        || this.targetEntity == null
                  )
            );
      } else if (!MC.thePlayer.isSprinting()) {
         this.wait = true;
      }
   }
}
