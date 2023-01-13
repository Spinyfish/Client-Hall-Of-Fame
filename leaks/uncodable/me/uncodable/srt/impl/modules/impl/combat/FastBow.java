package me.uncodable.srt.impl.modules.impl.combat;

import java.util.Comparator;
import me.uncodable.srt.Ries;
import me.uncodable.srt.impl.events.api.EventTarget;
import me.uncodable.srt.impl.events.events.entity.EventMotionUpdate;
import me.uncodable.srt.impl.modules.api.Module;
import me.uncodable.srt.impl.modules.api.ModuleInfo;
import me.uncodable.srt.impl.modules.api.settings.Setting;
import me.uncodable.srt.impl.utils.EntityUtils;
import me.uncodable.srt.impl.utils.RotationUtils;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemBow;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C0CPacketInput;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;

@ModuleInfo(
   internalName = "FastBow",
   name = "Fast Bow",
   desc = "Allows you to shoot arrows faster than the average HCF player!\nNOTE: Not to be used in school environments...",
   category = Module.Category.COMBAT
)
public class FastBow extends Module {
   private static final String INTERNAL_PLAYERS = "INTERNAL_PLAYERS";
   private static final String INTERNAL_ANIMALS = "INTERNAL_ANIMALS";
   private static final String INTERNAL_MOBS = "INTERNAL_MOBS";
   private static final String INTERNAL_INVISIBLES = "INTERNAL_INVISIBLES";
   private static final String INTERNAL_FRIENDS = "INTERNAL_FRIENDS";
   private static final String INTERNAL_DEAD = "INTERNAL_DEAD";
   private static final String INTERNAL_TEAMS = "INTERNAL_TEAMS";
   private static final String INTERNAL_RAYTRACE = "INTERNAL_WALLS";
   private static final String INTERNAL_LOCK_VIEW = "INTERNAL_LOCK_VIEW";
   private static final String LOCK_VIEW_SETTING_NAME = "Use Lock View";
   private static final String PLAYERS_SETTING_NAME = "Target Players";
   private static final String ANIMALS_SETTING_NAME = "Target Animals";
   private static final String MOBS_SETTING_NAME = "Target Mobs";
   private static final String INVISIBLES_SETTING_NAME = "Target Invisible Entities";
   private static final String FRIENDS_SETTING_NAME = "Target Friends";
   private static final String DEAD_SETTING_NAME = "Target Dead Entities";
   private static final String TEAMS_SETTING_NAME = "Teams Check";
   private static final String COMBO_BOX_SETTING_NAME = "Fast Bow Mode";
   private static final String RAYTRACE_SETTING_NAME = "Ray Trace";
   private static final String VANILLA = "Vanilla";
   private static final String MULTI_AURA = "Multi Aura";
   private static final String MULTI_AURA_DEMON = "Multi Aura Demon";
   private static final String LEGACY_VERUS = "Legacy Verus";
   private static final String LEGACY_GUARDIAN = "Legacy Guardian";
   private volatile boolean oneTargetSet;
   private float oldTimerSpeed;

   public FastBow(int key, boolean enabled) {
      super(key, enabled);
      Ries.INSTANCE
         .getSettingManager()
         .addComboBox(this, "INTERNAL_GENERAL_COMBO_BOX", "Fast Bow Mode", "Multi Aura", "Multi Aura Demon", "Vanilla", "Legacy Verus", "Legacy Guardian");
      Ries.INSTANCE.getSettingManager().addCheckbox(this, "INTERNAL_LOCK_VIEW", "Use Lock View");
      Ries.INSTANCE.getSettingManager().addCheckbox(this, "INTERNAL_WALLS", "Ray Trace", true);
      Ries.INSTANCE.getSettingManager().addCheckbox(this, "INTERNAL_PLAYERS", "Target Players", true);
      Ries.INSTANCE.getSettingManager().addCheckbox(this, "INTERNAL_ANIMALS", "Target Animals");
      Ries.INSTANCE.getSettingManager().addCheckbox(this, "INTERNAL_MOBS", "Target Mobs");
      Ries.INSTANCE.getSettingManager().addCheckbox(this, "INTERNAL_INVISIBLES", "Target Invisible Entities", true);
      Ries.INSTANCE.getSettingManager().addCheckbox(this, "INTERNAL_FRIENDS", "Target Friends", true);
      Ries.INSTANCE.getSettingManager().addCheckbox(this, "INTERNAL_DEAD", "Target Dead Entities", true);
      Ries.INSTANCE.getSettingManager().addCheckbox(this, "INTERNAL_TEAMS", "Teams Check");
   }

   @Override
   public void onDisable() {
      this.oneTargetSet = false;
      MC.timer.timerSpeed = this.oldTimerSpeed;
   }

   @Override
   public void onEnable() {
      this.oldTimerSpeed = MC.timer.timerSpeed;
   }

   @EventTarget(
      target = EventMotionUpdate.class
   )
   public void onMotion(EventMotionUpdate e) {
      if (e.getState() == EventMotionUpdate.State.PRE) {
         if (MC.thePlayer.ticksExisted % 10 == 0) {
            MC.theWorld.getLoadedEntityList().sort(Comparator.comparing(entity -> MC.thePlayer.getDistanceToEntity(entity)));
         }

         if (MC.thePlayer.getHeldItem() != null && MC.thePlayer.getHeldItem().getItem() instanceof ItemBow && MC.thePlayer.isUsingItem()) {
            String var2 = Ries.INSTANCE.getSettingManager().getSetting(this, "INTERNAL_GENERAL_COMBO_BOX", Setting.Type.COMBO_BOX).getCurrentCombo();
            switch(var2) {
               case "Vanilla":
                  MC.theWorld
                     .getLoadedEntityList()
                     .stream()
                     .filter(entity -> entity instanceof EntityLivingBase)
                     .forEach(
                        entity -> {
                           EntityLivingBase target = (EntityLivingBase)entity;
                           if (!this.oneTargetSet
                              && EntityUtils.skipEntity(target, this)
                              && (
                                 !Ries.INSTANCE.getSettingManager().getSetting(this, "INTERNAL_WALLS", Setting.Type.CHECKBOX).isTicked()
                                    || MC.thePlayer.canEntityBeSeen(entity)
                              )) {
                              this.oneTargetSet = true;
                              float[] rotations = this.doRotations(target, e);
                              this.doShooting(1, rotations);
                           }
                        }
                     );
                  this.oneTargetSet = false;
                  break;
               case "Legacy Verus":
                  MC.theWorld
                     .getLoadedEntityList()
                     .stream()
                     .filter(entity -> entity instanceof EntityLivingBase)
                     .forEach(
                        entity -> {
                           EntityLivingBase target = (EntityLivingBase)entity;
                           if (EntityUtils.skipEntity(target, this)
                              && (
                                 !Ries.INSTANCE.getSettingManager().getSetting(this, "INTERNAL_WALLS", Setting.Type.CHECKBOX).isTicked()
                                    || MC.thePlayer.canEntityBeSeen(entity)
                              )) {
                              float[] rotations = this.doRotations(target, e);
                              this.doShootingLegacyVerus(rotations);
                           }
                        }
                     );
                  break;
               case "Multi Aura":
                  MC.theWorld
                     .getLoadedEntityList()
                     .stream()
                     .filter(entity -> entity instanceof EntityLivingBase)
                     .forEach(
                        entity -> {
                           EntityLivingBase target = (EntityLivingBase)entity;
                           if (EntityUtils.skipEntity(target, this)
                              && (
                                 !Ries.INSTANCE.getSettingManager().getSetting(this, "INTERNAL_WALLS", Setting.Type.CHECKBOX).isTicked()
                                    || MC.thePlayer.canEntityBeSeen(entity)
                              )) {
                              float[] rotations = this.doRotations(target, e);
                              this.doShooting(1, rotations);
                           }
                        }
                     );
                  break;
               case "Multi Aura Demon":
                  MC.theWorld
                     .getLoadedEntityList()
                     .stream()
                     .filter(entity -> entity instanceof EntityLivingBase)
                     .forEach(
                        entity -> {
                           EntityLivingBase target = (EntityLivingBase)entity;
                           if (EntityUtils.skipEntity(target, this)
                              && (
                                 !Ries.INSTANCE.getSettingManager().getSetting(this, "INTERNAL_WALLS", Setting.Type.CHECKBOX).isTicked()
                                    || MC.thePlayer.canEntityBeSeen(entity)
                              )) {
                              float[] rotations = this.doRotations(target, e);
                              this.doShooting(7, rotations);
                           }
                        }
                     );
                  break;
               case "Legacy Guardian":
                  if (MC.thePlayer.onGround) {
                     MC.theWorld
                        .getLoadedEntityList()
                        .stream()
                        .filter(entity -> entity instanceof EntityLivingBase)
                        .forEach(
                           entity -> {
                              EntityLivingBase target = (EntityLivingBase)entity;
                              if (EntityUtils.skipEntity(target, this)
                                 && (
                                    !Ries.INSTANCE.getSettingManager().getSetting(this, "INTERNAL_WALLS", Setting.Type.CHECKBOX).isTicked()
                                       || MC.thePlayer.canEntityBeSeen(entity)
                                 )) {
                                 float[] rotations = this.doRotations(target, e);
                                 this.doShootingLegacyGuardian(rotations);
                              }
                           }
                        );
                  }
            }
         }
      }
   }

   private float[] doRotations(EntityLivingBase target, EventMotionUpdate e) {
      float[] rotations = RotationUtils.doBasicRotations(target);
      if (Ries.INSTANCE.getSettingManager().getSetting(this, "INTERNAL_LOCK_VIEW", Setting.Type.CHECKBOX).isTicked()) {
         MC.thePlayer.rotationYaw = rotations[0];
         MC.thePlayer.rotationPitch = MathHelper.clamp_float(rotations[1] - 3.0F, -90.0F, 90.0F);
      } else {
         e.setRotationYaw(rotations[0]);
         e.setRotationPitch(MathHelper.clamp_float(rotations[1] - 3.0F, -90.0F, 90.0F));
      }

      return new float[]{rotations[0], MathHelper.clamp_float(rotations[1] - 3.0F, -90.0F, 90.0F)};
   }

   private void doShooting(int iterations, float[] rotations) {
      for(int iteration = 0; iteration < iterations; ++iteration) {
         MC.playerController.sendUseItem(MC.thePlayer, MC.theWorld, MC.thePlayer.getHeldItem());

         for(int packetCount = 0; packetCount < 20; ++packetCount) {
            MC.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C05PacketPlayerLook(rotations[0], rotations[1], true));
         }

         MC.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
      }
   }

   private void doShootingLegacyVerus(float[] rotations) {
      for(int iteration = 0; iteration < 1; ++iteration) {
         MC.playerController.sendUseItem(MC.thePlayer, MC.theWorld, MC.thePlayer.getHeldItem());

         for(int packetCount = 0; packetCount < 20; ++packetCount) {
            MC.thePlayer
               .sendQueue
               .addToSendQueue(
                  new C03PacketPlayer.C06PacketPlayerPosLook(
                     MC.thePlayer.posX, MC.thePlayer.getEntityBoundingBox().minY, MC.thePlayer.posZ, rotations[0], rotations[1], MC.thePlayer.onGround
                  )
               );
            MC.thePlayer
               .sendQueue
               .addToSendQueue(
                  new C0CPacketInput(MC.thePlayer.moveStrafing, MC.thePlayer.moveForward, MC.thePlayer.movementInput.jump, MC.thePlayer.movementInput.sneak)
               );
         }

         MC.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
      }
   }

   private void doShootingLegacyGuardian(float[] rotations) {
      if (MC.thePlayer.ticksExisted % 3 == 0) {
         MC.thePlayer
            .sendQueue
            .addToSendQueue(
               new C03PacketPlayer.C04PacketPlayerPosition(
                  MC.thePlayer.posX, MC.thePlayer.getEntityBoundingBox().minY - 1.0, MC.thePlayer.posZ, MC.thePlayer.onGround
               )
            );
      }

      for(int iteration = 0; iteration < 1; ++iteration) {
         MC.playerController.sendUseItem(MC.thePlayer, MC.theWorld, MC.thePlayer.getHeldItem());

         for(int packetCount = 0; packetCount < 20; ++packetCount) {
            MC.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C05PacketPlayerLook(rotations[0], rotations[1], MC.thePlayer.onGround));
         }

         MC.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
      }
   }
}
