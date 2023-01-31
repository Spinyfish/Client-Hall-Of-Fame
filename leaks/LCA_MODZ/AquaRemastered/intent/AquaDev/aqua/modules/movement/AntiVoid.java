package intent.AquaDev.aqua.modules.movement;

import events.Event;
import events.listeners.EventPacket;
import events.listeners.EventTimerDisabler;
import events.listeners.EventUpdate;
import intent.AquaDev.aqua.Aqua;
import intent.AquaDev.aqua.modules.Category;
import intent.AquaDev.aqua.modules.Module;
import java.util.Random;
import net.minecraft.block.BlockAir;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;

public class AntiVoid extends Module {
   public AntiVoid() {
      super("AntiVoid", Module.Type.Movement, "AntiVoid", 0, Category.Movement);
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
      if (e instanceof EventTimerDisabler) {
         Packet packet = EventTimerDisabler.getPacket();
         if (packet instanceof C0FPacketConfirmTransaction || packet instanceof C00PacketKeepAlive) {
            float fallDistance = (float)MathHelper.getRandomDoubleInRange(new Random(), 2.5, 3.0);
            if (mc.thePlayer.fallDistance > fallDistance) {
            }
         }
      }

      if (e instanceof EventPacket) {
         Packet packet = EventPacket.getPacket();
         if (packet instanceof C0FPacketConfirmTransaction || packet instanceof C00PacketKeepAlive) {
            float fallDistance = (float)MathHelper.getRandomDoubleInRange(new Random(), 2.5, 3.0);
            if (mc.thePlayer.fallDistance > fallDistance) {
            }
         }
      }

      if (e instanceof EventUpdate && !this.isBlockUnder()) {
         float fallDistance = (float)MathHelper.getRandomDoubleInRange(new Random(), 2.95, 3.0);
         if (mc.thePlayer.fallDistance > fallDistance && !Aqua.moduleManager.getModuleByName("Longjump").isToggled()) {
            mc.thePlayer.motionY -= 0.42;
         }
      }
   }

   public boolean isBlockUnder() {
      for(int i = (int)mc.thePlayer.posY; i >= 0; --i) {
         BlockPos position = new BlockPos(mc.thePlayer.posX, (double)i, mc.thePlayer.posZ);
         if (!(mc.theWorld.getBlockState(position).getBlock() instanceof BlockAir)) {
            return true;
         }
      }

      return false;
   }
}
