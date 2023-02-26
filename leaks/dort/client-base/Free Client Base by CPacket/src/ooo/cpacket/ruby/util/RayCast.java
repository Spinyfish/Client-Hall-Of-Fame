package ooo.cpacket.ruby.util;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

public class RayCast {
	public static Entity raycast(Minecraft mc, double r3, Entity entiy) {
		if (entiy == null)
			return null;
        Entity var2 = mc.thePlayer;
        Vec3 var9 = entiy.getPositionVector().add(new Vec3(0, entiy.getEyeHeight(), 0));
        Vec3 var7 = mc.thePlayer.getPositionVector().add(new Vec3(0, mc.thePlayer.getEyeHeight(), 0));
        Vec3 var10 = null;
        float var11 = 1.0F;
        AxisAlignedBB a = mc.thePlayer.getEntityBoundingBox()
                .addCoord(var9.xCoord - var7.xCoord, var9.yCoord - var7.yCoord, var9.zCoord - var7.zCoord)
                .expand(var11, var11, var11);
        List var12 = mc.theWorld.getEntitiesWithinAABBExcludingEntity(var2, a);
        double var13 = r3 + 0.5f;
        Entity b = null;
        for (int var15 = 0; var15 < var12.size(); ++var15) {
            Entity var16 = (Entity) var12.get(var15);
 
            if (var16.canBeCollidedWith()) {
                float var17 = var16.getCollisionBorderSize();
                AxisAlignedBB var18 = var16.getEntityBoundingBox().expand((double) var17, (double) var17,
                        (double) var17);
                MovingObjectPosition var19 = var18.calculateIntercept(var7, var9);
 
                if (var18.isVecInside(var7)) {
                    if (0.0D < var13 || var13 == 0.0D) {
                        b = var16;
                        var10 = var19 == null ? var7 : var19.hitVec;
                        var13 = 0.0D;
                    }
                } else if (var19 != null) {
                    double var20 = var7.distanceTo(var19.hitVec);
 
                    if (var20 < var13 || var13 == 0.0D) {
                        b = var16;
                        var10 = var19.hitVec;
                        var13 = var20;
                    }
                }
            }
        }
        return b;
    }
}
