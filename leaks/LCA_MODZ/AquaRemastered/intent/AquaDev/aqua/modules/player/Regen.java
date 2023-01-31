package intent.AquaDev.aqua.modules.player;

import events.Event;
import events.listeners.EventTick;
import intent.AquaDev.aqua.modules.Category;
import intent.AquaDev.aqua.modules.Module;
import net.minecraft.network.play.client.C03PacketPlayer;

public class Regen extends Module {
   public Regen() {
      super("Regen", Module.Type.Player, "Regen", 0, Category.Player);
      System.out.println("Sprint::init");
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
      if (event instanceof EventTick && mc.thePlayer.getHealth() < 20.0F && mc.thePlayer.getFoodStats().getFoodLevel() > 19) {
         for(int i = 0; i < 10; ++i) {
            mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer(true));
         }
      }
   }
}
