package felix.module.impl.world;

import felix.api.annotations.Handler;
import felix.events.packet.EventPacketReceive;
import felix.events.player.EventMotionUpdate;
import felix.module.Module;
import felix.value.impl.EnumValue;
import felix.value.impl.NumberValue;
import net.minecraft.block.Block;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

public final class AutoBreaker extends Module {
	
   private final EnumValue<Mode> mode = new EnumValue<>("Mode", Mode.Cake);
   private final NumberValue<Integer> radius = new NumberValue<>("Radius", 5, 3, 6);

   public AutoBreaker() {
	   super("AutoBreaker", 0, ModuleCategory.WORLD);
	   addValues(mode, radius);
   }
   
   public void onEnable() {
	   super.onEnable();
   }
   
   public void onDisable() {
	   super.onDisable();
   }

   @Handler
   public final void onReceivePacket(final EventPacketReceive event) {
	  switch (mode.getValue()) {
	  case Cake:
	  if (event.getPacket() instanceof S02PacketChat) {
         final S02PacketChat packetChat = (S02PacketChat)event.getPacket();
         String text = packetChat.getChatComponent().getUnformattedText();
         if (text.contains("20 seconds") || text.contains("your own")) {
            event.setCancelled(true);
         }
      }
	  break;
	  case Bed:
		break;
	  }
   }

   @Handler
   public final void onMotionUpdate(final EventMotionUpdate event) {
      int range = radius.getValue();
      for(int x = -range; x < range; ++x) {
         for(int y = range; y > -range; --y) {
            for(int z = -range; z < range; ++z) {
               int xPos = (int)mc.thePlayer.posX + x;
               int yPos = (int)mc.thePlayer.posY + y;
               int zPos = (int)mc.thePlayer.posZ + z;
               BlockPos blockPos = new BlockPos(xPos, yPos, zPos);
               Block block = mc.theWorld.getBlockState(blockPos).getBlock();
               if (block.getBlockState().getBlock() == Block.getBlockById(getId())) {
                  mc.getNetHandler().addToSendQueueNoEvent(new C0APacketAnimation());
                  mc.getNetHandler().addToSendQueueNoEvent(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.START_DESTROY_BLOCK, blockPos, EnumFacing.NORTH));
                  mc.getNetHandler().addToSendQueueNoEvent(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK, blockPos, EnumFacing.NORTH));
               }
            }
         }
      }
   }

   private final int getId() {
      switch(mode.getValue()) {
      case Cake:
         return 92;
      case Bed:
         return 26;
      default:
         return 0;
      }
   }

   private enum Mode {
      Cake, Bed;
   }
}
