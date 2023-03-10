/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Predicate
 *  com.google.common.base.Predicates
 */
package lodomir.dev.utils.player;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockLiquid;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

public final class PlayerUtils {
    private static final Minecraft mc = Minecraft.getMinecraft();

    public static boolean isHoldingSword() {
        return PlayerUtils.mc.thePlayer.ticksExisted > 3 && PlayerUtils.mc.thePlayer.getCurrentEquippedItem() != null && PlayerUtils.mc.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemSword;
    }

    public static boolean isBlockUnder() {
        if (!(PlayerUtils.mc.thePlayer.posY < 0.0)) {
            for (int offset = 0; offset < (int)PlayerUtils.mc.thePlayer.posY + 2; offset += 2) {
                AxisAlignedBB bb = PlayerUtils.mc.thePlayer.getEntityBoundingBox().offset(0.0, -offset, 0.0);
                if (Minecraft.getMinecraft().theWorld.getCollidingBoundingBoxes(PlayerUtils.mc.thePlayer, bb).isEmpty()) continue;
                return true;
            }
        }
        return false;
    }

    public static boolean isOnSameTeam(EntityLivingBase entity) {
        if (entity.getTeam() != null && PlayerUtils.mc.thePlayer.getTeam() != null) {
            char c2;
            char c1 = entity.getDisplayName().getFormattedText().charAt(1);
            return c1 == (c2 = PlayerUtils.mc.thePlayer.getDisplayName().getFormattedText().charAt(1));
        }
        return false;
    }

    public static boolean isOnLiquid() {
        boolean onLiquid = false;
        AxisAlignedBB playerBB = PlayerUtils.mc.thePlayer.getEntityBoundingBox();
        WorldClient world = PlayerUtils.mc.theWorld;
        int y = (int)playerBB.offset((double)0.0, (double)-0.01, (double)0.0).minY;
        for (int x = MathHelper.floor_double(playerBB.minX); x < MathHelper.floor_double(playerBB.maxX) + 1; ++x) {
            for (int z = MathHelper.floor_double(playerBB.minZ); z < MathHelper.floor_double(playerBB.maxZ) + 1; ++z) {
                Block block = world.getBlockState(new BlockPos(x, y, z)).getBlock();
                if (block == null || block instanceof BlockAir) continue;
                if (!(block instanceof BlockLiquid)) {
                    return false;
                }
                onLiquid = true;
            }
        }
        return onLiquid;
    }

    public static int findGap() {
        for (int i = 36; i < 45; ++i) {
            ItemStack itemStack = PlayerUtils.mc.thePlayer.inventoryContainer.getSlot(i).getStack();
            if (itemStack == null || !itemStack.getDisplayName().contains("Golden") || itemStack.stackSize <= 0 || !(itemStack.getItem() instanceof ItemFood)) continue;
            return i;
        }
        return -1;
    }

    public static int findSoup() {
        for (int i = 36; i < 45; ++i) {
            ItemStack itemStack = PlayerUtils.mc.thePlayer.inventoryContainer.getSlot(i).getStack();
            if (itemStack == null || !itemStack.getDisplayName().contains("Stew") || itemStack.stackSize <= 0 || !(itemStack.getItem() instanceof ItemFood)) continue;
            return i;
        }
        return -1;
    }

    public static int findHead() {
        for (int i = 36; i < 45; ++i) {
            ItemStack itemStack = PlayerUtils.mc.thePlayer.inventoryContainer.getSlot(i).getStack();
            if (itemStack == null || !itemStack.getDisplayName().contains("Head") || itemStack.stackSize <= 0) continue;
            return i;
        }
        return -1;
    }

    public static int findBlock() {
        for (int i = 36; i < 45; ++i) {
            ItemStack itemStack = PlayerUtils.mc.thePlayer.inventoryContainer.getSlot(i).getStack();
            if (itemStack == null || itemStack.stackSize <= 0 || !(itemStack.getItem() instanceof ItemBlock)) continue;
            return i;
        }
        return -1;
    }

    public static int findEmptySlot() {
        for (int i = 36; i < 45; ++i) {
            ItemStack itemStack = PlayerUtils.mc.thePlayer.inventoryContainer.getSlot(i).getStack();
            if (itemStack != null) continue;
            return i - 36;
        }
        return -1;
    }

    public static boolean isInsideBlock() {
        if (PlayerUtils.mc.thePlayer.ticksExisted < 5) {
            return false;
        }
        EntityPlayerSP player = PlayerUtils.mc.thePlayer;
        WorldClient world = PlayerUtils.mc.theWorld;
        AxisAlignedBB bb = player.getEntityBoundingBox();
        for (int x = MathHelper.floor_double(bb.minX); x < MathHelper.floor_double(bb.maxX) + 1; ++x) {
            for (int y = MathHelper.floor_double(bb.minY); y < MathHelper.floor_double(bb.maxY) + 1; ++y) {
                for (int z = MathHelper.floor_double(bb.minZ); z < MathHelper.floor_double(bb.maxZ) + 1; ++z) {
                    AxisAlignedBB boundingBox;
                    Block block = world.getBlockState(new BlockPos(x, y, z)).getBlock();
                    if (block == null || block instanceof BlockAir || (boundingBox = block.getCollisionBoundingBox(world, new BlockPos(x, y, z), world.getBlockState(new BlockPos(x, y, z)))) == null || !player.getEntityBoundingBox().intersectsWith(boundingBox)) continue;
                    return true;
                }
            }
        }
        return false;
    }

    public static Block getBlockRelativeToPlayer(double offsetX, double offsetY, double offsetZ) {
        return PlayerUtils.mc.theWorld.getBlockState(new BlockPos(PlayerUtils.mc.thePlayer.posX + offsetX, PlayerUtils.mc.thePlayer.posY + offsetY, PlayerUtils.mc.thePlayer.posZ + offsetZ)).getBlock();
    }

    public static Block getBlock(double offsetX, double offsetY, double offsetZ) {
        return PlayerUtils.mc.theWorld.getBlockState(new BlockPos(offsetX, offsetY, offsetZ)).getBlock();
    }

    public static boolean isMouseOver(float yaw, float pitch, Entity target, float range) {
        float partialTicks = PlayerUtils.mc.timer.renderPartialTicks;
        Entity entity = mc.getRenderViewEntity();
        Entity mcPointedEntity = null;
        if (entity != null && PlayerUtils.mc.theWorld != null) {
            boolean flag;
            PlayerUtils.mc.mcProfiler.startSection("pick");
            double d0 = PlayerUtils.mc.playerController.getBlockReachDistance();
            MovingObjectPosition objectMouseOver = entity.rayTrace(d0, partialTicks);
            double d1 = d0;
            Vec3 vec3 = entity.getPositionEyes(partialTicks);
            boolean bl = flag = d0 > (double)range;
            if (objectMouseOver != null) {
                d1 = objectMouseOver.hitVec.distanceTo(vec3);
            }
            Vec3 vec31 = PlayerUtils.mc.thePlayer.getVectorForRotation(pitch, yaw);
            Vec3 vec32 = vec3.addVector(vec31.xCoord * d0, vec31.yCoord * d0, vec31.zCoord * d0);
            Entity pointedEntity = null;
            Vec3 vec33 = null;
            float f = 1.0f;
            List<Entity> list = PlayerUtils.mc.theWorld.getEntitiesInAABBexcluding(entity, entity.getEntityBoundingBox().addCoord(vec31.xCoord * d0, vec31.yCoord * d0, vec31.zCoord * d0).expand(1.0, 1.0, 1.0), (Predicate<? super Entity>)Predicates.and(EntitySelectors.NOT_SPECTATING, Entity::canBeCollidedWith));
            double d2 = d1;
            for (Entity entity1 : list) {
                double d3;
                float f1 = entity1.getCollisionBorderSize();
                AxisAlignedBB axisalignedbb = entity1.getEntityBoundingBox().expand(f1, f1, f1);
                MovingObjectPosition movingobjectposition = axisalignedbb.calculateIntercept(vec3, vec32);
                if (axisalignedbb.isVecInside(vec3)) {
                    if (!(d2 >= 0.0)) continue;
                    pointedEntity = entity1;
                    vec33 = movingobjectposition == null ? vec3 : movingobjectposition.hitVec;
                    d2 = 0.0;
                    continue;
                }
                if (movingobjectposition == null || !((d3 = vec3.distanceTo(movingobjectposition.hitVec)) < d2) && d2 != 0.0) continue;
                pointedEntity = entity1;
                vec33 = movingobjectposition.hitVec;
                d2 = d3;
            }
            if (pointedEntity != null && flag && vec3.distanceTo(vec33) > (double)range) {
                pointedEntity = null;
                objectMouseOver = new MovingObjectPosition(MovingObjectPosition.MovingObjectType.MISS, vec33, null, new BlockPos(vec33));
            }
            if (pointedEntity != null && (d2 < d1 || objectMouseOver == null)) {
                objectMouseOver = new MovingObjectPosition(pointedEntity, vec33);
                if (pointedEntity instanceof EntityLivingBase || pointedEntity instanceof EntityItemFrame) {
                    mcPointedEntity = pointedEntity;
                }
            }
            PlayerUtils.mc.mcProfiler.endSection();
            assert (objectMouseOver != null);
            return mcPointedEntity == target;
        }
        return false;
    }

    public static MovingObjectPosition getMouseOver(float yaw, float pitch, float range) {
        float partialTicks = PlayerUtils.mc.timer.renderPartialTicks;
        Entity entity = mc.getRenderViewEntity();
        Entity mcPointedEntity = null;
        if (entity != null && PlayerUtils.mc.theWorld != null) {
            boolean flag;
            PlayerUtils.mc.mcProfiler.startSection("pick");
            double d0 = PlayerUtils.mc.playerController.getBlockReachDistance();
            MovingObjectPosition objectMouseOver = entity.rayTrace(d0, partialTicks);
            double d1 = d0;
            Vec3 vec3 = entity.getPositionEyes(partialTicks);
            boolean bl = flag = d0 > (double)range;
            if (objectMouseOver != null) {
                d1 = objectMouseOver.hitVec.distanceTo(vec3);
            }
            Vec3 vec31 = PlayerUtils.mc.thePlayer.getVectorForRotation(pitch, yaw);
            Vec3 vec32 = vec3.addVector(vec31.xCoord * d0, vec31.yCoord * d0, vec31.zCoord * d0);
            Entity pointedEntity = null;
            Vec3 vec33 = null;
            float f = 1.0f;
            List<Entity> list = PlayerUtils.mc.theWorld.getEntitiesInAABBexcluding(entity, entity.getEntityBoundingBox().addCoord(vec31.xCoord * d0, vec31.yCoord * d0, vec31.zCoord * d0).expand(1.0, 1.0, 1.0), (Predicate<? super Entity>)Predicates.and(EntitySelectors.NOT_SPECTATING, Entity::canBeCollidedWith));
            double d2 = d1;
            for (Entity entity1 : list) {
                double d3;
                float f1 = entity1.getCollisionBorderSize();
                AxisAlignedBB axisalignedbb = entity1.getEntityBoundingBox().expand(f1, f1, f1);
                MovingObjectPosition movingobjectposition = axisalignedbb.calculateIntercept(vec3, vec32);
                if (axisalignedbb.isVecInside(vec3)) {
                    if (!(d2 >= 0.0)) continue;
                    pointedEntity = entity1;
                    vec33 = movingobjectposition == null ? vec3 : movingobjectposition.hitVec;
                    d2 = 0.0;
                    continue;
                }
                if (movingobjectposition == null || !((d3 = vec3.distanceTo(movingobjectposition.hitVec)) < d2) && d2 != 0.0) continue;
                pointedEntity = entity1;
                vec33 = movingobjectposition.hitVec;
                d2 = d3;
            }
            if (pointedEntity != null && flag && vec3.distanceTo(vec33) > (double)range) {
                pointedEntity = null;
                objectMouseOver = new MovingObjectPosition(MovingObjectPosition.MovingObjectType.MISS, vec33, null, new BlockPos(vec33));
            }
            if (pointedEntity != null && (d2 < d1 || objectMouseOver == null)) {
                objectMouseOver = new MovingObjectPosition(pointedEntity, vec33);
                if (pointedEntity instanceof EntityLivingBase || pointedEntity instanceof EntityItemFrame) {
                    mcPointedEntity = pointedEntity;
                }
            }
            PlayerUtils.mc.mcProfiler.endSection();
            assert (objectMouseOver != null);
            return objectMouseOver;
        }
        return null;
    }
}

