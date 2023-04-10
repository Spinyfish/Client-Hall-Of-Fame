package crispy.features.hacks.impl.misc;

import crispy.features.event.Event;
import crispy.features.event.impl.player.EventUpdate;
import crispy.features.hacks.Category;
import crispy.features.hacks.Hack;
import crispy.features.hacks.HackInfo;
import crispy.util.time.TimeHelper;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;

import java.util.ArrayList;

@HackInfo(name = "BedFlipper", category = Category.MISC)
public class Destroyer extends Hack {
    private static final int radius = 6;
    private final ArrayList<BlockPos> blockPos = new ArrayList<>();
    TimeHelper timer = new TimeHelper();
    int delay = 0;

    @Override
    public void onEvent(Event e) {
        if (e instanceof EventUpdate) {
            EventUpdate event = (EventUpdate) e;
            Minecraft mc = Minecraft.getMinecraft();
            if (timer.hasReached(1)) {
                timer.reset();
                blockPos.clear();
            }
            for (int x = -radius; x < radius; x++) {
                for (int y = radius; y > -radius; y--) {
                    for (int z = -radius; z < radius; z++) {
                        int xPos = ((int) mc.thePlayer.posX + x);
                        int yPos = ((int) mc.thePlayer.posY + y);
                        int zPos = ((int) mc.thePlayer.posZ + z);

                        BlockPos bP = new BlockPos(xPos, yPos, zPos);
                        Block b = Minecraft.theWorld.getBlockState(bP).getBlock();
                        if (blockPos.contains(bP))
                            return;
                        if (b.getBlockState().getBlock() == Block.getBlockById(26)) {
                            float[] facing = getFacingRotations(xPos, yPos, zPos,
                                    EnumFacing.NORTH);

                            float yaw = facing[0];
                            float pitch = Math.min(90, facing[1] + 9);

                            event.setYaw(yaw);
                            event.setPitch(pitch);
                            blockPos.add(bP);
                            mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.START_DESTROY_BLOCK, bP, EnumFacing.NORTH));
                            mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK, bP, EnumFacing.NORTH));


                        }
                        if (b.getBlockState().getBlock() == Block.getBlockById(122) || b.getBlockState().getBlock() == Block.getBlockById(122)) {
                            float[] facing = getFacingRotations(xPos, yPos, zPos,
                                    EnumFacing.NORTH);

                            float yaw = facing[0];
                            float pitch = Math.min(90, facing[1] + 9);

                            event.setYaw(yaw);
                            event.setPitch(pitch);

                            blockPos.add(bP);
                            if (delay >= 1) {
                                mc.playerController.onPlayerDamageBlock(bP, EnumFacing.NORTH);

                            }
                            if (delay == 19) {
                                delay = 0;
                            }
                        }


                    }

                }
            }
        }
    }

    private float[] getFacingRotations(int paramInt1, int paramInt2, int paramInt3, EnumFacing paramEnumFacing) {
        EntityPig localEntityPig = new EntityPig(Minecraft.theWorld);
        localEntityPig.posX = (double) paramInt1 + 0.5;
        localEntityPig.posY = (double) paramInt2 + 0.5;
        localEntityPig.posZ = (double) paramInt3 + 0.5;
        localEntityPig.posX += (double) paramEnumFacing.getDirectionVec().getX() * 0.25;
        localEntityPig.posY += (double) paramEnumFacing.getDirectionVec().getY() * 0.25;
        localEntityPig.posZ += (double) paramEnumFacing.getDirectionVec().getZ() * 0.25;
        return jdMethod_double(localEntityPig);
    }

    float[] jdMethod_double(EntityLivingBase paramEntityLivingBase) {
        double d1 = paramEntityLivingBase.posX - mc.thePlayer.posX;
        double d2 = paramEntityLivingBase.posY + (double) paramEntityLivingBase.getEyeHeight() - (mc.thePlayer.posY + (double) mc.thePlayer.getEyeHeight());
        double d3 = paramEntityLivingBase.posZ - mc.thePlayer.posZ;
        double d4 = MathHelper.sqrt_double(d1 * d1 + d3 * d3);
        float f1 = (float) (Math.atan2(d3, d1) * 180.0 / 3.141592653589793) - 90.0f;
        float f2 = (float) (-Math.atan2(d2, d4) * 180.0 / 3.141592653589793);
        return new float[]{f1, f2};
    }

}
