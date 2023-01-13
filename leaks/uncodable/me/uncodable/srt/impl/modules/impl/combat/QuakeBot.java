package me.uncodable.srt.impl.modules.impl.combat;

import me.uncodable.srt.Ries;
import me.uncodable.srt.impl.events.api.EventTarget;
import me.uncodable.srt.impl.events.events.entity.EventMotionUpdate;
import me.uncodable.srt.impl.modules.api.Module;
import me.uncodable.srt.impl.modules.api.ModuleInfo;
import me.uncodable.srt.impl.modules.api.settings.Setting;
import me.uncodable.srt.impl.utils.RotationUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemSword;

@ModuleInfo(
   internalName = "QuakeBot",
   name = "Quake Bot",
   desc = "Allows you to semi-automatically play Hypixel quake.",
   category = Module.Category.COMBAT
)
public class QuakeBot extends Module {
   private static final String INTERNAL_TEAMS = "INTERNAL_TEAMS";
   private static final String TEAMS_SETTING_NAME = "Teams";
   private boolean pressed;

   public QuakeBot(int key, boolean enabled) {
      super(key, enabled);
      Ries.INSTANCE.getSettingManager().addCheckbox(this, "INTERNAL_TEAMS", "Teams");
   }

   @EventTarget(
      target = EventMotionUpdate.class
   )
   public void onUpdate(EventMotionUpdate e) {
      boolean ticked = Ries.INSTANCE.getSettingManager().getSetting(this, "INTERNAL_TEAMS", Setting.Type.CHECKBOX).isTicked();
      if (MC.thePlayer.getHeldItem() != null
         && (
            MC.thePlayer.getHeldItem().getItem() instanceof ItemPickaxe
               || MC.thePlayer.getHeldItem().getItem() instanceof ItemAxe
               || MC.thePlayer.getHeldItem().getItem() instanceof ItemSword
               || MC.thePlayer.getHeldItem().getItem() instanceof ItemHoe
               || MC.thePlayer.getHeldItem().getItem() instanceof ItemSpade
         )) {
         MC.theWorld
            .getLoadedEntityList()
            .stream()
            .filter(
               o -> o instanceof EntityPlayer
                     && o != MC.thePlayer
                     && (ticked && MC.thePlayer.isOnSameTeam((EntityPlayer)o) || !ticked && !MC.thePlayer.isOnSameTeam((EntityPlayer)o))
            )
            .forEach(p -> {
               EntityPlayer player = (EntityPlayer)p;
               if (MC.thePlayer.canEntityBeSeen(player)) {
                  float[] rot = RotationUtils.doBasicRotations(player);
                  e.setRotationYaw(rot[0]);
                  e.setRotationPitch(rot[1]);
                  MC.playerController.sendUseItem(MC.thePlayer, MC.theWorld, MC.thePlayer.getHeldItem());
                  this.pressed = true;
               }
            });
      }

      if (this.pressed) {
         MC.playerController.onStoppedUsingItem(MC.thePlayer);
         this.pressed = false;
      }
   }
}
