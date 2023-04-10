package crispy.util.pathfinder;

import crispy.util.rotation.LookUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLadder;
import net.minecraft.block.BlockVine;
import net.minecraft.client.Minecraft;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3i;

import java.util.ArrayList;

public class WalkPathProcessor extends PathProcessor {
    public WalkPathProcessor(ArrayList<PathPos> path) {
        super(path);
    }

    @Override
    public void process() {
        BlockPos pos;
        if (mc.thePlayer.onGround) {
            pos = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY + 0.5, mc.thePlayer.posZ);
        } else pos = new BlockPos(mc.thePlayer.getPosition());
        PathPos nextPos = path.get(index);
        int posIndex = path.indexOf(new PathPos(pos));
        System.out.println(path.size());
        if (posIndex == -1)
            ticksOffPath++;
        else
            ticksOffPath = 0;

        // update index
        if (pos.equals(nextPos)) {
            index++;

            // disable when done
            if (index >= path.size())
                done = true;
            return;
        } else if (posIndex > index) {
            index = posIndex + 1;

            // disable when done
            if (index >= path.size())
                done = true;
            return;
        }
        lockControls();
        facePosition(nextPos);

        if (MathHelper.wrapAngleTo180_double(Math.abs(LookUtils
                .getHorizontalAngleToLookVec(new Vec3i(nextPos.getX(), nextPos.getY(), nextPos.getZ())))) > 90)
            return;
        if (pos.getX() != nextPos.getX() || pos.getZ() != nextPos.getZ()) {
            mc.gameSettings.keyBindForward.pressed = true;
            if (index > 0 && path.get(index - 1).isJumping()
                    || pos.getY() < nextPos.getY())
                mc.gameSettings.keyBindJump.pressed = true;
        } else if (pos.getY() != nextPos.getY()) {
            if (pos.getY() < nextPos.getY()) {
                // climb up
                // TODO: Spider
                Block block = Minecraft.theWorld.getBlockState(pos).getBlock();
                if (block instanceof BlockLadder || block instanceof BlockVine) {
                    faceVectorClientIgnorePitch(new Vec3i(pos.getX(), pos.getY(), pos.getZ()));

                    mc.gameSettings.keyBindForward.pressed = true;

                } else {
                    // directional jump
                    if (index < path.size() - 1
                            && !nextPos.up().equals(path.get(index + 1)))
                        index++;

                    // jump up

                    mc.gameSettings.keyBindJump.pressed = true;
                }

                // go down
            } else {
                // skip mid-air nodes and go straight to the bottom
                while (index < path.size() - 1
                        && path.get(index).down().equals(path.get(index + 1)))
                    index++;

                // walk off the edge
                if (mc.thePlayer.onGround)
                    mc.gameSettings.keyBindForward.pressed = true;
            }
        }


    }
}
