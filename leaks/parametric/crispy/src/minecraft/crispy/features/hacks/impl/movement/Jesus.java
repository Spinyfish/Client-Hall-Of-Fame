package crispy.features.hacks.impl.movement;

import crispy.features.event.Event;
import crispy.features.event.impl.movement.EventCollide;
import crispy.features.event.impl.player.EventUpdate;
import crispy.features.hacks.Category;
import crispy.features.hacks.Hack;
import crispy.features.hacks.HackInfo;
import crispy.util.player.BlockUtil;
import net.minecraft.block.BlockLiquid;
import net.minecraft.client.Minecraft;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.superblaubeere27.valuesystem.ModeValue;

import java.util.ArrayList;
import java.util.Arrays;

@HackInfo(name = "Jesus", category = Category.MOVEMENT)
public class Jesus extends Hack {
    ModeValue mode = new ModeValue("Mode", "NCP", "NCP", "Vanilla");
    @Override
    public void onEvent(Event e) {
        if(e instanceof EventUpdate) {
            switch (mode.getMode()) {
                case "NCP": {
                    EventUpdate event = (EventUpdate) e;

                    boolean shi = shouldAmen();
                    if(mc.thePlayer.isInWater() && !mc.thePlayer.isSneaking() && shi) {
                        mc.thePlayer.motionY = 0.09;
                    }
                    if(BlockUtil.isOnLiquid(0.001) && event.isPre()) {
                        if(BlockUtil.isTotalOnLiquid(0.001) && mc.thePlayer.onGround && !mc.thePlayer.isInWater()) {
                            event.setY(event.getY() +(mc.thePlayer.ticksExisted % 2 == 0 ? 0.0000000001D : -0.000000000001D));

                        }
                    }
                    break;
                }
                case "Vanilla": {
                    if (BlockUtil.isOnLiquid()) {
                        this.mc.thePlayer.motionY = 1.0E-6;
                        this.mc.thePlayer.onGround = true;
                        break;
                    }
                    break;
                }
            }

        } else if(e instanceof EventCollide) {
            EventCollide ebb = (EventCollide) e;
            if(BlockUtil.isOnLiquid(0.001)) {
            int thoseDUcks = -1;

            if (ebb.getPosY() + 0.9 < mc.thePlayer.boundingBox.minY) {
                BlockPos pos = new BlockPos(ebb.getPosX(), ebb.getPosY(), ebb.getPosZ());
                if (Minecraft.theWorld.getBlockState(pos).getProperties().get(BlockLiquid.LEVEL) instanceof Integer) {
                    thoseDUcks = (int) Minecraft.theWorld.getBlockState(pos).getProperties().get(BlockLiquid.LEVEL);
                }
                if (thoseDUcks <= 4) {
                    ebb.setBoundingBox(new AxisAlignedBB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1));

                }
            }

            }
        }
    }
    boolean shouldAmen(){
        double x = mc.thePlayer.posX; double y = mc.thePlayer.posY; double z = mc.thePlayer.posZ;
        ArrayList<BlockPos> pos = new ArrayList<BlockPos>(Arrays.asList(new BlockPos(x + 0.3, y, z+0.3),
                new BlockPos(x - 0.3, y, z+0.3),new BlockPos(x + 0.3, y, z-0.3),new BlockPos(x - 0.3, y, z-0.3)));
        for(BlockPos po : pos){
            if(!(Minecraft.theWorld.getBlockState(po).getBlock() instanceof BlockLiquid))
                continue;
            if(Minecraft.theWorld.getBlockState(po).getProperties().get(BlockLiquid.LEVEL) instanceof Integer){
                if((int) Minecraft.theWorld.getBlockState(po).getProperties().get(BlockLiquid.LEVEL) <= 4){
                    return true;
                }
            }
        }


        return false;
    }
}
