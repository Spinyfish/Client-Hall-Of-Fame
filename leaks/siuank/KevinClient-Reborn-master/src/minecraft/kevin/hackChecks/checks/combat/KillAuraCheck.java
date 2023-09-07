package kevin.hackChecks.checks.combat;

import kevin.hackChecks.Check;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

import java.util.List;

public class KillAuraCheck extends Check {
    short rotBuffer = 0, accBuffer = 0;
    public KillAuraCheck(EntityOtherPlayerMP playerMP) {
        super(playerMP);
        name = "KillAura";
        checkViolationLevel = 30;
    }

    @Override
    public void onLivingUpdate() {
        float delta = Math.abs(handlePlayer.prevRotationYaw - handlePlayer.rotationYaw);
        boolean flag = false;
        String msg = null;
        boolean ableReward = true;
        if (delta > 45 && handlePlayer.swingProgress != 0) {
            if ((rotBuffer += 6) > 20) {
                flag = true;
                msg = String.format("Rotation change too fast, delta yaw: %.2f", delta);
            }
        } else {
            rotBuffer -= rotBuffer > 0 ? 1 : 0;
            if (rotBuffer > 2) {
                ableReward = false;
            }
        }
        List<Entity> entities = mc.theWorld.getEntitiesWithinAABBExcludingEntity(handlePlayer, handlePlayer.getEntityBoundingBox().expand(6.0, 5.0, 6.0));
        if (!entities.isEmpty() && handlePlayer.swingProgress != 0) {
            Vec3 vecEye = handlePlayer.getPositionEyes(1f);
            Vec3 vecTarget = handlePlayer.getLookVec().addVector(vecEye.xCoord, vecEye.yCoord, vecEye.zCoord);
            short bufferAdder = 0;
            if (accBuffer > 0) bufferAdder = -1;
            for (Entity entity : entities) {
                AxisAlignedBB bb = entity.getEntityBoundingBox().expand(0.1, 0.1, 0.1);
                // Check if target didn't move or player's eyes is in other player
                if (bb.isVecInside(vecEye) || (entity.prevPosX == entity.posX && entity.prevPosZ == entity.posZ)) {
                    bufferAdder = 0;
                    continue;
                }
                MovingObjectPosition mop = bb.calculateIntercept(vecEye, vecTarget);
                if (mop.hitVec != null) { // hit!
                    bufferAdder = 3;
                    break;
                }
            }
            accBuffer += bufferAdder;
            if (bufferAdder > 0) {
                if (accBuffer > 55) { // max 2s? if timer is 1.
                    flag = true;
                    if (msg == null) msg = "Always aiming to other entity, b: " + accBuffer;
                }
            } else {
                if (accBuffer > 20) {
                    ableReward = false;
                }
            }
        }

        if (flag) {
            flag(msg, 4);
        } else if (ableReward) {
            reward();
        }
    }

    @Override
    public void reward() {
        shrinkVL(0.99);
    }

    @Override
    public String description() {
        return "always aiming to other entity";
    }
}
