package me.uncodable.srt.impl.modules.impl.visual;

import me.uncodable.srt.Ries;
import me.uncodable.srt.impl.events.api.EventTarget;
import me.uncodable.srt.impl.events.events.entity.EventUpdate;
import me.uncodable.srt.impl.events.events.packet.EventPacket;
import me.uncodable.srt.impl.modules.api.Module;
import me.uncodable.srt.impl.modules.api.ModuleInfo;
import me.uncodable.srt.impl.modules.api.settings.Setting;
import me.uncodable.srt.impl.utils.PacketUtils;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.MovingObjectPosition;
import org.lwjgl.input.Mouse;

@ModuleInfo(
   internalName = "Animations",
   name = "Animations",
   desc = "Allows you to display animations that do not come stock in vanilla 1.8.x.\nMojang can never do anything right.",
   category = Module.Category.VISUAL,
   legit = true,
   exp = true
)
public class Animations extends Module {
   private static final String INTERNAL_ONE_POINT_SEVEN = "INTERNAL_1_7";
   private static final String INTERNAL_F5_ANIMATION = "INTERNAL_F5_ANIMATION";
   private static final String ONE_POINT_SEVEN_SETTING_NAME = "1.7 Swing";
   private static final String F5_ANIMATION_SETTING_NAME = "F5 Rotations";
   private float prevYaw;

   public Animations(int key, boolean enabled) {
      super(key, enabled);
      Ries.INSTANCE.getSettingManager().addCheckbox(this, "INTERNAL_1_7", "1.7 Swing", true);
      Ries.INSTANCE.getSettingManager().addCheckbox(this, "INTERNAL_F5_ANIMATION", "F5 Rotations", true);
   }

   @Override
   public void onEnable() {
      if (MC.thePlayer != null) {
         this.prevYaw = MC.thePlayer.rotationYaw;
      }
   }

   @EventTarget(
      target = EventUpdate.class
   )
   public void onUpdate(EventUpdate e) {
      if (e.getState() == EventUpdate.State.PRE
         && Ries.INSTANCE.getSettingManager().getSetting(this, "INTERNAL_1_7", Setting.Type.CHECKBOX).isTicked()
         && MC.objectMouseOver != null
         && MC.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK
         && MC.thePlayer.isUsingItem()
         && Mouse.isButtonDown(0)
         && MC.currentScreen == null) {
         MC.thePlayer.swingItem();
      }
   }

   @EventTarget(
      target = EventPacket.class
   )
   public void onPacket(EventPacket e) {
      if ((e.getPacket() instanceof C03PacketPlayer.C05PacketPlayerLook || e.getPacket() instanceof C03PacketPlayer.C06PacketPlayerPosLook)
         && Ries.INSTANCE.getSettingManager().getSetting(this, "INTERNAL_F5_ANIMATION", Setting.Type.CHECKBOX).isTicked()) {
         C03PacketPlayer packet = PacketUtils.getPacket(e.getPacket());
         MC.thePlayer.rotationYawHead = packet.getYaw();
         MC.thePlayer.renderYawOffset = packet.getYaw();
      }
   }
}
