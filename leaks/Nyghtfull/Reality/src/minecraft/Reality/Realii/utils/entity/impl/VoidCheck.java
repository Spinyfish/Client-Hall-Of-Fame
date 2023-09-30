/*
 * Decompiled with CFR 0.150.
 */
package Reality.Realii.utils.entity.impl;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import Reality.Realii.utils.entity.ICheck;

public final class VoidCheck
        implements ICheck {
    @Override
    public boolean validate(Entity entity) {
        return this.isBlockUnder(entity);
    }

    private boolean isBlockUnder(Entity entity) {
        int offset = 0;
        while ((double) offset < entity.posY + (double) entity.getEyeHeight()) {
            AxisAlignedBB boundingBox = entity.getEntityBoundingBox().offset(Minecraft.getMinecraft().thePlayer.motionX * 4, -offset, Minecraft.getMinecraft().thePlayer.motionZ * 4);
            if (!Minecraft.getMinecraft().theWorld.getCollidingBoundingBoxes(entity, boundingBox).isEmpty()) {
                return true;
            }
            offset += 2;
        }
        return false;
    }
}

