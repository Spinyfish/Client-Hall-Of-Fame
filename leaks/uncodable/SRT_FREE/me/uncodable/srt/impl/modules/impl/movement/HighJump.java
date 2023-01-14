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
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S27PacketExplosion;
import net.minecraft.util.MathHelper;
import store.intent.intentguard.annotation.Native;

@ModuleInfo(
   internalName = "HighJump",
   name = "High Jump",
   desc = "Allows you to jump astronomically high.",
   category = Module.Category.MOVEMENT
)
public class HighJump extends Module {
   private static final String INTERNAL_SPEED_VALUE = "INTERNAL_SPEED_VALUE";
   private static final String INTERNAL_VERTICAL_MOTION_VALUE = "INTERNAL_VERTICAL_MOTION_VALUE";
   private static final String INTERNAL_BREAK_ON_TOGGLE = "INTERNAL_BREAK_ON_TOGGLE";
   private static final String INTERNAL_SPEED_SHIFTING = "INTERNAL_SPEED_SHIFTING";
   private static final String INTERNAL_SPEED_SHIFTING_VALUE = "INTERNAL_SPEED_SHIFTING_VALUE";
   private static final String COMBO_BOX_SETTING_NAME = "High Jump Mode";
   private static final String SPEED_VALUE_SETTING_NAME = "Speed";
   private static final String VERTICAL_MOTION_VALUE_SETTING_NAME = "Vertical Motion";
   private static final String BREAK_ON_TOGGLE_SETTING_NAME = "Break On Toggle";
   private static final String SPEED_SHIFTING_SETTING_NAME = "Manual Mode";
   private static final String SPEED_SHIFTING_VALUE_SETTING_NAME = "Speed Shift";
   private static final String DRAGPAK = "Dragpak";
   private static final String DEMON = "Demon";
   private static final String HELLCAT = "Hellcat";
   private static final String HOVER = "Hover";
   private static final String VANILLA = "Vanilla";
   private static final String FALL_DAMAGE = "Fall Damage";
   private static final String ZERO = "y = 0";
   private int gear;
   private boolean jumped;

   public HighJump(int key, boolean enabled) {
      super(key, enabled);
      Ries.INSTANCE
         .getSettingManager()
         .addComboBox(this, "INTERNAL_GENERAL_COMBO_BOX", "High Jump Mode", "Dragpak", "Demon", "Hellcat", "Hover", "Vanilla", "Fall Damage", "y = 0");
      Ries.INSTANCE.getSettingManager().addSlider(this, "INTERNAL_SPEED_VALUE", "Speed", 5.0, 0.0, 10.0);
      Ries.INSTANCE.getSettingManager().addSlider(this, "INTERNAL_VERTICAL_MOTION_VALUE", "Vertical Motion", 5.0, 0.0, 10.0);
      Ries.INSTANCE.getSettingManager().addSlider(this, "INTERNAL_SPEED_SHIFTING_VALUE", "Speed Shift", 0.5, 0.0, 10.0);
      Ries.INSTANCE.getSettingManager().addCheckbox(this, "INTERNAL_BREAK_ON_TOGGLE", "Break On Toggle", true);
      Ries.INSTANCE.getSettingManager().addCheckbox(this, "INTERNAL_SPEED_SHIFTING", "Manual Mode", true);
   }

   @Override
   public void onDisable() {
      if (Ries.INSTANCE.getSettingManager().getSetting(this, "INTERNAL_BREAK_ON_TOGGLE", Setting.Type.CHECKBOX).isTicked() && MovementUtils.isMoving2()) {
         MovementUtils.zeroMotion2();
      }

      String var1 = Ries.INSTANCE.getSettingManager().getSetting(this, "INTERNAL_GENERAL_COMBO_BOX", Setting.Type.COMBO_BOX).getCurrentCombo();
      switch(var1) {
         case "Hellcat":
         case "Demon":
            MC.renderGlobal.loadRenderers();
         default:
            this.gear = 0;
            this.jumped = false;
      }
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
      double speed = Ries.INSTANCE.getSettingManager().getSetting(this, "INTERNAL_SPEED_VALUE", Setting.Type.SLIDER).getCurrentValue();
      double verticalMotion = Ries.INSTANCE.getSettingManager().getSetting(this, "INTERNAL_VERTICAL_MOTION_VALUE", Setting.Type.SLIDER).getCurrentValue();
      String mode = Ries.INSTANCE.getSettingManager().getSetting(this, "INTERNAL_GENERAL_COMBO_BOX", Setting.Type.COMBO_BOX).getCurrentCombo();
      if (e.getState() == EventMotionUpdate.State.PRE) {
         switch(mode) {
            case "Vanilla":
               if (MovementUtils.isMoving()) {
                  if (MC.thePlayer.onGround) {
                     MC.thePlayer.motionY = verticalMotion;
                  }

                  MovementUtils.setMoveSpeed(speed);
               }
               break;
            case "Hover":
               if (MovementUtils.isMoving()) {
                  if (MC.thePlayer.onGround) {
                     MC.thePlayer.motionY = verticalMotion;
                  }

                  MovementUtils.setMoveSpeed(speed);
                  if (MC.thePlayer.fallDistance > 0.0F) {
                     MC.thePlayer.motionY = 0.0;
                  }
               }
               break;
            case "Fall Damage":
               if (MC.thePlayer.fallDistance > 4.0F && !this.jumped) {
                  e.setOnGround(true);
                  MovementUtils.setMoveSpeed(speed);
                  MC.thePlayer.motionY = verticalMotion;
                  this.jumped = true;
               } else if (MC.thePlayer.onGround) {
                  this.jumped = false;
               }
               break;
            case "y = 0":
               if (MC.thePlayer.posY <= 0.0 && MC.thePlayer.hurtTime > 0 && !this.jumped) {
                  MovementUtils.setMoveSpeed(speed);
                  MC.thePlayer.motionY = verticalMotion;
                  this.jumped = true;
               } else if (MC.thePlayer.onGround) {
                  this.jumped = false;
               }
               break;
            case "Hellcat":
            case "Demon":
            case "Dragpak":
               if (MovementUtils.isMoving()) {
                  if (MC.thePlayer.onGround) {
                     MC.thePlayer.motionY = verticalMotion;
                  }

                  MovementUtils.setMoveSpeed(speed);
               }

               if (MC.thePlayer.fallDistance > 0.0F) {
                  MC.thePlayer.motionY = 0.0;
                  if (MovementUtils.isMoving()) {
                     MovementUtils.setMoveSpeedTeleport(mode.equals("Hellcat") ? 1000.0 : (mode.equals("Demon") ? 10000.0 : 100000.0));
                  }
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
         case "Fall Damage":
            e.setCancelled(this.jumped && (e.getPacket() instanceof S12PacketEntityVelocity || e.getPacket() instanceof S27PacketExplosion));
      }
   }
}
