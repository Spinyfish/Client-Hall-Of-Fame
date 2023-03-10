/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.ai;

import java.util.Random;
import net.minecraft.entity.EntityCreature;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

public class RandomPositionGenerator {
    private static Vec3 staticVector = new Vec3(0.0, 0.0, 0.0);

    public static Vec3 findRandomTarget(EntityCreature entitycreatureIn, int xz, int y) {
        return RandomPositionGenerator.findRandomTargetBlock(entitycreatureIn, xz, y, null);
    }

    public static Vec3 findRandomTargetBlockTowards(EntityCreature entitycreatureIn, int xz, int y, Vec3 targetVec3) {
        staticVector = targetVec3.subtract(entitycreatureIn.posX, entitycreatureIn.posY, entitycreatureIn.posZ);
        return RandomPositionGenerator.findRandomTargetBlock(entitycreatureIn, xz, y, staticVector);
    }

    public static Vec3 findRandomTargetBlockAwayFrom(EntityCreature entitycreatureIn, int xz, int y, Vec3 targetVec3) {
        staticVector = new Vec3(entitycreatureIn.posX, entitycreatureIn.posY, entitycreatureIn.posZ).subtract(targetVec3);
        return RandomPositionGenerator.findRandomTargetBlock(entitycreatureIn, xz, y, staticVector);
    }

    private static Vec3 findRandomTargetBlock(EntityCreature entitycreatureIn, int xz, int y, Vec3 targetVec3) {
        double d1;
        double d0;
        Random random = entitycreatureIn.getRNG();
        boolean flag = false;
        int i = 0;
        int j = 0;
        int k = 0;
        float f = -99999.0f;
        boolean flag1 = entitycreatureIn.hasHome() ? (d0 = entitycreatureIn.getHomePosition().distanceSq(MathHelper.floor_double(entitycreatureIn.posX), MathHelper.floor_double(entitycreatureIn.posY), MathHelper.floor_double(entitycreatureIn.posZ)) + 4.0) < (d1 = (double)(entitycreatureIn.getMaximumHomeDistance() + (float)xz)) * d1 : false;
        for (int j1 = 0; j1 < 10; ++j1) {
            float f1;
            int l = random.nextInt(2 * xz + 1) - xz;
            int k1 = random.nextInt(2 * y + 1) - y;
            int i1 = random.nextInt(2 * xz + 1) - xz;
            if (targetVec3 != null && !((double)l * targetVec3.xCoord + (double)i1 * targetVec3.zCoord >= 0.0)) continue;
            if (entitycreatureIn.hasHome() && xz > 1) {
                BlockPos blockpos = entitycreatureIn.getHomePosition();
                l = entitycreatureIn.posX > (double)blockpos.getX() ? (l -= random.nextInt(xz / 2)) : (l += random.nextInt(xz / 2));
                i1 = entitycreatureIn.posZ > (double)blockpos.getZ() ? (i1 -= random.nextInt(xz / 2)) : (i1 += random.nextInt(xz / 2));
            }
            BlockPos blockpos1 = new BlockPos(l += MathHelper.floor_double(entitycreatureIn.posX), k1 += MathHelper.floor_double(entitycreatureIn.posY), i1 += MathHelper.floor_double(entitycreatureIn.posZ));
            if (flag1 && !entitycreatureIn.isWithinHomeDistanceFromPosition(blockpos1) || !((f1 = entitycreatureIn.getBlockPathWeight(blockpos1)) > f)) continue;
            f = f1;
            i = l;
            j = k1;
            k = i1;
            flag = true;
        }
        if (flag) {
            return new Vec3(i, j, k);
        }
        return null;
    }
}

