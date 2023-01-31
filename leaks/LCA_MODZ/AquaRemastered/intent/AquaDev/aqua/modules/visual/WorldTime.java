package intent.AquaDev.aqua.modules.visual;

import de.Hero.settings.Setting;
import events.Event;
import events.listeners.EventPacket;
import intent.AquaDev.aqua.Aqua;
import intent.AquaDev.aqua.modules.Category;
import intent.AquaDev.aqua.modules.Module;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S03PacketTimeUpdate;

public class WorldTime extends Module {
   public WorldTime() {
      super("WorldTime", Module.Type.Visual, "WorldTime", 0, Category.Visual);
      Aqua.setmgr.register(new Setting("Time", this, 3.0, 0.0, 24.0, false));
   }

   @Override
   public void onEnable() {
      super.onEnable();
   }

   @Override
   public void onDisable() {
      super.onDisable();
   }

   @Override
   public void onEvent(Event event) {
      float time = (float)Aqua.setmgr.getSetting("WorldTimeTime").getCurrentNumber();
      if (event instanceof EventPacket) {
         mc.theWorld.setWorldTime((long)(Math.round(time * 1000.0F) - 100));
         Packet p = EventPacket.getPacket();
         if (p instanceof S03PacketTimeUpdate) {
            event.setCancelled(true);
         }
      }
   }
}
