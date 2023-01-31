package intent.AquaDev.aqua.modules.world;

import events.Event;
import events.listeners.EventPacket;
import intent.AquaDev.aqua.modules.Category;
import intent.AquaDev.aqua.modules.Module;
import intent.AquaDev.aqua.utils.RotationUtil;
import intent.AquaDev.aqua.utils.TimeUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.block.BlockCake;
import net.minecraft.block.BlockDragonEgg;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

public class BedFucker extends Module {
   public static BlockPos pos;
   TimeUtil time = new TimeUtil();
   double range = 4.5;

   public BedFucker() {
      super("BedFucker", Module.Type.World, "BedFucker", 0, Category.World);
   }

   public static void lookAtPos(double x, double y, double z) {
      double dirx = mc.thePlayer.posX - x;
      double diry = mc.thePlayer.posY - y;
      double dirz = mc.thePlayer.posZ - z;
      double len = Math.sqrt(dirx * dirx + diry * diry + dirz * dirz);
      dirx /= len;
      diry /= len;
      dirz /= len;
      float yaw = (float)Math.atan2(dirz, dirx);
      float pitch = (float)Math.asin(diry);
      pitch = (float)((double)pitch * 180.0 / Math.PI);
      yaw = (float)((double)yaw * 180.0 / Math.PI);
      yaw = (float)((double)yaw + 90.0);
      float f2 = Minecraft.getMinecraft().gameSettings.mouseSensitivity * 0.6F + 0.2F;
      float f3 = f2 * f2 * f2 * 1.2F;
      yaw -= yaw % f3;
      pitch -= pitch % (f3 * f2);
      RotationUtil.setYaw(yaw, 180.0F);
      RotationUtil.setPitch(pitch, 90.0F);
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
      if (event instanceof EventPacket) {
         for(int y = (int)this.range; (double)y >= -this.range; --y) {
            for(int x = (int)(-this.range); (double)x <= this.range; ++x) {
               for(int z = (int)(-this.range); (double)z <= this.range; ++z) {
                  int posX = (int)(mc.thePlayer.posX - 0.5 + (double)x);
                  int posZ = (int)(mc.thePlayer.posZ - 0.5 + (double)z);
                  int posY = (int)(mc.thePlayer.posY - 0.5 + (double)y);
                  pos = new BlockPos(posX, posY, posZ);
                  Block block = mc.theWorld.getBlockState(pos).getBlock();
                  if (block instanceof BlockBed || block instanceof BlockCake || block instanceof BlockDragonEgg) {
                     PlayerControllerMP playerController = mc.playerController;
                     long timeLeft = (long)(PlayerControllerMP.curBlockDamageMP / 2.0F);
                     if (this.time.hasReached(800L)) {
                        mc.thePlayer.sendQueue.addToSendQueue(new C0APacketAnimation());
                        mc.getNetHandler().addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.START_DESTROY_BLOCK, pos, EnumFacing.DOWN));
                        mc.getNetHandler().addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK, pos, EnumFacing.DOWN));
                        mc.getNetHandler().addToSendQueue(new C0APacketAnimation());
                        this.time.reset();
                     }
                  }
               }
            }
         }
      }
   }
}
