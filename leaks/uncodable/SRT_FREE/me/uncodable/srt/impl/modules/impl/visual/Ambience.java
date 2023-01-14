package me.uncodable.srt.impl.modules.impl.visual;

import me.uncodable.srt.Ries;
import me.uncodable.srt.impl.events.api.EventTarget;
import me.uncodable.srt.impl.events.events.packet.EventPacket;
import me.uncodable.srt.impl.events.events.render.Event3DRender;
import me.uncodable.srt.impl.modules.api.Module;
import me.uncodable.srt.impl.modules.api.ModuleInfo;
import me.uncodable.srt.impl.modules.api.settings.Setting;
import me.uncodable.srt.impl.utils.PacketUtils;
import net.minecraft.network.play.server.S03PacketTimeUpdate;

@ModuleInfo(
   internalName = "Ambience",
   name = "Ambience",
   desc = "Allows you to change the time, without the server interfering.\nUncodable also knows how to spell unlike other client developers.",
   category = Module.Category.VISUAL,
   legit = true
)
public class Ambience extends Module {
   private long oldWorldTime;
   private long worldTime;
   private static final String COMBO_BOX_SETTING_NAME = "Time";
   private static final String DAWN = "Dawn";
   private static final String DAYLIGHT = "Daylight";
   private static final String DUSK = "Dusk";
   private static final String NIGHT_TIME = "Night Time";
   private static final String SPINNING = "Spinning";

   public Ambience(int key, boolean enabled) {
      super(key, enabled);
      Ries.INSTANCE.getSettingManager().addComboBox(this, "INTERNAL_GENERAL_COMBO_BOX", "Time", "Dawn", "Daylight", "Dusk", "Night Time", "Spinning");
   }

   @Override
   public void onEnable() {
      if (MC.theWorld != null) {
         this.oldWorldTime = MC.theWorld.getWorldTime();
      } else {
         this.oldWorldTime = 0L;
      }
   }

   @Override
   public void onDisable() {
      MC.theWorld.setWorldTime(this.oldWorldTime);
      this.worldTime = 0L;
   }

   @EventTarget(
      target = Event3DRender.class
   )
   public void onRender(Event3DRender e) {
      String var2 = Ries.INSTANCE.getSettingManager().getSetting(this, "INTERNAL_GENERAL_COMBO_BOX", Setting.Type.COMBO_BOX).getCurrentCombo();
      byte var3 = -1;
      switch(var2.hashCode()) {
         case -1943403498:
            if (var2.equals("Spinning")) {
               var3 = 0;
            }
         default:
            switch(var3) {
               case 0:
                  if (this.worldTime > 24000L) {
                     this.worldTime = 0L;
                  }

                  MC.theWorld.setWorldTime(this.worldTime);
                  this.worldTime += 10L;
            }
      }
   }

   @EventTarget(
      target = EventPacket.class
   )
   public void onPacket(EventPacket e) {
      if (e.getPacket() instanceof S03PacketTimeUpdate) {
         S03PacketTimeUpdate packet = PacketUtils.getPacket(e.getPacket());
         String var3 = Ries.INSTANCE.getSettingManager().getSetting(this, "INTERNAL_GENERAL_COMBO_BOX", Setting.Type.COMBO_BOX).getCurrentCombo();
         switch(var3) {
            case "Dawn":
               packet.setWorldTime(0L);
               break;
            case "Daylight":
               packet.setWorldTime(6000L);
               break;
            case "Dusk":
               packet.setWorldTime(12000L);
               break;
            case "Night Time":
               packet.setWorldTime(18000L);
               break;
            case "Spinning":
               e.setCancelled(true);
         }
      }
   }
}
