package intent.AquaDev.aqua.modules.combat;

import de.Hero.settings.Setting;
import events.Event;
import events.listeners.EventReceivedPacket;
import events.listeners.EventUpdate;
import intent.AquaDev.aqua.Aqua;
import intent.AquaDev.aqua.modules.Category;
import intent.AquaDev.aqua.modules.Module;
import intent.AquaDev.aqua.utils.PlayerUtil;
import intent.AquaDev.aqua.utils.TimeUtil;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S27PacketExplosion;

public class Velocity extends Module {
   TimeUtil timeUtil = new TimeUtil();

   public Velocity() {
      super("Velocity", Module.Type.Combat, "Velocity", 0, Category.Combat);
      Aqua.setmgr.register(new Setting("MatrixValue", this, 0.1, 0.0, 0.8, false));
      Aqua.setmgr.register(new Setting("Mode", this, "Cancel", new String[]{"CancelLongjump", "Matrix", "Cancel", "Edit"}));
   }

   @Override
   public void setup() {
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
      if (event instanceof EventReceivedPacket) {
         if (Aqua.setmgr.getSetting("VelocityMode").getCurrentMode().equalsIgnoreCase("Edit")) {
            Packet p = EventReceivedPacket.INSTANCE.getPacket();
            if (p instanceof S27PacketExplosion) {
               event.setCancelled(true);
            }

            if (p instanceof S12PacketEntityVelocity) {
               S12PacketEntityVelocity packetVelocity = (S12PacketEntityVelocity)p;
               if (packetVelocity.getEntityID() == mc.thePlayer.getEntityId()) {
                  packetVelocity.setMotionX(0);
                  packetVelocity.setMotionZ(0);
               }
            }
         }

         if (Aqua.setmgr.getSetting("VelocityMode").getCurrentMode().equalsIgnoreCase("CancelLongjump")) {
            Packet p = EventReceivedPacket.INSTANCE.getPacket();
            if (p instanceof S12PacketEntityVelocity) {
               S12PacketEntityVelocity packet = (S12PacketEntityVelocity)p;
               if (packet.getEntityID() == mc.thePlayer.getEntityId()
                  && Aqua.setmgr.getSetting("LongjumpMode").getCurrentMode().equalsIgnoreCase("Gamster")
                  && !Aqua.moduleManager.getModuleByName("Longjump").isToggled()) {
                  EventReceivedPacket.INSTANCE.setCancelled(true);
               }
            }

            if (p instanceof S27PacketExplosion) {
               EventReceivedPacket.INSTANCE.setCancelled(true);
            }
         }

         if (Aqua.setmgr.getSetting("VelocityMode").getCurrentMode().equalsIgnoreCase("Cancel")) {
            Packet p = EventReceivedPacket.INSTANCE.getPacket();
            if (p instanceof S12PacketEntityVelocity) {
               S12PacketEntityVelocity packet = (S12PacketEntityVelocity)p;
               if (packet.getEntityID() == mc.thePlayer.getEntityId()) {
                  EventReceivedPacket.INSTANCE.setCancelled(true);
               }
            }

            if (p instanceof S27PacketExplosion) {
               EventReceivedPacket.INSTANCE.setCancelled(true);
            }
         }
      }

      if (event instanceof EventUpdate
         && Aqua.setmgr.getSetting("VelocityMode").getCurrentMode().equalsIgnoreCase("Matrix")
         && !Aqua.moduleManager.getModuleByName("Longjump").isToggled()
         && mc.thePlayer.hurtTime != 0
         && !mc.gameSettings.keyBindJump.pressed) {
         PlayerUtil.setSpeed((double)((float)Aqua.setmgr.getSetting("VelocityMatrixValue").getCurrentNumber()));
      }
   }
}
