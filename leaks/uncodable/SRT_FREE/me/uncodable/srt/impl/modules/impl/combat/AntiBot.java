package me.uncodable.srt.impl.modules.impl.combat;

import java.util.HashMap;
import me.uncodable.srt.Ries;
import me.uncodable.srt.impl.events.api.EventTarget;
import me.uncodable.srt.impl.events.events.entity.EventUpdate;
import me.uncodable.srt.impl.modules.api.Module;
import me.uncodable.srt.impl.modules.api.ModuleInfo;
import me.uncodable.srt.impl.modules.api.settings.Setting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

@ModuleInfo(
   internalName = "AntiBot",
   name = "Anti-Bot",
   desc = "Prevents the server from detecting you for Kill Aura.\nSuck this dick Mineplex.",
   category = Module.Category.COMBAT
)
public class AntiBot extends Module {
   private static final String COMBO_BOX_SETTING_NAME = "Anti-Cheat";
   private static final String GWEN = "GWEN";
   private final HashMap<EntityPlayer, Integer> inRangeTicks = new HashMap<>();

   public AntiBot(int key, boolean enabled) {
      super(key, enabled);
      Ries.INSTANCE.getSettingManager().addComboBox(this, "INTERNAL_GENERAL_COMBO_BOX", "Anti-Cheat", "GWEN");
   }

   @Override
   public void onDisable() {
      this.inRangeTicks.clear();
   }

   @EventTarget(
      target = EventUpdate.class
   )
   public void onUpdate(EventUpdate e) {
      if (e.getState() == EventUpdate.State.PRE) {
         String var2 = Ries.INSTANCE.getSettingManager().getSetting(this, "INTERNAL_GENERAL_COMBO_BOX", Setting.Type.COMBO_BOX).getCurrentCombo();
         switch(var2) {
            case "GWEN":
               if (MC.thePlayer.ticksExisted % 200 == 0) {
                  this.inRangeTicks.clear();
               }

               for(Entity entity : MC.theWorld.loadedEntityList) {
                  if (entity instanceof EntityPlayer) {
                     EntityPlayer player = (EntityPlayer)entity;
                     if ((double)MC.thePlayer.getDistanceToEntity(player) <= 4.6 && player != MC.thePlayer) {
                        if (Math.abs(player.posY - MC.thePlayer.posY) >= 2.2
                           && player.posY > MC.thePlayer.posY
                           && Math.abs(player.posX - MC.thePlayer.posX) < 2.0
                           && Math.abs(player.posZ - MC.thePlayer.posZ) < 2.0) {
                           Ries.INSTANCE.msg("reached code");
                           if (!this.inRangeTicks.containsKey(player)) {
                              this.inRangeTicks.put(player, 0);
                           } else {
                              this.inRangeTicks.put(player, this.inRangeTicks.get(player) + 1);
                              Ries.INSTANCE.msg(String.format("%d", this.inRangeTicks.get(player)));
                           }

                           if (this.inRangeTicks.get(player) > 20) {
                              Ries.INSTANCE.msg("removed " + player.getDisplayName().getUnformattedText());
                              MC.theWorld.removeEntity(player);
                           }
                        } else if (player.getDistance(player.lastTickPosX, player.lastTickPosY, player.lastTickPosZ) > 4.0
                           && this.inRangeTicks.get(player) > 5) {
                           Ries.INSTANCE.msg("removed " + player.getDisplayName().getUnformattedText());
                           MC.theWorld.removeEntity(player);
                        }
                     }
                  }
               }
         }
      }
   }
}
