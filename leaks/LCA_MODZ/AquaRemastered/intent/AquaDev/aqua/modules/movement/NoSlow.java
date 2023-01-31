package intent.AquaDev.aqua.modules.movement;

import de.Hero.settings.Setting;
import events.Event;
import events.listeners.EventPacket;
import events.listeners.EventUpdate;
import intent.AquaDev.aqua.Aqua;
import intent.AquaDev.aqua.modules.Category;
import intent.AquaDev.aqua.modules.Module;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C09PacketHeldItemChange;

public class NoSlow extends Module {
   public NoSlow() {
      super("NoSlow", Module.Type.Movement, "NoSlow", 0, Category.Movement);
      Aqua.setmgr.register(new Setting("Mode", this, "Watchdog", new String[]{"Watchdog", "Vanilla"}));
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
      if (event instanceof EventPacket && Aqua.setmgr.getSetting("NoSlowMode").getCurrentMode().equalsIgnoreCase("Watchdog")) {
         Packet packet = EventPacket.getPacket();
         if (packet instanceof C09PacketHeldItemChange
            && mc.thePlayer.isUsingItem()
            && !Aqua.moduleManager.getModuleByName("Scaffold").isToggled()
            && !mc.isSingleplayer()
            && !mc.getCurrentServerData().serverIP.equalsIgnoreCase("cubecraft.net")
            && !mc.isSingleplayer()) {
            event.setCancelled(true);
         }
      }

      if (event instanceof EventUpdate
         && Aqua.setmgr.getSetting("NoSlowMode").getCurrentMode().equalsIgnoreCase("Watchdog")
         && !Aqua.moduleManager.getModuleByName("Scaffold").isToggled()
         && !mc.isSingleplayer()
         && !mc.getCurrentServerData().serverIP.equalsIgnoreCase("cubecraft.net")
         && !mc.isSingleplayer()) {
         int curSlot = mc.thePlayer.inventory.currentItem;
         int spoof = curSlot == 0 ? 1 : -1;
         if (mc.thePlayer.isUsingItem()) {
            mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(curSlot + spoof));
            mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(curSlot));
            event.setCancelled(true);
         }
      }
   }
}
