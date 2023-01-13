package me.uncodable.srt.impl.modules.impl.movement;

import me.uncodable.srt.Ries;
import me.uncodable.srt.impl.events.api.EventTarget;
import me.uncodable.srt.impl.events.events.entity.EventJump;
import me.uncodable.srt.impl.events.events.entity.EventMotionUpdate;
import me.uncodable.srt.impl.events.events.packet.EventPacket;
import me.uncodable.srt.impl.events.events.peripheral.EventKeyPress;
import me.uncodable.srt.impl.modules.api.Module;
import me.uncodable.srt.impl.modules.api.ModuleInfo;
import me.uncodable.srt.impl.modules.api.settings.Setting;
import me.uncodable.srt.impl.utils.MovementUtils;
import me.uncodable.srt.impl.utils.PacketUtils;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.potion.Potion;
import net.minecraft.util.MathHelper;
import store.intent.intentguard.annotation.Native;

@ModuleInfo(
   internalName = "Speed",
   name = "Speed",
   desc = "Allows you to increase your movement speed drastically.\n(P.S try out the Dragpak, Hellcat, and Demon modes!)",
   category = Module.Category.MOVEMENT
)
public class Speed extends Module {
   private static final String INTERNAL_SPEED_VALUE = "INTERNAL_SPEED_VALUE";
   private static final String INTERNAL_BREAK_ON_TOGGLE = "INTERNAL_BREAK_ON_TOGGLE";
   private static final String INTERNAL_SPEED_SHIFTING = "INTERNAL_SPEED_SHIFTING";
   private static final String INTERNAL_SPEED_SHIFTING_VALUE = "INTERNAL_SPEED_SHIFTING_VALUE";
   private static final String COMBO_BOX_SETTING_NAME = "Speed Mode";
   private static final String SPEED_VALUE_SETTING_NAME = "Speed";
   private static final String BREAK_ON_TOGGLE_SETTING_NAME = "Break On Toggle";
   private static final String SPEED_SHIFTING_SETTING_NAME = "Manual Mode";
   private static final String SPEED_SHIFTING_VALUE_SETTING_NAME = "Speed Shift";
   private static final String DRAGPAK = "Dragpak";
   private static final String DEMON = "Demon";
   private static final String HELLCAT = "Hellcat";
   private static final String VANILLA_BHOP = "Vanilla Bunny Hop";
   private static final String VANILLA_GROUND = "Vanilla Ground";
   private static final String VANILLA_BHOP_FRICTION = "Vanilla Bunny Hop Friction";
   private static final String VERUS = "Verus";
   private static final String ANTICHEAT_RELOADED = "AntiCheat Reloaded";
   private static final String VENOM = "Venom";
   private static final String NCP = "NoCheat+";
   private static final String LEGACY_WATCHDOG = "Legacy Watchdog";
   private static final String VERUS_II = "Verus II";
   private static final String VERUS_III = "Verus III";
   private static final String SPARTAN = "Spartan";
   private static final String LEGACY_VOID = "Legacy Void Anti-Cheat";
   private static final String LEGACY_JI = "Legacy JI Janitor";
   private static final String LEGACY_AGC_II = "Legacy AntiGamingChair II";
   private static final String LEGACY_AGC_III = "Legacy AntiGamingChair III";
   private static final String LEGACY_ALICE = "Legacy Alice";
   private static final String LEGACY_ANTIVIRUS = "Legacy Antivirus";
   private static final String ANTIHAXERMAN = "AntiHaxerman";
   private static final String GUARD = "Guard";
   private static final String LEGACY_AGC = "Legacy AntiGamingChair";
   private static final String TROJAN = "Trojan";
   private static final String MATRIX = "Matrix";
   private static final String HURT_TIME = "Hurt-Time";
   private static final String GWEN = "GWEN";
   private static final String SPACE_POLICE = "Space Police";
   private static final String AAC_3_3_15 = "AAC 3.3.15";
   private static final String SYUU = "Syuu";
   private static final String SYUU_II = "Syuu II";
   private static final String CARBON = "Carbon";
   private static final String AREA_51 = "Area 51";
   private static final String WATCHDOG_TEST = "Watchdog DEV";
   private static final String VULCAN = "Vulcan";
   private double lastSpeed;
   private float oldTimerSpeed;
   private int gear;
   private int stage;

   public Speed(int key, boolean enabled) {
      super(key, enabled);
      Ries.INSTANCE
         .getSettingManager()
         .addComboBox(
            this,
            "INTERNAL_GENERAL_COMBO_BOX",
            "Speed Mode",
            "Dragpak",
            "Demon",
            "Hellcat",
            "Vanilla Bunny Hop",
            "Vanilla Ground",
            "Vanilla Bunny Hop Friction",
            "Hurt-Time",
            "Verus",
            "Verus II",
            "Verus III",
            "Vulcan",
            "Spartan",
            "Matrix",
            "AntiCheat Reloaded",
            "Venom",
            "NoCheat+",
            "Watchdog DEV",
            "Legacy Watchdog",
            "Syuu",
            "Syuu II",
            "GWEN",
            "Legacy Void Anti-Cheat",
            "Legacy JI Janitor",
            "Trojan",
            "Legacy Alice",
            "Legacy Antivirus",
            "AntiHaxerman",
            "Guard",
            "Legacy AntiGamingChair",
            "Legacy AntiGamingChair II",
            "Legacy AntiGamingChair III",
            "Space Police",
            "AAC 3.3.15",
            "Carbon",
            "Area 51"
         );
      Ries.INSTANCE.getSettingManager().addSlider(this, "INTERNAL_SPEED_VALUE", "Speed", 5.0, 0.0, 10.0);
      Ries.INSTANCE.getSettingManager().addSlider(this, "INTERNAL_SPEED_SHIFTING_VALUE", "Speed Shift", 0.5, 0.0, 10.0);
      Ries.INSTANCE.getSettingManager().addCheckbox(this, "INTERNAL_BREAK_ON_TOGGLE", "Break On Toggle", true);
      Ries.INSTANCE.getSettingManager().addCheckbox(this, "INTERNAL_SPEED_SHIFTING", "Manual Mode", true);
   }

   @Override
   public void onEnable() {
      String var1 = Ries.INSTANCE.getSettingManager().getSetting(this, "INTERNAL_GENERAL_COMBO_BOX", Setting.Type.COMBO_BOX).getCurrentCombo();
      byte var2 = -1;
      switch(var1.hashCode()) {
         case -1336708222:
            if (var1.equals("Watchdog DEV")) {
               var2 = 0;
            }
         default:
            switch(var2) {
               case 0:
                  Module disabler = Ries.INSTANCE.getModuleManager().getModuleByName("Disabler");
                  Ries.INSTANCE
                     .getSettingManager()
                     .getSetting(disabler, "INTERNAL_GENERAL_COMBO_BOX", Setting.Type.COMBO_BOX)
                     .setCurrentCombo("Verus Partial");
                  if (!disabler.isEnabled()) {
                     disabler.toggle();
                  }
               default:
                  this.oldTimerSpeed = MC.timer.timerSpeed;
            }
      }
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
            MC.timer.timerSpeed = this.oldTimerSpeed;
            this.lastSpeed = 0.0;
            this.stage = this.gear = 0;
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
      target = EventJump.class
   )
   public void onJump(EventJump e) {
      String var2 = Ries.INSTANCE.getSettingManager().getSetting(this, "INTERNAL_GENERAL_COMBO_BOX", Setting.Type.COMBO_BOX).getCurrentCombo();
      switch(var2) {
         case "Matrix":
         case "Guard":
         case "Demon":
         case "Hellcat":
         case "Verus II":
         case "AntiCheat Reloaded":
         case "Space Police":
         case "Carbon":
         case "Spartan":
            e.setCancelled(true);
      }
   }

   @EventTarget(
      target = EventMotionUpdate.class
   )
   @Native
   public void onMotion(EventMotionUpdate e) {
      if (e.getState() == EventMotionUpdate.State.PRE) {
         double speed = Ries.INSTANCE.getSettingManager().getSetting(this, "INTERNAL_SPEED_VALUE", Setting.Type.SLIDER).getCurrentValue();
         String mode = Ries.INSTANCE.getSettingManager().getSetting(this, "INTERNAL_GENERAL_COMBO_BOX", Setting.Type.COMBO_BOX).getCurrentCombo();
         switch(mode) {
            case "Hurt-Time":
               if (MC.thePlayer.hurtTime > 0 && MC.thePlayer.hurtTime < 5) {
                  MovementUtils.setMoveSpeed(speed);
               }
               break;
            case "Vanilla Bunny Hop Friction":
               if (!MC.thePlayer.onGround) {
                  MovementUtils.addFriction();
               }
            case "Vanilla Bunny Hop":
               if (MovementUtils.isMoving()) {
                  if (MC.thePlayer.onGround) {
                     MC.thePlayer.jump();
                  }

                  MovementUtils.setMoveSpeed(speed);
               } else {
                  MovementUtils.zeroMotion();
               }
               break;
            case "Area 51":
               MC.timer.timerSpeed = 0.4F;
               if (MovementUtils.isMoving()) {
                  if (MC.thePlayer.onGround) {
                     MC.thePlayer.jump();
                  }

                  MovementUtils.setMoveSpeed(speed);
               } else {
                  MovementUtils.zeroMotion();
               }
               break;
            case "Vanilla Ground":
               MovementUtils.setMoveSpeed(MovementUtils.isMoving() ? speed : 0.0);
               break;
            case "Hellcat":
            case "Demon":
            case "Dragpak":
               if (MovementUtils.isMoving()) {
                  MovementUtils.setMoveSpeedTeleport(mode.equals("Hellcat") ? 1000.0 : (mode.equals("Demon") ? 10000.0 : 100000.0));
               }
               break;
            case "Vulcan":
               double hurtTimeBoost = MC.thePlayer.hurtTime > 0 ? 0.5 : 0.0;
               if (MovementUtils.isMoving() && MC.thePlayer.onGround) {
                  MovementUtils.setMoveSpeed(MovementUtils.getBaseMoveSpeed() + 0.015 + hurtTimeBoost);
                  MC.thePlayer.jump();
               }
               break;
            case "Verus":
               if (MovementUtils.isMoving()) {
                  double hurtTimeBoost = MC.thePlayer.hurtTime > 0 ? 0.15 : 0.0;
                  double speedEffect = MC.thePlayer.isPotionActive(Potion.moveSpeed)
                     ? (double)MC.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier() * 0.1 - 0.01445
                     : 0.0;
                  MovementUtils.setMoveSpeed(MovementUtils.getBaseMoveSpeed() + 0.0445 - speedEffect + hurtTimeBoost);
                  if (MC.thePlayer.onGround) {
                     MovementUtils.setMoveSpeed(MovementUtils.getBaseMoveSpeed() - 0.021 + hurtTimeBoost);
                     MC.thePlayer.jump();
                  }
               }
               break;
            case "Verus III":
               if (MovementUtils.isMoving()) {
                  double hurtTimeBoost = MC.thePlayer.hurtTime > 0 && MC.thePlayer.hurtTime < 16 ? 0.6 : 0.0;
                  double speedEffect = MC.thePlayer.isPotionActive(Potion.moveSpeed)
                     ? (double)MC.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier() * 0.1 - 0.01445
                     : 0.0;
                  MovementUtils.setMoveSpeed(MovementUtils.getBaseMoveSpeed() + 0.0445 - speedEffect + hurtTimeBoost);
                  if (MC.thePlayer.onGround) {
                     MovementUtils.setMoveSpeed(MovementUtils.getBaseMoveSpeed() - 0.021 + hurtTimeBoost);
                     MC.thePlayer.jump();
                  }
               }
               break;
            case "AntiCheat Reloaded":
               double decrement = (
                     MC.thePlayer.getActivePotionEffect(Potion.moveSpeed) != null
                        ? (double)MC.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier() * 0.05
                        : 0.0
                  )
                  - ((float)MC.thePlayer.hurtTime > 0.0F ? 0.65 : 0.0);
               if (MC.thePlayer.ticksExisted % 3 == 0) {
                  MovementUtils.setMoveSpeed(MovementUtils.getBaseMoveSpeed() + 0.07 - decrement);
               }
               break;
            case "Venom":
               if (MovementUtils.isMoving()) {
                  double hawkSpeed = MovementUtils.getBaseMoveSpeed();
                  if (MC.thePlayer.onGround) {
                     MC.thePlayer.motionY = 0.42F;
                  }

                  if (this.targetEntity == null && MC.thePlayer.moveStrafing == 0.0F) {
                     if (MC.thePlayer.fallDistance <= 0.0F) {
                        hawkSpeed += 0.095;
                     } else {
                        hawkSpeed += 0.03;
                     }
                  }

                  if (MC.thePlayer.hurtTime > 0) {
                     hawkSpeed += 0.2;
                  }

                  MovementUtils.setMoveSpeed(hawkSpeed);
               } else {
                  MovementUtils.zeroMotion();
               }
               break;
            case "Watchdog DEV":
               if (MovementUtils.isMoving()) {
                  double amplifierBoost = MC.thePlayer.isPotionActive(Potion.moveSpeed)
                     ? (double)MC.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier() * 0.01
                     : 0.0;
                  double ncpSpeed = MovementUtils.getBaseMoveSpeed() + 0.11 + amplifierBoost;
                  if (MC.thePlayer.hurtTime > 3 && MC.thePlayer.hurtTime < 19) {
                     ncpSpeed += 0.02;
                  }

                  if (MC.thePlayer.onGround) {
                     this.lastSpeed = ncpSpeed;
                     MC.thePlayer.jump();
                  } else {
                     MovementUtils.setMoveSpeed(this.lastSpeed *= 0.95);
                  }
               } else {
                  MovementUtils.zeroMotion();
               }
               break;
            case "Legacy Watchdog":
               if (MovementUtils.isMoving() && MC.thePlayer.moveStrafing == 0.0F && MC.gameSettings.keyBindForward.isKeyDown()) {
                  double amplifierBoost = MC.thePlayer.isPotionActive(Potion.moveSpeed)
                     ? (double)MC.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier() * 0.01
                     : 0.0;
                  double ncpSpeed = MovementUtils.getBaseMoveSpeed() + 0.0911495 + amplifierBoost;
                  if (MC.thePlayer.hurtTime > 3 && MC.thePlayer.hurtTime < 19) {
                     ncpSpeed += 0.02;
                  }

                  if (MC.thePlayer.onGround) {
                     this.lastSpeed = ncpSpeed;
                     MC.thePlayer.jump();
                  } else {
                     MovementUtils.setMoveSpeed(this.lastSpeed *= 0.9685);
                  }
               } else {
                  MovementUtils.zeroMotion();
               }
               break;
            case "NoCheat+":
               if (MovementUtils.isMoving()) {
                  double amplifierBoost = MC.thePlayer.isPotionActive(Potion.moveSpeed)
                     ? (double)MC.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier() * 0.0095
                     : 0.0;
                  double ncpSpeed = MovementUtils.getBaseMoveSpeed() + 0.091154 + amplifierBoost;
                  if (MC.thePlayer.hurtTime > 3 && MC.thePlayer.hurtTime < 19) {
                     ncpSpeed += 0.05;
                  }

                  if (MC.thePlayer.onGround) {
                     this.lastSpeed = ncpSpeed;
                     MC.thePlayer.jump();
                  } else {
                     MovementUtils.setMoveSpeed(this.lastSpeed *= 0.97F);
                  }
               } else {
                  MovementUtils.zeroMotion();
               }
               break;
            case "Verus II":
               if (MovementUtils.isMoving() && MC.thePlayer.onGround) {
                  MovementUtils.addFriction(1.22);
               }
               break;
            case "Legacy Void Anti-Cheat":
               MC.thePlayer.motionY = 0.0;
               MovementUtils.setMoveSpeed(MovementUtils.isMoving() ? 4.0 : 0.0);
               break;
            case "Legacy JI Janitor":
               double speedDecrement = MC.thePlayer.isPotionActive(Potion.moveSpeed)
                  ? (double)MC.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier() * 0.09
                  : 0.0;
               double jiSpeed = MovementUtils.getBaseMoveSpeed() + 0.27 - speedDecrement;
               if (MovementUtils.isMoving()) {
                  if (MC.thePlayer.onGround) {
                     MC.thePlayer.jump();
                  }

                  MovementUtils.setMoveSpeed(jiSpeed);
               } else {
                  MovementUtils.zeroMotion();
               }
               break;
            case "Legacy AntiGamingChair II":
               if (MC.thePlayer.onGround) {
                  MC.thePlayer.jump();
                  MC.timer.timerSpeed = 0.5F;
               } else if (MC.thePlayer.fallDistance > 0.0F) {
                  MC.timer.timerSpeed = 40.0F;
               }
               break;
            case "Legacy Alice":
               if (!MovementUtils.isMoving()
                  || !MC.thePlayer.onGround
                  || MC.gameSettings.keyBindJump.isKeyDown()
                  || MC.thePlayer.moveStrafing != 0.0F
                  || MC.thePlayer.fallDistance != 0.0F) {
                  MovementUtils.addFriction();
               } else if (MC.thePlayer.ticksExisted % 2 == 0) {
                  e.setPosY(MC.thePlayer.getEntityBoundingBox().minY - 0.01);
               } else {
                  MovementUtils.setMoveSpeed(MovementUtils.getBaseMoveSpeed() + 0.17);
               }
               break;
            case "Legacy Antivirus":
               if (MC.thePlayer.ticksExisted % 2 == 0) {
                  MovementUtils.setMoveSpeed(speed);
               } else {
                  MovementUtils.zeroMotion();
               }
               break;
            case "AntiHaxerman":
               if (MovementUtils.isMoving()) {
                  if (MC.thePlayer.onGround) {
                     MC.thePlayer.jump();
                     this.stage = 0;
                  } else if (this.stage == 0) {
                     MovementUtils.addFriction(1.022);
                     ++this.stage;
                  }
               }
               break;
            case "Legacy AntiGamingChair":
               if (MovementUtils.isMoving()) {
                  MovementUtils.setMoveSpeed(MC.thePlayer.ticksExisted % 3 == 0 ? speed : 0.0);
                  if (MC.thePlayer.onGround) {
                     MC.thePlayer.jump();
                  }
               } else {
                  MovementUtils.zeroMotion();
               }
               break;
            case "Guard":
               MovementUtils.setMoveSpeed(
                  MC.thePlayer.moveStrafing == 0.0F && MC.thePlayer.ticksExisted % 2 == 0 ? MovementUtils.getBaseMoveSpeed() + 0.2653 : 0.0
               );
               break;
            case "Spartan":
               double increment = MC.thePlayer.onGround ? 0.0851 : 0.0;
               if (MovementUtils.isMoving() && MC.gameSettings.keyBindForward.isKeyDown() && MC.thePlayer.moveStrafing == 0.0F) {
                  MovementUtils.setMoveSpeed(MovementUtils.getBaseMoveSpeed() + increment);
               }
               break;
            case "Legacy AntiGamingChair III":
               ++this.stage;
               Ries.INSTANCE.msg("stage: " + this.stage);
               if (this.stage > 2) {
                  MC.timer.timerSpeed = 20.0F;
               } else {
                  MC.timer.timerSpeed = 0.01F;
               }

               if (this.stage > 220) {
                  this.stage = 0;
                  this.toggle();
               }
               break;
            case "Trojan":
               if (MovementUtils.isMoving()) {
                  MovementUtils.setMoveSpeed(MovementUtils.getBaseMoveSpeed() + 0.04);
                  if (MC.thePlayer.onGround) {
                     MC.thePlayer.jump();
                  }
               } else {
                  MovementUtils.zeroMotion();
               }
               break;
            case "Matrix":
               if (MovementUtils.isMoving() && MC.gameSettings.keyBindForward.isKeyDown() && MC.thePlayer.moveStrafing == 0.0F) {
                  MovementUtils.addFriction(1.02285);
               } else {
                  MovementUtils.zeroMotion();
               }
               break;
            case "GWEN":
               if (MovementUtils.isMoving()) {
                  if (MC.thePlayer.getActivePotionEffect(Potion.moveSpeed) != null) {
                     MovementUtils.setMoveSpeed(MovementUtils.getBaseMoveSpeed());
                  } else {
                     MovementUtils.setMoveSpeed(MovementUtils.getBaseMoveSpeed() + 0.05);
                  }

                  if (MC.thePlayer.onGround) {
                     MC.thePlayer.jump();
                  }
               } else {
                  MovementUtils.zeroMotion();
               }
               break;
            case "Space Police":
               if (MovementUtils.isMoving()) {
                  if (MC.thePlayer.hurtTime > 0 && MC.thePlayer.hurtTime < 10) {
                     MovementUtils.setMoveSpeed(MovementUtils.getBaseMoveSpeed() + 0.5);
                  } else if (MC.thePlayer.onGround) {
                     MovementUtils.addFriction(1.3);
                  }
               }
               break;
            case "AAC 3.3.15":
               if (MovementUtils.isMoving() && MC.thePlayer.onGround && MC.gameSettings.keyBindForward.isKeyDown()) {
                  MC.thePlayer.jump();
                  MovementUtils.setMoveSpeed(MovementUtils.getBaseMoveSpeed());
                  MovementUtils.addFriction(1.7);
               }
               break;
            case "Syuu":
               if (MovementUtils.isMoving()) {
                  double amplifierBoost = MC.thePlayer.isPotionActive(Potion.moveSpeed)
                     ? (double)MC.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier() * 0.01
                     : 0.0;
                  double ncpSpeed = MovementUtils.getBaseMoveSpeed() + 0.089 + amplifierBoost;
                  if (MC.thePlayer.hurtTime > 3 && MC.thePlayer.hurtTime < 19) {
                     ncpSpeed += 0.05;
                  }

                  if (MC.thePlayer.onGround) {
                     this.lastSpeed = ncpSpeed;
                     MC.thePlayer.jump();
                  } else {
                     MovementUtils.setMoveSpeed(this.lastSpeed *= 0.97F);
                  }
               } else {
                  MovementUtils.zeroMotion();
               }
               break;
            case "Syuu II":
               if (MovementUtils.isMoving()) {
                  double amplifierBoost = MC.thePlayer.isPotionActive(Potion.moveSpeed)
                     ? (double)MC.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier() * 0.12
                     : 0.0;
                  double ncpSpeed = MovementUtils.getBaseMoveSpeed() + 0.2 + amplifierBoost;
                  if (MC.thePlayer.hurtTime > 3 && MC.thePlayer.hurtTime < 19) {
                     ncpSpeed += 0.05;
                  }

                  if (MC.thePlayer.onGround) {
                     this.lastSpeed = ncpSpeed;
                     MC.thePlayer.jump();
                  } else {
                     MovementUtils.setMoveSpeed(this.lastSpeed *= 0.97F);
                  }
               } else {
                  MovementUtils.zeroMotion();
               }
               break;
            case "Carbon":
               if (MovementUtils.isMoving() && MC.thePlayer.onGround) {
                  double carbonSpeed = MovementUtils.getBaseMoveSpeed() + 0.5;
                  if (MC.thePlayer.hurtTime > 0) {
                     carbonSpeed *= 2.0;
                  }

                  MovementUtils.setMoveSpeed(carbonSpeed);
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
         case "Verus":
            if (e.getPacket() instanceof C0BPacketEntityAction) {
               C0BPacketEntityAction packet = PacketUtils.getPacket(e.getPacket());
               e.setCancelled(
                  packet.getAction() == C0BPacketEntityAction.Action.START_SPRINTING || packet.getAction() == C0BPacketEntityAction.Action.STOP_SPRINTING
               );
            }
      }
   }
}
