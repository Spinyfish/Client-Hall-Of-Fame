package me.uncodable.srt.impl.modules.impl.movement;

import me.uncodable.srt.Ries;
import me.uncodable.srt.impl.events.api.EventTarget;
import me.uncodable.srt.impl.events.events.entity.EventMotionUpdate;
import me.uncodable.srt.impl.events.events.packet.EventPacket;
import me.uncodable.srt.impl.events.events.peripheral.EventKeyPress;
import me.uncodable.srt.impl.modules.api.Module;
import me.uncodable.srt.impl.modules.api.ModuleInfo;
import me.uncodable.srt.impl.modules.api.settings.Setting;
import me.uncodable.srt.impl.utils.MovementUtils;
import me.uncodable.srt.impl.utils.PacketUtils;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S27PacketExplosion;
import net.minecraft.util.MathHelper;
import store.intent.intentguard.annotation.Native;

@ModuleInfo(
   internalName = "LongJump",
   name = "Long Jump",
   desc = "Allows you to set world records with the amount of blocks you hop!",
   category = Module.Category.MOVEMENT
)
public class LongJump extends Module {
   private static final String INTERNAL_SPEED_VALUE = "INTERNAL_SPEED_VALUE";
   private static final String INTERNAL_AUTO_TOGGLE = "INTERNAL_AUTO_TOGGLE";
   private static final String INTERNAL_SPEED_SHIFTING = "INTERNAL_SPEED_SHIFTING";
   private static final String INTERNAL_SPEED_SHIFTING_VALUE = "INTERNAL_SPEED_SHIFTING_VALUE";
   private static final String COMBO_BOX_SETTING_NAME = "Long Jump Mode";
   private static final String SPEED_VALUE_SETTING_NAME = "Speed";
   private static final String AUTO_TOGGLE_SETTING_NAME = "Auto Toggle";
   private static final String SPEED_SHIFTING_SETTING_NAME = "Manual Mode";
   private static final String SPEED_SHIFTING_VALUE_SETTING_NAME = "Speed Shift";
   private static final String VANILLA = "Vanilla";
   private static final String NCP = "NoCheat+";
   private static final String SYUU = "Syuu";
   private static final String NO_GROUND_DAMAGE = "No Ground Damage";
   private boolean launched;
   private boolean setSpeed;
   private boolean damaged;
   private int gear;
   private int stage;

   public LongJump(int key, boolean enabled) {
      super(key, enabled);
      Ries.INSTANCE.getSettingManager().addComboBox(this, "INTERNAL_GENERAL_COMBO_BOX", "Long Jump Mode", "Vanilla", "No Ground Damage", "NoCheat+", "Syuu");
      Ries.INSTANCE.getSettingManager().addSlider(this, "INTERNAL_SPEED_VALUE", "Speed", 5.0, 0.0, 10.0);
      Ries.INSTANCE.getSettingManager().addSlider(this, "INTERNAL_SPEED_SHIFTING_VALUE", "Speed Shift", 0.5, 0.0, 10.0);
      Ries.INSTANCE.getSettingManager().addCheckbox(this, "INTERNAL_AUTO_TOGGLE", "Auto Toggle", true);
      Ries.INSTANCE.getSettingManager().addCheckbox(this, "INTERNAL_SPEED_SHIFTING", "Manual Mode", true);
   }

   @Override
   public void onDisable() {
      this.launched = this.setSpeed = this.damaged = false;
      MovementUtils.zeroMotion();
      this.gear = this.stage = 0;
   }

   @EventTarget(
      target = EventKeyPress.class
   )
   @Native
   public void onKeyPress(EventKeyPress e) {
      double shift = Ries.INSTANCE.getSettingManager().getSetting(this, "INTERNAL_SPEED_SHIFTING_VALUE", Setting.Type.SLIDER).getCurrentValue();
      double speed = Ries.INSTANCE.getSettingManager().getSetting(this, "INTERNAL_SPEED_VALUE", Setting.Type.SLIDER).getCurrentValue();
      double min = Ries.INSTANCE.getSettingManager().getSetting(this, "INTERNAL_SPEED_VALUE", Setting.Type.SLIDER).getMinimumValue();
      double max = Ries.INSTANCE.getSettingManager().getSetting(this, "INTERNAL_SPEED_VALUE", Setting.Type.SLIDER).getMaximumValue();
      if (Ries.INSTANCE.getSettingManager().getSetting(this, "INTERNAL_SPEED_SHIFTING", Setting.Type.CHECKBOX).isTicked()) {
         switch(e.getKey()) {
            case 200:
               if (speed + shift > max && !MC.gameSettings.expertMode) {
                  Ries.INSTANCE
                     .msg(
                        "The speed value will exceed the maximum speed limit with this shift. The value has been rounded downwards to the maximum allowed. To bypass this limit, enable expert mode."
                     );
                  shift = Math.abs(max - speed);
               }

               ++this.gear;
               this.gear = MathHelper.clamp_int(this.gear, -1, 8);
               if (this.gear <= 8) {
                  Ries.INSTANCE.getSettingManager().getSetting(this, "INTERNAL_SPEED_VALUE", Setting.Type.SLIDER).setCurrentValue(speed + shift);
               }

               if (this.gear == 0) {
                  Ries.INSTANCE.getSettingManager().getSetting(this, "INTERNAL_SPEED_VALUE", Setting.Type.SLIDER).setCurrentValue(0.0);
                  Ries.INSTANCE.msg(String.format("Shifted into neutral for \"%s.\"", this.getInfo().name()));
               } else {
                  Ries.INSTANCE.msg(String.format("Up-shifted into speed %d for \"%s.\"", this.gear, this.getInfo().name()));
               }
               break;
            case 208:
               if (speed - shift < min && !MC.gameSettings.expertMode) {
                  Ries.INSTANCE
                     .msg(
                        "The speed value will exceed the minimum speed limit with this shift. The value has been rounded upwards to the minimum allowed. To bypass this limit, enable expert mode."
                     );
                  shift = Math.abs(min - speed);
               }

               --this.gear;
               this.gear = MathHelper.clamp_int(this.gear, -1, 8);
               if (this.gear > -1) {
                  Ries.INSTANCE.getSettingManager().getSetting(this, "INTERNAL_SPEED_VALUE", Setting.Type.SLIDER).setCurrentValue(speed - shift);
               }

               switch(this.gear) {
                  case -1:
                     Ries.INSTANCE.getSettingManager().getSetting(this, "INTERNAL_SPEED_VALUE", Setting.Type.SLIDER).setCurrentValue(-shift);
                     Ries.INSTANCE.msg(String.format("Shifted into reverse for \"%s.\"", this.getInfo().name()));
                     break;
                  case 0:
                     Ries.INSTANCE.getSettingManager().getSetting(this, "INTERNAL_SPEED_VALUE", Setting.Type.SLIDER).setCurrentValue(0.0);
                     Ries.INSTANCE.msg(String.format("Shifted into neutral for \"%s.\"", this.getInfo().name()));
                     break;
                  default:
                     Ries.INSTANCE.msg(String.format("Down-shifted into speed %d for \"%s.\"", this.gear, this.getInfo().name()));
               }
         }
      }
   }

   @EventTarget(
      target = EventMotionUpdate.class
   )
   @Native
   public void onMotion(EventMotionUpdate e) {
      if (e.getState() == EventMotionUpdate.State.PRE) {
         String mode = Ries.INSTANCE.getSettingManager().getSetting(this, "INTERNAL_GENERAL_COMBO_BOX", Setting.Type.COMBO_BOX).getCurrentCombo();
         double speed = Ries.INSTANCE.getSettingManager().getSetting(this, "INTERNAL_SPEED_VALUE", Setting.Type.SLIDER).getCurrentValue();
         boolean autoToggle = Ries.INSTANCE.getSettingManager().getSetting(this, "INTERNAL_AUTO_TOGGLE", Setting.Type.CHECKBOX).isTicked();
         if (autoToggle && !mode.equals("No Ground Damage")) {
            if (!this.launched && MC.thePlayer.onGround) {
               this.launched = true;
            } else if (this.launched && MC.thePlayer.onGround) {
               this.toggle();
               return;
            }
         } else if (autoToggle) {
            if (this.damaged && !this.launched && MC.thePlayer.onGround) {
               this.launched = true;
            } else if (this.launched && MC.thePlayer.onGround && this.damaged) {
               this.toggle();
               return;
            }
         }

         switch(mode) {
            case "Vanilla":
               if (MC.thePlayer.onGround) {
                  MC.thePlayer.jump();
               }

               MovementUtils.setMoveSpeed(speed);
               break;
            case "NoCheat+":
               if (MC.thePlayer.onGround) {
                  MC.thePlayer.jump();
                  this.setSpeed = false;
               } else {
                  if (!this.setSpeed) {
                     MovementUtils.setMoveSpeed(MovementUtils.getBaseMoveSpeed() + 1.0);
                     this.setSpeed = true;
                  }

                  MovementUtils.addFriction(0.97);
               }
               break;
            case "No Ground Damage":
               if (MC.thePlayer.onGround) {
                  MC.thePlayer.jump();
                  ++this.stage;
               }

               if (this.stage > 4 || MC.thePlayer.hurtTime > 0 && MC.thePlayer.hurtTime < 18) {
                  this.stage = 0;
                  this.damaged = true;
                  if (MC.thePlayer.onGround) {
                     MC.thePlayer.jump();
                  }

                  if (MovementUtils.isMoving()) {
                     MovementUtils.setMoveSpeed(speed);
                  } else {
                     MovementUtils.zeroMotion();
                  }
               } else {
                  MovementUtils.zeroMotion();
               }
               break;
            case "Syuu":
               if (MC.thePlayer.onGround) {
                  MC.thePlayer.jump();
                  this.setSpeed = false;
               } else if (!this.setSpeed) {
                  MovementUtils.setMoveSpeed(MovementUtils.getBaseMoveSpeed() + speed);
                  this.setSpeed = true;
               }
         }
      }
   }

   @EventTarget(
      target = EventPacket.class
   )
   @Native
   public void onPacket(EventPacket e) {
      String var2 = Ries.INSTANCE.getSettingManager().getSetting(this, "INTERNAL_GENERAL_COMBO_BOX", Setting.Type.COMBO_BOX).getCurrentCombo();
      switch(var2) {
         case "No Ground Damage":
            if (this.stage <= 3 && e.getPacket() instanceof C03PacketPlayer) {
               C03PacketPlayer packet = PacketUtils.getPacket(e.getPacket());
               packet.setOnGround(false);
            }

            e.setCancelled(e.getPacket() instanceof S12PacketEntityVelocity || e.getPacket() instanceof S27PacketExplosion);
      }
   }
}
