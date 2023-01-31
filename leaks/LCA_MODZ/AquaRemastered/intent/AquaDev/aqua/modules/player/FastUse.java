package intent.AquaDev.aqua.modules.player;

import de.Hero.settings.Setting;
import events.Event;
import events.listeners.EventTick;
import intent.AquaDev.aqua.Aqua;
import intent.AquaDev.aqua.modules.Category;
import intent.AquaDev.aqua.modules.Module;
import net.minecraft.item.ItemFood;
import net.minecraft.network.play.client.C03PacketPlayer;

public class FastUse extends Module {
   public static double Packets;

   public FastUse() {
      super("FastUse", Module.Type.Player, "FastUse", 0, Category.Player);
      Aqua.setmgr.register(new Setting("Packets", this, 6.0, 0.1, 50.0, false));
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
   public void onEvent(Event e) {
      if (e instanceof EventTick
         && mc.thePlayer.getCurrentEquippedItem() != null
         && mc.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemFood
         && mc.thePlayer.isUsingItem()) {
         Packets = (double)((float)Aqua.setmgr.getSetting("FastUsePackets").getCurrentNumber());

         for(int i = 0; (double)i < Packets; ++i) {
            mc.getNetHandler().addToSendQueue(new C03PacketPlayer());
         }
      }
   }
}
