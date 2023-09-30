package club.pulsive.impl.module.impl.player;

import club.pulsive.api.event.eventBus.handler.EventHandler;
import club.pulsive.api.event.eventBus.handler.Listener;
import club.pulsive.api.main.Pulsive;
import club.pulsive.impl.event.network.PacketEvent;
import club.pulsive.impl.event.player.PlayerMotionEvent;
import club.pulsive.impl.module.Category;
import club.pulsive.impl.module.Module;
import club.pulsive.impl.module.ModuleInfo;
import club.pulsive.impl.module.impl.movement.Flight;
import club.pulsive.impl.property.Property;
import club.pulsive.impl.property.implementations.DoubleProperty;
import club.pulsive.impl.util.math.apache.ApacheMath;
import club.pulsive.impl.util.network.PacketUtil;
import club.pulsive.impl.util.player.MovementUtil;
import club.pulsive.impl.util.player.PlayerUtil;
import club.pulsive.impl.util.player.RotationUtil;
import com.sun.org.apache.xpath.internal.operations.Bool;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;

import java.util.ArrayList;
import java.util.Objects;

@ModuleInfo(name = "Breaker", description = "Breaks blocks for you", category = Category.PLAYER)
public final class Breaker extends Module {

    private float dmg;
    private BlockPos blockPos;
    private Vec3 respawnPoint;

    private final DoubleProperty range = new DoubleProperty("Range", 4, 1, 7, 0.5);
    private final Property<Boolean> swing = new Property<Boolean>("Swing", true);
    private final Property<Boolean> cake = new Property<Boolean>("Cake", true);
    private final Property<Boolean> bed = new Property<Boolean>("Bed", true);
    private final Property<Boolean> whitelistOwnBase = new Property<Boolean>("Whitelist own bed", true);
    private final Property<Boolean> breakAbove = new Property<Boolean>("Mineplex through walls", false);
    private final Property<Boolean> rotations = new Property<Boolean>("Rotations", true);

    @EventHandler
    private final Listener<PlayerMotionEvent> playerMotionEventListener = event -> {
      if(event.isPre()){
          blockPos = null;
          if (mc.gameSettings.keyBindAttack.isKeyDown()) return;
          //Cake
          ArrayList<Integer> pos = getBlock(Blocks.cake, range.getValue().intValue());

          if (cake.getValue()) {
              if (pos != null && mc.thePlayer != null && (respawnPoint == null || mc.thePlayer.getDistance(respawnPoint.xCoord, respawnPoint.yCoord, respawnPoint.zCoord) > 25 || !whitelistOwnBase.getValue())) {
                  final BlockPos pos2 = new BlockPos(mc.thePlayer.posX + pos.get(0), mc.thePlayer.posY + pos.get(1), mc.thePlayer.posZ + pos.get(2));
                  final BlockPos posAbove = new BlockPos(mc.thePlayer.posX + pos.get(0), mc.thePlayer.posY + pos.get(1) + 1, mc.thePlayer.posZ + pos.get(2));
                  final BlockPos currentPos = pos2.add(pos.get(0), pos.get(1), pos.get(2));

                  if (rotations.getValue()) {
                      final float[] rotations = RotationUtil.getBlockRotations(blockPos.getX(), blockPos.getY(), blockPos.getZ());
                      event.setYaw(rotations[0]);
                      event.setPitch(rotations[1]);
                  }

                  if (breakAbove.getValue()) {
                      mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.START_DESTROY_BLOCK, posAbove, EnumFacing.NORTH));
                      mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK, posAbove, EnumFacing.NORTH));
                  }

                  if (!Pulsive.INSTANCE.getModuleManager().getModule(Flight.class).isToggled())
                      mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, mc.thePlayer.getHeldItem(), pos2, EnumFacing.UP, new Vec3(currentPos.getX(), currentPos.getY(), currentPos.getZ()));

                  if (swing.getValue()) mc.thePlayer.swingItem();
              }
          }

          //Bed
          if (blockPos == null || mc.theWorld.getBlockState(blockPos).getBlock() != Block.getBlockById(26) && mc.thePlayer != null) {
              pos = getBlock(Blocks.bed, range.getValue().intValue());

              if (pos != null)
                  blockPos = new BlockPos(mc.thePlayer.posX + Objects.requireNonNull(pos).get(0), mc.thePlayer.posY + Objects.requireNonNull(pos).get(1), mc.thePlayer.posZ + Objects.requireNonNull(pos).get(2));
          }

          if (bed.getValue()) {
              if (blockPos != null && (respawnPoint == null || mc.thePlayer.getDistance(respawnPoint.xCoord, respawnPoint.yCoord, respawnPoint.zCoord) > 25 || !whitelistOwnBase.getValue())) {
                  if (breakAbove.getValue()) {
                      final BlockPos posAbove = new BlockPos(mc.thePlayer.posX + blockPos.getX(), mc.thePlayer.posY + blockPos.getY() + 1, mc.thePlayer.posZ + blockPos.getZ());
                      mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.START_DESTROY_BLOCK, posAbove, EnumFacing.NORTH));
                      mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK, posAbove, EnumFacing.NORTH));
                  }

                  if (rotations.getValue()) {
                      final float[] rotations = RotationUtil.getBlockRotations(blockPos.getX(), blockPos.getY(), blockPos.getZ());
                      event.setYaw(rotations[0]);
                      event.setPitch(rotations[1]);
                  } else if (PlayerUtil.isOnServer("hypixel")) {
                      event.setYaw((float) (ApacheMath.random() * 360));
                      event.setPitch((float) (-90 + ApacheMath.random() * 180));
                  }

                  if (dmg == 0) {
                      mc.getNetHandler().addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.START_DESTROY_BLOCK, blockPos, EnumFacing.UP));

                      if (PlayerUtil.isOnServer("hypixel")) {
                          PacketUtil.sendPacketNoEvent(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK, blockPos, EnumFacing.UP));
                          PacketUtil.sendPacket(new C03PacketPlayer(mc.thePlayer.onGround));
                          mc.playerController.onPlayerDestroyBlock(blockPos, EnumFacing.DOWN);
                      }

                      if (mc.theWorld.getBlockState(blockPos).getBlock().getPlayerRelativeBlockHardness(mc.thePlayer, mc.theWorld, blockPos) >= 1) {
                          if (swing.getValue()) mc.thePlayer.swingItem();

                          mc.playerController.onPlayerDestroyBlock(blockPos, EnumFacing.DOWN);

                          dmg = 0;
                          blockPos = null;
                          return;
                      }
                  }

                  if (swing.getValue()) mc.thePlayer.swingItem();

                  dmg += mc.theWorld.getBlockState(blockPos).getBlock().getPlayerRelativeBlockHardness(mc.thePlayer, mc.theWorld, blockPos);
                  mc.theWorld.sendBlockBreakProgress(mc.thePlayer.getEntityId(), blockPos, (int) (dmg * 10) - 1);

                  if (dmg >= 1) {
                      PacketUtil.sendPacketNoEvent(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK, blockPos, EnumFacing.UP));
                      mc.playerController.onPlayerDestroyBlock(blockPos, EnumFacing.DOWN);

                      dmg = 0;
                      blockPos = null;
                  }
              }
          }
      }
    };

    @EventHandler
    private final Listener<PacketEvent> packetEventListener = event -> {
        switch(event.getEventState()){
            case RECEIVING:{
                if (!whitelistOwnBase.getValue()) return;

                final Packet<?> p = event.getPacket();

                if (p instanceof S08PacketPlayerPosLook) {
                    final S08PacketPlayerPosLook s08 = (S08PacketPlayerPosLook) p;
                    final double x = s08.getX();
                    final double y = s08.getY();
                    final double z = s08.getZ();

                    if (mc.thePlayer.getDistance(x, y, z) > 40) {
                        respawnPoint = new Vec3(x, y, z);
                    }
                }
                break;
            }
        }
    };

    public ArrayList<Integer> getBlock(final Block b, final int r) {
        final ArrayList<Integer> pos = new ArrayList<>();

        for (int x = -r; x < r; ++x) {
            for (int y = r; y > -r; --y) {
                for (int z = -r; z < r; ++z) {
                    if (mc.theWorld.getBlockState(new BlockPos(mc.thePlayer.posX + x, mc.thePlayer.posY + y, mc.thePlayer.posZ + z)).getBlock() == b) {
                        pos.add(x);
                        pos.add(y);
                        pos.add(z);
                        return pos;
                    }
                }
            }
        }
        return null;
    }
}