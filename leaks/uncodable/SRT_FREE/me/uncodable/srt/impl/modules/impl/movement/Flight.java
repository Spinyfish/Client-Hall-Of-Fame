package me.uncodable.srt.impl.modules.impl.movement;

import java.util.ArrayList;
import me.uncodable.srt.Ries;
import me.uncodable.srt.impl.events.api.EventTarget;
import me.uncodable.srt.impl.events.events.block.EventAddCollision;
import me.uncodable.srt.impl.events.events.entity.EventMotionUpdate;
import me.uncodable.srt.impl.events.events.packet.EventPacket;
import me.uncodable.srt.impl.events.events.peripheral.EventKeyPress;
import me.uncodable.srt.impl.modules.api.Module;
import me.uncodable.srt.impl.modules.api.ModuleInfo;
import me.uncodable.srt.impl.modules.api.settings.Setting;
import me.uncodable.srt.impl.utils.MovementUtils;
import me.uncodable.srt.impl.utils.PacketUtils;
import me.uncodable.srt.impl.utils.Timer;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S27PacketExplosion;
import net.minecraft.potion.Potion;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import store.intent.intentguard.annotation.Native;

@ModuleInfo(
   internalName = "Flight",
   name = "Flight",
   desc = "Allows you to take flight and fly away.",
   category = Module.Category.MOVEMENT
)
@Native
public class Flight extends Module {
   private static final String INTERNAL_SPEED_VALUE = "INTERNAL_SPEED_VALUE";
   private static final String INTERNAL_BREAK_ON_TOGGLE = "INTERNAL_BREAK_ON_TOGGLE";
   private static final String INTERNAL_SPEED_SHIFTING = "INTERNAL_SPEED_SHIFTING";
   private static final String INTERNAL_SPEED_SHIFTING_VALUE = "INTERNAL_SPEED_SHIFTING_VALUE";
   private static final String COMBO_BOX_SETTING_NAME = "Flight Mode";
   private static final String SPEED_VALUE_SETTING_NAME = "Speed";
   private static final String BREAK_ON_TOGGLE_SETTING_NAME = "Break On Toggle";
   private static final String SPEED_SHIFTING_SETTING_NAME = "Manual Mode";
   private static final String SPEED_SHIFTING_VALUE_SETTING_NAME = "Speed Shift";
   private static final String CREATIVE = "Creative";
   private static final String DRAGPAK = "Dragpak";
   private static final String DEMON = "Demon";
   private static final String HELLCAT = "Hellcat";
   private static final String MOTION = "Motion";
   private static final String ANTI_KICK = "Anti-Kick Motion";
   private static final String VERUS = "Verus";
   private static final String VERUS_II = "Verus II";
   private static final String VERUS_III = "Verus III";
   private static final String COLLISION = "Collision";
   private static final String NO_GROUND_DAMAGE = "No Ground Damage";
   private static final String ANTICHEAT_RELOADED_GLIDE = "AntiCheat Reloaded Glide";
   private static final String LEGACY_NCP = "Legacy NoCheat+";
   private static final String LEGACY_ANTIVIRUS = "Legacy Antivirus";
   private static final String NORULES = "No Rules";
   private static final String LEGACY_AGC = "Legacy AntiGamingChair";
   private static final String CHUNK_GLIDE = "Chunk Glide";
   private static final String VULCAN_TEST = "Vulcan DEV";
   private static final String LEGACY_AGC_II = "Legacy AntiGamingChair II";
   private static final String LEGACY_CARBON = "Legacy Carbon";
   private static final String AREA_51 = "Area 51";
   private final ArrayList<C03PacketPlayer> players = new ArrayList<>();
   private int stage;
   private int stage2;
   private int groundTicks;
   private int gear;
   private final Timer timer = new Timer();
   private float oldTimerSpeed;
   private double decrement;
   private double x;
   private double y;
   private double z;
   private boolean damaged;

   public Flight(int key, boolean enabled) {
      super(key, enabled);
      Ries.INSTANCE
         .getSettingManager()
         .addComboBox(
            this,
            "INTERNAL_GENERAL_COMBO_BOX",
            "Flight Mode",
            "Creative",
            "Dragpak",
            "Demon",
            "Hellcat",
            "Motion",
            "Anti-Kick Motion",
            "Verus",
            "Verus II",
            "Verus III",
            "Collision",
            "No Ground Damage",
            "AntiCheat Reloaded Glide",
            "Legacy NoCheat+",
            "Legacy Antivirus",
            "No Rules",
            "Chunk Glide",
            "Vulcan DEV",
            "Legacy AntiGamingChair",
            "Legacy AntiGamingChair II",
            "Legacy Carbon",
            "Area 51"
         );
      Ries.INSTANCE.getSettingManager().addSlider(this, "INTERNAL_SPEED_VALUE", "Speed", 5.0, 0.0, 10.0);
      Ries.INSTANCE.getSettingManager().addSlider(this, "INTERNAL_SPEED_SHIFTING_VALUE", "Speed Shift", 0.5, 0.0, 10.0);
      Ries.INSTANCE.getSettingManager().addCheckbox(this, "INTERNAL_BREAK_ON_TOGGLE", "Break On Toggle", true);
      Ries.INSTANCE.getSettingManager().addCheckbox(this, "INTERNAL_SPEED_SHIFTING", "Manual Mode", true);
   }

   @Native
   @Override
   public void onEnable() {
      if (MC.thePlayer != null) {
         String var1 = Ries.INSTANCE.getSettingManager().getSetting(this, "INTERNAL_GENERAL_COMBO_BOX", Setting.Type.COMBO_BOX).getCurrentCombo();
         switch(var1) {
            case "Verus III":
               for(int i = 0; i < 3; ++i) {
                  MC.thePlayer
                     .sendQueue
                     .packetNoEvent(
                        new C03PacketPlayer.C06PacketPlayerPosLook(
                           MC.thePlayer.posX,
                           MC.thePlayer.getEntityBoundingBox().minY + 1.1,
                           MC.thePlayer.posZ,
                           MC.thePlayer.rotationYaw,
                           MC.thePlayer.rotationPitch,
                           false
                        )
                     );
                  MC.thePlayer
                     .sendQueue
                     .packetNoEvent(
                        new C03PacketPlayer.C06PacketPlayerPosLook(
                           MC.thePlayer.posX,
                           MC.thePlayer.getEntityBoundingBox().minY,
                           MC.thePlayer.posZ,
                           MC.thePlayer.rotationYaw,
                           MC.thePlayer.rotationPitch,
                           false
                        )
                     );
               }

               MC.thePlayer
                  .sendQueue
                  .packetNoEvent(
                     new C03PacketPlayer.C06PacketPlayerPosLook(
                        MC.thePlayer.posX,
                        MC.thePlayer.getEntityBoundingBox().minY,
                        MC.thePlayer.posZ,
                        MC.thePlayer.rotationYaw,
                        MC.thePlayer.rotationPitch,
                        true
                     )
                  );
               break;
            case "Verus II":
               MovementUtils.zeroMotion();

               for(int i = 0; i < 8; ++i) {
                  MC.thePlayer
                     .sendQueue
                     .packetNoEvent(
                        new C03PacketPlayer.C06PacketPlayerPosLook(
                           MC.thePlayer.posX,
                           MC.thePlayer.getEntityBoundingBox().minY + 0.42F,
                           MC.thePlayer.posZ,
                           MC.thePlayer.rotationYaw,
                           MC.thePlayer.rotationPitch,
                           false
                        )
                     );
                  MC.thePlayer
                     .sendQueue
                     .packetNoEvent(
                        new C03PacketPlayer.C06PacketPlayerPosLook(
                           MC.thePlayer.posX,
                           MC.thePlayer.getEntityBoundingBox().minY,
                           MC.thePlayer.posZ,
                           MC.thePlayer.rotationYaw,
                           MC.thePlayer.rotationPitch,
                           false
                        )
                     );
               }

               MC.thePlayer
                  .sendQueue
                  .packetNoEvent(
                     new C03PacketPlayer.C06PacketPlayerPosLook(
                        MC.thePlayer.posX,
                        MC.thePlayer.getEntityBoundingBox().minY,
                        MC.thePlayer.posZ,
                        MC.thePlayer.rotationYaw,
                        MC.thePlayer.rotationPitch,
                        true
                     )
                  );
               break;
            case "Vulcan DEV":
               Ries.INSTANCE.msg("This bypass is currently in development.");
               break;
            case "Legacy AntiGamingChair II":
               MC.thePlayer.setPositionAndUpdate(MC.thePlayer.posX, MC.thePlayer.posY - 2.0, MC.thePlayer.posZ);
               break;
            case "Legacy Carbon":
               if (!MC.thePlayer.onGround || Ries.INSTANCE.getModuleManager().getModuleByName("AirJump").isEnabled()) {
                  Ries.INSTANCE.msg("You must be on ground to toggle this flight mode.");
                  this.toggle();
               }
         }

         this.oldTimerSpeed = MC.timer.timerSpeed;
         this.x = MC.thePlayer.posX;
         this.y = MC.thePlayer.posY;
         this.z = MC.thePlayer.posZ;
      } else {
         this.toggle();
      }
   }

   @Override
   public void onDisable() {
      if (MC.thePlayer != null) {
         if (Ries.INSTANCE.getSettingManager().getSetting(this, "INTERNAL_BREAK_ON_TOGGLE", Setting.Type.CHECKBOX).isTicked() && MovementUtils.isMoving2()) {
            MovementUtils.zeroMotion2();
         }

         String var1 = Ries.INSTANCE.getSettingManager().getSetting(this, "INTERNAL_GENERAL_COMBO_BOX", Setting.Type.COMBO_BOX).getCurrentCombo();
         switch(var1) {
            case "Dragpak":
            case "Hellcat":
            case "Demon":
               MC.renderGlobal.loadRenderers();
               break;
            case "Legacy AntiGamingChair II":
               MovementUtils.zeroMotion();
         }

         this.players.forEach(packet -> MC.thePlayer.sendQueue.packetNoEvent(packet));
         this.players.clear();
         MC.thePlayer.capabilities.isFlying = this.damaged = false;
         this.stage = this.stage2 = this.groundTicks = this.gear = 0;
         MC.timer.timerSpeed = this.oldTimerSpeed;
         this.decrement = this.x = this.y = this.z = 0.0;
         this.timer.reset();
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
      String mode = Ries.INSTANCE.getSettingManager().getSetting(this, "INTERNAL_GENERAL_COMBO_BOX", Setting.Type.COMBO_BOX).getCurrentCombo();
      if (e.getState() == EventMotionUpdate.State.PRE) {
         switch(mode) {
            case "Creative":
               MC.thePlayer.capabilities.isFlying = true;
               break;
            case "Area 51":
               MC.timer.timerSpeed = 0.4F;
            case "Anti-Kick Motion":
            case "Motion":
               if (MovementUtils.isMoving()) {
                  MovementUtils.setMoveSpeed(speed);
               } else {
                  MovementUtils.zeroMotion();
               }

               if (MC.gameSettings.keyBindJump.isKeyDown()) {
                  MC.thePlayer.motionY = MathHelper.clamp_double(speed, 0.0, 10.0);
               } else if (MC.gameSettings.keyBindSneak.isKeyDown()) {
                  MC.thePlayer.motionY = -MathHelper.clamp_double(speed, 0.0, 10.0);
               } else {
                  MC.thePlayer.motionY = mode.equals("Anti-Kick Motion") ? -0.125 : 0.0;
               }
               break;
            case "Hellcat":
            case "Demon":
            case "Dragpak":
               if (MovementUtils.isMoving()) {
                  MovementUtils.setMoveSpeedTeleport(mode.equals("Hellcat") ? 1000.0 : (mode.equals("Demon") ? 10000.0 : 100000.0));
               } else if (MC.gameSettings.keyBindJump.isKeyDown()) {
                  MC.thePlayer.motionY = MathHelper.clamp_double(speed, 0.0, 10.0);
               } else if (MC.gameSettings.keyBindSneak.isKeyDown()) {
                  MC.thePlayer.motionY = -MathHelper.clamp_double(speed, 0.0, 10.0);
               } else {
                  MC.thePlayer.motionY = 0.0;
               }
               break;
            case "AntiCheat Reloaded Glide":
               if (MC.thePlayer.fallDistance > 0.0F && MC.thePlayer.ticksExisted % 5 == 0) {
                  MovementUtils.zeroMotion2();
                  MovementUtils.setMoveSpeed(0.2225 - (double)MC.thePlayer.fallDistance * 0.01);
               }
               break;
            case "No Ground Damage":
               if (MC.thePlayer.onGround) {
                  MC.thePlayer.jump();
                  ++this.stage;
               }

               if (this.stage > 4 || MC.thePlayer.hurtTime > 0 && MC.thePlayer.hurtTime < 18) {
                  this.stage = 0;
                  if (MovementUtils.isMoving()) {
                     MovementUtils.setMoveSpeed(speed);
                  } else {
                     MovementUtils.zeroMotion();
                  }

                  if (MC.gameSettings.keyBindJump.isKeyDown()) {
                     MC.thePlayer.motionY = MathHelper.clamp_double(speed, 0.0, 10.0);
                  } else if (MC.gameSettings.keyBindSneak.isKeyDown()) {
                     MC.thePlayer.motionY = -MathHelper.clamp_double(speed, 0.0, 10.0);
                  } else {
                     MC.thePlayer.motionY = 0.0;
                  }
               } else {
                  MovementUtils.zeroMotion();
               }
               break;
            case "Legacy NoCheat+":
               MC.thePlayer.motionY = 0.0;
               MC.thePlayer.posY += MC.thePlayer.ticksExisted % 2 == 0 ? 1.0E-4 : -1.0E-4;
               MovementUtils.setMoveSpeed(0.25);
               break;
            case "Legacy Antivirus":
               if (MC.thePlayer.ticksExisted % 2 == 0) {
                  MovementUtils.setMoveSpeed(speed);
               } else {
                  MovementUtils.zeroMotion();
               }

               MC.thePlayer.motionY = -0.17;
               break;
            case "No Rules":
               if (!MC.thePlayer.isPotionActive(Potion.moveSpeed)) {
                  Ries.INSTANCE.msg("This bypass requires speed to use.");
                  this.toggle();
                  return;
               }

               if (MC.thePlayer.hurtTime == 0 && this.stage == 0) {
                  MC.timer.timerSpeed = 0.08F;

                  for(int i = 0; i < 8; ++i) {
                     MC.thePlayer
                        .sendQueue
                        .packetNoEvent(
                           new C03PacketPlayer.C06PacketPlayerPosLook(
                              MC.thePlayer.posX, MC.thePlayer.posY + 0.42F, MC.thePlayer.posZ, MC.thePlayer.rotationYaw, MC.thePlayer.rotationPitch, false
                           )
                        );
                     MC.thePlayer
                        .sendQueue
                        .packetNoEvent(
                           new C03PacketPlayer.C06PacketPlayerPosLook(
                              MC.thePlayer.posX, MC.thePlayer.posY, MC.thePlayer.posZ, MC.thePlayer.rotationYaw, MC.thePlayer.rotationPitch, false
                           )
                        );
                  }

                  MC.thePlayer
                     .sendQueue
                     .packetNoEvent(
                        new C03PacketPlayer.C06PacketPlayerPosLook(
                           MC.thePlayer.posX, MC.thePlayer.posY, MC.thePlayer.posZ, MC.thePlayer.rotationYaw, MC.thePlayer.rotationPitch, true
                        )
                     );
                  ++this.stage;
               } else if (this.stage == 1) {
                  if (MovementUtils.isMoving()) {
                     MovementUtils.setMoveSpeed(MovementUtils.getBaseMoveSpeed() + 0.312);
                  } else {
                     MovementUtils.zeroMotion();
                  }

                  MC.thePlayer.motionY = 0.0;
                  MC.timer.timerSpeed = 1.8F;
               }
               break;
            case "Verus":
               if (MC.thePlayer.isCollidedVertically && MC.thePlayer.onGround) {
                  if (this.groundTicks > 1) {
                     MC.thePlayer.jump();
                     this.groundTicks = 0;
                  }

                  ++this.groundTicks;
               }
               break;
            case "Verus II":
               if (1.5 - this.decrement > 0.33) {
                  MovementUtils.setMoveSpeed(1.5 - this.decrement);
                  this.decrement += 0.05;
               } else {
                  MovementUtils.setMoveSpeed(0.33);
               }

               if (MC.thePlayer.isCollidedVertically && MC.thePlayer.onGround) {
                  if (this.groundTicks > 1) {
                     MC.thePlayer.jump();
                     this.groundTicks = 0;
                  }

                  ++this.groundTicks;
               }
               break;
            case "Verus III":
               if (MC.thePlayer.hurtTime > 0 && MC.thePlayer.hurtTime < 25) {
                  MovementUtils.setMoveSpeed(speed);
               } else {
                  MovementUtils.setMoveSpeed(0.33);
               }

               if (MC.thePlayer.isCollidedVertically && MC.thePlayer.onGround) {
                  if (this.groundTicks > 1) {
                     MC.thePlayer.jump();
                     this.groundTicks = 0;
                  }

                  ++this.groundTicks;
               }
               break;
            case "Legacy AntiGamingChair":
               if (MC.thePlayer.ticksExisted % 3 == 0) {
                  MovementUtils.setMoveSpeed(speed);
                  if (MC.timer.timerSpeed == 0.1F) {
                     MC.timer.timerSpeed = 1.0F;
                  }

                  if (MC.gameSettings.keyBindSneak.isKeyDown()) {
                     MC.thePlayer.setPositionAndUpdate(MC.thePlayer.posX, MC.thePlayer.posY - 5.0, MC.thePlayer.posZ);
                  }
               } else {
                  MovementUtils.zeroMotion();
               }

               if (MC.thePlayer.ticksExisted % 20 == 0 && MC.gameSettings.keyBindJump.isKeyDown()) {
                  MC.timer.timerSpeed = 0.1F;
                  MC.thePlayer.setPositionAndUpdate(MC.thePlayer.posX, MC.thePlayer.posY + 10.0, MC.thePlayer.posZ);
               }

               MC.thePlayer.motionY = -0.125;
               break;
            case "Vulcan DEV":
               if (MC.thePlayer.ticksExisted % (this.stage2 % 2 == 0 ? 6 : 5) == 0) {
                  this.stage = 1;
                  ++this.stage2;
               }

               if (this.stage == 1 && MC.thePlayer.fallDistance > 0.4F) {
                  this.stage = 0;
               }
               break;
            case "Chunk Glide":
               MC.thePlayer.motionY = -0.08;
               MC.thePlayer.motionY *= 0.98F;
               MovementUtils.addFriction(0.91F);
               break;
            case "Legacy AntiGamingChair II":
               if (!this.damaged && MC.thePlayer.hurtTime > 0) {
                  this.damaged = true;
               } else {
                  MC.timer.timerSpeed = 0.25F;
                  if (MovementUtils.isMoving()) {
                     MovementUtils.setMoveSpeed(8.0);
                  } else {
                     MovementUtils.zeroMotion();
                  }

                  MC.thePlayer.motionY = 0.0;
                  if (this.timer.elapsed(5000L)) {
                     this.toggle();
                     this.timer.reset();
                  }
               }
               break;
            case "Legacy Carbon":
               switch(this.stage) {
                  case 0:
                     if (MC.thePlayer.posY < this.y + 20.0) {
                        MC.thePlayer.setPositionAndUpdate(this.x, MC.thePlayer.posY, this.z);
                        if (MC.thePlayer.ticksExisted % 4 == 0) {
                           MC.thePlayer.motionY = 0.25;
                        }
                     } else {
                        this.stage = 1;
                     }
                     break;
                  case 1:
                     if (!(MC.thePlayer.fallDistance > 12.0F) && !MC.thePlayer.onGround) {
                        MovementUtils.setMoveSpeed(0.25);
                     } else {
                        this.x = MC.thePlayer.posX;
                        this.y = MC.thePlayer.posY;
                        this.z = MC.thePlayer.posZ;
                        MC.thePlayer.fallDistance = 0.0F;
                        this.stage = 0;
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
         case "No Ground Damage":
            if (this.stage <= 3 && e.getPacket() instanceof C03PacketPlayer) {
               C03PacketPlayer packet = PacketUtils.getPacket(e.getPacket());
               packet.setOnGround(false);
            }

            e.setCancelled(e.getPacket() instanceof S12PacketEntityVelocity || e.getPacket() instanceof S27PacketExplosion);
            break;
         case "Legacy AntiGamingChair II":
            if (e.getPacket() instanceof C03PacketPlayer && this.damaged) {
               this.players.add(PacketUtils.getPacket(e.getPacket()));
               e.setCancelled(true);
            }
            break;
         case "Verus II":
         case "Verus III":
            e.setCancelled(e.getPacket() instanceof S27PacketExplosion || e.getPacket() instanceof S12PacketEntityVelocity);
      }
   }

   @EventTarget(
      target = EventAddCollision.class
   )
   @Native
   public void onCollision(EventAddCollision e) {
      String var2 = Ries.INSTANCE.getSettingManager().getSetting(this, "INTERNAL_GENERAL_COMBO_BOX", Setting.Type.COMBO_BOX).getCurrentCombo();
      switch(var2) {
         case "Vulcan DEV":
            if (this.stage == 1) {
               break;
            }
         case "Verus":
         case "Verus II":
         case "Verus III":
         case "Collision":
            e.setBoundingBox(
               new AxisAlignedBB(-3.0, -2.0, -3.0, 3.0, 1.0, 3.0).offset((double)e.getPos().getX(), (double)e.getPos().getY(), (double)e.getPos().getZ())
            );
      }
   }
}
