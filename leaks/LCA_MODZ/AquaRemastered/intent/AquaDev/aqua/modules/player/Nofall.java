package intent.AquaDev.aqua.modules.player;

import events.Event;
import events.listeners.EventPacketNofall;
import events.listeners.EventUpdate;
import intent.AquaDev.aqua.modules.Category;
import intent.AquaDev.aqua.modules.Module;
import intent.AquaDev.aqua.modules.movement.Fly;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.util.BlockPos;

public class Nofall extends Module {
   public Nofall() {
      super("Nofall", Module.Type.Player, "Nofall", 0, Category.Player);
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
      if (event instanceof EventUpdate && mc.thePlayer.fallDistance > 2.0F && mc.thePlayer.ticksExisted > 20 && !mc.thePlayer.onGround) {
         Fly.sendPacketUnlogged(new C08PacketPlayerBlockPlacement(new BlockPos(-1, -1, -1), 255, new ItemStack(Items.water_bucket), 0.0F, 0.5F, 0.0F));
         Fly.sendPacketUnlogged(
            new C08PacketPlayerBlockPlacement(
               new BlockPos(Fly.getX(), Fly.getY() - 1.5, Fly.getZ()),
               1,
               new ItemStack(Blocks.stone.getItem(mc.theWorld, new BlockPos(-1, -1, -1))),
               0.0F,
               0.94F,
               0.0F
            )
         );
      }

      if (event instanceof EventPacketNofall) {
         Packet p = EventPacketNofall.getPacket();
         if (mc.thePlayer.fallDistance > 2.0F
            && mc.thePlayer.ticksExisted > 20
            && !mc.thePlayer.onGround
            && !mc.isSingleplayer()
            && !mc.getCurrentServerData().serverIP.contains("cubecraft.net")
            && p instanceof C0FPacketConfirmTransaction) {
            event.setCancelled(true);
         }

         if (p instanceof C03PacketPlayer) {
            if (mc.thePlayer.fallDistance > 2.0F && mc.thePlayer.ticksExisted > 20 && !mc.thePlayer.onGround) {
               C03PacketPlayer c03PacketPlayer = (C03PacketPlayer)p;
               c03PacketPlayer.onGround = true;
               mc.thePlayer.fallDistance = 0.0F;
            } else {
               C03PacketPlayer c03PacketPlayer = (C03PacketPlayer)p;
            }
         }
      }
   }
}
