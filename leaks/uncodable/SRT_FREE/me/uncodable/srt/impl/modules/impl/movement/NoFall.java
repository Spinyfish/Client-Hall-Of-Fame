package me.uncodable.srt.impl.modules.impl.movement;

import me.uncodable.srt.Ries;
import me.uncodable.srt.impl.events.api.EventTarget;
import me.uncodable.srt.impl.events.events.block.EventAddCollision;
import me.uncodable.srt.impl.events.events.entity.EventMotionUpdate;
import me.uncodable.srt.impl.modules.api.Module;
import me.uncodable.srt.impl.modules.api.ModuleInfo;
import me.uncodable.srt.impl.modules.api.settings.Setting;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import store.intent.intentguard.annotation.Native;

@ModuleInfo(
   internalName = "NoFall",
   name = "No Fall",
   desc = "Allows you to never take fall damage again.",
   category = Module.Category.MOVEMENT
)
public class NoFall extends Module {
   private static final String COMBO_BOX_SETTING_NAME = "No Fall Mode";
   private static final String GROUND_SPOOF_TRUE = "Ground Spoof True";
   private static final String GROUND_SPOOF_FALSE = "Ground Spoof False";
   private static final String GROUND_SPOOF_ALTERNATE = "Ground Spoof Alternate";
   private static final String VERUS = "Verus";
   private static final String LEGACY_NCP = "Legacy NoCheat+ Flag";
   private static final String DAMAGE = "Damage";
   private float counter = 0.0F;

   public NoFall(int key, boolean enabled) {
      super(key, enabled);
      Ries.INSTANCE
         .getSettingManager()
         .addComboBox(
            this,
            "INTERNAL_GENERAL_COMBO_BOX",
            "No Fall Mode",
            "Ground Spoof True",
            "Ground Spoof False",
            "Ground Spoof Alternate",
            "Damage",
            "Verus",
            "Legacy NoCheat+ Flag"
         );
   }

   @Override
   public void onDisable() {
      this.counter = 0.0F;
   }

   @EventTarget(
      target = EventMotionUpdate.class
   )
   @Native
   public void onMotion(EventMotionUpdate e) {
      String mode = Ries.INSTANCE.getSettingManager().getSetting(this, "INTERNAL_GENERAL_COMBO_BOX", Setting.Type.COMBO_BOX).getCurrentCombo();
      switch(mode) {
         case "Legacy NoCheat+ Flag":
            BlockPos blockPos = new BlockPos(MC.thePlayer.posX, MC.thePlayer.posY - 1.0, MC.thePlayer.posZ);
            BlockPos blockPos2 = new BlockPos(MC.thePlayer.posX, MC.thePlayer.posY - 2.0, MC.thePlayer.posZ);
            BlockPos blockPos3 = new BlockPos(MC.thePlayer.posX, MC.thePlayer.posY - 3.0, MC.thePlayer.posZ);
            Block block = MC.theWorld.getBlockState(blockPos).getBlock();
            Block block2 = MC.theWorld.getBlockState(blockPos2).getBlock();
            Block block3 = MC.theWorld.getBlockState(blockPos3).getBlock();
            if ((block != Blocks.air || block2 != Blocks.air || block3 != Blocks.air) && MC.thePlayer.fallDistance > 2.0F) {
               MC.thePlayer
                  .sendQueue
                  .packetNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(MC.thePlayer.posX, MC.thePlayer.posY + 0.1, MC.thePlayer.posZ, false));
               MC.thePlayer.motionY -= 100.0;
               MC.thePlayer.fallDistance = 0.0F;
            }
         default:
            if (e.getState() == EventMotionUpdate.State.PRE) {
               switch(mode) {
                  case "Ground Spoof True":
                     if (MC.thePlayer.fallDistance > 2.0F) {
                        e.setOnGround(true);
                     }
                     break;
                  case "Ground Spoof False":
                     e.setOnGround(false);
                     break;
                  case "Ground Spoof Alternate":
                     if (MC.thePlayer.fallDistance > 2.0F) {
                        e.setOnGround(MC.thePlayer.ticksExisted % 2 == 0);
                     }
                     break;
                  case "Damage":
                     if (MC.thePlayer.fallDistance > 4.0F) {
                        e.setOnGround(true);
                     }
               }
            }
      }
   }

   @EventTarget(
      target = EventAddCollision.class
   )
   @Native
   public void onCollision(EventAddCollision e) {
      String var2 = Ries.INSTANCE.getSettingManager().getSetting(this, "INTERNAL_GENERAL_COMBO_BOX", Setting.Type.COMBO_BOX).getCurrentCombo();
      byte var3 = -1;
      switch(var2.hashCode()) {
         case 82544993:
            if (var2.equals("Verus")) {
               var3 = 0;
            }
         default:
            switch(var3) {
               case 0:
                  if (MC.thePlayer.fallDistance > 1.5F) {
                     if (this.counter > 3.0F) {
                        this.counter = 0.0F;
                        e.setBoundingBox(
                           new AxisAlignedBB(-3.0, -2.0, -3.0, 3.0, 1.0, 3.0)
                              .offset((double)e.getPos().getX(), (double)e.getPos().getY(), (double)e.getPos().getZ())
                        );
                     }

                     this.counter += 0.5F;
                  }
            }
      }
   }
}
