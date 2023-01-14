package me.uncodable.srt.impl.modules.impl.fuzzers;

import me.uncodable.srt.Ries;
import me.uncodable.srt.impl.events.api.EventTarget;
import me.uncodable.srt.impl.events.events.entity.EventMotionUpdate;
import me.uncodable.srt.impl.events.events.entity.EventUpdate;
import me.uncodable.srt.impl.events.events.packet.EventPacket;
import me.uncodable.srt.impl.events.events.render.Event2DRender;
import me.uncodable.srt.impl.modules.api.Module;
import me.uncodable.srt.impl.modules.api.ModuleInfo;
import me.uncodable.srt.impl.modules.api.settings.Setting;
import me.uncodable.srt.impl.utils.MovementUtils;
import me.uncodable.srt.impl.utils.PacketUtils;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.util.BlockPos;

@ModuleInfo(
   internalName = "SpeedFuzzer",
   name = "Speed Fuzzer",
   desc = "Allows you to automatically generate a speed bypass.",
   category = Module.Category.FUZZER,
   exp = true
)
public class SpeedFuzzer extends Module {
   private static final String INTERNAL_STRAFE = "INTERNAL_STRAFE";
   private static final String INTERNAL_JUMP = "INTERNAL_JUMP";
   private static final String INTERNAL_STARTING_SPEED_VALUE = "INTERNAL_STARTING_SPEED_VALUE";
   private static final String INTERNAL_SPEED_INCREMENT_VALUE = "INTERNAL_SPEED_INCREMENT_VALUE";
   private static final String INTERNAL_FRICTION_VALUE = "INTERNAL_FRICTION_VALUE";
   private static final String COMBO_BOX_SETTING_NAME = "Selected AntiCheat";
   private static final String STRAFE_SETTING_NAME = "Strafe";
   private static final String JUMP_SETTING_NAME = "Jump";
   private static final String STARTING_SPEED_VALUE_SETTING_NAME = "Starting Speed";
   private static final String SPEED_INCREMENT_VALUE_SETTING_NAME = "Speed Increment";
   private static final String FRICTION_VALUE_SETTING_NAME = "Applied Friction";
   private static final String VERUS = "Verus";
   private static final String LAG_BACK = "Lag-Back";
   private BlockPos lastSetPos;
   private int stage;
   private double speed;
   private double increment;
   private String fuzzingWait = "Fuzzing.";

   public SpeedFuzzer(int key, boolean enabled) {
      super(key, enabled);
      Ries.INSTANCE.getSettingManager().addComboBox(this, "INTERNAL_GENERAL_COMBO_BOX", "Selected AntiCheat", "Verus", "Lag-Back");
      Ries.INSTANCE.getSettingManager().addSlider(this, "INTERNAL_STARTING_SPEED_VALUE", "Starting Speed", 0.01, 0.01, 1.0);
      Ries.INSTANCE.getSettingManager().addSlider(this, "INTERNAL_SPEED_INCREMENT_VALUE", "Speed Increment", 0.01, 0.01, 1.0);
      Ries.INSTANCE.getSettingManager().addSlider(this, "INTERNAL_FRICTION_VALUE", "Applied Friction", 0.09, 0.0, 1.0);
      Ries.INSTANCE.getSettingManager().addCheckbox(this, "INTERNAL_STRAFE", "Strafe", true);
      Ries.INSTANCE.getSettingManager().addCheckbox(this, "INTERNAL_JUMP", "Jump", true);
   }

   @Override
   public void onEnable() {
      if (MC.thePlayer != null) {
         MC.thePlayer.rotationYaw = MC.thePlayer.rotationPitch = 0.0F;
         this.lastSetPos = MC.thePlayer.getPosition();
      } else {
         this.toggle();
      }

      this.speed = Ries.INSTANCE.getSettingManager().getSetting(this, "INTERNAL_STARTING_SPEED_VALUE", Setting.Type.SLIDER).getCurrentValue();
      this.increment = Ries.INSTANCE.getSettingManager().getSetting(this, "INTERNAL_SPEED_INCREMENT_VALUE", Setting.Type.SLIDER).getCurrentValue();
   }

   @Override
   public void onDisable() {
      MC.gameSettings.keyBindForward.setKeyDown(false);
      MC.gameSettings.keyBindLeft.setKeyDown(false);
      MC.gameSettings.keyBindRight.setKeyDown(false);
      MC.gameSettings.keyBindBack.setKeyDown(false);
      MC.gameSettings.keyBindSprint.setKeyDown(false);
      this.stage = 0;
   }

   @EventTarget(
      target = EventUpdate.class
   )
   public void onUpdate(EventUpdate e) {
      if (e.getState() == EventUpdate.State.PRE && MC.thePlayer.ticksExisted % 20 == 0) {
         this.fuzzingWait = this.fuzzingWait.concat(".");
      }
   }

   @EventTarget(
      target = Event2DRender.class
   )
   public void onRender(Event2DRender e) {
      if (this.fuzzingWait.length() >= 11) {
         this.fuzzingWait = "Fuzzing.";
      }

      GlStateManager.pushMatrix();
      GlStateManager.scale(2.0, 2.0, 2.0);
      GlStateManager.translate(2.0, 100.0, 0.0);
      MC.fontRendererObj.drawStringWithShadow(this.fuzzingWait, 0.0F, 0.0F, 16777215);
      GlStateManager.popMatrix();
   }

   @EventTarget(
      target = EventMotionUpdate.class
   )
   public void onMotion(EventMotionUpdate e) {
      if (e.getState() == EventMotionUpdate.State.PRE) {
         MC.thePlayer.rotationYaw = MC.thePlayer.rotationPitch = 0.0F;
         MovementUtils.setMoveSpeed(this.speed);
         if (MC.thePlayer.onGround && Ries.INSTANCE.getSettingManager().getSetting(this, "INTERNAL_JUMP", Setting.Type.CHECKBOX).isTicked()) {
            MC.thePlayer.jump();
         }

         switch(this.stage) {
            case 0:
               MC.gameSettings.keyBindSprint.setKeyDown(true);
               MC.gameSettings.keyBindForward.setKeyDown(true);
               break;
            case 1:
               if (!Ries.INSTANCE.getSettingManager().getSetting(this, "INTERNAL_STRAFE", Setting.Type.CHECKBOX).isTicked()) {
                  ++this.stage;
               } else {
                  MC.gameSettings.keyBindLeft.setKeyDown(true);
               }
               break;
            case 2:
               MC.gameSettings.keyBindBack.setKeyDown(true);
               break;
            case 3:
               if (!Ries.INSTANCE.getSettingManager().getSetting(this, "INTERNAL_STRAFE", Setting.Type.CHECKBOX).isTicked()) {
                  ++this.stage;
               } else {
                  MC.gameSettings.keyBindRight.setKeyDown(true);
               }
               break;
            default:
               this.stage = 0;
               Ries.INSTANCE.msg(String.format("Speed increased by %.2f.", this.increment));
               this.speed += this.increment;
         }

         if (MC.thePlayer.getDistance((double)this.lastSetPos.getX(), (double)this.lastSetPos.getY(), (double)this.lastSetPos.getZ()) > 10.0) {
            MC.gameSettings.keyBindForward.setKeyDown(false);
            MC.gameSettings.keyBindLeft.setKeyDown(false);
            MC.gameSettings.keyBindRight.setKeyDown(false);
            MC.gameSettings.keyBindBack.setKeyDown(false);
            MC.gameSettings.keyBindSprint.setKeyDown(false);
            this.lastSetPos = MC.thePlayer.getPosition();
            ++this.stage;
         }

         if (!MC.thePlayer.onGround) {
            MovementUtils.addFriction(
               1.0 - Ries.INSTANCE.getSettingManager().getSetting(this, "INTERNAL_FRICTION_VALUE", Setting.Type.SLIDER).getCurrentValue()
            );
         }
      }
   }

   @EventTarget(
      target = EventPacket.class
   )
   public void onPacket(EventPacket e) {
      String var2 = Ries.INSTANCE.getSettingManager().getSetting(this, "INTERNAL_GENERAL_COMBO_BOX", Setting.Type.COMBO_BOX).getCurrentCombo();
      switch(var2) {
         case "Verus":
            if (e.getPacket() instanceof S02PacketChat) {
               S02PacketChat packet = PacketUtils.getPacket(e.getPacket());
               if (packet.getChatComponent().getUnformattedText().contains("Verus")) {
                  Ries.INSTANCE.msg("Final speed value: " + this.speed + ".");
                  this.toggle();
               }
            }
            break;
         case "Lag-Back":
            if (e.getPacket() instanceof S08PacketPlayerPosLook) {
               Ries.INSTANCE.msg("Final speed value: " + this.speed + ".");
               this.toggle();
            }
      }
   }
}
